
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class InGame implements Serializable {
	public static final long serialVersionUID = 1L;

	private String code;
	private String from_whom;
	private Room from_where;
	private String masterUser;
	public List<String> players = new ArrayList<>();
	public List<String> observers = new ArrayList<>();
//	public Vector<Card> totalCard = new Vector<Card>();
	public List<Player> players222;
	
	public Vector<Card> upCard = new Vector<Card>();
	public Vector<Card> downCard = new Vector<Card>();

	public InGame(String code, String from_whom, Room from_where) {
		this.code = code;
		this.from_whom = from_whom;
		this.from_where = from_where;
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

	public String getFrom_whom() {
		return from_whom;
	}

	public void setFrom_whom(String from_whom) {
		this.from_whom = from_whom;
	}

	public Room getFrom_where() {
		return from_where;
	}

	public void setFrom_where(Room from_where) {
		this.from_where = from_where;
	}
}
