package no.kristiania.http.controller;

import no.kristiania.db.DepartmentDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class DepartmentDeleteController extends AbstractController {
    private final DepartmentDao departmentDao;

    public DepartmentDeleteController(DataSource dataSource) {
        this.departmentDao = new DepartmentDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        Map <String, String> query = handlePostRequest(request, socket);
        departmentDao.delete(Long.parseLong(query.get("department")));
        sendPostResponse(socket,"http://localhost:8080/deleteMember.html");
    }
}
