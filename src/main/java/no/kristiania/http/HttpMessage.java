package no.kristiania.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {


    private final Map <String, String> headers = new HashMap <>();
    private String startLine;
    private String code;
    private String body;

    public static String readLine(Socket socket) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;

        while((c = socket.getInputStream().read()) != -1){
            if(c == '\r'){
                socket.getInputStream().read();
                break;
            }
            sb.append((char) c);
        }
        return URLDecoder.decode((sb.toString()), StandardCharsets.UTF_8);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeAndStartLine(String code) {
        this.code = code;
        String startLine = "";
        switch(code){
            case "200":
                startLine = "HTTP/1.1 200 OK";
                break;
            case "201":
                startLine = "HTTP/1.1 201 Created";
                break;
            case "404":
                startLine = "HTTP/1.1 404 Not Found";
                break;
            case "204":
                startLine = "HTTP/1.1 204 No Content";
                break;
            case "302":
                startLine = "HTTP/1.1 302 Found";
                break;
            case "401":
                startLine = "HTTP/1.1 401 Unauthorized";
                break;
            case "205":
                startLine = "HTTP/1.1 205 Reset Content";
        }

        setStartLine(startLine);
    }

    public void setHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getStartLine() {
        return startLine;
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public void write(Socket socket) throws IOException {
        writeLine(socket, startLine);
        for(Map.Entry <String, String> header : headers.entrySet()){
            writeLine(socket, header.getKey() + ": " + header.getValue());
        }
        writeLine(socket, "");
        if(body != null){
            socket.getOutputStream().write(getBody().getBytes());
        }
    }

    public void write(Socket socket, ByteArrayOutputStream buffer) throws IOException {
        writeLine(socket, startLine);

        for(Map.Entry <String, String> header : headers.entrySet()){
            writeLine(socket, header.getKey() + ": " + header.getValue());
        }
        writeLine(socket, "");

        socket.getOutputStream().write(buffer.toByteArray());
    }

    public void writeLine(Socket socket, String line) throws IOException {
        socket.getOutputStream().write((line + "\r\n").getBytes());
    }

    public void readAndSetHeaders(Socket socket) throws IOException {
        String headerLine;
        while(!(headerLine = HttpMessage.readLine(socket)).isEmpty()){
            int colonPos = headerLine.indexOf(':');
            String headerName = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos + 1).trim();
            setHeader(headerName, headerValue);
        }
    }

    public String readBody(Socket socket, int contentLength) throws IOException {
        StringBuilder body = new StringBuilder();
        for(int i = 0 ; i < contentLength ; i++){
            body.append((char) socket.getInputStream().read());
        }

        return URLDecoder.decode((body.toString()), StandardCharsets.UTF_8);
    }
}
