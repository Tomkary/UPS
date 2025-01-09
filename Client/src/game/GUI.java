package game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class GUI extends JFrame implements Runnable{
	
	//textures
	private BufferedImage tableTexture;
    private BufferedImage backTexture;
    private BufferedImage backCard;
    private BufferedImage backgroundTexture;
    private BufferedImage[][] cardTexture = new BufferedImage[4][8];
    private BufferedImage[] colorTexture = new BufferedImage[4];
    private ClientSocket client;
    
    GameWindow game;
    LobbyWindow lobby;
    LoginWindow login;
	
    @Override
	public void run() {
		
        // Set the title and default close operation
        //super("Card Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the system's look and feel for dark/light mode support
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        prepTexture();

        // Create the custom table panel
        game = new GameWindow(this, client, cardTexture, colorTexture, backgroundTexture, backCard, backTexture, tableTexture);
        lobby = new LobbyWindow(this, game, client, cardTexture, backgroundTexture, backTexture);
        login = new LoginWindow(this, lobby, client, cardTexture, backgroundTexture, backTexture);
        add(login);
        client.setGUI(this);
        //add(lobby);
        //add(game);

        // Set the window size
        setSize(1280, 720);
        setLocationRelativeTo(null);  // Center the window
        setVisible(true);
        
    }
	
	public GameWindow getGame() {
		return game;
	}

	public LobbyWindow getLobby() {
		return lobby;
	}

	public LoginWindow getLogin() {
		return login;
	}
	
/*	
	private void prepTexture() {
	    try {
	        // Load texture for the table
	        tableTexture = ImageIO.read(getClass().getResource("/Textures/drevos.jpg"));
	        backTexture = ImageIO.read(getClass().getResource("/Textures/zed.jpg"));
	        backCard = ImageIO.read(getClass().getResource("/Textures/back.jpg"));
	        backgroundTexture = ImageIO.read(getClass().getResource("/Textures/drev2.jpg"));
	        for (int i = 1; i < 5; i++) {
	            for (int j = 1; j < 9; j++) {
	                cardTexture[i - 1][j - 1] = ImageIO.read(getClass().getResource("/Textures/" + i + "_" + j + ".jpg"));
	            }
	            colorTexture[i - 1] = ImageIO.read(getClass().getResource("/Textures/ch_" + i + ".jpg"));
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
*/

	private void prepTexture() {
		try {
            // Load texture for the table
            tableTexture = ImageIO.read(new File("Textures/drevos.jpg"));  // Path to the texture file
            backTexture = ImageIO.read(new File("Textures/zed.jpg"));
            backCard = ImageIO.read(new File("Textures/back.jpg"));
            backgroundTexture = ImageIO.read(new File("Textures/drev2.jpg"));  // Background texture
            for(int i = 1; i < 5; i++) {
	           	 for(int j = 1; j < 9; j++) {
	           		 cardTexture[i-1][j-1] = ImageIO.read(new File("Textures/"+i+"_"+j+".jpg"));
	           	 }
	           	 colorTexture[i-1] = ImageIO.read(new File("Textures/ch_"+i+".jpg"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
   }
	
	public void setClient(ClientSocket client) {
		this.client = client;
	}
/*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI());
    }
*/
	/*
	@Override
	public void run() {
		 SwingUtilities.invokeLater(() -> new GUI());	
	}
	*/
}
