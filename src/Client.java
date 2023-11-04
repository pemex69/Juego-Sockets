import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintWriter out;
    private Player currentPlayer;


    public Client() {
        super("TriviaConflux - Desafío de Saberes Multidisciplinarios");
        chatArea = new JTextArea(15, 30);
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        messageField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel panel = new JPanel();
        panel.add(messageField);
        panel.add(sendButton);

        add(chatScroll, BorderLayout.NORTH);
        add(panel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setVisible(true);

        String serverName = "192.168.68.109"; // Dirección IP del servidor
        int port = 1234; // Puerto del servidor

        try {
            System.out.println("Conectándose a " + serverName + " en el puerto " + port);
            Socket clientSocket = new Socket(serverName, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("Conectado.");
            while (true) {
                String line = in.readLine();
                if (line != null) {
                    chatArea.append(line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        out.println(message);
        messageField.setText("");
    }

    //Metodo booleano, si el jugador actual es el mismo que el jugador que envia la pregunta, envia la pregunta, cuando se envie la pregunta, se regresa true, si no, regresa false
    public boolean sendQuestion(Player currentPlayer, String question) {
        if (this.currentPlayer == currentPlayer) {
            currentPlayer.send(question);
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
