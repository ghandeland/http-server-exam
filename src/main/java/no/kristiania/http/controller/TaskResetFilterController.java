package no.kristiania.http.controller;

import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class TaskResetFilterController extends AbstractController {
    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        request.readAndSetHeaders(socket);
        TaskFilterPostController.setFilterMemberId(null);
        TaskFilterPostController.setFilterStatus(null);
        TaskFilterPostController.setFilterList(null);
        sendPostResponse(socket, request.getHeader("Referer"));
    }
}
