import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolServer {
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 70; // Adjust the pool size as needed

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Listening for connections on port " + PORT + "...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            Runnable task = new ClientHandler(clientSocket);
            executorService.execute(task);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                handleRequest(clientSocket);
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

        private void handleRequest(Socket clientSocket) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

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
                    "\r\n" +
                    "<html><body><h1>Hello, World!</h1></body></html>";

            out.println(response);

            // Close the streams
            out.close();
            in.close();
        }
    }
}
