package no.kristiania.http;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HttpServer {

    public static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final ServerSocket serverSocket;
    private final Map <String, AbstractController> controllers;

    public HttpServer(int port, DataSource dataSource) throws IOException {
        this.serverSocket = new ServerSocket(port);
        controllers = new HashMap <>();
        controllers.put("/api/addNewMember", new MemberPostController(dataSource));
        controllers.put("/api/addNewTask", new TaskPostController(dataSource));
        controllers.put("/api/addMemberToTask", new MemberTaskPostController(dataSource));
        controllers.put("/api/member", new MemberGetController(dataSource));
        controllers.put("/api/task", new TaskGetController(dataSource));
        controllers.put("/api/memberSelect", new MemberSelectGetController(dataSource));
        controllers.put("/api/taskSelect", new TaskSelectGetController(dataSource));
        controllers.put("/api/department", new DepartmentGetController(dataSource));
        controllers.put("/api/addNewDepartment", new DepartmentPostController(dataSource));
        controllers.put("/api/departmentSelect", new DepartmentSelectGetController(dataSource));
        controllers.put("/api/filterTask", new TaskFilterPostController(dataSource));
        controllers.put("/api/showFilterTask", new TaskGetController(dataSource));
        controllers.put("/api/alterTask", new TaskAlterController(dataSource));
        controllers.put("/api/deleteTask", new TaskDeleteController(dataSource));
        controllers.put("/api/deleteMember", new MemberDeleteController(dataSource));
        controllers.put("/api/deleteDepartment", new DepartmentDeleteController(dataSource));
        controllers.put("/echo", new EchoController());
        controllers.put("/api/deleteFinishedTasks", new TaskDeleteFinishedController(dataSource));

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

    private AbstractController getController(String requestPath) {
        return controllers.get(requestPath);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    private void handleRequest(Socket socket) throws IOException, SQLException {
        HttpMessage request = new HttpMessage();
        String requestPath = getRequestPath(request, socket);

        if(requestPath.equals("/") || requestPath.equals(""))
            requestPath = "/index.html";

        AbstractController controller = getController(requestPath);
        if(controller != null)
            controller.handle(request, socket);
        else
            handleFileRequest(socket, requestPath);
    }

    private String getRequestPath(HttpMessage request, Socket socket) throws IOException {
        String requestLine = HttpMessage.readLine(socket);

        if(requestLine == null) return "";

        String[] requestLineParts = requestLine.split(" ");
        String requestTarget = requestLineParts.length > 1 ? requestLineParts[1] : "";

        if(!requestTarget.equals("/favicon.ico")) logger.info("REQUEST LINE: {}", requestLine);

        int questionPosition = requestTarget.indexOf('?');
        String requestPath = questionPosition != -1 ? requestTarget.substring(0, questionPosition) : requestTarget;
        if(questionPosition != -1){
            String queryStringLine = requestTarget.substring(questionPosition + 1);
            QueryString.putQueryParametersIntoHttpMessageHeaders(request, queryStringLine);
        }
        return requestPath;
    }


    private void handleFileRequest(Socket socket, String requestPath) throws IOException {
        HttpMessage response = new HttpMessage();
        try(InputStream inputStream = getClass().getResourceAsStream(requestPath)){
            if(inputStream == null || requestPath.endsWith(".java") || requestPath.endsWith(".class")){
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

            String fileExtension = requestPath.split("\\.(?=[^.]+$)")[1];

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
