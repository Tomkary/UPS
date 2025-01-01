package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;


public class LobbyWindow extends JPanel {
    private BufferedImage background;
    private BufferedImage[][] cardTexture = new BufferedImage[4][8];
    private BufferedImage form;
    private int color = 1;

    // New components
    private JList<String> itemList;
    private JScrollPane scrollPane;
    private JButton joinButton;
    private JButton createButton;
    private JButton disconnect;
    
    private ClientSocket client;

    public LobbyWindow(JFrame mainFrame, JPanel nextWin, ClientSocket client, BufferedImage[][] cardTexture, BufferedImage backgroundTexture, BufferedImage backTexture) {
        setLayout(null); // Absolute positioning
        
        this.cardTexture = cardTexture;
    	this.form = backgroundTexture;
    	this.background = backTexture;
    	
    	this.client = client;
        /*
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
        */
        Random rd = new Random();
        color = rd.nextInt(3) + 1;

        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Item 1");
        listModel.addElement("Item 2");
        listModel.addElement("Item 3");
        listModel.addElement("Item 4");
        listModel.addElement("Item 5");

        itemList = new JList<>(listModel); // Create the list
        scrollPane = new JScrollPane(itemList); // Make it scrollable
        
        joinButton = new JButton("Join");
        createButton = new JButton("Create");
        disconnect = new JButton("Disconnect");
        
        add(scrollPane); // Add the scroll list
        add(joinButton); // Add the first button
        add(createButton);
        add(disconnect);
        
        
        // Action for OK button: Print selected item
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = itemList.getSelectedValue();
                if (selectedItem != null) {
                    System.out.println("Selected item: " + selectedItem);
                    mainFrame.getContentPane().removeAll();
                	mainFrame.add(nextWin);
                	mainFrame.revalidate();
                	mainFrame.repaint();
                } else {
                    System.out.println("No item selected");
                }
            }
        });

        // Action for Create button: Add a new item to the list
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newItem = "New Item " + (listModel.getSize() + 1); // New item label
                listModel.addElement(newItem); // Add new item to the model
                System.out.println("Added new item: " + newItem);
            }
        });
        
        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	System.out.println("odpoj");
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
        revalidate();
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
        
     // Set bounds for scroll list and buttons
        int padding = formHeight / 8;
        scrollPane.setBounds(formX + padding, formY + padding, formWidth - 2 * padding, formHeight / 2);
        
        joinButton.setBounds(formX + padding, formY + formHeight - 2 * padding, (formWidth - 3 * padding) / 2, formHeight / 10);
        disconnect.setBounds(this.getWidth() / 2 - (formWidth - 5 * padding) / 2, formY - 5 * joinButton.getHeight(), (formWidth - 5 * padding), formHeight / 10);
        createButton.setBounds(formX + formWidth - padding - joinButton.getWidth(), formY + formHeight - 2 * padding, (formWidth - 3 * padding) / 2, formHeight / 10);
    }

}

