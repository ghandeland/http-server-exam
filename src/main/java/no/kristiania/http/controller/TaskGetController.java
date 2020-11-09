package no.kristiania.http.controller;

import no.kristiania.db.*;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static no.kristiania.db.Task.taskStatusToString;

public class TaskGetController extends AbstractController {
    private final MemberDao memberDao;
    private final TaskMemberDao taskMemberDao;
    private final TaskDao taskDao;

    public TaskGetController(DataSource dataSource) {
        memberDao = new MemberDao(dataSource);
        taskMemberDao = new TaskMemberDao(dataSource);
        taskDao = new TaskDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();
        body.append("<ul>");

        List<Task> taskList;
        if(TaskFilterPostController.getFilterList() != null) {
            taskList = TaskFilterPostController.getFilterList();
        } else {
            taskList = taskDao.list();
        }

        for (Task task : taskList) {
            body.append("<li id =\"task-li-").append(task.getId()).append("\"><strong>Task: </strong> ")
                    .append(task.getName());
            if (task.getDescription() != null) {
                body.append(" <strong>Description: </strong> ")
                        .append(task.getDescription());
            }

            body.append(" <strong>Status: </strong>").append(taskStatusToString(task.getStatus()))
                    .append("</li>");

            LinkedHashSet<Long> memberIDsOnTask = taskMemberDao.retrieveMembersByTaskId(task.getId());

            if (memberIDsOnTask.size() > 0) {
                body.append("<ul>");

                for (Long memberId : memberIDsOnTask) {
                    Member member = memberDao.retrieve(memberId);
                    body.append("<li>").append(member.getFirstName()).append(" ").append(member.getLastName()).append("</li>");
                }
                body.append("</ul>");
            }
        }

        body.append("</ul>");
        System.out.println("handleRunning");
        sendGetResponse(socket, body);
    }
}
