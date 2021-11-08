
// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.io.Serializable;
import java.util.ArrayList;

class Room implements Serializable {
	private static final long serialVersionUID = 1L;
	public String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, 500: Mouse Event
	public String masterUser;
	public int room_index;
	public String room_name;
	public String password;
	public ArrayList<String> players;
	public ArrayList<String> observers;
	public String status;
	

	public Room(String masterUser, String code, String room_name, String password) {
		this.masterUser = masterUser;
		this.code = code;
		this.room_name = room_name;
		this.password = password;
	}
}