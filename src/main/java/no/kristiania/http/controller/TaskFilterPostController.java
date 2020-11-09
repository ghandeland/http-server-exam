package no.kristiania.http.controller;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import no.kristiania.db.TaskStatus;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TaskFilterPostController extends AbstractController {
    private static List <Task> filterList;

    private static TaskStatus filterStatus;
    private static String filterMemberId;
    private final TaskDao taskDao;

    public TaskFilterPostController(DataSource dataSource) {
        this.taskDao = new TaskDao(dataSource);
    }

    public static String getFilterMemberId() {
        return filterMemberId;
    }

    public static void setFilterMemberId(String filterMemberId) {
        TaskFilterPostController.filterMemberId = filterMemberId;
    }

    public static List <Task> getFilterList() {
        return filterList;
    }

    public static void setFilterList(List <Task> filterList) {
        TaskFilterPostController.filterList = filterList;
    }

    public static TaskStatus getFilterStatus() {
        return filterStatus;
    }

    public static void setFilterStatus(TaskStatus filterStatus) {
        TaskFilterPostController.filterStatus = filterStatus;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        Map <String, String> taskQueryMap = handlePostRequest(request, socket);

        String taskStatus = taskQueryMap.get("taskStatus");
        String memberId = taskQueryMap.get("taskMember");

        filterStatus = taskStatus.equals("*") || taskStatus == null ? null : TaskStatus.valueOf(taskStatus);
        filterMemberId = memberId.equals("*") ? null : memberId;

        if(taskStatus.equals("*") && memberId.equals("*")){
            filterList = null;
        }else{
            filterList = taskDao.filterTaskAndMember(taskStatus, memberId);
        }

        sendPostResponse(socket, request.getHeader("Referer"));
    }
}
