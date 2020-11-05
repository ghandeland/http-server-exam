package no.kristiania.http.controller;

import no.kristiania.db.*;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedHashSet;

import static no.kristiania.db.Task.taskStatusToString;

public class TaskGetController extends AbstractController {
    private final TaskDao taskDao;
    private final MemberDao memberDao;
    private final TaskMemberDao taskMemberDao;

    public TaskGetController(DataSource dataSource) {
        this.taskDao = new TaskDao(dataSource);
        this.memberDao = new MemberDao(dataSource);
        this.taskMemberDao = new TaskMemberDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        body.append("<ul>");

        for(Task task : taskDao.list()){

            body.append("<li id =\"task-li-").append(task.getId()).append("\"><strong>Task: </strong> ")
                    .append(task.getName()).append(" <strong>Description: </strong>").append(task.getDescription())
                    .append("  <strong>Status: </strong>")
                    .append(taskStatusToString(task.getStatus()))
                    .append("</li>");

            LinkedHashSet <Long> memberIDsOnTask = taskMemberDao.retrieveMembersByTaskId(task.getId());

            if(memberIDsOnTask.size() > 0){
                body.append("<ul>");

                for(Long memberId : memberIDsOnTask){
                    Member member = memberDao.retrieve(memberId);
                    body.append("<li>").append(member.getFirstName()).append(" ").append(member.getLastName()).append("</li>");
                }
                body.append("</ul>");
            }
        }
        body.append("</ul>");
        sendGetResponse(socket, body);
    }
}
