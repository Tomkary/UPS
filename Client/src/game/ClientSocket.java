package game;

import java.io.*;
import java.net.*;

public class ClientSocket extends Thread {
    private String serverIp;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = false;
    private LoginWindow login;
    private LobbyWindow lobby;
    private GameWindow game;

    public ClientSocket(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public synchronized void connect() throws IOException {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(serverIp, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            running = true;
            System.out.println("Connected to the server at " + serverIp + ":" + serverPort);
        }
    }

    public synchronized void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            running = false;
            if (out != null) out.close();
            if (in != null) in.close();
            socket.close();
            System.out.println("Disconnected from the server.");
        }
    }

    public synchronized void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            System.out.println("Sent: " + message);
        } else {
            System.out.println("Cannot send message. Not connected to the server.");
        }
    }
    
    public void setGUI(GUI gui) {
    	this.lobby = gui.getLobby();
    	this.login = gui.getLogin();
    	this.game = gui.getGame();
    }

    @Override
    public void run() {
        try {
            while (running) {
                if (in != null) {
                    String serverMessage = in.readLine();
                    if (serverMessage != null) {
                        System.out.println("Received: " + serverMessage);
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error during communication: " + e.getMessage());
            }
        } finally {
            try {
                disconnect();
            } catch (IOException e) {
                System.err.println("Error during disconnect: " + e.getMessage());
            }
        }
    }
}

