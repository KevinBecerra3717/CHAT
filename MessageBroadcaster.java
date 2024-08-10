import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class MessageBroadcaster {

    private HashMap<String, SocketChannel> clients = new HashMap<>();
    private ByteBuffer buffer = ByteBuffer.allocate(256);

    public void registerClient(SocketChannel clientChannel, String clientName) {
        clients.put(clientName, clientChannel);
    }

    public void deregisterClient(SocketChannel clientChannel, String clientName) {
        clients.remove(clientName);
    }

    public void broadcastMessage(String message, SocketChannel senderChannel) throws IOException {
        for (SocketChannel client : clients.values()) {
            if (client != senderChannel) {
                buffer.clear();
                buffer.put(message.getBytes());
                buffer.flip();
                client.write(buffer);
            }
        }
        System.out.println("Mensaje enviado: " + message);
    }
}
