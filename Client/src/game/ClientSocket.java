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

    public void startPing(int id) {
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(5000);
                    sendMessage("ping|"+id+"|\n");
                } catch (InterruptedException e) {
                    System.err.println("Ping thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }

    public void receiveMessage(String message) {
        if (message.contains("|")) {
        	
            String[] parts = message.split("|");

            if(parts[0].equals("connect")) {
            	handleConnect(parts);
            }
            else if(parts[0].equals("lobby")) {
            	handleLobby(parts);
            }
            else if(parts[0].equals("create")) {
            	handleCreate(parts);
            }
            else if(parts[0].equals("dis")) {
            	handleDis(parts);
            }
            else if(parts[0].equals("join")) {
            	handleJoin(parts);
            }
            
            
        } else {
            System.err.println("Error: Message does not contain the character '|'!");
        }
    }
    
    public void handleConnect(String[] message) {
    	if(message[1].equals("ok")) {
    		int id = Integer.valueOf(message[2]);
    		lobby.setMyId(id);
    		game.setMyId(id);
    		login.changeLobby();
    		startPing(id);
    	}
    	else if(message[1].equals("err")) {
    		if(Integer.valueOf(message[2]) == 1) {
    			login.invalidName();
    		}
    		else {
    			login.connectionError();
    		}
    	}
    }
    
    public void handleLobby(String[] message) {
    	int rooms = Integer.valueOf(message[1]);
    	lobby.deleteRooms();
    	for(int i = 0; i < rooms; i++) {
    		lobby.listRoom(Integer.valueOf(message[i + 2]));
    	}
    	lobby.repaint();
    }
    
    public void handleCreate(String[] message) {
    	if(message[1].equals("err")) {
    		if(Integer.valueOf(message[2]) == 5) {
    			lobby.cannotCreate();
    		}
    	}
    }
    
    public void handleDis(String[] message) {
    	if(message[1].equals("ok")) {
    		lobby.changePanel(login.getLogin());
    		running = false;
    	}
    	else {
    		lobby.failDis();
    	}
    }
    
    public void handleJoin(String[] message) {
    	if(message[1].equals("ok")) {
    		game.setStarted(false);
    		game.setMyId(lobby.getMyId());
    		lobby.changePanel(lobby.getNextWin());
    		
    	}
    	else if(message[1].equals("err")) {
    		if(Integer.valueOf(message[2]) == 2) {
    			lobby.fullRoom();
    		}
    		else if(Integer.valueOf(message[2]) == 3) {
    			lobby.gameStarted();
    		}
    		else {
    			lobby.failJoin();
    		}
    	}
    }

    @Override
    public void run() {
        try {
            while (running) {
                if (in != null) {
                    String serverMessage = in.readLine();
                    if (serverMessage != null) {
                        receiveMessage(serverMessage);
                    }
                }
                if(socket.isClosed()) {
                	running = false;
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
    
    public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setGUI(GUI gui) {
    	this.lobby = gui.getLobby();
    	this.login = gui.getLogin();
    	this.game = gui.getGame();
    }
}

