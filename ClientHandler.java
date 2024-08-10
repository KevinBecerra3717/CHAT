import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientHandler {

    private SocketChannel clientChannel;
    private MessageBroadcaster broadcaster;
    private ByteBuffer buffer = ByteBuffer.allocate(256);
    private String clientName;

    public ClientHandler(SocketChannel clientChannel, MessageBroadcaster broadcaster) {
        this.clientChannel = clientChannel;
        this.broadcaster = broadcaster;
        this.clientName = "Cliente_" + clientChannel.socket().getPort();
    }

    public void readMessage() throws IOException {
        buffer.clear();
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            broadcaster.deregisterClient(clientChannel, clientName);
            clientChannel.close();
            System.out.println(clientName + " se ha desconectado");
            return;
        }

        buffer.flip();
        String message = clientName + ": " + new String(buffer.array(), 0, buffer.limit());
        broadcaster.broadcastMessage(message, clientChannel);
    }

    public String getClientName() {
        return clientName;
    }
