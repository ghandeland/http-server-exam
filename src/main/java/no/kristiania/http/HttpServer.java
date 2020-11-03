package no.kristiania.http;

import no.kristiania.db.DepartmentDao;
import no.kristiania.db.MemberDao;
import no.kristiania.db.TaskDao;
import no.kristiania.db.TaskMemberDao;
import no.kristiania.http.controller.*;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Properties;

public class HttpServer {

    public static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final ServerSocket serverSocket;
    private final Map <String, HttpController> controllers;

    public HttpServer(int port, DataSource dataSource) throws IOException {
        this.serverSocket = new ServerSocket(port);
        MemberDao memberDao = new MemberDao(dataSource);
        TaskDao taskDao = new TaskDao(dataSource);
        TaskMemberDao taskMemberDao = new TaskMemberDao(dataSource);
        DepartmentDao departmentDao = new DepartmentDao(dataSource);

        controllers = Map.ofEntries(
                new SimpleEntry <>("/api/addNewMember", new MemberPostController(memberDao)),
                new SimpleEntry <>("/api/addNewTask", new TaskPostController(taskDao)),
                new SimpleEntry <>("/api/addMemberToTask", new MemberTaskPostController(taskMemberDao)),
                new SimpleEntry <>("/api/member", new MemberGetController(memberDao, departmentDao)),
                new SimpleEntry <>("/api/task", new TaskGetController(taskDao, memberDao, taskMemberDao)),
                new SimpleEntry <>("/api/memberSelect", new MemberSelectGetController(memberDao)),
                new SimpleEntry <>("/api/taskSelect", new TaskSelectGetController(taskDao)),
                new SimpleEntry <>("/api/department", new DepartmentGetController(departmentDao)),
                new SimpleEntry <>("/api/addNewDepartment", new DepartmentPostController(departmentDao)),
                new SimpleEntry <>("/api/departmentSelect", new DepartmentSelectGetController(departmentDao)),
                new SimpleEntry <>("/api/filterTask", new TaskFilterPostController(taskDao)),
                new SimpleEntry <>("/api/showFilterTask", new TaskFilterGetController(memberDao, taskMemberDao)),
                new SimpleEntry <>("/api/alterTask", new TaskAlterController(taskDao))
        );

        new Thread(() -> {
            while(true){
                try{
                    Socket socket = serverSocket.accept();
                    handleRequest(socket);
                }catch(SQLException | IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        try(FileReader fileReader = new FileReader("pgr203.properties")){
            properties.load(fileReader);
        }

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));

        Flyway.configure().dataSource(dataSource).load().migrate();

        HttpServer server = new HttpServer(8080, dataSource);
        logger.info("Started on http://localhost:{}/", server.getPort());
        logger.info("Add project members -> http://localhost:{}/addProjectMember.html", server.getPort());
        logger.info("Add tasks -> http://localhost:{}/addProjectTask.html", server.getPort());
        logger.info("Assign members to tasks -> http://localhost:{}/addMemberToTask.html", server.getPort());
    }

    private HttpController getController(String requestPath) {
        return controllers.get(requestPath);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    private void handleRequest(Socket socket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage();
        HttpMessage request = new HttpMessage();

        String requestLine = HttpMessage.readLine(socket);
        if(requestLine == null) return;

        String[] requestLineParts = requestLine.split(" ");

        String requestMethod = requestLineParts[0];

        String requestTarget = requestLineParts.length > 1 ? requestLineParts[1] : "";

        if(!requestTarget.equals("/favicon.ico")){
            logger.info("REQUEST LINE: {}", requestLine);
        }

        int questionPosition = requestTarget.indexOf('?');
        String requestPath = questionPosition != -1 ? requestTarget.substring(0, questionPosition) : requestTarget;

        if(requestTarget.equals("/") || requestTarget.equals("")){
            requestTarget = "/index.html";
        }

        if(requestPath.startsWith("/api/")){
            getController(requestPath).handle(request, socket);
            return;
        }

        if(questionPosition != -1){
            String queryStringLine = requestTarget.substring(questionPosition + 1);
            QueryString.putQueryParametersIntoHttpMessageHeaders(request, queryStringLine);

            handleQueryRequest(socket, response, request);
            return;

        }

        handleFileRequest(socket, response, requestTarget);
    }

    private void handleQueryRequest(Socket socket, HttpMessage response, HttpMessage request) throws IOException {

        if(request.getHeader("body") != null){
            response.setBody(request.getHeader("body"));
        }else{
            response.setBody("Hello World");
        }

        if(request.getHeader("Location") != null){
            response.setHeader("Location", request.getHeader("Location"));
        }

        if(request.getHeader("status") != null){
            response.setCodeAndStartLine(request.getHeader("status"));
        }else{
            response.setCodeAndStartLine("200");
        }

        response.setHeader("Connection", "close");
        response.setHeader("Content-type", "text/plain");
        if(response.getBody() != null){
            response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        }
        response.write(socket);
    }

    private void handleFileRequest(Socket socket, HttpMessage response, String requestPath) throws IOException {

        try(InputStream inputStream = getClass().getResourceAsStream(requestPath)){
            if(inputStream == null){
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
            switch(fileExtension){
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

}
