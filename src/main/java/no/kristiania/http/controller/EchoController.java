package no.kristiania.http.controller;

import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;

public class EchoController extends AbstractController {

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException {
        HttpMessage response = new HttpMessage();

        if(request.getHeader("body") != null) response.setBody(request.getHeader("body"));
        else response.setBody("Hello World");
        if(request.getHeader("Location") != null)
            response.setHeader("Location", request.getHeader("Location"));
        if(request.getHeader("status") != null)
            response.setCodeAndStartLine(request.getHeader("status"));
        else response.setCodeAndStartLine("200");

        response.setHeader("Connection", "close");
        response.setHeader("Content-type", "text/plain");
        if(response.getBody() != null)
            response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.write(socket);
    }
}
