package no.kristiania.http.controller;

import no.kristiania.db.TaskMemberDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class MemberTaskPostController extends AbstractController {
    private final TaskMemberDao taskMemberDao;

    public MemberTaskPostController(DataSource dataSource) {
        this.taskMemberDao = new TaskMemberDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        Map <String, String> taskMemberMap = handlePostRequest(request, socket);

        long memberId = Long.parseLong(taskMemberMap.get("member"));
        long taskId = Long.parseLong(taskMemberMap.get("task"));

        taskMemberDao.insert(taskId, memberId);

        sendPostResponse(socket);
    }
}
