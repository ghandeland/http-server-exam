package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpClientTest {
    @Test
    void shouldReturnSuccessfulStatusCode() throws IOException {
        HttpClient client = makeEchoRequest("/echo");
        HttpMessage response = client.executeRequest();

        assertEquals("200", response.getCode());
    }

    @Test
    void shouldReturnUnsuccessfulStatusCode() throws IOException {
        HttpClient client = makeEchoRequest("/echo?status=404");

        assertEquals("404", client.executeRequest().getCode());
    }

    private HttpClient makeEchoRequest(String s) throws IOException {
        return new HttpClient("urlecho.appspot.com", 80, s);
    }

    @Test
    void shouldReadResponseBody() throws IOException {
        HttpClient client = makeEchoRequest("/echo?body=Hello");
        assertEquals("Hello", client.executeRequest().getBody());
    }
}