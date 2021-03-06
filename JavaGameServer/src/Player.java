
import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

class Player implements Serializable {
	public static final long serialVersionUID = 1L;

	private String code;
	private String player_name;
	private Room current_room;
	public Vector<Card> back = new Vector<Card>();
	public Vector<Card> front = new Vector<Card>();
	private String rank;
	private Boolean isDead = false;
	private Boolean idDeadChecked = false;

	Player(String player_name, Room current_room) {
		this.player_name = player_name;
		this.current_room = current_room;
	}
	
	Player() {	}

	public Boolean getIsDead() {
		return isDead;
	}

	public void setIsDead(Boolean isDead) {
		this.isDead = isDead;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPlayer_name() {
		return player_name;
	}

	public void setPlayer_name(String player_name) {
		this.player_name = player_name;
	}

	public Room getCurrent_room() {
		return current_room;
	}

	public void setCurrent_room(Room current_room) {
		this.current_room = current_room;
	}

	public Vector<Card> getBack() {
		return back;
	}

	public void setBack(Vector<Card> back) {
		this.back = back;
	}

	public Vector<Card> getFront() {
		return front;
	}

	public void setFront(Vector<Card> front) {
		this.front = front;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Boolean getIdDeadChecked() {
		return idDeadChecked;
	}

	public void setIdDeadChecked(Boolean idDeadChecked) {
		this.idDeadChecked = idDeadChecked;
	}
}
