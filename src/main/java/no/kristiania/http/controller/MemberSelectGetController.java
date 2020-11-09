package no.kristiania.http.controller;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class MemberSelectGetController extends AbstractController {
    private final MemberDao memberDao;

    public MemberSelectGetController(DataSource dataSource) {
        this.memberDao = new MemberDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();
        String filterId = TaskFilterPostController.getFilterMemberId();

        for(Member member : memberDao.list()){
            body.append("<option ");

            if(filterId != null){
                if(member.getId() == Integer.parseInt(filterId)){
                    body.append("selected ");
                }
            }

            body.append("value=\"").append(member.getId()).append("\">")
                    .append(member.getFirstName())
                    .append(" ")
                    .append(member.getLastName())
                    .append("</option>");
        }
        sendGetResponse(socket, body);
    }

}
