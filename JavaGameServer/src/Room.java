
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class Room implements Serializable {
	public static final long serialVersionUID = 1L;
	private String code;
	private String masterUser;
	private int room_index;
	private String room_name;
	private String password;
	public List<String> players = new ArrayList<>();
	public Vector<String> observers = new Vector<>();
	public List<Room> roomList = new ArrayList<>();
	private String status = "¥Î±‚¡ﬂ";
	private String from_whom;

	public Room(String code, String masterUser) {
		this.code = code;
		this.masterUser = masterUser;
	}

	public Room(String code) {
		this.code = code;
	}
	
	public Room() {}

	public String getFrom_whom() {
		return from_whom;
	}

	public void setFrom_whom(String from_whom) {
		this.from_whom = from_whom;
	}

	public List<Room> getRoomList() {
		return roomList;
	}

	public void setRoomList(List<Room> roomList) {
		this.roomList = roomList;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMasterUser() {
		return masterUser;
	}

	public void setMasterUser(String masterUser) {
		this.masterUser = masterUser;
	}

	public int getRoom_index() {
		return room_index;
	}

	public void setRoom_index(int room_index) {
		this.room_index = room_index;
	}

	public String getRoom_name() {
		return room_name;
	}

	public void setRoom_name(String room_name) {
		this.room_name = room_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}