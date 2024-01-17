import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ImprovedThreadPoolWebServer {
    private static final int PORT = 8080;
    private static final int MAX_THREAD_POOL_SIZE = 100; // Maximum threads in the pool
    private static final int QUEUE_CAPACITY = 50; // Capacity for the task queue

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = new ThreadPoolExecutor(
                10, // Core pool size
                MAX_THREAD_POOL_SIZE,
                5000L, TimeUnit.MILLISECONDS, // Keep alive time for idle threads
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy() // Policy for task rejection
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down web server...");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Listening for connections on port " + PORT + "...");
            while (!executorService.isShutdown()) {
                try {
                    final Socket clientSocket = serverSocket.accept();
                    executorService.execute(new ClientHandler(clientSocket));
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
        private static class ClientHandler implements Runnable {
            private final Socket clientSocket;

            public ClientHandler(Socket socket) {
                this.clientSocket = socket;
            }

            @Override
            public void run() {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    // Read the request
                    String requestLine;
                    while ((requestLine = in.readLine()) != null) {
                        if (requestLine.isEmpty()) {
                            break; // End of the request header
                        }
                    }

                    // Write the response
                    String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/html\r\n" +
                            "Connection: close\r\n" + // Indicate that we will close the connection after the response
                            "\r\n" +
                            "<html><body><h1>Hello, World!</h1></body></html>";

                    out.println(response);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }