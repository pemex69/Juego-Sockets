import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando servidor . . .");
        SwingUtilities.invokeLater(() -> {
            Server server = new Server();
            System.out.println("GUI iniciada.\nEsperando jugadores . . .\n");
            ServerGUI serverGUI = new ServerGUI(server);
            //Iniciar el servidor en un hilo diferente al de la interfaz gráfica
            new Thread(() -> {
                System.out.println("Segundo hilo . . .");
                serverGUI.appendToLog("Iniciando servidor . . .");
                server.acceptPlayers();
                System.out.println("jugadores aceptados en serverGUI");
                serverGUI.appendToLog("Jugadores aceptados.\nIniciando juego . . .");
                server.startGame();
                System.out.println("juego iniciado en serverGUI");
            }).start();
        });
    }
}
