package no.kristiania.http.controller;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class MemberSelectGetController implements HttpController {
    private MemberDao memberDao;

    public MemberSelectGetController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        for(Member member : memberDao.list()){
            body.append("<option value=\"" + member.getId() + "\">")
                    .append(member.getFirstName())
                    .append(" ")
                    .append(member.getLastName())
                    .append("</option>");
        }

        HttpMessage response = new HttpMessage();
        response.setBody(body.toString());
        response.setCodeAndStartLine("200");
        response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Connection", "close");
        response.write(socket);
    }
}
