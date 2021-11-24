import java.io.Serializable;

class Card implements Serializable {
	public static final long serialVersionUID = 1L;

	private String owner;
	private Room current_room;
	private String card_info;
	
	public Card(String card_info, Room current_room) {
		this.card_info = card_info;
		this.current_room = current_room;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Room getCurrent_room() {
		return current_room;
	}

	public void setCurrent_room(Room current_room) {
		this.current_room = current_room;
	}

	public String getCard_info() {
		return card_info;
	}

	public void setCard_info(String card_info) {
		this.card_info = card_info;
	}
}
