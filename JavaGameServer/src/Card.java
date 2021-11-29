import java.io.Serializable;

class Card implements Serializable {
	public static final long serialVersionUID = 1L;

	private String owner;
	private Room current_room;
	private String card_info;
	private String card_type;
	private int card_number;
	
	public Card(String card_info, Room current_room) {
		this.card_info = card_info;
		this.current_room = current_room;
	}

	public String getCard_type() {
		return card_type;
	}

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public int getCard_number() {
		return card_number;
	}

	public void setCard_number(int card_number) {
		this.card_number = card_number;
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
