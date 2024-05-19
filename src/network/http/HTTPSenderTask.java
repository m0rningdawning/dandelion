package network.http;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HTTPSenderTask implements Runnable {
    private final int threadNum;
    private final String targetAddr;
    private final int timeout;
    private final CountDownLatch latch;
    private int count;

    private static final Logger logger = Logger.getLogger(HTTPSenderTask.class.getName());

    HTTPSenderTask(int threadNum, int targetPort, int timeout, CountDownLatch latch) {
        this.threadNum = threadNum;
        this.timeout = timeout;
        this.latch = latch;
        this.count = 0;
        this.targetAddr = "http://localhost:" + targetPort;
    }

    @Override
    public void run() {
        long startingTime = System.nanoTime();
        long lastPacketTime = startingTime;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            while (!Thread.currentThread().isInterrupted()) {
                long currentTime = System.nanoTime();
                long elapsedTime = currentTime - lastPacketTime;

                if (elapsedTime >= 0) {
                    send(httpClient);
                    lastPacketTime = currentTime;
                }

                if (System.nanoTime() - startingTime > timeout * 1_000_000_000L) {
                    System.out.println("Thread " + Thread.currentThread().getName() + " timed out.");
                    latch.countDown();
                    break;
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating HttpClient", e);
        }
    }

    private void send(CloseableHttpClient httpClient) {
        try {
            HttpGet request = new HttpGet(targetAddr);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                count++;
                System.out.println("Thread " + threadNum + " Request " + count + ". Response code: " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            System.out.println("Within \"send\" method, HTTPSenderTask.java: " + e.getMessage());
            logger.log(Level.SEVERE, "Within \"send\" method, HTTPSenderTask.java: ", e);
            System.out.println("Implement a proper error handling procedure. E.g (If error occurred -> ask user if he wants to retry connection.)");
        }
    }
}
