import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String serverName = "10.202.95.248"; // Dirección IP del servidor (por ahora mi computadora xD)
        int port = 1234; // Puerto en el que el servidor está escuchando

        try {
            System.out.println("Conectándose a " + serverName + " en el puerto " + port);
            Socket clientSocket = new Socket(serverName, port);

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Envía un mensaje al servidor
            out.println("¡Hola, servidor!");

            // Lee la respuesta del servidor
            String response = in.readLine();
            System.out.println("Respuesta del servidor: " + response);

            // Cierra la conexión
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
