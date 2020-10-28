package no.kristiania.http;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
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
import java.util.Map;
import java.util.Properties;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final ServerSocket serverSocket;
    private ServerThread serverThread;
    private MemberDao memberDao;
    private TaskDao taskDao;


    public HttpServer(int port, DataSource dataSource) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.memberDao = new MemberDao(dataSource);
        this.taskDao = new TaskDao(dataSource);
        serverThread = new ServerThread();
    }

    public HttpServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        serverThread = new ServerThread();
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
        server.start();
        logger.info("Started on http://localhost:{}/", 8080);
        logger.info("Go to http://localhost:{}/addProjectMember.html to add project members", 8080);
        logger.info("Go to http://localhost:{}/addProjectTask.html to add tasks", 8080);
    }

    public void start() {
        serverThread.start();
    }

    public void stop() throws IOException {
        serverSocket.close();
        serverThread = null;
    }

    public int getActualPort() {
        return serverSocket.getLocalPort();
    }


    private void handleRequest(Socket socket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage();
        HttpMessage request = new HttpMessage();

        String requestLine = HttpMessage.readLine(socket);
        if(requestLine == null)return;

        String[] requestLineParts = requestLine.split(" ");

        String requestMethod = requestLineParts[0];

        String requestTarget = requestLineParts.length > 1 ? requestLineParts[1] : "";

        if(!requestTarget.equals("/favicon.ico")){
            logger.info("REQUEST LINE: {}", requestLine);
        }

        int questionPosition = requestTarget.indexOf('?');
        String requestPath = questionPosition != -1 ? requestTarget.substring(0, questionPosition) : requestTarget;

        if(requestTarget.equals("/")||requestTarget.equals("")){
            requestTarget = "/index.html";
        }

        if (requestPath.equals("/favicon.ico")) {
            handleFileRequest(socket, response, requestPath);
            return;
        }

        if(requestMethod.equals("POST") || requestTarget.equals("/submit")){
            handlePostRequest(socket, response, request, requestTarget);
            return;
        }

        if (requestPath.startsWith("/api/")) {
            handleGetData(socket, requestPath);
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

    private void handleGetData(Socket socket, String requestPath) throws SQLException, IOException {

        if(requestPath.equals("/api/member")) {
            handleGetMember(socket);
        } else if(requestPath.equals("/api/task")) {
            handleGetTask(socket);
        } else if(requestPath.equals("/api/memberSelect")) {
            handleGetMemberSelect(socket);
        }

        return;
    }

    private void handleGetMemberSelect(Socket socket) throws SQLException, IOException {
        StringBuilder body = new StringBuilder();

        body.append("<select id=\"select-member\">");

        for(Member member : memberDao.list()){
            body.append("<option value=\"member-select-" + member.getId() + "\">")
                    .append(member.getFirstName())
                    .append(" ")
                    .append(member.getLastName())
                    .append("</option>");
        }

        body.append("</select>");
        body.append("<button onclick=\"addMemberToTask()\">Add</button>");

        HttpMessage response = new HttpMessage();
        response.setBody(body.toString());
        response.setCodeAndStartLine("200");
        response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Connection", "close");
        response.write(socket);
    }

    private void handleGetTask(Socket socket) throws SQLException, IOException {

        StringBuilder body = new StringBuilder();

        body.append("<ul>");

        for(Task task : taskDao.list()){
            body.append("<li id =\"task-li-" + task.getId() + "\"><strong>Task: </strong> " + task.getName())
                    .append(" <strong>Description: </strong>" + task.getDescription())
                    .append("  <strong>Status: </strong>" + task.getStatus().toString())
                    .append("<button onclick=\"renderSelectMembers(" + task.getId() + ")\">+</button>")
                    .append("</li>");
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

    private void handleGetMember(Socket socket) throws SQLException, IOException {

        StringBuilder body = new StringBuilder();

        body.append("<ul>");

        for(Member member : memberDao.list()){
            body.append("<li><strong>Name:</strong> ").append(member.getFirstName()).append(" ").append(member.getLastName()).append(" - <strong>Email:</strong> ").append(member.getEmail()).append("</li>");
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

    private void handlePostRequest(Socket socket, HttpMessage response, HttpMessage request, String requestTarget) throws IOException, SQLException {

        if(requestTarget.equals("/api/addNewMember")) {
            postNewMember(socket, response, request);
        }

        if(requestTarget.equals("/api/addNewTask")) {
            postNewTask(socket, response, request);
        }
    }

    private void postNewTask(Socket socket, HttpMessage response, HttpMessage request) throws IOException, SQLException {
        request.readAndSetHeaders(socket);
        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);

        request.setBody(body);

        Map<String, String> memberQueryMap = QueryString.queryStringToHashMap(body);

        String taskName = memberQueryMap.get("name");
        String taskDescription = memberQueryMap.get("description");
        String taskStatus = memberQueryMap.get("status");

        Task task = new Task(taskName, taskDescription, taskStatus);

        taskDao.insert(task);

        response.setCodeAndStartLine("204");
        response.setHeader("Connection", "close");
        response.setHeader("Content-length", "0");
        response.write(socket);

    }

    private void postNewMember(Socket socket, HttpMessage response, HttpMessage request) throws IOException, SQLException {
        request.readAndSetHeaders(socket);
        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);
        request.setBody(body);

        Map<String, String> memberQueryMap = QueryString.queryStringToHashMap(body);
        String memberFirstName = memberQueryMap.get("firstName");
        String memberLastName = memberQueryMap.get("lastName");
        String memberEmail = memberQueryMap.get("email");

        Member member = new Member(memberFirstName, memberLastName, memberEmail);

        memberDao.insert(member);

        response.setCodeAndStartLine("204");
        response.setHeader("Connection", "close");
        response.setHeader("Content-length", "0");
        response.write(socket);
    }

    private class ServerThread extends Thread {
        @Override
        public void run() {
            while(true){
                try{
                    Socket socket = serverSocket.accept();
                    handleRequest(socket);
                }catch(IOException | SQLException e){
                    //e.printStackTrace();
                }
            }
        }
    }
}
