package game;

public class Card {
	
	private String name;
	
	private int width;
	
	private int height;
	
	private int posX;
	
	private int posY;
	
	public Card(String name, int width, int height, int posX, int posY) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.posX = posX;
		this.posY = posY;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public void setPosX(int posX) {
		this.posX = posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public void setPosY(int posY) {
		this.posY = posY;
	}

	public boolean contains(int mouseX, int mouseY) {
		if(((mouseX >= posX) && (mouseX <= (posX + width))) && ((mouseY >= posY) && (mouseY <= (posY + height)))) {
			return true;
		}
		return false;
	}
	
	public boolean equals(Card tested) {
		if(this.name.equals(tested.name)) {
			return true;
		}
		return false;
	}
	
	
}
