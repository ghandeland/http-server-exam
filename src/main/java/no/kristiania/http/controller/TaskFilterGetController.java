package no.kristiania.http.controller;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.db.Task;
import no.kristiania.db.TaskMemberDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class TaskFilterGetController implements HttpController {

    private final MemberDao memberDao;
    private final TaskMemberDao taskMemberDao;

    public TaskFilterGetController(MemberDao memberDao, TaskMemberDao taskMemberDao) {
        this.memberDao = memberDao;
        this.taskMemberDao = taskMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        body.append("<ul>");
        if(TaskFilterPostController.getFilterList() != null){
            for(Task task : TaskFilterPostController.getFilterList()){

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

            body.append("</ul>");
        }

        HttpMessage response = new HttpMessage();
        response.setBody(body.toString());
        response.setCodeAndStartLine("200");
        response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.setHeader("Content-Type", "text/html");
        response.setHeader("Connection", "close");
        response.write(socket);
    }

}