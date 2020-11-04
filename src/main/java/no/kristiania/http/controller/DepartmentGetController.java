package no.kristiania.http.controller;

import no.kristiania.db.Department;
import no.kristiania.db.DepartmentDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class DepartmentGetController implements HttpController {
    private final DepartmentDao departmentDao;

    public DepartmentGetController(DataSource dataSource) {
        this.departmentDao = new DepartmentDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        body.append("<strong>Departments:</strong>")
                .append("<ul>");

        for(Department department : departmentDao.list()){

            body.append("<li>")
                    .append(department.getName())
                    .append("</li>");
        }
        body.append("</ul>");

        getResponse(socket, body);
    }

}
