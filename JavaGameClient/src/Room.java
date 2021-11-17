
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Room implements Serializable {
	public static final long serialVersionUID = 1L;
	private String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, 500: Mouse Event
	private String masterUser;
	private int room_index;
	private String room_name;
	private String password;
	private List<String> players;
	private List<String> observers;
	private List<Room> roomList;
	private int players_cnt;
	private int observers_cnt;
	private String status;
	private String from_whom;

	public Room(String code, String masterUser) {
		this.code = code;
		this.masterUser = masterUser;
//		this.players = new ArrayList<>() {
//			private static final long serialVersionUID = 1L;
//			{ add(masterUser); }
//		};
		// players.add(masterUser);
		this.observers = new ArrayList<>();
	}
	
	public Room(String code) {
		this.code = code;
	}
	

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

	public List<String> getPlayers() {
		return players;
	}

	public void setPlayers(List<String> players) {
		this.players = players;
	}

	public List<String> getObservers() {
		return observers;
	}

	public void setObservers(List<String> observers) {
		this.observers = observers;
	}

	public int getPlayers_cnt() {
		return players_cnt;
	}

	public void setPlayers_cnt(int players_cnt) {
		this.players_cnt = players_cnt;
	}

	public int getObservers_cnt() {
		return observers_cnt;
	}

	public void setObservers_cnt(int observers_cnt) {
		this.observers_cnt = observers_cnt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}