package no.kristiania.http;

import no.kristiania.db.*;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    private final JdbcDataSource dataSource = new JdbcDataSource();
    private HttpServer server;

    @BeforeEach
    void SetUp() throws IOException {
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        server = new HttpServer(0, dataSource);
    }

    @Test
    void shouldReadResponseHeader() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=Hello");
        assertEquals("5", client.executeRequest().getHeader("Content-Length"));
    }

    @Test
    void shouldReadResponseCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/");
        HttpMessage response = client.executeRequest();
        assertEquals("200", response.getCode());
    }

    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/doesnotexist");
        HttpMessage response = client.executeRequest();
        assertEquals("404", response.getCode());
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "echo/?status=404");
        HttpMessage response = client.executeRequest();
        assertEquals("404", response.getCode());
    }

    @Test
    void shouldParseRequestParameters() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "echo/?status=401");
        HttpMessage response = client.executeRequest();
        assertEquals("401", response.getCode());
    }


    @Test
    void shouldParseRequestParametersWithLocation() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "?status=302&Location=http://www.example.com");
        HttpMessage response = client.executeRequest();
        assertEquals("http://www.example.com", response.getHeader("Location"));
    }

    @Test
    void shouldParseRequestParametersWithBody() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "?body=HelloWorld");
        HttpMessage response = client.executeRequest();
        assertEquals("HelloWorld", response.getBody());
    }

    @Test
    void shouldReturnFileOnDisk() throws IOException {
        File documentRoot = new File("target/test-classes");
        String fileContent = "Test " + new Date();
        Files.writeString(new File(documentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/test.txt");
        HttpMessage response = client.executeRequest();

        assertEquals(fileContent, response.getBody());

    }


    @Test
    void shouldReturn404IfFileNotFound() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/nonexistingFile.txt");

        HttpMessage response = client.executeRequest();

        assertEquals("404", response.getCode());


    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        File documentRoot = new File("target/test-classes");

        Files.writeString(new File(documentRoot, "addProjectMember.html").toPath(), "<html>Hello world</html>");
        Files.writeString(new File(documentRoot, "test.txt").toPath(), "Hello world");

        HttpClient client1 = new HttpClient("localhost", server.getPort(), "/test.txt");
        HttpMessage response1 = client1.executeRequest();

        assertEquals("text/plain", response1.getHeader("Content-Type"));

        HttpClient client2 = new HttpClient("localhost", server.getPort(), "/addProjectMember.html");
        HttpMessage response2 = client2.executeRequest();

        assertEquals("text/html", response2.getHeader("Content-Type"));

    }

    @Test
    void shouldPostMember() throws IOException, SQLException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/addNewMember", "firstName=Someone&lastName=Somelastname&email=someone@example.com&department=-1");

        HttpMessage response = client.executeRequest();
        assertEquals("204", response.getCode());

        MemberDao memberDao = new MemberDao(dataSource);

        List <String> listName = new ArrayList <>();
        for(Member member : memberDao.list()){
            listName.add(member.getFirstName() + member.getLastName());
        }
        assertThat(listName).contains("SomeoneSomelastname");
    }

    @Test
    void shouldPostTask() throws IOException, SQLException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/addNewTask", "name=Someone&description=Some&status=OPEN");

        HttpMessage response = client.executeRequest();
        assertEquals("204", response.getCode());

        TaskDao taskDao = new TaskDao(dataSource);

        List <String> tasklist = new ArrayList <>();
        for(Task task : taskDao.list()){
            tasklist.add(task.getName() + " " + task.getDescription() + " " + task.getStatus());
        }
        assertThat(tasklist).contains("Someone Some OPEN");
    }

    @Test
    void MemberTaskPostController() throws IOException, SQLException {
        HttpClient clientMember = new HttpClient("localhost", server.getPort(), "/api/addNewMember", "firstName=Someone&lastName=Somelastname&email=someone@example.com&department=-1");
        clientMember.executeRequest();
        HttpClient clientTask = new HttpClient("localhost", server.getPort(), "/api/addNewTask", "name=Someone&description=Some&status=OPEN");
        clientTask.executeRequest();

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/addMemberToTask", "member=1&task=1");
        HttpMessage response = client.executeRequest();
        TaskMemberDao taskMemberDao = new TaskMemberDao(dataSource);

        assertEquals("204", response.getCode());
        assertThat(taskMemberDao.retrieveMembersByTaskId(1L)).contains(1L);
    }

    @Test
    void DepartmentPost() throws IOException, SQLException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/addNewDepartment", "name=lorumipsum");
        HttpMessage response = client.executeRequest();
        DepartmentDao departmentDao = new DepartmentDao(dataSource);

        assertEquals("204", response.getCode());

        List <String> arrayList = new ArrayList <>();
        for(Department department : departmentDao.list()){
            arrayList.add(department.getName());
        }
        assertThat(arrayList).contains("lorumipsum");
    }

    @Test
    void MemberTaskPost() throws IOException, SQLException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/addNewDepartment", "name=lorumipsum");
        HttpMessage response = client.executeRequest();
        DepartmentDao departmentDao = new DepartmentDao(dataSource);

        assertEquals("204", response.getCode());

        List <String> arrayList = new ArrayList <>();
        for(Department department : departmentDao.list()){
            arrayList.add(department.getName());
        }
        assertThat(arrayList).contains("lorumipsum");
    }


}
