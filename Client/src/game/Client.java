package game;

import java.io.IOException;

public class Client {

	public static void main(String[] args) {
		GUI gui = new GUI();
		ClientSocket client = new ClientSocket("127.0.0.1", 12345);
		gui.setClient(client);
		client.setGUI(gui);
		/*
		try {
			client.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		client.start();
		gui.run();
	}

}
