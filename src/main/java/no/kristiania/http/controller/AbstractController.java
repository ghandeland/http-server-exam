package no.kristiania.http.controller;

import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public abstract class AbstractController {
    protected void getResponse(Socket socket, StringBuilder body) throws IOException {
        HttpMessage response = new HttpMessage();
        response.setBody(body.toString());
        response.setCodeAndStartLine("200");
        response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Connection", "close");
        response.write(socket);
    }

    protected void postResponse(Socket socket) throws IOException {
        HttpMessage response = new HttpMessage();
        response.setCodeAndStartLine("204");
        response.setHeader("Connection", "close");
        response.setHeader("Content-length", "0");
        response.write(socket);
    }

    protected Map <String, String> handlePost(HttpMessage request, Socket socket) throws IOException {
        request.readAndSetHeaders(socket);
        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);
        request.setBody(body);
        return QueryString.queryStringToHashMap(body);
    }

    public abstract void handle(HttpMessage request, Socket socket) throws IOException, SQLException;
}