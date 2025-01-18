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
    private boolean end = false;
    private LoginWindow login;
    private LobbyWindow lobby;
    private GameWindow game;
    private boolean pingRec = true;
    private long time = 0;

    public ClientSocket(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public synchronized void connect() throws IOException {
        if (socket == null || socket.isClosed()) {
        	try{
	            socket = new Socket(serverIp, serverPort);
	            out = new PrintWriter(socket.getOutputStream(), true);
	            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            running = true;
	            end = false;
	            System.out.println("Connected to the server at " + serverIp + ":" + serverPort);
        	}
        	catch(NoRouteToHostException e) {
        		System.out.println("Disconnected from the server.");
        	}
        }
    }

    public synchronized void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            running = false;
            if (out != null) out.close();
            if (in != null) in.close();
            socket.close();
            System.out.println("Disconnected from the server.");
            lobby.changePanel(login);
            game.restart();
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
        	time = System.currentTimeMillis();
            while (!end) {
                try {
                	if(!pingRec) {
                		if(System.currentTimeMillis() - time >= 4000 && System.currentTimeMillis() - time < 6000) {
                			try {
								socket.close();
							} catch (IOException e) {
								System.err.println("Cannot close: " + e.getMessage());
							}
                			if(game.getStarted()) {
                				game.disconnected();
                			}
                			else {
                				lobby.disconnected();
                			}
                		}
                		try {
							connect();
						} catch (IOException e) {
							e.printStackTrace();
						}
                		if(socket.isConnected()) {
                			running = true;
                			sendMessage("rejoin|"+id+"|\n");
                		}
                		if(System.currentTimeMillis() - time >= 60000) {
                			System.err.println("konec");
                			running = false;
                			end = true;
                			try {
								disconnect();
							} catch (IOException e) {
								e.printStackTrace();
							}
                			break;
                		}
                		Thread.sleep(5000);
                	}else {        		
	                    //sendMessage("ping|"+id+"|\n");
                		sendMessage("ping|"+id+"|\n");
	                    pingRec = false;
	                    time = System.currentTimeMillis();
	                    Thread.sleep(4000);
                	}
                } catch (InterruptedException e) {
                    System.err.println("Ping thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }

    public void receiveMessage(String message) {
    	//System.out.println(message);
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
      //      else if(parts[0].equals("end")) {
      //      	handleEnd(parts);
      //      }
            else {
            	System.err.println("Error: Message not recognised!");
            	System.out.println("----");
            	System.out.println(message);
            	System.out.println("----");
            }
            
            
        } else {
        	if(!message.equalsIgnoreCase("")) {
        		System.err.println("Error: Message does not contain the character '|'!");
        	}
        }
    }
    
    public void handleRejoin(String[] message) {
    	if(message[1].equals("ok")) {
    		pingRec = true;
    		if(game.getStarted()) {
    			game.reconnected();
    		}
    		else {
    			lobby.reconnected();
    		}
    		//lobby.changePanel(game);
    		//game.repaint();
    	}
    }
    
    public void handlePing(String[] message) {
    	if(message[1].equals("ok")) {
    		pingRec = true;
    		time = System.currentTimeMillis();
    	}
    	else if(message[1].equals("server")) {
    		//pingRec = true;
    		sendMessage("ping|ok|"+lobby.getMyId()+"|"+'\n');
    	}
    }
    
    public void handleEnd(String[] message) {
    	lobby.changePanel(lobby);
    }
    
    public void handleWin(String[] message) {
    	int winId = Integer.valueOf(message[1]);
    	if(game.ended == -1) {
    		game.setWinner(winId);
    	}
    	game.repaint();
    }
    
    public void handleStart(String[] message) {
    	if(message[1].equals("err")){
    		game.failStart();
    	}
    	else {
    		game.resetCards();
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
    				//char[] cardChars = card.toCharArray();
    				//card.substring(0, 0);
    				try {
	    				if(Integer.parseInt(card.substring(0, 1)) < 1 || Integer.valueOf(card.substring(0, 1)) > 4) {
	    					this.running = false;
	    					System.err.println("Error: Message not recognised!  Disconnecting");
	    				}
	    				else if(Integer.valueOf(card.substring(2, 3)) < 1 || Integer.valueOf(card.substring(2, 3)) > 8) {
	    					this.running = false;
	    					System.err.println("Error: Message not recognised!  Disconnecting");
	    				}
	    				else {
	    					game.getCard(card);
	    				}
    				} catch(NumberFormatException e) {
    					this.running = false;
    					System.err.println("Error: Message not recognised! Disconnecting");
    				}
    				
    				//game.getCard(card);
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
    		if(message[2].equals("t")) {
    			if(Integer.valueOf(message[3]) == 11) {
        			game.failDeckEmpty();
        		}
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
    		lobby.repaint();
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
    	while(!end) {
    		//System.out.println("nekoncim");
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
	            Thread.sleep(100);
	        } catch (IOException | InterruptedException e) {
	            if (running) {
	                System.err.println("Error during communication: " + e.getMessage());
	            	running = false;
	            }
	        }
    	}
    	System.err.println("koncim poslouchani");
            try {
                disconnect();
            } catch (IOException e) {
                System.err.println("Error during disconnect: " + e.getMessage());
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

