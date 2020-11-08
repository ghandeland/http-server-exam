package no.kristiania.http.controller;

import no.kristiania.db.Department;
import no.kristiania.db.DepartmentDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class DepartmentSelectGetController extends AbstractController {
    private final DepartmentDao departmentDao;

    public DepartmentSelectGetController(DataSource dataSource) {
        this.departmentDao = new DepartmentDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();
        for(Department department : departmentDao.list()){
            body.append("<option value=\"").append(department.getId()).append("\">")
                    .append(department.getName())
                    .append("</option>");
        }
        sendGetResponse(socket, body);
    }
}
