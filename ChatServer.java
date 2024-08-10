import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class ChatServer {

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private MessageBroadcaster broadcaster;

    public ChatServer(int port) throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new java.net.InetSocketAddress(port));
        serverChannel.configureBlocking(false);

        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        broadcaster = new MessageBroadcaster();
        System.out.println("Servidor iniciado en el puerto " + port);
    }

    public void start() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (key.isAcceptable()) {
                    acceptConnection();
                } else if (key.isReadable()) {
                    ClientHandler clientHandler = (ClientHandler) key.attachment();
                    clientHandler.readMessage();
                }
            }
        }
    }

    private void acceptConnection() throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        SelectionKey key = clientChannel.register(selector, SelectionKey.OP_READ);
        ClientHandler clientHandler = new ClientHandler(clientChannel, broadcaster);
        key.attach(clientHandler);
        broadcaster.registerClient(clientChannel, clientHandler.getClientName());
        System.out.println("Nuevo cliente conectado: " + clientHandler.getClientName());
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer(12345);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
