package no.kristiania.http.controller;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MemberFilterPostController extends AbstractController {
    private static List <Task> filterList = null;
    private final TaskDao taskDao;

    public MemberFilterPostController(DataSource dataSource) {
        taskDao = new TaskDao(dataSource);
    }

    public static List <Task> getFilterList() {
        return filterList;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        Map <String, String> taskQueryMap = handlePostRequest(request, socket);
        String taskStatus = taskQueryMap.get("member");

        filterList = taskDao.filterMember(taskStatus);

        sendPostResponse(socket, "http://localhost:8080/filterByMember.html");
    }
}
