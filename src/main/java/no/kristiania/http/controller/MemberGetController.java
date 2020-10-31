package no.kristiania.http.controller;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class MemberGetController implements HttpController {
    private MemberDao memberDao;

    public MemberGetController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }


    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        body.append("<ul>");

        for(Member member : memberDao.list()){
            body.append("<li><strong>Name:</strong> ").append(member.getFirstName()).append(" ").append(member.getLastName()).append(" - <strong>Email:</strong> ").append(member.getEmail()).append("</li>");
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
