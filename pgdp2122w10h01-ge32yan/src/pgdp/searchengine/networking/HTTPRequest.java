package pgdp.searchengine.networking;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.stream.Collectors;

public class HTTPRequest {

    private final String host;
    private final String path;

    public HTTPRequest(String host, String path){
        this.host = host;
        this.path = path;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public HTTPResponse send(int port) {

        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) factory.createSocket(getHost(),port);

            PrintWriter request = new PrintWriter(socket.getOutputStream(), true);
            request.println("GET /" + getPath() + " HTTP/1.1" + "\r\n" + "Host: " + getHost() + "\r\n" + "\r\n");

            BufferedReader response = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String answer = response.lines().collect(Collectors.joining("\n"));

            request.close();
            response.close();
            socket.close();

            return new HTTPResponse(answer);
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
