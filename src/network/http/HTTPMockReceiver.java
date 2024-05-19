package network.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class HTTPMockReceiver {
    private static final int port = 9999;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> {
            String response = "This is the response";

            Headers requestHeaders = exchange.getRequestHeaders();
            for (Map.Entry<String, List<String>> header : requestHeaders.entrySet()) {
                System.out.println(header.getKey() + ": " + header.getValue());
            }

            System.out.println("Received request: " + exchange.getRequestURI());
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }
}
