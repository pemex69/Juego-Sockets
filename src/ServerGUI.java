import javax.swing.*;
import java.awt.event.*;

public class ServerGUI extends JFrame {
    private final JTextArea logArea;
    private final Server server;

    public ServerGUI(Server server) {
        this.server = server;
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("SERVER | TriviaConflux");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        logArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane);
        frame.setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                closeServer();
            }
        });
    }

    public void appendToLog(String message) {
        logArea.append(message + "\n");
    }

    private void closeServer() {
        if (server != null) {
            server.closeServer(); // Llamada a un método que cierra el servidor y los sockets cuando se cierra la ventana
        }
    }
}
