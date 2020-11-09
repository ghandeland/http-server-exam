package no.kristiania.http.controller;

import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ResetTaskFilterController extends AbstractController {
    public ResetTaskFilterController() {

    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        TaskFilterPostController.setFilterMemberId(null);
        TaskFilterPostController.setFilterStatus(null);

        sendPostResponse(socket, request.getHeader("Referer"));
    }
}
