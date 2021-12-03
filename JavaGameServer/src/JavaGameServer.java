//JavaObjServer.java ObjectStream 기반 채팅 Server

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JavaGameServer extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea;
	private JTextField txtPortNumber;
	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector<UserService> UserVec = new Vector<UserService>(); // 연결된 사용자를 저장할 벡터
	private ArrayList<Room> roomList_server = new ArrayList<>(); // 전체 룸의 리스트
	private ArrayList<InGame> inGameList_server = new ArrayList<>(); // 실행중이 게임 정보
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.out.println("SERVER");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaGameServer frame = new JavaGameServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JavaGameServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				appendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					appendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					appendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					appendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					appendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public void appendText(String str) {
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void appendMsg(ChatMsg msg) {
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.userName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void appendRoom(Room room) {
		textArea.append("code = " + room.getCode() + "\n");
		textArea.append("room_name = " + room.getRoom_name() + "\n");
		textArea.append("password = " + room.getPassword() + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void appendInGame(InGame ingame) {
		textArea.append("code = " + ingame.getCode() + "\n");
		textArea.append("from_whom = " + ingame.getFrom_whom() + "\n");
		textArea.append("from_where = " + ingame.getFrom_where().getRoom_name() + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public enum UserStatus {
		WAITING, PLAYING, OBSERVING;
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread {
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String userName = "";
		public UserStatus userStatus = UserStatus.WAITING;
		public Room enteredRoom = null;
		public Vector<Card> total_cards;
		public Vector<Card> myUpCards = new Vector<Card>();
		public Vector<Card> myDownCards = new Vector<Card>();

		public UserService(Socket client_socket) {
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());
			} catch (Exception e) {
				appendText("userService error");
			}
		}

		public void login() {
			appendText("새로운 참가자 " + userName + " 입장.");
			writeOne("Welcome to Java chat server\n");
			writeOne(userName + "님 환영합니다.\n"); // 연결된 사용자에게 정상접속을 알림
			String msg = "[" + userName + "]님이 입장 하였습니다.\n";
			writeOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다.
		}

		public void logout() {
			String msg = "[" + userName + "]님이 퇴장 하였습니다.\n";
			UserVec.removeElement(this); // logout한 현재 객체를 벡터에서 지운다
			writeAll(msg); // 나를 제외한 다른 User들에게 전송
			appendText("사용자 " + "[" + userName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}

		// 모든 User들에게 방송. 각각의 UserService Thread의 writeOne() 을 호출한다.
		public void writeAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == UserStatus.WAITING)
					user.writeOne(str);
			}
		}

		public void writeOneObject(Object ob) {
			try {
				//oos.reset();
				oos.writeObject(ob);
				oos.reset();
			} catch (IOException e) {
				appendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logout();
			}
		}

		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void writeAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == UserStatus.WAITING)
					user.writeOneObject(ob);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 writeOne() 을 호출한다.
		public void writeOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.userStatus == UserStatus.WAITING)
					user.writeOne(str);
			}
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public void writeOne(String msg) {
			try {
				// ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
				ChatMsg obcm = new ChatMsg.ChatMsgBuilder("200", "SERVER").data(msg).build();
				oos.writeObject(obcm);
				oos.reset();
			} catch (IOException e) {
				appendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		public void sendRoomListToAll() {
			Room room = new Room("601");
			room.setRoomList(roomList_server);
			writeAllObject(room);
		}

		public void allowObservingRoom(Room room) {
			enteredRoom = room;
			for (Room aroom : roomList_server) { // 조사한다
				if (aroom.getRoom_name().equals(room.getRoom_name())) { // 내가 찾는 방이 있음
					if (aroom.getObservers().size() > 4) { // 방 꽉참
						System.out.println("Room is full to observe!!");
						return;
					}
					Room roomTmp = room;
					roomTmp.setCode("609");
					List<String> observers;
					if (room.getObservers() != null) {
						observers = room.getObservers();
						observers.add(room.getFrom_whom());
					} else {
						observers = new ArrayList<>();
						observers.add(room.getFrom_whom());
					}
					roomTmp.setObservers(observers);
					aroom.setObservers(observers);
					writeOneObject(roomTmp);

					System.out.println("들어왔다");
				} else {
					System.out.println("해당 방이 없습니다..");
				}
			}
		}

		public void allowEnteringRoom(Room room) {
			for (int i = 0; i < roomList_server.size(); i++) { // 조사한다
				if (roomList_server.get(i).getRoom_name()
						.equals(room.getRoom_name())) { // 내가 찾는 방이 있음
					enteredRoom = roomList_server.get(i); // 현재 들어간 방을 표시
					userStatus = UserStatus.PLAYING;
					// 방을 만든 사람이 나라면
					if (room.getMasterUser() != null 
							&& room.getMasterUser().equals(userName)) {
						enteredRoom.setCode("603");
						enteredRoom.players.add(userName); // 플레이어 정보를 룸에 추가
						writeOneObject(enteredRoom); // 입장한 사람에게만 send
					} else { // 그 외 참가자
						enteredRoom.setCode("607");
						enteredRoom.players.add(room.getFrom_whom());
						for (int j = 0; j < user_vc.size(); j++) {
							UserService user = (UserService) user_vc.elementAt(j);
							for (String player : enteredRoom.players) { 
								if (user.userName.equals(player)) {
									user.writeOneObject(enteredRoom);
								}
							}
						} // 같은 방 player들에게 모두 send, 그래야 기존의 참가자도 guest를 인지
					}
				} else {
					System.out.println("해당 방이 없습니다..");
				}
			}
		}

		public Vector<Card> cardGenerator() {
			total_cards = new Vector<>();

			for (int j = 0; j < 5; j++) {
				Card card1 = new Card(CardConfig.PLUM1, enteredRoom);
				card1.setCard_type(CardConfig.PLUM);
				card1.setCard_number(1);
				total_cards.addElement(card1);
			}
			for (int j = 0; j < 3; j++) {
				Card card2 = new Card(CardConfig.PLUM2, enteredRoom);
				card2.setCard_type(CardConfig.PLUM);
				card2.setCard_number(2);
				total_cards.addElement(card2);
			}
			for (int j = 0; j < 3; j++) {
				Card card3 = new Card(CardConfig.PLUM3, enteredRoom);
				card3.setCard_type(CardConfig.PLUM);
				card3.setCard_number(3);
				total_cards.addElement(card3);
			}
			for (int j = 0; j < 2; j++) {
				Card card4 = new Card(CardConfig.PLUM4, enteredRoom);
				card4.setCard_type(CardConfig.PLUM);
				card4.setCard_number(4);
				total_cards.addElement(card4);
			}
			for (int j = 0; j < 1; j++) {
				Card card5 = new Card(CardConfig.PLUM5, enteredRoom);
				card5.setCard_type(CardConfig.PLUM);
				card5.setCard_number(5);
				total_cards.addElement(card5);
			}

			//

			for (int j = 0; j < 5; j++) {
				Card card1 = new Card(CardConfig.BERRY1, enteredRoom);
				card1.setCard_type(CardConfig.BERRY);
				card1.setCard_number(1);
				total_cards.addElement(card1);
			}
			for (int j = 0; j < 3; j++) {
				Card card2 = new Card(CardConfig.BERRY2, enteredRoom);
				card2.setCard_type(CardConfig.BERRY);
				card2.setCard_number(2);
				total_cards.addElement(card2);
			}
			for (int j = 0; j < 3; j++) {
				Card card3 = new Card(CardConfig.BERRY3, enteredRoom);
				card3.setCard_type(CardConfig.BERRY);
				card3.setCard_number(3);
				total_cards.addElement(card3);
			}
			for (int j = 0; j < 2; j++) {
				Card card4 = new Card(CardConfig.BERRY4, enteredRoom);
				card4.setCard_type(CardConfig.BERRY);
				card4.setCard_number(4);
				total_cards.addElement(card4);
			}
			for (int j = 0; j < 1; j++) {
				Card card5 = new Card(CardConfig.BERRY5, enteredRoom);
				card5.setCard_type(CardConfig.BERRY);
				card5.setCard_number(5);
				total_cards.addElement(card5);	
			}
			

			//

			for (int j = 0; j < 5; j++) {
				Card card1 = new Card(CardConfig.BANANA1, enteredRoom);
				card1.setCard_type(CardConfig.BANANA);
				card1.setCard_number(1);
				total_cards.addElement(card1);
			}
			for (int j = 0; j < 3; j++) {
				Card card2 = new Card(CardConfig.BANANA2, enteredRoom);
				card2.setCard_type(CardConfig.BANANA);
				card2.setCard_number(2);
				total_cards.addElement(card2);
			}
			for (int j = 0; j < 3; j++) {
				Card card3 = new Card(CardConfig.BANANA3, enteredRoom);
				card3.setCard_type(CardConfig.BANANA);
				card3.setCard_number(3);
				total_cards.addElement(card3);
			}
			for (int j = 0; j < 2; j++) {
				Card card4 = new Card(CardConfig.BANANA4, enteredRoom);
				card4.setCard_type(CardConfig.BANANA);
				card4.setCard_number(4);
				total_cards.addElement(card4);
			}
			for (int j = 0; j < 1; j++) {
				Card card5 = new Card(CardConfig.BANANA5, enteredRoom);
				card5.setCard_type(CardConfig.BANANA);
				card5.setCard_number(5);
				total_cards.addElement(card5);
			}
			

//

			for (int j = 0; j < 5; j++) {
				Card card1 = new Card(CardConfig.PEAR1, enteredRoom);
				card1.setCard_type(CardConfig.PEAR);
				card1.setCard_number(1);
				total_cards.addElement(card1);
			}
			for (int j = 0; j < 3; j++) {
				Card card2 = new Card(CardConfig.PEAR2, enteredRoom);
				card2.setCard_type(CardConfig.PEAR);
				card2.setCard_number(2);
				total_cards.addElement(card2);
			}
			for (int j = 0; j < 3; j++) {
				Card card3 = new Card(CardConfig.PEAR3, enteredRoom);
				card3.setCard_type(CardConfig.PEAR);
				card3.setCard_number(3);
				total_cards.addElement(card3);
			}
			for (int j = 0; j < 2; j++) {
				Card card4 = new Card(CardConfig.PEAR4, enteredRoom);
				card4.setCard_type(CardConfig.PEAR);
				card4.setCard_number(4);
				total_cards.addElement(card4);
			}
			for (int j = 0; j < 1; j++) {
				Card card5 = new Card(CardConfig.PEAR5, enteredRoom);
				card5.setCard_type(CardConfig.PEAR);
				card5.setCard_number(5);
				total_cards.addElement(card5);
			}
			

			Collections.shuffle(total_cards); // 셔플

			return total_cards;
		}

		public Vector<Vector> cardDistributor(Vector<Card> vec) {
			Vector<Card> card1_vec = new Vector<Card>();
			Vector<Card> card2_vec = new Vector<Card>();
			Vector<Card> card3_vec = new Vector<Card>();
			Vector<Card> card4_vec = new Vector<Card>();

			for (int i = 0; i < vec.size(); i++) {
				if (i % 4 == 0)
					card1_vec.add(vec.get(i));
				else if (i % 4 == 1)
					card2_vec.add(vec.get(i));
				else if (i % 4 == 2)
					card3_vec.add(vec.get(i));
				else if (i % 4 == 3)
					card4_vec.add(vec.get(i));
			}

			Vector<Vector> result = new Vector<Vector>();
			result.add(card1_vec);
			result.add(card2_vec);
			result.add(card3_vec);
			result.add(card4_vec);

			return result;
		}
		
		public void isHit(InGame ingame, InGame aGame, Vector<Player> players, Vector<Card> front_cards) {
			int[] res = {0,0,0,0,0};
			
			// 제대로 bell을 쳤는지 판단
			for(Card card : front_cards) {
				if(card.getCard_type().equals(CardConfig.PEAR)) {
					res[0] += card.getCard_number();
				} else if(card.getCard_type().equals(CardConfig.BERRY)) {
					res[1] += card.getCard_number();
				}else if(card.getCard_type().equals(CardConfig.PLUM)) {
					res[2] += card.getCard_number();
				}else if(card.getCard_type().equals(CardConfig.BANANA)) {
					res[3] += card.getCard_number();
				}
			}
			
			Vector<Card> getCards = new Vector<Card>();
			int current_turn;
			for(int i = 0; i < res.length; i++) {
				if(res[i] == 5) { // 올바로 bell을 침
					// 모든 플레이어들의 front card들을 가져옴
					for(int j = 0; j < players.size(); j++) {
						if(players.get(j).getPlayer_name().equals(ingame.getFrom_whom())) {
							current_turn = j;
							aGame.setWhose_turn(current_turn); // 올바르게 bell을 친 사람에게 turn을 줌
						}
						for(Card card: players.get(j).front) {
							getCards.add(card);
						}
						players.get(j).front.clear();
					}
					break;
				}
			}
			for(int i = 0; i < aGame.players.size(); i++) {
				if(aGame.players.get(i).getPlayer_name()	// 메시지를 보낸 player를 찾아 update
						.equals(ingame.getFrom_whom())) {
					Player player = aGame.players.get(i);
					for(int j = 0; j < getCards.size(); j++) {
						player.back.add(getCards.get(j)); // 카드를 추가
					}
				} 
			}
		
			// TODO: 자신 이외의 플레이어들에게 카드를 나눠줌 (나눠줄 카드 수가 3장 이하라면 랜덤하게 플레이어를 골라 줌)
			Vector<Card> fault_cards = new Vector<Card>(); 
			if(getCards.isEmpty()) { // bell을 치면 안되는데 침 -> getCards에 들어간게 없음
				System.out.println("Fault!!");
				// 살아있는 플레이어의 수 & 자신이 가지고 있는 카드의 수를 체크해야 함
				int playerCnt = 0; // 살아있는 플레이어의 수
				int cardCnt = 0;	// 실수로 종을 친 플레이어의 카드에서 압수한 카드 (playerCnt >= cardCnt)
				
				for(Player player : players) {
					if(!player.getIsDead()) playerCnt++;
				}
				
				for(int j = 0; j < players.size(); j++) { // 4명
					if(players.get(j).getPlayer_name().equals(ingame.getFrom_whom())) { // 종을 친 자기 자신
						for(int k = 0; k < playerCnt-1; k++) { // 살아 있는 사람의 수만큼, 카드를 back에서 제거
							if(!players.get(j).back.isEmpty()) {
								// 세 장 이하라면, 있는 만큼 제거
								fault_cards.add(players.get(j).back.remove(players.get(j).back.size()-1));
								cardCnt++;
							}
						}
					}
				}
				
				// TODO: 카드를 줄 때, 게임에서 이미 진 사람에겐 주면 안된다
				if(cardCnt == playerCnt -1) { // 줄 수 있는 카드가 살아있는 사람의 수와 같다면
					for(int j = 0; j < players.size(); j++) { // 4명
						int index = 0;
						if(players.get(j).getPlayer_name().equals(ingame.getFrom_whom())) { // 종을 친 자기 자신은 pass
							continue;
						}
						if(!players.get(j).getIsDead()) // 죽지 않았다면 카드를 줌
							players.get(j).back.add(fault_cards.get(index++));
					}
					for(int j = 0; j < players.size(); j++) {
						System.out.println("player " + players.get(j).getPlayer_name() +" / " + players.get(j).back.size());
					}
				}
				
				if(cardCnt < playerCnt -1) { // 줄 수 있는 카드가 살아있는 사람의 수보다 적다면.. 살아있는 사람 중 랜덤하게 뽑아서 줘야 해
					Vector<Integer> tmp = new Vector<Integer>();
					while(true) {
						int ran_index = (int) (Math.random() * players.size()); // 하나를 랜덤하게 뽑아서 검사
						System.out.println("ran >> " + ran_index);
						if(!tmp.isEmpty()) {
							System.out.println("not empty");
							for(int i = 0; i < tmp.size(); i++) { // 이미 있는 숫자라면 다시 뽑음
								System.out.println("기존 > " + tmp.get(i) + ", ran > " + ran_index);
								if(tmp.get(i) != null && tmp.get(i) == ran_index) continue;
								else break;
							}
						}
						if(players.get(ran_index).getPlayer_name().equals(ingame.getFrom_whom())
								|| players.get(ran_index).getIsDead()) { // 종을 친 자기 자신이거나 이미 죽은 사람이면 pass
							continue; // pass
						}
						else tmp.add(ran_index);
						
						if(tmp.size() == fault_cards.size()) break;
					}
					// fault_cards : 종을 잘못 친 플레이어로부터 뽑아온 카드 (1, 2, 3)
					// tmp : 카드를 줄 플레이어의 ran_index (0, 1, 2, 3)
					int cnt = 0;
					for(int i = 0; i < tmp.size(); i++) {
						players.get(tmp.get(i)).back.add(fault_cards.get(cnt));
						System.out.println("who are lucky? " + players.get(tmp.get(i)).getPlayer_name() + ", ran " + tmp.get(i)) ;
					}
					
				}
			}
			
			for(Player player : players) {	// 죽은 player의 상태 처리
				if(player.back.size() == 0 && player.front.size() == 0) {
					player.setIsDead(true);
					player.setWhenDead(new Date());
				}
			}
		}

		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg chatmsg = null;
					Room room = null;
					InGame ingame = null;

					if (socket == null)
						break;
					// 메시지 수신
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						return;
					}

					// obcm의 타입 체크
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						chatmsg = (ChatMsg) obcm;
						appendMsg(chatmsg);
					} else if (obcm instanceof Room) {
						room = (Room) obcm;
						appendRoom(room);
					} else if (obcm instanceof InGame) {
						ingame = (InGame) obcm;
						appendInGame(ingame);
					} else {
						writeAllObject(chatmsg);
						continue;
					}

					// 프로토콜 체크
					if (chatmsg != null) {
						if (chatmsg.code.matches("100")) {
							userName = chatmsg.userName;
							userStatus = UserStatus.WAITING;
							if (roomList_server.size() > 0)
								sendRoomListToAll();
							login();
						} else if (chatmsg.code.matches("200")) {
							msg = String.format("[%s] %s", chatmsg.userName, chatmsg.data);
							appendText(msg); // server 화면에 출력
							writeAllObject(chatmsg);
						} else if (chatmsg.code.matches("201")) {
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.userStatus.equals(UserStatus.PLAYING))
									user.writeOneObject(chatmsg);
							}
						} else if (chatmsg.code.matches("400")) { // logout message 처리
							logout();
							break;
						}
					}
					if (room != null) {
						if (room.getCode().matches("600")) { // create new room
							System.out.println("Room Created");
							// room에는 masterUser, room_name, password 들어있음
							roomList_server.add(room);
							allowEnteringRoom(room);
							sendRoomListToAll();
						} else if (room.getCode().matches("606")) { // 방 입장 (Play)
							System.out.println("Guest entered here!!");
							allowEnteringRoom(room);
							sendRoomListToAll();
						} else if (room.getCode().matches("605")) { // 방 퇴장 (Play)
							System.out.println("Exit!!");
							enteredRoom = null;
							userStatus = UserStatus.WAITING;
							for (Room aroom : roomList_server) {
								if (aroom.getRoom_name().equals(room.getRoom_name())) {
									List<String> players = new ArrayList<>();
									players = aroom.getPlayers();
									players.remove(room.getFrom_whom());
									break;
								}
							}
							sendRoomListToAll();
						} else if (room.getCode().matches("608")) { // 방 입장 (Observe)
							System.out.println("Observer In");
							allowObservingRoom(room);
							sendRoomListToAll();
						}
					}

					if (ingame != null) {
						if (ingame.getCode().matches("700")) { // Game start echo
							System.out.println("start!!");
							Vector<Card> total_cards = cardGenerator();
							Vector<Vector> vec = cardDistributor(total_cards);

							InGame aGame = new InGame();
							aGame.setCode("700");
							aGame.setFrom_where(ingame.getFrom_where()); // 현재 진행되는 룸
							
							for(Room aroom:roomList_server) {
								if(aroom.getRoom_name().equals(ingame.getFrom_where().getRoom_name())) {
									aroom.setStatus("게임중");
								}
							}
							
							Vector<Player> players = new Vector<Player>();
							for(int i = 0; i < ingame.getFrom_where().players.size(); i++) { // 룸 안에 있는 사람들끼리
								// player 정보 생성
								Player player = new Player(ingame.getFrom_where().players.get(i), ingame.getFrom_where());
								player.back = (Vector<Card>) vec.get(i);
								
								players.add(player);
							}
							aGame.players = players;
							inGameList_server.add(aGame); // 전체 서버에서 관리하는 inGameList에 추가
							
							// aGame에 있는 정보
							// code, from_where(current_entered_room), players(player, 카드 정보)

							// original
							for (int i = 0; i < ingame.getFrom_where().players.size(); i++) {
								for (int j = 0; j < user_vc.size(); j++) {
									UserService user = (UserService) user_vc.elementAt(j);
									if (user.userStatus.equals(UserStatus.PLAYING)
											&& ingame.getFrom_where().players.get(i).equals(user.userName)) {
										user.writeOneObject(aGame);
									}
								}
							}
						} else if (ingame.getCode().matches("701")) {
							System.out.println("Got 701");
							InGame aGame = new InGame();
							
							for(int i = 0; i < inGameList_server.size(); i++) { // 서버 ingame list에서 해당 게임을 찾음
								if(inGameList_server.get(i).getFrom_where().getRoom_name()
										.equals(ingame.getFrom_where().getRoom_name()))
									aGame = inGameList_server.get(i);
							}
							for(int i = 0; i < aGame.players.size(); i++) {
								if(aGame.players.get(i).getPlayer_name()	// 메시지를 보낸 player를 찾아 update
										.equals(ingame.getFrom_whom())) {
									Player player = aGame.players.get(i);
									if(!player.back.isEmpty())
										player.front.add(player.back.remove(0)); // 카드를 뒤집음
								} 
							}
							aGame.setCode("701");
							int current_turn = aGame.getWhose_turn();
							// TODO: player가 뒤집을 카드가 있는지 판단
							current_turn++;
							while(true) { // 다음 차례의 player가 뒤집을 카드가 없다면 턴을 바로 넘겨라
								if(aGame.players.get((current_turn)%4).back.size() == 0) {
									current_turn++;
									continue;
								}
								else break;
							}
							aGame.setWhose_turn(current_turn);
							// update된 정보를 모든 player들에게 뿌림
							for (int i = 0; i < ingame.getFrom_where().players.size(); i++) {
								for (int j = 0; j < user_vc.size(); j++) {
									UserService user = (UserService) user_vc.elementAt(j);
									if (user.userStatus.equals(UserStatus.PLAYING)
											&& ingame.getFrom_where().players.get(i).equals(user.userName)) {
										user.writeOneObject(aGame);
									}
								}
							}
						} else if (ingame.getCode().matches("800")) { // bell hit
							System.out.println("Got 800");
							InGame aGame = new InGame();
							
							
							for(int i = 0; i < inGameList_server.size(); i++) { // 서버 ingame list에서 해당 게임을 찾음
								if(inGameList_server.get(i).getFrom_where().getRoom_name()
										.equals(ingame.getFrom_where().getRoom_name()))
									aGame = inGameList_server.get(i);
							}
							
							Vector<Player> players;
							Vector<Card> front_cards = new Vector<Card>();
							players = aGame.players;
							for(Player player : players) {
								if(!player.front.isEmpty())
									front_cards.add(player.front.get(player.front.size()-1)); // 각 플레이어들의 맨 앞 카드를 벡터에 저장
							}
							// ingame : client로부터 전달받은 값
							// aGame : server에서 새로 생성해서 관리하는 객체
							// players : 모든 player들의 정보
							// front_cards : 모든 player들의 앞면 카드
							isHit(ingame, aGame, players, front_cards);
							
							for (int i = 0; i < ingame.getFrom_where().players.size(); i++) { // 방 안의 유저에게 뿌림
								for (int j = 0; j < user_vc.size(); j++) {
									UserService user = (UserService) user_vc.elementAt(j);
									if (user.userStatus.equals(UserStatus.PLAYING)
											&& ingame.getFrom_where().players.get(i).equals(user.userName)) {
										aGame.setCode("800");
										user.writeOneObject(aGame);
									}
								}
							}
						}
					}
				} catch (IOException e) {
					appendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}

}
