package no.kristiania.http.controller;

import no.kristiania.db.MemberDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class DeleteMemberController extends AbstractController {
    private final MemberDao memberDao;

    public DeleteMemberController(DataSource dataSource) {
        this.memberDao = new MemberDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        Map <String, String> bodyMap = handlePostRequest(request, socket);
        long member = Long.parseLong(bodyMap.get("member"));
        memberDao.delete(member);
        sendPostResponse(socket);
    }
}
