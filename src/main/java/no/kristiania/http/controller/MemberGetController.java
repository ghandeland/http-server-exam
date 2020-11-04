package no.kristiania.http.controller;

import no.kristiania.db.DepartmentDao;
import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class MemberGetController extends AbstractController {
    private final DepartmentDao departmentDao;
    private final MemberDao memberDao;

    public MemberGetController(DataSource dataSource) {
        this.memberDao = new MemberDao(dataSource);
        this.departmentDao = new DepartmentDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        body.append("<ul>");

        for(Member member : memberDao.list()){
            Long departmentId = member.getDepartmentId();

            body.append("<li><strong>Name:</strong> ")
                    .append(member.getFirstName())
                    .append(" ")
                    .append(member.getLastName())
                    .append(" - <strong>Email:</strong> ")
                    .append(member.getEmail());

            if(departmentId != null){
                body.append(" - <strong>Department:</strong> ")
                        .append(departmentDao.retrieve(departmentId).getName());
            }
            body.append("</li>");
        }
        body.append("</ul>");

        getResponse(socket, body);
    }
}
