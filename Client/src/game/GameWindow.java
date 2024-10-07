package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameWindow extends JPanel{
	private BufferedImage tableTexture;
    private BufferedImage backTexture;
    private BufferedImage backCard;
    private BufferedImage backgroundTexture;
    private BufferedImage textTexture;
    private BufferedImage[][] cardTexture = new BufferedImage[4][8];
    private BufferedImage[] colorTexture = new BufferedImage[4];
    //private CustomButton button;
    
    private List<Player> players = new ArrayList<>();
 // Store card positions for click detection
    private List<Card> cards = new ArrayList<>();
    private Card playedCard = new Card("4_5",0,0,0,0);
    private Card deck = new Card("deck",0,0,0,0);
    //
    private Rectangle[] colors = new Rectangle[4];
    
    private Rectangle button = new Rectangle();
   // private String[] cards = {"1_2", "2_5", "3_4", "1_7","2_4", "4_8", "3_8", "4_3", "1_5"};  // Cards on the table
    
    
    private boolean drag = false;
    double dragx;
	double dragy;
	Card draging;

    public GameWindow() {
        setBackground(new Color(39, 119, 20)); // Green background   
        
        // Set layout to null so we can manually position the button
        setLayout(null);

        prepTexture();
        
        players.add(new Player("Honza", 4, 1));
        players.add(new Player("Jirka", 4, 1));
        players.add(new Player("Pepa", 4, 1));
        players.add(new Player("Tomáš", 4, 1));
        String[] cards = {"1_2", "2_5", "3_4", "1_7","2_4", "4_8", "3_8", "4_3"};
        createCards(cards);
        
        // Add mouse listener for card click detection
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	if(isOnDeck(e.getX(), e.getY())) {
            		System.out.println("Deck is hit");
            		getCard("4_5");
            	}
                handleCardClick(e.getX(), e.getY());
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
            	int color = clickedColor(e.getX(), e.getY());
            	if(color < 5) {
            		System.out.println("change color to: " + color);
            	}
            	if(hitStay(e.getX(), e.getY())) {
            		System.out.println("Stojim tu");
            	}
            }
            
            @Override
			public void mouseReleased(MouseEvent e) {
            	if(draging != null) {
            		if(!isOnPlayCard(e.getX(), e.getY())) {
            			returnDrag();
            		}
            		else {
            			System.out.println("played card: " + draging.getName());
            		}
					draging = null;
            	}
				repaint();
				
		}
        });
        
        this.addMouseMotionListener(new MouseMotionListener() {
        	
        	/**
        	 * Metoda spoustejici drag
        	 * @param e MouseEvent
        	 */
			@Override
			public void mouseDragged(MouseEvent e) {
				if(draging != null) {
						Drag(e.getX(), e.getY(), draging);
						repaint();
				}			
			}
			/**
			 * Metoda nevyuzita
			 */
			@Override
			public void mouseMoved(MouseEvent e) {
				
			}
        });
    }
    
    private void createCards(String[] cards) {
    	for (int i = 0; i < cards.length; i++) {
    		this.cards.add(new Card(cards[i],0,0,0,0));
    	}
	}
    
    public void getCard(String card) {
    	cards.add(new Card(card,0,0,0,0));
    }

	public void Drag(double x, double y, Card drag) {
		this.drag = true;
		this.dragx = x;
		this.dragy = y;

	}
	
	public boolean isOnPlayCard(int mouseX, int mouseY){
		if(playedCard.contains(mouseX, mouseY)) {
			return true;
		}
		return false;
	}
	
	public boolean isOnDeck(int mouseX, int mouseY){
		if(deck.contains(mouseX, mouseY)) {
			return true;
		}
		return false;
	}
	
	public boolean hitStay(int mouseX, int mouseY) {
		if(button.contains(mouseX, mouseY)) {
			return true;
		}
		return false;
	}
	
	public int clickedColor(int mouseX, int mouseY) {
		for(int i = 0; i < 4; i++) {
			if(colors[i].contains(mouseX, mouseY)) {
				return i + 1;
			}
		}
		return 5;
	}
	
	public void removeDrag(){
		for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.equals(draging)) {
                cards.remove(i);
                break;
            }
        }
	}
	
	public void returnDrag() {
		cards.add(draging);
	}
    
    private void handleCardClick(int mouseX, int mouseY) {
        // Iterate through card positions to check if a card was clicked
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.contains(mouseX, mouseY)) {
                System.out.println("Clicked on card: " + card.getName());
                draging = card;
                removeDrag();
                // You can add additional logic here, e.g., highlight the card or take some action.
                break;
            }
        }
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawTable(g);
        drawMech(g);
        drawCards(g);
        
        if(this.drag) {
        	if(this.draging != null) {
        		drawDrag(g, (int)dragx - draging.getWidth() / 2, (int)dragy - draging.getHeight() / 2, draging.getWidth(), draging.getHeight(), draging);
        	}
			this.drag = false;
		}
    }
    
    private void drawDrag(Graphics g, int x, int y, int width, int height, Card card) {
    	String[] split = card.getName().split("_");
    	int i = Integer.parseInt(split[0]);
    	int j = Integer.parseInt(split[1]);
    	
	    Graphics2D g2d = (Graphics2D) g;
	    g2d.setPaint(new TexturePaint(cardTexture[i-1][j-1], new Rectangle(x, y, width, height)));
	    g2d.fillRect(x, y, width, height);

    }
    
    private void prepTexture() {
    	 try {
             // Load texture for the table
             tableTexture = ImageIO.read(new File("Textures/drevos.jpg"));  // Path to the texture file
             backTexture = ImageIO.read(new File("Textures/zed.jpg"));
             backCard = ImageIO.read(new File("Textures/back.jpg"));
             backgroundTexture = ImageIO.read(new File("Textures/drev2.jpg"));  // Background texture
             textTexture = ImageIO.read(new File("Textures/drev3.jpg"));  // Text texture
             for(int i = 1; i < 5; i++) {
            	 for(int j = 1; j < 9; j++) {
            		 cardTexture[i-1][j-1] = ImageIO.read(new File("Textures/"+i+"_"+j+".jpg"));
            	 }
            	 colorTexture[i-1] = ImageIO.read(new File("Textures/ch_"+i+".jpg"));
            	 colors[i-1] = new Rectangle();
             }
         } catch (IOException e) {
             e.printStackTrace();
         }

    	 
    }
    
    private void drawMech(Graphics g) {
    	 //drawButton(g);
    	 //changeColor(g);
    	 listPlayers(g);
         
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
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(tableX, tableY, tableWidth, tableHeight);
        } else {
            // Fallback if texture is not loaded
            g.setColor(new Color(100, 50, 20));  // Brown color for the table
            g.fillRect(tableX, tableY, tableWidth, tableHeight);  // Table position and size
        }
    }
 /*   
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
*/
    private void drawCards(Graphics g) {
        // Example of drawing some playing cards on the table
        //String[] cards = {"1_2", "2_5", "3_4", "1_7","2_4", "4_8", "3_8", "4_3", "1_5"};  // Ace of Spades, 10 of Diamonds, etc.

        // Dynamically resize cards based on the table's size
        int tableWidth = (int) (getWidth() * 0.85);
        int tableHeight = (int) (getHeight() * 0.75);
        int tableX = (getWidth() - tableWidth) / 2;
        int tableY = (getHeight() - tableHeight) / 2;

        // Scale card size relative to the table
        // Calculate the maximum card width based on available table width
        int maxCardWidth = tableWidth / (cards.size() + 1);  // Adding some padding between cards

        // Calculate the maximum card height based on available table height (e.g., max 20% of table height)
        int maxCardHeight = tableHeight / 5;  // Cards should not exceed 20% of table height
        int cardWidth = Math.min(maxCardWidth, maxCardHeight * 2 / 3);
        int cardHeight = cardWidth * 3 / 2;
        int cardSpace = cardWidth / 6;

        // Calculate starting position to center the cards horizontally at the bottom of the table
        int daleko = cards.size();
        if(daleko > 8) {
        	daleko = 8;
        }
        int totalCardWidth = daleko * cardWidth + (daleko - 1) * cardSpace;  // Total width of all cards with spacing
        int startX = tableX + (tableWidth - totalCardWidth) / 2;  // Center cards horizontally

        // Position cards at the bottom of the table
        int cardY = tableY + tableHeight - cardHeight - cardSpace * 2;  // Slight offset from the bottom of the table

        //last played card
        drawCard(g,tableX+(tableWidth/2)-(3*cardWidth/2),tableY+cardHeight,cardWidth,cardHeight,playedCard);

        //card deck
        drawDeck(g,cardWidth,cardHeight,tableX+(tableWidth/2)+cardWidth/2,tableY+cardHeight);
        
        if(cards.size() > 8) {
        	cardY = cardY - cardHeight - 10;
        }
        // Iterate over cards and draw them
        for (int i = 0; i < cards.size(); i++) {
            int cardX = startX + i * (cardWidth + cardSpace);
            if(i <= 7) {
            	 drawCard(g, cardX, cardY, cardWidth, cardHeight, cards.get(i));
            }
            else {
            	if(i == 8) {
            		cardY = cardY + cardHeight + 10;
            	}
            	cardX = startX + (i-8) * (cardWidth + cardSpace);
            	drawCard(g, cardX, cardY, cardWidth, cardHeight, cards.get(i));
            }
            
            //cards.add(new Card(cardX, cardY, cardWidth, cardHeight));  // Save the card's bounds
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
    		 //neni textura
    	}
    	
    	//update card
        deck.setHeight(height);
        deck.setWidth(width);
        deck.setPosX(x);
        deck.setPosY(y);
    }

    private void drawCard(Graphics g, int x, int y, int width, int height, Card card) {

    	String[] split = card.getName().split("_");
    	int i = Integer.parseInt(split[0]);
    	int j = Integer.parseInt(split[1]);
    	
    	if (cardTexture[i-1][j-1] != null) {
	    	TexturePaint texturePaint = new TexturePaint(cardTexture[i-1][j-1], new Rectangle(x, y, width, height));
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setPaint(texturePaint);
	        g2d.fillRect(x, y, width, height);
    	}
    	else {
    		 //neni textura
    	}
    	
    	//update card
        card.setHeight(height);
        card.setWidth(width);
        card.setPosX(x);
        card.setPosY(y);
    	
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
	        	//neni textura
        	}
        	colors[i].setBounds(x, y, width, height);
        	x += 3*width/2;
        }
    	
    }
    
    public void drawButton(Graphics g) {

    	 Graphics2D g2d = (Graphics2D) g;
    	 int panelWidth = getWidth();
         int panelHeight = getHeight();

         //table size and position
         int tableWidth = (int) (panelWidth * 0.85); 
         int tableHeight = (int) (panelHeight * 0.75); 
         int tableX = (panelWidth - tableWidth) / 2;
         int tableY = (panelHeight - tableHeight) / 2;

         //button dimensions
         int buttonWidth = tableWidth/8;
         int buttonHeight = tableHeight/12;
         int buttonX = tableX + (2*tableWidth/3);
         int buttonY = tableY + 1*tableHeight/4;
         
         button.setRect(buttonX, buttonY, buttonWidth, buttonHeight);
         
         if (backgroundTexture != null) {
        	 //draw button
             TexturePaint texturePaint = new TexturePaint(backgroundTexture, new Rectangle(0, 0, getWidth(), getHeight()));
             g2d.setPaint(texturePaint);
             g2d.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 20, 20);
             g2d.setColor(new Color(33, 32, 32));
             g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
	         g2d.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 20, 20);
	         
	         //darw text
	         String text = "Stát";
	         int fontSize = buttonWidth / 6;
	         g2d.setFont(new Font("SansSerif", Font.BOLD, fontSize));
	         FontMetrics fm = g2d.getFontMetrics();
	         int textX = buttonX + ((buttonWidth - fm.stringWidth(text)) / 2);
	         int textY = buttonY + ((buttonHeight + fm.getAscent()) / 2 - fm.getDescent());
	         g2d.drawString(text, textX, textY);
         }
    }
    
    public void listPlayers(Graphics g) {
    	
    	Graphics2D g2d = (Graphics2D) g;
    	int listHeight = getHeight() / 15;
    	int listWidth = getWidth() - 20;
    	int listX = 10;
    	int listY = 10;
    	
    	g2d.setColor(new Color(209, 209, 209));
    	g2d.fillRoundRect(listX, listY, listWidth, listHeight, 20, 20);
    	g2d.setColor(new Color(33, 32, 32));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        g2d.drawRoundRect(listX, listY, listWidth, listHeight, 20, 20);
        
        int textX = listX;
        int textY = listY;
        int spacing = 20;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String text = "| "+player.getName()+" ("+player.getCardCount()+") - Status: ("+player.getStatus()+") |";
	        int fontSize = listWidth / 100;
	        g2d.setFont(new Font("SansSerif", Font.BOLD, fontSize));
	        FontMetrics fm = g2d.getFontMetrics();
	        textX = textX + spacing;
	        textY = listY + ((listHeight + fm.getAscent()) / 2 - fm.getDescent());
	        g2d.drawString(text, textX, textY);
	        textX = textX + fm.stringWidth(text);
        }
    }
}
/*
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
*/