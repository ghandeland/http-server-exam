package no.kristiania.http.controller;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class MemberPostController extends AbstractController {

    private final MemberDao memberDao;

    public MemberPostController(DataSource dataSource) {
        this.memberDao = new MemberDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        Map <String, String> memberQueryMap = handlePostRequest(request, socket);
        String memberFirstName = memberQueryMap.get("firstName");
        String memberLastName = memberQueryMap.get("lastName");
        String memberEmail = memberQueryMap.get("email");

        Long departmentId = null;
        long departmentValue = Long.parseLong(memberQueryMap.get("department"));
        if(departmentValue != -1L) departmentId = departmentValue;

        Member member = new Member(memberFirstName, memberLastName, memberEmail, departmentId);

        memberDao.insert(member);

        sendPostResponse(socket);
    }
}
