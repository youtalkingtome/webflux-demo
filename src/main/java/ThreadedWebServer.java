import java.io.*;
import java.net.*;

public class ThreadedWebServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Listening for connections on port 8080...");

        while (true) {
            final Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + clientSocket);

            // Handle the request in a new thread
            new Thread(new Runnable() {
                public void run() {
                    try {
                        handleRequest(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
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
