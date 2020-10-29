package no.kristiania.http.controller;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class MemberPostController implements HttpController {

    private MemberDao memberDao;

    public MemberPostController(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        request.readAndSetHeaders(socket);

        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);
        request.setBody(body);

        Map<String, String> memberQueryMap = QueryString.queryStringToHashMap(body);
        String memberFirstName = memberQueryMap.get("firstName");
        String memberLastName = memberQueryMap.get("lastName");
        String memberEmail = memberQueryMap.get("email");

        Member member = new Member(memberFirstName, memberLastName, memberEmail);

        System.out.println("Happenedincontroller");

        memberDao.insert(member);

        HttpMessage response = new HttpMessage();
        response.setCodeAndStartLine("204");
        response.setHeader("Connection", "close");
        response.setHeader("Content-length", "0");
        response.write(socket);
    }
}
