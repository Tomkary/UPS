package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class CardTableApp extends JFrame {
    public CardTableApp() {
        // Set the title and default close operation
        super("Card Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the system's look and feel for dark/light mode support
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the custom table panel
        TablePanel tablePanel = new TablePanel();
        add(tablePanel);

        // Set the window size
        setSize(800, 600);
        setLocationRelativeTo(null);  // Center the window
        setVisible(true);
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CardTableApp());
    }
}

class TablePanel extends JPanel {
    private BufferedImage tableTexture;
    private BufferedImage backTexture;
    private BufferedImage backCard;
    private BufferedImage[][] cardTexture = new BufferedImage[4][8];
    private BufferedImage[] colorTexture = new BufferedImage[4];
    private CustomButton button;
 // Store card positions for click detection
    private ArrayList cardPositions = new ArrayList();
    private String[] cards = {"1_2", "2_5", "3_4", "1_7","2_4", "4_8", "3_8", "4_3", "1_5"};  // Cards on the table

    public TablePanel() {
        setBackground(new Color(39, 119, 20)); // Green background
        
        // Create the custom button with textures
        button = new CustomButton("STÁT", "Textures/drev2.jpg", "Textures/drev3.jpg");

        // Set layout to null so we can manually position the button
        setLayout(null);

        // Add the button to the panel
        add(button);
        prepTexture();
        
        // Add mouse listener for card click detection
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleCardClick(e.getX(), e.getY());
            }
        });
    }
    
    private void handleCardClick(int mouseX, int mouseY) {
        // Iterate through card positions to check if a card was clicked
        for (int i = 0; i < cardPositions.size(); i++) {
            Rectangle cardBounds = (Rectangle) cardPositions.get(i);
            if (cardBounds.contains(mouseX, mouseY)) {
                System.out.println("Clicked on card: " + cards[i]);
                // You can add additional logic here, e.g., highlight the card or take some action.
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTable(g);
        drawCards(g);
      //  drawMech(g);
    }
    
    private void prepTexture() {
    	 try {
             // Load texture for the table
             tableTexture = ImageIO.read(new File("Textures/drevos.jpg"));  // Path to the texture file
             backTexture = ImageIO.read(new File("Textures/zed.jpg"));
             backCard = ImageIO.read(new File("Textures/back.jpg"));
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
    
    private void drawMech(Graphics g) {
    	 resizeAndPlaceButton();
    	 changeColor(g);
         
    }

    private void drawTable(Graphics g) {
        // Get current panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Calculate table size and position, keeping it centered and proportionate
        int tableWidth = (int) (panelWidth * 0.85);  // Table takes 75% of panel's width
        int tableHeight = (int) (panelHeight * 0.75); // Table takes 60% of panel's height
        int tableX = (panelWidth - tableWidth) / 2;
        int tableY = (panelHeight - tableHeight) / 2;

        // Draw textured table and back
        if (backTexture != null) {
            TexturePaint texturePaint = new TexturePaint(backTexture, new Rectangle(0, 0, getWidth(), getHeight()));
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(texturePaint);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        if (tableTexture != null) {
            TexturePaint texturePaint = new TexturePaint(tableTexture, new Rectangle(tableX, tableY, tableWidth, tableHeight));
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(texturePaint);
            g2d.fillRect(tableX, tableY, tableWidth, tableHeight);
        } else {
            // Fallback if texture is not loaded
            g.setColor(new Color(100, 50, 20));  // Brown color for the table
            g.fillRect(tableX, tableY, tableWidth, tableHeight);  // Table position and size
        }
    }
    
    private void resizeAndPlaceButton() {
        // Get current panel dimensions
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Calculate table size and position
        int tableWidth = (int) (panelWidth * 0.85);  // Table takes 85% of panel's width
        int tableHeight = (int) (panelHeight * 0.75); // Table takes 75% of panel's height
        int tableX = (panelWidth - tableWidth) / 2;
        int tableY = (panelHeight - tableHeight) / 2;

        // Place the button in the center-bottom of the table
        int buttonWidth = tableWidth/4;
        int buttonHeight = tableHeight/8;
        int buttonX = tableX + (2*tableWidth/3);
        int buttonY = tableY + 1*tableHeight/4;  

        // Set button's bounds dynamically
        button.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
    }

    private void drawCards(Graphics g) {
        // Example of drawing some playing cards on the table
        //String[] cards = {"1_2", "2_5", "3_4", "1_7","2_4", "4_8", "3_8", "4_3", "1_5"};  // Ace of Spades, 10 of Diamonds, etc.
        String playedCard = "2_7";

        // Dynamically resize cards based on the table's size
        int tableWidth = (int) (getWidth() * 0.85);
        int tableHeight = (int) (getHeight() * 0.75);
        int tableX = (getWidth() - tableWidth) / 2;
        int tableY = (getHeight() - tableHeight) / 2;

        // Scale card size relative to the table
        // Calculate the maximum card width based on available table width
        int maxCardWidth = tableWidth / (cards.length + 1);  // Adding some padding between cards

        // Calculate the maximum card height based on available table height (e.g., max 20% of table height)
        int maxCardHeight = tableHeight / 5;  // Cards should not exceed 20% of table height
        int cardWidth = Math.min(maxCardWidth, maxCardHeight * 2 / 3);
        int cardHeight = cardWidth * 3 / 2;
        int cardSpace = cardWidth / 6;

        // Calculate starting position to center the cards horizontally at the bottom of the table
        int daleko = cards.length;
        if(daleko > 8) {
        	daleko = 8;
        }
        int totalCardWidth = daleko * cardWidth + (daleko - 1) * cardSpace;  // Total width of all cards with spacing
        int startX = tableX + (tableWidth - totalCardWidth) / 2;  // Center cards horizontally

        // Position cards at the bottom of the table
        int cardY = tableY + tableHeight - cardHeight - cardSpace;  // Slight offset from the bottom of the table

        //last played card
        drawCard(g,tableX+(tableWidth/2)-(3*cardWidth/2),tableY+cardHeight,cardWidth,cardHeight,playedCard);
        //card deck
        drawDeck(g,cardWidth,cardHeight,tableX+(tableWidth/2)+cardWidth/2,tableY+cardHeight);
        
        if(cards.length > 8) {
        	cardY = cardY - cardHeight - 10;
        }
        // Iterate over cards and draw them
        for (int i = 0; i < cards.length; i++) {
            int cardX = startX + i * (cardWidth + cardSpace);
            if(i <= 7) {
            	 drawCard(g, cardX, cardY, cardWidth, cardHeight, cards[i]);
            }
            else {
            	if(i == 8) {
            		cardY = cardY + cardHeight + 10;
            	}
            	cardX = startX + (i-8) * (cardWidth + cardSpace);
            	drawCard(g, cardX, cardY, cardWidth, cardHeight, cards[i]);
            }
            cardPositions.add(new Rectangle(cardX, cardY, cardWidth, cardHeight));  // Save the card's bounds
        }
    }
    
    private void drawDeck(Graphics g, int width, int height, int x, int y) {
    	if (backCard != null) {
	    	TexturePaint texturePaint = new TexturePaint(backCard, new Rectangle(x, y, width, height));
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setPaint(texturePaint);
	        g2d.fillRect(x, y, width, height);
    	}
    	else {
    		 g.setColor(Color.WHITE);
    	     g.fillRoundRect(x, y, width, height, 10, 10);

    	     // Draw card border
    	     g.setColor(Color.BLACK);
    	     g.drawRoundRect(x, y, width, height, 10, 10);
    	}
    }

    private void drawCard(Graphics g, int x, int y, int width, int height, String card) {

    	String[] split = card.split("_");
    	int i = Integer.parseInt(split[0]);
    	int j = Integer.parseInt(split[1]);
    	
    	if (cardTexture[i-1][j-1] != null) {
	    	TexturePaint texturePaint = new TexturePaint(cardTexture[i-1][j-1], new Rectangle(x, y, width, height));
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setPaint(texturePaint);
	        g2d.fillRect(x, y, width, height);
    	}
    	else {
    		 g.setColor(Color.WHITE);
    	        g.fillRoundRect(x, y, width, height, 10, 10);

    	        // Draw card border
    	        g.setColor(Color.BLACK);
    	        g.drawRoundRect(x, y, width, height, 10, 10);

    	        // Draw card rank and suit in the top left corner
    	        g.setFont(new Font("SansSerif", Font.BOLD, width / 5));  // Font size scales with card size
    	        g.drawString(card, x + 10, y + 25);
    	}
    	
    }
    
    private void changeColor(Graphics g) {
    	
    	// Calculate table size and position
        int tableWidth = (int) (getWidth() * 0.85);  // Table takes 85% of panel's width
        int tableHeight = (int) (getHeight() * 0.75); // Table takes 75% of panel's height
        int tableX = (getWidth() - tableWidth) / 2;
        int tableY = (getHeight() - tableHeight) / 2;
        
        int maxWidth = tableWidth / 20;
        int maxHeight = tableHeight / 10;
        
        //int width = Math.min(tableWidth, tableHeight)/12;
        int width = Math.min(maxWidth, maxHeight * 2 / 3);
        int height = width;
       
        int x = (tableX+(tableWidth/2)-(6*width))/2;
        int y = tableY + 4*height;
       
        for(int i = 0; i < 4; i++) {
        	if (cardTexture[i] != null) {
    	    	TexturePaint texturePaint = new TexturePaint(colorTexture[i], new Rectangle(x, y, width, height));
    	        Graphics2D g2d = (Graphics2D) g;
    	        g2d.setPaint(texturePaint);
    	        g2d.fillRect(x, y, width, height);
        	}
        	else {
	        	g.setColor(Color.WHITE);
	            g.fillRoundRect(x, y, width, height, 10, 10);
	
	            g.setColor(Color.BLACK);
	            g.drawRoundRect(x, y, width, height, 10, 10);
        	}
        	x += 3*width/2;
        }
    	
    }
}

class CustomButton extends JButton {
    private String text;
    private BufferedImage backgroundTexture;
    private BufferedImage textTexture;

    public CustomButton(String text, String backgroundTextureFile, String textTextureFile) {
        this.text = text;
        setContentAreaFilled(false);  // Don't fill the background
        setFocusPainted(false);       // Disable focus border

        // Load textures for background and text
        try {
            backgroundTexture = ImageIO.read(new File(backgroundTextureFile));  // Background texture
            textTexture = ImageIO.read(new File(textTextureFile));  // Text texture
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPreferredSize(new Dimension(200, 100));  // Initial button size (this will change dynamically)
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the button background texture
        if (backgroundTexture != null) {
            TexturePaint texturePaint = new TexturePaint(backgroundTexture, new Rectangle(0, 0, getWidth(), getHeight()));
            g2d.setPaint(texturePaint);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Calculate dynamic font size based on button size
        int fontSize = getWidth() / 6;  // Adjust the divisor to control text size relative to the button width
        Font font = new Font("SansSerif", Font.BOLD, fontSize);
        g2d.setFont(font);

        // Get the metrics to center the text
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(text)) / 2;
        int textY = (getHeight() + fm.getAscent()) / 2 - fm.getDescent();

        // Draw the text with texture
        if (textTexture != null) {
            TexturePaint textPaint = new TexturePaint(textTexture, new Rectangle(0, 0, textTexture.getWidth(), textTexture.getHeight()));
            g2d.setPaint(textPaint);
            g2d.drawString(text, textX, textY);
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);  // Draw a simple border
    }
}
