
// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Room implements Serializable {
	private static final long serialVersionUID = 1L;
	public String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, 500: Mouse Event
	public String masterUser;
	public int room_index;
	public String room_name;
	public String password;
	public List<String> players = new ArrayList<String>();
	public List<String> observers = new ArrayList<String>();
	public List<Room> roomList =  Collections.synchronizedList(new ArrayList<Room>());
	public String status = "대기중";
	
	public static class RoomBuilder {
		private static final long serialVersionUID = 1L;
		public String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, 500: Mouse Event
		public String masterUser;
		public int room_index;
		public String room_name;
		public String password;
		public List<String> players = new ArrayList<String>();
		public List<String> observers = new ArrayList<String>();
		public List<Room> roomList =  Collections.synchronizedList(new ArrayList<Room>());
		public String status = "대기중";
		
		public RoomBuilder(String code) {
			this.code = code;
		}
		public RoomBuilder masterUser(String masterUser) {this.masterUser = masterUser; return this;}
		public RoomBuilder room_index(int room_index) {this.room_index = room_index; return this;}
		public RoomBuilder room_name(String room_name) {this.room_name = room_name; return this;}
		public RoomBuilder password(String password) {this.password = password; return this;}
		public RoomBuilder players(List<String> players) {this.players = players; return this;}
		public RoomBuilder observers(List<String> observers) {this.observers = observers; return this;}
		public RoomBuilder roomList(List<Room> roomList) {this.roomList = roomList; return this;}
		public Room build() {
			Room room = new Room();
			room.code = this.code;
			room.masterUser = this.masterUser;
			room.room_index = this.room_index;
			room.room_name = this.room_name;
			room.password = this.password;
			room.players = this.players;
			room.observers = this.observers;
			room.roomList = this.roomList;
			return room;
		}
		
	}
}