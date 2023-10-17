import java.io.*;
import java.net.*;

class Player implements Runnable {
    private Server server;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Player(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        System.out.println("Player");
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Player try end.");
        } catch (IOException e) {
            System.out.println("Error al configurar la comunicación con el jugador: " + e.getMessage());
        }
    }

    public void send(String message) {
        out.println(message);
    }

    public void close() {
        try {
            if (in != null) {
                in.close(); // Cierra el flujo de entrada
            }
            if (out != null) {
                out.close(); // Cierra el flujo de salida
            }
            if (socket != null) {
                socket.close(); // Cierra el socket
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar el socket del jugador: " + e.getMessage());
        }
    }


    @Override
    public void run() {
        try {
            String input;
            while ((input = in.readLine()) != null) {
                server.processAnswers(input);
            }
        } catch (IOException e) {
            System.out.println("Error de comunicación con el jugador: " + e.getMessage());
        }
    }
}