import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;

    public ChatClient() {
    }

    public void connect(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server.");

            Thread receiveThread = new Thread(new ReceiveMessages());
            receiveThread.start();

            Scanner scanner = new Scanner(System.in);
            OutputStream outputStream = socket.getOutputStream();
            while (true) {
                String message = scanner.nextLine();
                outputStream.write(message.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ReceiveMessages implements Runnable {

        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    String message = new String(buffer, 0, length);
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.connect("130.193.229.200", 12345);
    }
}