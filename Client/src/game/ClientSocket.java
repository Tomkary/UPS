package game;

import java.io.*;
import java.net.*;

public class ClientSocket extends Thread {
    private String serverIp;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running = false;
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
    	message = message.trim();
    	System.out.println(message);
        if (message.contains("|")) {
        	
            String[] parts = message.split("\\|");

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
            else if(parts[0].equals("leave")) {
            	handleLeave(parts);
            }
            else if(parts[0].equals("turn")) {
            	handleTurn(parts);
            }
            else if(parts[0].equals("status")) {
            	handleStatus(parts);
            }
            else if(parts[0].equals("start")) {
            	handleStart(parts);
            }
            else if(parts[0].equals("win")) {
            	handleWin(parts);
            }
            else if(parts[0].equals("ping")) {
            	handlePing(parts);
            }
            else if(parts[0].equals("rejoin")) {
            	handleRejoin(parts);
            }
            else if(parts[0].equals("end")) {
            	handleEnd(parts);
            }
            else {
            	System.err.println("Error: Message not recognised!");
            }
            
            
        } else {
            System.err.println("Error: Message does not contain the character '|'!");
        }
    }
    
    public void handleRejoin(String[] message) {
    	//TODO
    }
    
    public void handlePing(String[] message) {
    	//TODO
    }
    
    public void handleEnd(String[] message) {
    	lobby.changePanel(lobby);
    }
    
    public void handleWin(String[] message) {
    	int winId = Integer.valueOf(message[1]);
    	game.setWinner(winId);
    	game.repaint();
    }
    
    public void handleStart(String[] message) {
    	if(message[1].equals("err")){
    		game.failStart();
    	}
    	else {
	    	int cardCount = Integer.valueOf(message[1]);
	    	String[] cards = new String[cardCount];
	    	for(int i = 0; i < cardCount; i++) {
	    		cards[i] = message[i+2];
	    	}
	    	game.createCards(cards);
	    	
	    	int index = cardCount + 2;
	    	int players = Integer.valueOf(message[index]);
	    	for(int i = 1; i <= players; i++) {
	    		String[] player = message[index+i].split(";");
	    		game.addPlayer(Integer.valueOf(player[0]), player[1]);
	    	}
	    	
	    	game.setStarted(true);
	    	game.repaint();
    	}
    	
    }
    
    public void handleStatus(String[] message) {
    	int players = Integer.valueOf(message[1]);
    	for(int i = 0; i < players; i++) {
    		String[] info = message[i+2].split(";");
    		game.updatePlayer(Integer.valueOf(info[0]), Integer.valueOf(info[1]), Integer.valueOf(info[2]));
    	}
    	int state = Integer.valueOf(message[players + 2]);
    	int color = Integer.valueOf(message[players + 3]);
    	String card = message[players + 4];
    	int nextId = Integer.valueOf(message[players + 5]);
    	game.statusChange(state, color, nextId, card);
    	game.repaint();
    }
    
    public void handleTurn(String[] message) {
    	if(message[1].equals("ok")) {
    		if(message[2].equals("t")) {
    			int cardCount = Integer.valueOf(message[3]);
    			for(int i = 0; i < cardCount; i++) {
    				String card = message[i+4];
    				char[] cardChars = card.toCharArray();
    				if(Integer.valueOf(cardChars[0]) < 1 || Integer.valueOf(cardChars[0]) > 4) {
    					System.err.println("Error: Message not recognised!");
    				}
    				if(Integer.valueOf(cardChars[2]) < 1 || Integer.valueOf(cardChars[2]) > 8) {
    					System.err.println("Error: Message not recognised!");
    				}
    				
    				game.getCard(card);
    			}
    		}
    		else if(message[2].equals("s")) {
    			
    		}
    		else if(message[2].equals("p")) {
    			game.play();
    		}
    	}
    	else if(message[1].equals("err")) {
    		if(message[2].equals("p")) {
    			game.returnMove();
    		}
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
    
    public void handleLeave(String[] message) {
    	if(message[1].equals("ok")) {
    		lobby.changePanel(lobby);
    		game.restart();
    	}
    	else if(message[1].equals("err")) {
    		if(Integer.valueOf(message[2]) == 9) {
    			game.failLeave();
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

