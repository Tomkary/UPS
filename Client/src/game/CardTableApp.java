package game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

    public TablePanel() {
        setBackground(new Color(39, 119, 20)); // Green background
        try {
            // Load texture for the table
            tableTexture = ImageIO.read(new File("drevos.jpg"));  // Path to the texture file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTable(g);
        drawCards(g);
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

        // Draw textured table
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

    private void drawCards(Graphics g) {
        // Example of drawing some playing cards on the table
        String[] cards = {"AS", "10D", "3H", "QC","AS", "10D", "3H", "QC", "QC"};  // Ace of Spades, 10 of Diamonds, etc.

        // Dynamically resize cards based on the table's size
        int tableWidth = (int) (getWidth() * 0.85);
        int tableHeight = (int) (getHeight() * 0.75);
        int tableX = (getWidth() - tableWidth) / 2;
        int tableY = (getHeight() - tableHeight) / 2;

        // Scale card size relative to the table
        int tot = tableWidth;
        if (tableWidth >= tableHeight) {
        	tot = tableHeight;
        }
        else {
        	tot = tableWidth;
        }
        int cardWidth = tot / 8;  // Card width proportional to table width
        int cardHeight = cardWidth * 3 / 2;  // Standard card aspect ratio is 3:2
        int cardSpace = cardWidth / 3;

        // Calculate starting position to center the cards horizontally at the bottom of the table
        int daleko = cards.length;
        if(daleko > 8) {
        	daleko = 8;
        }
        int totalCardWidth = daleko * cardWidth + (daleko - 1) * cardSpace;  // Total width of all cards with spacing
        int startX = tableX + (tableWidth - totalCardWidth) / 2;  // Center cards horizontally

        // Position cards at the bottom of the table
        int cardY = tableY + tableHeight - cardHeight - cardSpace;  // Slight offset from the bottom of the table

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
            	cardX = startX + (i-8) * (cardWidth + cardSpace);
            	cardY = cardY + cardHeight + 10;
            	drawCard(g, cardX, cardY, cardWidth, cardHeight, cards[i]);
            }
        }
    }

    private void drawCard(Graphics g, int x, int y, int width, int height, String card) {
        // Draw card background
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
