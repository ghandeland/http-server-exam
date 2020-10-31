package no.kristiania.http.controller;

import no.kristiania.db.*;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class TaskGetController implements HttpController {
    private final TaskDao taskDao;
    private final MemberDao memberDao;
    private final TaskMemberDao taskMemberDao;

    public TaskGetController(TaskDao taskDao, MemberDao memberDao, TaskMemberDao taskMemberDao) {
        this.taskDao = taskDao;
        this.memberDao = memberDao;
        this.taskMemberDao = taskMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        body.append("<ul>");

        for(Task task : taskDao.list()){

            body.append("<li id =\"task-li-").append(task.getId()).append("\"><strong>Task: </strong> ")
                    .append(task.getName()).append(" <strong>Description: </strong>").append(task.getDescription())
                    .append("  <strong>Status: </strong>").append(task.getStatus().toString())
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

        body.append("/ul>");

        HttpMessage response = new HttpMessage();
        response.setBody(body.toString());
        response.setCodeAndStartLine("200");
        response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Connection", "close");
        response.write(socket);
    }
}
