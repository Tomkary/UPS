package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginWindow extends JPanel {
    private BufferedImage background;
    private BufferedImage[][] cardTexture = new BufferedImage[4][8];
    private BufferedImage form;
    private int color = 1;
    
    // Form components
    private JTextField nick;
    private JTextField ip;
    private JTextField port;
    private JButton login;
    private JFrame mainFrame;
    private JPanel nextWin;
    
    private ClientSocket client;

    public LoginWindow(JFrame mainFrame, JPanel nextWin, ClientSocket client, BufferedImage[][] cardTexture, BufferedImage backgroundTexture, BufferedImage backTexture) {
    	
    	this.cardTexture = cardTexture;
    	this.form = backgroundTexture;
    	this.background = backTexture;
    	this.mainFrame = mainFrame;
    	this.nextWin = nextWin;
    	
    	this.client = client;
    	/*
    	try {
           background = ImageIO.read(new File("Textures/zed.jpg"));  // Path to the texture file
           form = ImageIO.read(new File("Textures/drev2.jpg"));  // Path to the texture file
           for(int i = 1; i < 5; i++) {
          	 for(int j = 1; j < 9; j++) {
          		 cardTexture[i-1][j-1] = ImageIO.read(new File("Textures/"+i+"_"+j+".jpg"));
          	 }
           }
        } catch (IOException e) {
            e.printStackTrace();
        }
    	*/
    	Random rd = new Random();
    	color = rd.nextInt(3) + 1;
    	
    	// Initialize and place form components
        nick = new JTextField();
        nick.setToolTipText("Nick");
        ip = new JTextField();
        ip.setToolTipText("Server IP address");
        port = new JTextField();
        port.setToolTipText("Port");
        login = new JButton("Connect");

        // Add components to panel
        add(port);
        add(nick);
        add(ip);
        add(login);
        
        
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if all text fields are filled
                if (nick.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Please fill in your nick for the game", "Incomplete Field", JOptionPane.WARNING_MESSAGE);
                }
                else if (ip.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Please fill in ip address of the game server", "Incomplete Field", JOptionPane.WARNING_MESSAGE);
                }
                else if (port.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Please fill in port to connect to the server", "Incomplete Field", JOptionPane.WARNING_MESSAGE);
                }
                else {
                	String ipAdd = ip.getText();
                	String serverPort = port.getText();
                	client.setServerIp(ipAdd);
                	client.setServerPort(Integer.valueOf(serverPort));
                	try {
						client.connect();
						client.start();
					} catch (IOException e1) {
						connectionError();
					}
                	//client.start();
                	String playerName = nick.getText();
                	client.sendMessage("connect|"+playerName+"|"+'\n');
                }
            }
        });
        
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (background != null) {
            TexturePaint texturePaint = new TexturePaint(background, new Rectangle(0, 0, getWidth(), getHeight()));
            g2d.setPaint(texturePaint);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        drawCards(g);
        drawTitle(g);
        drawForm(g);
    }
    
    public void invalidName() {
    	JOptionPane.showMessageDialog(LoginWindow.this,"Connection failed, name already used on server, please use different name and connect again", "Invalid name", JOptionPane.WARNING_MESSAGE);
    }
    
    public void connectionError() {
    	JOptionPane.showMessageDialog(LoginWindow.this,"Connection failed, please try again", "Connection failed", JOptionPane.WARNING_MESSAGE);
    }
    
    public JPanel getLogin() {
    	return this;
    }
    
    public void changeLobby() {
    	mainFrame.getContentPane().removeAll();
    	mainFrame.add(nextWin);
    	mainFrame.revalidate();
    	mainFrame.repaint();
    }
    
    public void drawCards(Graphics g) {
    	
    	Graphics2D g2d = (Graphics2D) g;

    	// Circle parameters
        int circleRadius = 3 * Math.min(getWidth(), getHeight()) / 8;
        int circleX = getWidth() / 2 ;
        int circleY = getHeight() / 2 ;

        // Rectangle parameters (size)
        int rectWidth = Math.min(getWidth(), getHeight()) / 8;
        int rectHeight = 3 * rectWidth / 2;

        // Define the number of rectangles and angle range (180 to 360 degrees)
        int numRectangles = 8;
        int startAngle = -200;
        int endAngle = 20;
        
        // Calculate angle step
        double angleStep = (double) (endAngle - startAngle) / (numRectangles - 1);
        
        // Loop through angles from 180 to 360 degrees with even spacing
        for (int i = 0; i < numRectangles; i++) {
            // Calculate the angle in degrees and convert to radians
            double angle = startAngle + i * angleStep;
            double radians = Math.toRadians(angle);

            // Calculate the rectangle's position (center of the rectangle on the circle edge)
            int rectCenterX = (int) (circleX + circleRadius* 1.4 * Math.cos(radians));
            int rectCenterY = (int) (circleY + circleRadius * Math.sin(radians));

            // Apply transformations for rotation
            AffineTransform old = g2d.getTransform();  // Save the original transform
            g2d.translate(rectCenterX, rectCenterY);  // Translate to the rectangle's center            
            g2d.rotate(radians);  // Rotate by the specified angle          
            g2d.rotate(Math.toRadians(90)); // rotate the card to face the correct side
            
            if (cardTexture[color][i] != null) {
    	    	TexturePaint texturePaint = new TexturePaint(cardTexture[color][i], new Rectangle(-rectWidth / 2, -rectHeight / 2, rectWidth, rectHeight));
    	        g2d.setPaint(texturePaint);
    	        g2d.fillRect(-rectWidth / 2, -rectHeight / 2, rectWidth, rectHeight);
        	}
            // Restore the original transform to avoid cumulative transformations
            g2d.setTransform(old);
        }
    }
    
    public void drawTitle(Graphics g) {
    	
    	Graphics2D g2d = (Graphics2D) g;
    	
    	String text = "PRŠÍ";
    	int fontSize = Math.min(getWidth(), getHeight()) / 10;
        g2d.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() / 2) - (fm.stringWidth(text) / 2);
        int textY = (2 * getHeight() / 5);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    	
    }
    
    public void drawForm(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
    	
    	int formWidth =  Math.min(getWidth(), getHeight()) / 3;
    	int formHeight = formWidth;
    	int formX = getWidth() / 2 - formWidth / 2;
    	int formY = getHeight() / 2 - formHeight / 4;
    	
    	 if (form != null) {
             TexturePaint texturePaint = new TexturePaint(form, new Rectangle(formX, formY, formWidth, formHeight));
             g2d.setPaint(texturePaint);
             g2d.fillRoundRect(formX, formY, formWidth, formHeight, 20, 20);
         }
    	 
    	 nick.setBounds(formX + 20, formY + formHeight / 9, formWidth - 40, formHeight / 9);
         ip.setBounds(formX + 20, formY + 3 * formHeight / 9, formWidth - 40, formHeight / 9);
         port.setBounds(formX + 20, formY + 5 * formHeight / 9, formWidth - 40, formHeight / 9);
         login.setBounds(formX + 20, formY + 7 * formHeight / 9, formWidth - 40, formHeight / 9);
    }

}
