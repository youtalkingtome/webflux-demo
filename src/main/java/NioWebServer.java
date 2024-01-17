import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NioWebServer {
    private static final int PORT = 8080;
    private static final String RESPONSE = "HTTP/1.1 200 OK\r\n" + "Content-Length: 38\r\n" + "Content-Type: text/html\r\n\r\n" + "<html><body>Hello World!</body></html>";

    public static void main(String[] args) throws Exception {
        // Open a server channel and bind to port
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.configureBlocking(false); // Configure non-blocking mode

        // Create a selector to mulåtiplex client connections
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        System.out.println("Server started on port " + PORT);

        while (true) {
            selector.select(); // Blocking until there are events
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    // Accept connection
                    SocketChannel client = serverChannel.accept();
                    if (client != null) {
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        System.out.println("Accepted connection from " + client);
                    }
                }

                if (key.isReadable()) {
                    // Read data
                    SocketChannel client = (SocketChannel) key.channel();
                    buffer.clear();
                    int bytesRead = client.read(buffer);
                    if (bytesRead == -1) {
                        // Client closed connection
                        client.close();
                    } else {
                        // Prepare the buffer for writing to the channel
                        buffer.flip();
                        // Write a fixed HTTP response
                        buffer = ByteBuffer.wrap(RESPONSE.getBytes(StandardCharsets.UTF_8));
                        client.write(buffer);
                        // Close connection after response is sent
                        client.close();
                    }
                }
                iter.remove();
            }
        }
    }
}

