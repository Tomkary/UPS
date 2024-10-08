package game;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class GUI extends JFrame{
	public GUI() {
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
        GameWindow game = new GameWindow();
        LoginWindow login = new LoginWindow();
        LobbyWindow lobby = new LobbyWindow();
        //add(login);
        //add(lobby);
        add(game);

        // Set the window size
        setSize(800, 600);
        setLocationRelativeTo(null);  // Center the window
        setVisible(true);
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI());
    }
}
