package game;

public class Player {

	private String name;
	private int cardCount;
	/**status = 1 playing 
	 * status = 2 pause
	 * status = 3 disconnected
	 * status = 4 unknown 
	 * */
	private int status;
	
	public Player(String name, int cardCount, int status) {
		this.name = name;
		this.cardCount = cardCount;
		this.status = status;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getCardCount() {
		return cardCount;
	}
	
	public void setCardCount(int cardCount) {
		this.cardCount = cardCount;
	}
	
	public String getStatus() {
		if(status == 1) {
			return "playing";
		}
		if(status == 2) {
			return "pause";
		}
		if(status == 3) {
			return "disconnected";
		}
		return "unknown";
		
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

}
