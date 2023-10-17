import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private final List<Player> players;
    private Player currentPlayer;
    private final int port = 1234;
    private String question = "";

    public Server() {
        players = new ArrayList<>();
        List<String> topics = Arrays.asList("Historia", "Matematicas", "Fisica", "Informatica"); // Lista de temas disponibles
        String currentTopic = ""; // Tema actual inicializado como vacío
        currentPlayer = null; // Jugador actual inicializado como nulo

        try {
            //Puerto para la conexión
            int port = 1234;
            // IP local de la computadora servidor
            String localIPV4 = "10.202.95.248";
            //Creación del socket del servidor
            InetAddress inetAddress = InetAddress.getByName(localIPV4);
            serverSocket = new ServerSocket(port, 0, inetAddress);
            System.out.println("Servidor iniciado en: " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    public void acceptPlayers() {
        System.out.println("acceptPlayers");
        try {
            while (players.size() < 8) { // Acepta hasta 8 jugadores, esta comentado para que pueda probarlo con otras computadoras.
                System.out.println("try acceptPlayers");
                // Acepta conexiones de jugadores
                Socket socket = serverSocket.accept();
                System.out.println("Socket acceptado");
                Player player = new Player(socket, this);
                players.add(player);

                System.out.println("Jugador aceptado");
                new Thread(player).start();
                System.out.println("Jugador aceptado");
                startGame();
            }
        } catch (IOException e) {
            System.out.println("Error al aceptar conexiones de jugadores: " + e.getMessage());
        }
    }

    public boolean chooseCurrentPlayer() {
        Random random = new Random();
        currentPlayer = players.get(random.nextInt(players.size())); // Elige al azar al jugador actual
        currentPlayer.send("Te toca preguntar!, envia una pregunta");
        for (Player player : players) {
            if (player != currentPlayer) {
                player.send("Esperando la pregunta . . .");
            }
        }
        // Espera la pregunta del jugador actual, cuando la recibe, el servidor pide la respuesta a la pregunta, despues le envia la pregunta a los demas jugadores

        try {
            System.out.println("try 1");
            String question = currentPlayer.receive();
            System.out.println("question = currentPlayer.receive()");
            if (question != null) {
                this.question = question;
                System.out.println("this.question = question");
                //preguntar por la respuesta a la pregunta al jugador actual
                currentPlayer.send("\nIngresa la respuesta a la pregunta: ");
                System.out.println("preguntado");
                String answer = currentPlayer.receive();
                System.out.println("recivido");
                if (answer != null) {
                    System.out.println("if 2");
                    for (Player player : players) {
                        if (player != currentPlayer) {
                            player.send("\nPregunta: " + question);
                            player.send("\nRespuesta: " + answer);
                        } else {
                            player.send("\nPreguntaste: " + question + "\nEspera las respuestas . . .");
                            player.send("\nRespuesta dada: " + answer);
                        }
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al recibir la pregunta del jugador: " + e.getMessage());
        }


/*        try {
            String question = currentPlayer.receive();
            if (question != null) {
                this.question = question;
                for (Player player : players) {
                    if (player != currentPlayer) {
                        player.send("Pregunta: " + question);
                    } else {
                        player.send("Preguntaste: " + question + "\nEspera las respuestas . . .");
                    }
                }
                return true;
            }
        } catch (IOException e) {
            System.out.println("Error al recibir la pregunta del jugador: " + e.getMessage());
        }*/
        return false;
    }

    public void startGame() {
        boolean asked = chooseCurrentPlayer();
        if (asked) {
            sendQuestionToPlayers(question);
        }
    }

    public void sendQuestionToPlayers(String question) {
        for (Player player : players) {
            player.send("Pregunta: " + question);
        }
    }

    public void closeServer() {
        try {
            for (Player player : players) {
                player.close(); // Implementa un método de cierre en la clase Player para cerrar los sockets
            }
            serverSocket.close(); // Cierra el servidor
        } catch (IOException e) {
            System.out.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }

    public void processAnswers(String answer) {
        // Procesa las respuestas de los jugadores y determina el ganador
        // Implementa la lógica de comparación de respuestas con la distancia de Levenshtein aquí
    }
}