import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private ServerSocket serverSocket;
    private Map<Integer, Socket> clients;
    private int clientIdCounter;

    public ChatServer() {
        clients = new HashMap<>();
        clientIdCounter = 1;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port,50, InetAddress.getByName("130.193.229.200"));
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from " + clientSocket.getRemoteSocketAddress());
                int clientId = clientIdCounter++;
                clients.put(clientId, clientSocket);

                Thread clientThread = new Thread(new ClientHandler(clientSocket, clientId));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message, int senderId) {
        for (Map.Entry<Integer, Socket> entry : clients.entrySet()) {
            int clientId = entry.getKey();
            Socket socket = entry.getValue();

            if (clientId != senderId) {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(("Client " + senderId + ": " + message).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private int clientId;

        public ClientHandler(Socket clientSocket, int clientId) {
            this.clientSocket = clientSocket;
            this.clientId = clientId;
        }


        public void run() {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    String message = new String(buffer, 0, length);
                    broadcast(message, clientId);
                }

                System.out.println("Client " + clientId + " disconnected");
                clients.remove(clientId);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start(12345);
    }
}
