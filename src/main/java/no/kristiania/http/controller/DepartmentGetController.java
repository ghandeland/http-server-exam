package no.kristiania.http.controller;

import no.kristiania.db.Department;
import no.kristiania.db.DepartmentDao;
import no.kristiania.db.Member;
import no.kristiania.db.Task;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class DepartmentGetController implements HttpController {

    private final DepartmentDao departmentDao;

    public DepartmentGetController(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
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

        HttpMessage response = new HttpMessage();
        response.setBody(body.toString());
        response.setCodeAndStartLine("200");
        response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Connection", "close");
        response.write(socket);
    }
}
