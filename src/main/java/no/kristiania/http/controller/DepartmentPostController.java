package no.kristiania.http.controller;

import no.kristiania.db.Department;
import no.kristiania.db.DepartmentDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class DepartmentPostController extends AbstractController {
    private final DepartmentDao departmentDao;

    public DepartmentPostController(DataSource dataSource) {
        this.departmentDao = new DepartmentDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        Map <String, String> bodyMap = handlePostRequest(request, socket);
        String departmentName = bodyMap.get("name");
        Department department = new Department(departmentName);
        departmentDao.insert(department);

        sendPostResponse(socket, request.getHeader("Referer"));
    }


}
