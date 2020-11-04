package no.kristiania.http.controller;

import no.kristiania.db.Department;
import no.kristiania.db.DepartmentDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class DepartmentPostController implements HttpController {
    private final DepartmentDao departmentDao;

    public DepartmentPostController(DataSource dataSource) {
        this.departmentDao = new DepartmentDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        request.readAndSetHeaders(socket);

        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);
        request.setBody(body);

        Map <String, String> memberQueryMap = QueryString.queryStringToHashMap(body);
        String departmentName = memberQueryMap.get("name");

        Department department = new Department(departmentName);

        departmentDao.insert(department);

        postResponse(socket);
    }


}
