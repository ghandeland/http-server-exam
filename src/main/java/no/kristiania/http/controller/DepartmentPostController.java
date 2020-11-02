package no.kristiania.http.controller;

import no.kristiania.db.Department;
import no.kristiania.db.DepartmentDao;
import no.kristiania.db.Member;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class DepartmentPostController implements HttpController {

    private DepartmentDao departmentDao;

    public DepartmentPostController(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        request.readAndSetHeaders(socket);

        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);
        request.setBody(body);

        Map<String, String> memberQueryMap = QueryString.queryStringToHashMap(body);
        String departmentName = memberQueryMap.get("name");

        Department department = new Department(departmentName);

        departmentDao.insert(department);

        HttpMessage response = new HttpMessage();
        response.setCodeAndStartLine("204");
        response.setHeader("Connection", "close");
        response.setHeader("Content-length", "0");
        response.write(socket);
    }


}
