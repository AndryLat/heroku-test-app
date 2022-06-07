package ua.com.javarush.andrylat.tcp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Server {
    private static final int DEFAULT_PORT = 80;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(DEFAULT_PORT);
            System.out.println("Server Started");
            System.out.println("Server port: " + server.getLocalPort());

            while (true) {
                Socket socket = server.accept(); // на каждого нового клиента создаем отдельный поток для обработки

                SocketHandler socketHandler = new SocketHandler(socket);
                new Thread(socketHandler).start();
                System.out.println("New socket is under processing: " + socket);
            }
        } catch (IOException e) {
            System.err.println("Server can't start. Reason: " + e.getMessage());
        }

    }
}

class SocketHandler implements Runnable {
    private static final String FINISH_CONNECTION_MARKER = "exit";

    private Socket socket;
    private PrintWriter out;
    private Scanner in;

    public SocketHandler(Socket socket) throws IOException {
        this.socket = socket;

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                String data = in.nextLine();
                if (FINISH_CONNECTION_MARKER.equals(data)) // пока клиент не пришлет exit
                    break;

                System.out.println("Echoing: " + data.toLowerCase());
                out.println(data.toLowerCase());
            }
            System.out.println("closing socket: " + socket);
        } catch (NoSuchElementException ex) {
            System.out.println("Connection is finished!");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Socket can't be closed!");
            }
        }
    }
}
