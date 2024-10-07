package game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class LobbyWindow extends JPanel {
    private BufferedImage background;
    private BufferedImage[][] cardTexture = new BufferedImage[4][8];
    private BufferedImage form;
    private int color = 1;

    // New components
    private JList<String> itemList;
    private JScrollPane scrollPane;
    private JButton okButton;
    private JButton cancelButton;

    public LobbyWindow() {
        setLayout(null); // Absolute positioning
        
        try {
            background = ImageIO.read(new File("Textures/zed.jpg"));  // Path to the texture file
            form = ImageIO.read(new File("Textures/drev2.jpg"));  // Path to the texture file
            for (int i = 1; i < 5; i++) {
                for (int j = 1; j < 9; j++) {
                    cardTexture[i - 1][j - 1] = ImageIO.read(new File("Textures/" + i + "_" + j + ".jpg"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Random rd = new Random();
        color = rd.nextInt(3) + 1;

        // New components: Scroll list and buttons
        String[] items = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};
        itemList = new JList<>(items); // Create the list
        scrollPane = new JScrollPane(itemList); // Make it scrollable
        
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        
        add(scrollPane); // Add the scroll list
        add(okButton); // Add the first button
        add(cancelButton); // Add the second button
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
        positionFormComponents();  // Position form components on the screen
    }

    public void drawCards(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int circleRadius = 3 * Math.min(getWidth(), getHeight()) / 8;
        int circleX = getWidth() / 2;
        int circleY = getHeight() / 2;

        int rectWidth = Math.min(getWidth(), getHeight()) / 8;
        int rectHeight = 3 * rectWidth / 2;

        int numRectangles = 8;
        int startAngle = -200;
        int endAngle = 20;

        double angleStep = (double) (endAngle - startAngle) / (numRectangles - 1);

        for (int i = 0; i < numRectangles; i++) {
            double angle = startAngle + i * angleStep;
            double radians = Math.toRadians(angle);

            int rectCenterX = (int) (circleX + circleRadius * 1.4 * Math.cos(radians));
            int rectCenterY = (int) (circleY + circleRadius * Math.sin(radians));

            AffineTransform old = g2d.getTransform();
            g2d.translate(rectCenterX, rectCenterY);
            g2d.rotate(radians);
            g2d.rotate(Math.toRadians(90));

            if (cardTexture[color][i] != null) {
                TexturePaint texturePaint = new TexturePaint(cardTexture[color][i], new Rectangle(-rectWidth / 2, -rectHeight / 2, rectWidth, rectHeight));
                g2d.setPaint(texturePaint);
                g2d.fillRect(-rectWidth / 2, -rectHeight / 2, rectWidth, rectHeight);
            }
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

        int formWidth = Math.min(getWidth(), getHeight()) / 3;
        int formHeight = formWidth;
        int formX = getWidth() / 2 - formWidth / 2;
        int formY = getHeight() / 2 - formHeight / 4;

        if (form != null) {
            TexturePaint texturePaint = new TexturePaint(form, new Rectangle(formX, formY, formWidth, formHeight));
            g2d.setPaint(texturePaint);
            g2d.fillRoundRect(formX, formY, formWidth, formHeight, 20, 20);
        }
    }

    public void positionFormComponents() {
        int formWidth = Math.min(getWidth(), getHeight()) / 3;
        int formHeight = formWidth;
        int formX = getWidth() / 2 - formWidth / 2;
        int formY = getHeight() / 2 - formHeight / 4;

        // Set bounds for scroll list and buttons
        int padding = 20;
        scrollPane.setBounds(formX + padding, formY + padding, formWidth - 2 * padding, formHeight / 2);
        
        okButton.setBounds(formX + padding, formY + formHeight / 2 + 10 + padding, (formWidth - 3 * padding) / 2, 30);
        cancelButton.setBounds(formX + (formWidth / 2) + padding / 2, formY + formHeight / 2 + 10 + padding, (formWidth - 3 * padding) / 2, 30);
    }
}

