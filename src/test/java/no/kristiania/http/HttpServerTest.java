package no.kristiania.http;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
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

    @Test
    void shouldReadResponseCode() throws IOException {
        HttpServer server = new HttpServer(10000);
        server.start();
        int port = server.getActualPort();
        HttpClient client = new HttpClient("localhost", 10000, "");
        HttpMessage response = client.executeRequest();
        assertEquals("200", response.getCode());
    }

    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        HttpServer server = new HttpServer(10001);
        server.start();
        HttpClient client = new HttpClient("localhost", 10001, "/doesnotexist");
        HttpMessage response = client.executeRequest();
        assertEquals("404", response.getCode());
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {

        HttpServer server = new HttpServer(10002);
        server.start();
        HttpClient client = new HttpClient("localhost", 10002, "echo/?status=404");
        HttpMessage response = client.executeRequest();
        assertEquals("404", response.getCode());
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldParseRequestParameters() throws IOException {

        HttpServer server = new HttpServer(10004);
        server.start();
        HttpClient client = new HttpClient("localhost", 10004, "echo/?status=401");
        HttpMessage response = client.executeRequest();
        assertEquals("401", response.getCode());
        client.closeSocket();
        server.stop();
    }


    @Test
    void shouldParseRequestParametersWithLocation() throws IOException {
        HttpServer server = new HttpServer(10005);
        server.start();
        HttpClient client = new HttpClient("localhost", 10005, "?status=302&Location=http://www.example.com");
        HttpMessage response = client.executeRequest();
        assertEquals("http://www.example.com", response.getHeader("Location"));
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldParseRequestParametersWithBody() throws IOException {
        HttpServer server = new HttpServer(10006);
        server.start();
        HttpClient client = new HttpClient("localhost", 10006, "?body=HelloWorld");
        HttpMessage response = client.executeRequest();
        assertEquals("HelloWorld", response.getBody());
        client.closeSocket();
        server.stop();
    }

    @Test
    void shouldReturnFileOnDisk() throws IOException {
        HttpServer server = new HttpServer(10013);
        server.start();

        File documentRoot = new File("target/test-classes");
        String fileContent = "Test " + new Date();
        Files.writeString(new File(documentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", 10013, "/test.txt");
        HttpMessage response = client.executeRequest();

        assertEquals(fileContent, response.getBody());

        client.closeSocket();
        server.stop();
    }


    @Test
    void shouldReturn404IfFileNotFound() throws IOException {
        HttpServer server = new HttpServer(10014);
        server.start();

        HttpClient client = new HttpClient("localhost", 10014, "/nonexistingFile.txt");

        HttpMessage response = client.executeRequest();

        assertEquals("404", response.getCode());

        client.closeSocket();
        server.stop();

    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10015);
        server.start();

        File documentRoot = new File("target/test-classes");


        Files.writeString(new File(documentRoot, "addProjectMember.html").toPath(), "<html>Hello world</html>");
        Files.writeString(new File(documentRoot, "test.txt").toPath(), "Hello world");

        HttpClient client1 = new HttpClient("localhost", 10015, "/test.txt");
        HttpMessage response1 = client1.executeRequest();
        client1.closeSocket();

        assertEquals("text/plain", response1.getHeader("Content-Type"));

        HttpClient client2 = new HttpClient("localhost", 10015, "/addProjectMember.html");
        HttpMessage response2 = client2.executeRequest();
        client2.closeSocket();

        assertEquals("text/html", response2.getHeader("Content-Type"));

        server.stop();
    }

    @Test
    void shouldPostHttpContent() throws IOException, SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        HttpServer server = new HttpServer(10016, dataSource);
        server.start();

        HttpClient postRequest = new HttpClient("localhost", 10016, "/submit", "firstName=Someone&lastName=Somelastname&email=someone@example.com");

        HttpMessage response = postRequest.executeRequest();
        assertEquals("204", response.getCode());

        MemberDao memberDao = new MemberDao(dataSource);

        List<String> listName = new ArrayList<>();
        for(Member member : memberDao.list()) {
            listName.add(member.getFirstName()+member.getLastName());
        }

        assertThat(listName).contains("SomeoneSomelastname");

        postRequest.closeSocket();
        server.stop();
    }
}
