package no.kristiania.http;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final ServerSocket serverSocket;
    private ServerThread serverThread;
    private MemberDao memberDao;


    public HttpServer(int port, DataSource dataSource) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.memberDao = new MemberDao(dataSource);
        serverThread = new ServerThread();
    }

    public HttpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        serverThread = new ServerThread();
    }


    private class ServerThread extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    Socket socket = serverSocket.accept();
                    handleRequest(socket);
                } catch (IOException | SQLException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    public void start(){ serverThread.start(); }

    public void stop() throws IOException {
        serverSocket.close();
        serverThread = null;
    }

    public int getActualPort(){
        return serverSocket.getLocalPort();
    }


    private void handleRequest(Socket socket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage();
        HttpMessage request = new HttpMessage();

        String requestLine = HttpMessage.readLine(socket);

        String[] requestLineParts = requestLine.split(" ");

        String requestMethod = requestLineParts[0];

        String requestTarget = requestLineParts.length > 1 ? requestLineParts[1] : "";;

        if(!requestTarget.equals("/favicon.ico")){
            logger.info("REQUEST LINE: {}", requestLine);
        }

        int questionPosition = requestTarget.indexOf('?');
        String requestPath = questionPosition != -1 ? requestTarget.substring(0, questionPosition) : requestTarget;

        if (requestTarget.equals("") || requestTarget.equals("/")) {

            response.setCodeAndStartLine("200");
            response.setBody("Hello world");
            response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
            response.setHeader("Content-Type", "text/plain");
            response.setHeader("Connection", "close");
            response.write(socket);
            return;
        }

        if(requestPath.equals("/favicon.ico")) {
            handleFileRequest(socket, response, requestPath);
            return;
        }

        if(requestPath.equals("/api/member")) {
            handleGetMembers(socket);
            return;
        }

        if(requestMethod.equals("POST") || requestTarget.equals("/submit")) {
            handlePostRequest(socket, response, request);
            return;
        }



        if (questionPosition != -1) {
            String queryStringLine = requestTarget.substring(questionPosition + 1);
            QueryString.putQueryParametersIntoHttpMessageHeaders(request, queryStringLine);

        } else if (!requestTarget.contains("echo")) {
            handleFileRequest(socket, response, requestTarget);
            return;
        }

        if (request.getHeader("body") != null) {
            String requestBody = request.getHeader("body");
            response.setBody(requestBody);
        } else {
            response.setBody("Hello World");
        }

        if (request.getHeader("Location") != null) {
            response.setHeader("Location", request.getHeader("Location"));
        }

        if (request.getHeader("status") != null) {
            response.setCode(request.getHeader("status"));
        } else {
            response.setCode("200");
        }

        System.out.println("Bottom of handleRequest()");
        response.write(socket);
    }

    private void handleFileRequest(Socket socket, HttpMessage response, String requestPath) throws IOException {

        try (InputStream inputStream = getClass().getResourceAsStream(requestPath)) {
            if (inputStream == null) {
                String body = requestPath + " does not exist";

                response.setCodeAndStartLine("404");
                response.setBody(body);
                response.setHeader("Content-Length", Integer.toString(body.length()));
                response.setHeader("Content-Type", "text/plain");
                response.setHeader("Connection", "close");
                response.write(socket);
                return;
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);

            String fileExtension = requestPath.split("\\.(?=[^\\.]+$)")[1];

            String contentType;
            switch (fileExtension) {
                case "txt":
                    contentType = "text/plain";
                    break;
                case "css":
                    contentType = "text/css";
                    break;
                default:
                    contentType = "text/html";
                    break;
            }

            response.setCodeAndStartLine("200");
            response.setHeader("Content-Length", String.valueOf(buffer.toByteArray().length));
            response.setHeader("Connection", "close");
            response.setHeader("Content-Type", contentType);
            response.write(socket, buffer);
        }
    }

    private void handleGetMembers(Socket socket) throws SQLException, IOException {

        StringBuilder body = new StringBuilder();
        body.append("<ul>");

        for (Member member : memberDao.list()) {
            body.append("<li><strong>Name:</strong> " + member.getFirstName() + " " + member.getLastName() + " - <strong>Email:</strong> " + member.getEmail() + "</li>");
        }

        body.append("</ul>");

        HttpMessage response = new HttpMessage();
        response.setBody(body.toString());
        response.setCodeAndStartLine("200");
        response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Connection", "close");
        response.write(socket);
    }

    private void handlePostRequest(Socket socket, HttpMessage response, HttpMessage request) throws IOException, SQLException {

        request.readAndSetHeaders(socket);
        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);
        request.setBody(body);


        Map<String, String> memberQueryMap = QueryString.queryStringToHashMap(body);
        String memberFirstName = java.net.URLDecoder.decode(memberQueryMap.get("firstName"), StandardCharsets.ISO_8859_1.name());
        String memberLastName = java.net.URLDecoder.decode(memberQueryMap.get("lastName"), StandardCharsets.ISO_8859_1.name());
        String memberEmail = java.net.URLDecoder.decode(memberQueryMap.get("email"), StandardCharsets.ISO_8859_1.name());

        Member member = new Member(memberFirstName, memberLastName, memberEmail);

        memberDao.insert(member);

        response.setCodeAndStartLine("204");
        response.setHeader("Connection", "close");
        response.setHeader("Content-length", "0");
        response.write(socket);
    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
        }

        PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));

        Flyway.configure().dataSource(dataSource).load().migrate();

        HttpServer server = new HttpServer(8080, dataSource);
        server.start();
        logger.info("Started on http://localhost:{}/index.html", 8080);
        logger.info("Go to http://localhost:{}/addProjectMember.html to add project members", 8080);
    }
}
