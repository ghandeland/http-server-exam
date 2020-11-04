package no.kristiania.http.controller;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class MemberSelectGetController implements HttpController {
    private final MemberDao memberDao;

    public MemberSelectGetController(DataSource dataSource) {
        this.memberDao = new MemberDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        for(Member member : memberDao.list()){
            body.append("<option value=\"").append(member.getId()).append("\">")
                    .append(member.getFirstName())
                    .append(" ")
                    .append(member.getLastName())
                    .append("</option>");
        }
        getResponse(socket, body);
    }
}
