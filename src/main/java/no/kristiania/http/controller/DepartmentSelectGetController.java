package no.kristiania.http.controller;

import no.kristiania.db.Department;
import no.kristiania.db.DepartmentDao;
import no.kristiania.db.Member;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class DepartmentSelectGetController implements HttpController {

    private DepartmentDao departmentDao;

    public DepartmentSelectGetController(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        for(Department department : departmentDao.list()){
            body.append("<option value=\"").append(department.getId()).append("\">")
                    .append(department.getName())
                    .append("</option>");
        }

        HttpMessage response = new HttpMessage();
        response.setBody(body.toString());
        response.setCodeAndStartLine("200");
        response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Connection", "close");
        response.write(socket);
    }
}
