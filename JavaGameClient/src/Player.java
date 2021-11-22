import java.io.Serializable;

public class Player implements Serializable{
	public static final long serialVersionUID = 1L;
	private String code;
	private String player_username;
	private Room player_entered_room;
	
	public Player(String code, String player_username) {
		this.code = code;
		this.player_username = player_username;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPlayer_username() {
		return player_username;
	}

	public void setPlayer_username(String player_username) {
		this.player_username = player_username;
	}

	public Room getPlayer_entered_room() {
		return player_entered_room;
	}

	public void setPlayer_entered_room(Room player_entered_room) {
		this.player_entered_room = player_entered_room;
	}

}
