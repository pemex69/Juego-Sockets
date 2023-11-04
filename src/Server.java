import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private final List<Player> players;
    private Player currentPlayer;
    private final int port = 1234;
    private String question = "";
    private String answer = "";

    public Server() {
        players = new ArrayList<>();
        List<String> topics = Arrays.asList("Historia", "Matematicas", "Fisica", "Informatica"); // Lista de temas disponibles
        String currentTopic = ""; // Tema actual inicializado como vacío
        currentPlayer = null; // Jugador actual inicializado como nulo

        try {
            //Puerto para la conexión
            int port = 1234;
            // IP local de la computadora servidor
            String localIPV4 = "192.168.68.109";
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
        int rounds = 5;
        while (rounds > 0) {

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
                    answer = currentPlayer.receive();
                    System.out.println("recivido");
                    if (answer != null) {
                        System.out.println("if 2");
                        for (Player player : players) {
                            if (player != currentPlayer) {
                                player.send("\nPregunta: " + question + "\nIngresa la respuesta a la pregunta: " + answer);
                                //Aceptar la respuesta del jugador actual
                                String answerPlayer = player.receive();
                                String answerResponse = processAnswers(answerPlayer, player);
                                player.send(answerResponse);

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
            rounds--;

        }
        return false;

        // Una vez terminado el ciclo, se regresa false


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
    }

    public void startGame() {
        boolean asked = chooseCurrentPlayer();
        if (asked) {
            sendQuestionToPlayers(question);
        } else {
            endGame();
        }
    }

    public void endGame() {
        //TODO: Mostrar la puntuación de los jugadores y determina el ganador
        for (Player player : players) {
            player.send("El juego ha terminado, tu puntuación es: " + player.getScore());
        }
        closeServer();
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

    public String processAnswers(String answerPlayer, Player player) {
        if (answerPlayer != null) {
            System.out.println("if 3");
            if (answerPlayer.equals(answer)) {
                System.out.println("if 4");
                player.updateScore(100);
                return "\nRespuesta perfecta!, tu puntuación es: " + player.getScore() + "\n";
            } else {
                double puntuacion = Levenshtein(answer, answerPlayer);
                //TODO: Registra la puntuación de los jugadores
                player.updateScore(puntuacion);
                return "\nRespuesta incorrecta, tu puntuación es: " + player.getScore() + "\n";
            }
        }
        return "\nRespuesta incorrecta";
    }

    public double Levenshtein(String respuesta, String intento) {
        //Implementa distancia de Levenshtein, dependiendo de la cercanía de el String respuesta dado el String de intento, se le asigna una calificación al jugador del 1 al 100 basado en porcentaje
        int[][] matriz = new int[respuesta.length() + 1][intento.length() + 1];
        for (int i = 0; i <= respuesta.length(); i++) {
            matriz[i][0] = i;
        }
        for (int j = 0; j <= intento.length(); j++) {
            matriz[0][j] = j;
        }
        for (int i = 1; i <= respuesta.length(); i++) {
            for (int j = 1; j <= intento.length(); j++) {
                if (respuesta.charAt(i - 1) == intento.charAt(j - 1)) {
                    matriz[i][j] = matriz[i - 1][j - 1];
                } else {
                    matriz[i][j] = Math.min(matriz[i - 1][j - 1] + 1, Math.min(matriz[i][j - 1] + 1, matriz[i - 1][j] + 1));
                }
            }
        }
        //Se regresa una calificación al jugador del 1 al 100 basado en la similitud de la respuesta con el intento
        return 100 - (matriz[respuesta.length()][intento.length()] * 100 / respuesta.length());
    }
}