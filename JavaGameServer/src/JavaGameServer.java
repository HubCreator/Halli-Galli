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
	private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
	private ArrayList<Room> roomList_server = new ArrayList<>(); // 전체 룸의 리스트
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
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void appendMsg(ChatMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.userName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void appendRoom(Room room) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + room.getCode() + "\n");
		textArea.append("room_name = " + room.getRoom_name() + "\n");
		textArea.append("password = " + room.getPassword() + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public enum Status {
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
		public Status userStatus = Status.WAITING;
		public Room enteredRoom = null;

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
				if (user.userStatus == Status.WAITING)
					user.writeOne(str);
			}
		}

		public void writeOneObject(Object ob) {
			try {
				oos.reset();
				oos.writeObject(ob);
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
				if (user.userStatus == Status.WAITING)
					user.writeOneObject(ob);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 writeOne() 을 호출한다.
		public void writeOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.userStatus == Status.WAITING)
					user.writeOne(str);
			}
		}
		
		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public void writeOne(String msg) {
			try {
				// ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
				ChatMsg obcm = new ChatMsg.ChatMsgBuilder("200", "SERVER").data(msg).build();
				oos.writeObject(obcm);
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
					if(aroom.getObservers().size() > 4) { // 방 꽉참
						System.out.println("Room is full to observe!!");
						return;
					}
					Room roomTmp = room;
					roomTmp.setCode("609");
					List<String> observers;
					if(room.getObservers() != null) {
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
					enteredRoom = roomList_server.get(i);
					userStatus = Status.PLAYING;
					// 방을 만든 사람이 나라면
					if (room.getMasterUser() != null 
							&& room.getMasterUser().equals(userName)) {
						//Room roomTmp = room;
						//List<String> players = new ArrayList<>();
						//players.add(userName);
						// roomTmp.setPlayers(players);
						// aroom.setPlayers(players);
						enteredRoom.setCode("603");
						enteredRoom.players.add(userName);
						writeOneObject(enteredRoom);
						System.out.println("내가 만든방");
					} else { // 그 외 참가자
						//List<String> players = new ArrayList<>();
						//players = aroom.getPlayers();
						//players.add(room.getFrom_whom());
						//aroom.setPlayers(players);
						//Room roomTmp = room;
						
						// room.getFrom_whom() == userName
						enteredRoom.setCode("607");
						enteredRoom.players.add(room.getFrom_whom());
						for (int j = 0; j < user_vc.size(); j++) {
							UserService user = (UserService) user_vc.elementAt(j);
							for(String player : enteredRoom.players) {
								if(user.userName.equals(player))
									user.writeOneObject(enteredRoom);
							}
							/*
							 * if (user.userName.equals(enteredRoom.players.get(i))) {
							 * System.out.println(enteredRoom.players.get(i));
							 * user.writeOneObject(enteredRoom); }
							 */
						}
						// writeAllObject(aroom); // 모두에게 입장을 알려라
						System.out.println("딴놈이 만든방 입장");
					}
				} else {
					System.out.println("해당 방이 없습니다..");
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
					} else {
						writeAllObject(chatmsg);
						continue;
					}

					// 프로토콜 체크
					if (chatmsg != null) {
						if (chatmsg.code.matches("100")) {
							userName = chatmsg.userName;
							userStatus = Status.WAITING;
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
								if (user.userStatus == Status.WAITING)
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
							System.out.println("Just entered here!!");
							allowEnteringRoom(room);
							sendRoomListToAll();
						} else if (room.getCode().matches("604")) { // 방 퇴장 (Play)
							System.out.println("Exit!!");
							enteredRoom = null;
							userStatus = Status.WAITING;
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
