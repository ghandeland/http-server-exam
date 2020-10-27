package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {
    private Socket socket;
    private String requestTarget;
    private String host;
    private String requestBody;

    public HttpClient(final String hostName, int port, final String requestTarget) throws IOException {

        this.socket = new Socket(hostName, port);
        this.host = hostName;
        this.requestTarget = requestTarget;

    }

    public HttpClient(String hostName, int port, String requestTarget, String requestBody) throws IOException {
        this.socket = new Socket(hostName, port);
        this.host = hostName;
        this.requestTarget = requestTarget;
        this.requestBody = requestBody;
    }

    public void closeSocket() throws IOException {
        socket.close();
    }


    public HttpMessage executeRequest() throws IOException {
        HttpMessage request = new HttpMessage();

        if(requestBody != null){
            request.setStartLine("POST " + requestTarget + " HTTP/1.1");
            request.setBody(requestBody);
            request.setHeader("Host", host);
            request.setHeader("Content-Length", "" + requestBody.length());
            request.write(socket);
            return handleResponse();
        }

        request.setStartLine("GET " + requestTarget + " HTTP/1.1");
        request.setHeader("Host", host);
        request.write(socket);
        return handleResponse();
    }

    public HttpMessage executePostRequest() throws IOException {

        HttpMessage request = new HttpMessage();

        request.setHeader("Host", host);
        request.setStartLine("POST " + requestTarget + " HTTP/1.1");
        request.setHeader("Host", host);

        request.write(socket);

        return handleResponse();
    }

    public HttpMessage handleResponse() throws IOException {
        HttpMessage response = new HttpMessage();

        String responseLine = HttpMessage.readLine(socket);
        response.setStartLine(responseLine);

        String[] responseLineParts = response.getStartLine().split(" ");

        response.setCode(responseLineParts[1]);

        response.readAndSetHeaders(socket);

        if(response.getHeader("Content-Length") != null){
            int contentLength = Integer.parseInt(response.getHeader("Content-Length"));
            response.setBody(response.readBody(socket, contentLength));
        }

        return response;
    }


}
