//JavaObjServer.java ObjectStream ��� ä�� Server

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // ��������
	private Socket client_socket; // accept() ���� ������ client ����
	private Vector UserVec = new Vector(); // ����� ����ڸ� ������ ����
	private ArrayList<Room> roomList_server = new ArrayList<>(); // ��ü ���� ����Ʈ
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����

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
				btnServerStart.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
				txtPortNumber.setEnabled(false); // ���̻� ��Ʈ��ȣ ������ �ϰ� ���´�
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// ���ο� ������ accept() �ϰ� user thread�� ���� �����Ѵ�.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					appendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
					appendText("���ο� ������ from " + client_socket);
					// User �� �ϳ��� Thread ����
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // ���ο� ������ �迭�� �߰�
					new_user.start(); // ���� ��ü�� ������ ����
					appendText("���� ������ �� " + UserVec.size());
				} catch (IOException e) {
					appendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public void appendText(String str) {
		// textArea.append("����ڷκ��� ���� �޼��� : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void appendMsg(ChatMsg msg) {
		// textArea.append("����ڷκ��� ���� object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.userName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void appendRoom(Room room) {
		// textArea.append("����ڷκ��� ���� object : " + str+"\n");
		textArea.append("code = " + room.getCode() + "\n");
		textArea.append("room_name = " + room.getRoom_name()+ "\n");
		textArea.append("password = " + room.getPassword() + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User �� �����Ǵ� Thread
	// Read One ���� ��� -> Write All
	class UserService extends Thread {

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String userName = "";
		public String userStatus;

		public UserService(Socket client_socket) {
			// �Ű������� �Ѿ�� �ڷ� ����
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
			appendText("���ο� ������ " + userName + " ����.");
			writeOne("Welcome to Java chat server\n");
			writeOne(userName + "�� ȯ���մϴ�.\n"); // ����� ����ڿ��� ���������� �˸�
			String msg = "[" + userName + "]���� ���� �Ͽ����ϴ�.\n";
			writeOthers(msg); // ���� user_vc�� ���� ������ user�� ���Ե��� �ʾҴ�.
		}

		public void logout() {
			String msg = "[" + userName + "]���� ���� �Ͽ����ϴ�.\n";
			UserVec.removeElement(this); // logout�� ���� ��ü�� ���Ϳ��� �����
			writeAll(msg); // ���� ������ �ٸ� User�鿡�� ����
			appendText("����� " + "[" + userName + "] ����. ���� ������ �� " + UserVec.size());
		}

		// ��� User�鿡�� ���. ������ UserService Thread�� writeOne() �� ȣ���Ѵ�.
		public void writeAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOne(str);
			}
		}

		public void writeOneObject(Object ob) {
			try {
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

		// ��� User�鿡�� Object�� ���. ä�� message�� image object�� ���� �� �ִ�
		public void writeAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOneObject(ob);
			}
		}

		// ���� ������ User�鿡�� ���. ������ UserService Thread�� writeOne() �� ȣ���Ѵ�.
		public void writeOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.userStatus == "O")
					user.writeOne(str);
			}
		}

		// UserService Thread�� ����ϴ� Client ���� 1:1 ����
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
				logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}
		
		public void sendRoooooooomListToAll() {
			try {
				oos.reset();
				Room room = new Room("999");
				room.setRoomList(roomList_server);
				writeAllObject(room);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void sendRoomListToAll() {
			if(roomList_server.size() == 0) {
				// Room room = new Room.RoomBuilder("603").build();
				Room room = new Room("603");
				writeAllObject(room);
			}
			else if (roomList_server.size() == 1) {
				Room original_room = roomList_server.get(0);
				// room.code = "601";
//				Room room = new Room.RoomBuilder("601")
//									.masterUser(original_room.masterUser)
//									.players_cnt(original_room.players_cnt)
//									.room_name(original_room.room_name)
//									.room_index(original_room.room_index)
//									.status(original_room.status)
//									.build();
				Room room = new Room("601");
				room.setMasterUser(original_room.getMasterUser());
				room.setPlayers_cnt(original_room.getPlayers_cnt());
				room.setRoom_name(original_room.getRoom_name());
				room.setRoom_index(original_room.getRoom_index());
				room.setStatus(original_room.getStatus());
				System.out.println("1!!!> "+room.getPlayers_cnt());
				writeAllObject(room);
			} else {
				for (int i = 0; i < roomList_server.size(); i++) {
					Room room = roomList_server.get(i);
					if (i == 0) {
						System.out.println("2!!!> "+room.getPlayers_cnt());
						room.setCode("601");
					} else {
						System.out.println("3!!!> "+room.getPlayers_cnt());
						room.setCode("602");
					}
					writeAllObject(room);
					System.out.println("4@@> "+room.getPlayers_cnt());
				}
			}
			for(Room room:roomList_server) {
				System.out.println("### Room Info ###");
				System.out.println("code> " + room.getCode());
				System.out.println("room_name> " + room.getRoom_name());
				System.out.println("masterUser> " + room.getMasterUser());
				System.out.println("players_cnt> " + room.getPlayers_cnt());
				System.out.println();
			}
		}

		// �ӼӸ� ����
		public void WritePrivate(String msg) {
			try {
				ChatMsg obcm = new ChatMsg.ChatMsgBuilder("200", "�ӼӸ�").data(msg).build();
				oos.writeObject(obcm);
			} catch (IOException e) {
				appendText("dos.writeObject() error");
				try {
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logout(); // �������� ���� ��ü�� ���Ϳ��� �����
			}
		}

		public void run() {
			while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg chatmsg = null;
					Room room = null;
					if (socket == null)
						break;
					// �޽��� ����
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						return;
					}

					// obcm�� Ÿ�� üũ
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

					// �������� üũ
					if (chatmsg != null) {
						if (chatmsg.code.matches("100")) {
							userName = chatmsg.userName;
							userStatus = "O"; // Online ����
							if (roomList_server.size() > 0)
								sendRoooooooomListToAll();
							login();
						} else if (chatmsg.code.matches("200")) {
							msg = String.format("[%s] %s", chatmsg.userName, chatmsg.data);
							appendText(msg); // server ȭ�鿡 ���
							String[] args = msg.split(" "); // �ܾ���� �и��Ѵ�.
							if (args.length == 1) { // Enter key �� ���� ��� Wakeup ó���� �Ѵ�.
								userStatus = "O";
							} else if (args[1].matches("/exit")) {
								logout();
								break;
							} else if (args[1].matches("/list")) {
								writeOne("User list\n");
								writeOne("Name\tStatus\n");
								writeOne("-----------------------------\n");
								for (int i = 0; i < user_vc.size(); i++) {
									UserService user = (UserService) user_vc.elementAt(i);
									writeOne(user.userName + "\t" + user.userStatus + "\n");
								}
								writeOne("-----------------------------\n");
							} else if (args[1].matches("/sleep")) {
								userStatus = "S";
							} else if (args[1].matches("/wakeup")) {
								userStatus = "O";
							} else if (args[1].matches("/to")) { // �ӼӸ�
								for (int i = 0; i < user_vc.size(); i++) {
									UserService user = (UserService) user_vc.elementAt(i);
									if (user.userName.matches(args[2]) && user.userName.matches("O")) {
										String msg2 = "";
										for (int j = 3; j < args.length; j++) {// ���� message �κ�
											msg2 += args[j];
											if (j < args.length - 1)
												msg2 += " ";
										}
										// /to ����.. [�ӼӸ�] [user1] Hello user2..
										user.WritePrivate(args[0] + " " + msg2 + "\n");
										// user.writeOne("[�ӼӸ�] " + args[0] + " " + msg2 + "\n");
										break;
									}
								}
							} else { // �Ϲ� ä�� �޽���
								userStatus = "O";
								writeAllObject(chatmsg);
							}
						} else if (chatmsg.code.matches("201")) {
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.userStatus == "O")
									user.writeOneObject(chatmsg);
							}
							// writeAllObject(chatmsg);
						}
						
						else if (chatmsg.code.matches("604")) {
							for (Room aroom : roomList_server) { // �� ������
								if (aroom.getRoom_name().equals(chatmsg.data)) {
									aroom.getPlayers().remove(chatmsg.userName);
									int tmp = aroom.getPlayers_cnt();
									aroom.setPlayers_cnt(tmp--);
									System.out.println("Exit room " + aroom.getPlayers_cnt());
									if(aroom.getPlayers_cnt() == 0)
										roomList_server.remove(aroom);
									break;
								}
							}
							sendRoooooooomListToAll();
						} else if (chatmsg.code.matches("606")) { // �� ����
							System.out.println("Just entered here!!");
							for (Room aroom : roomList_server) {
								if (aroom.getRoom_name().equals(chatmsg.room_dst)) {
									aroom.getPlayers().add(chatmsg.userName);
									int tmp = aroom.getPlayers_cnt();
									aroom.setPlayers_cnt(tmp--);
									// sending allowing protocol
									System.out.println("players> " + aroom.getPlayers_cnt());
									ChatMsg chattmp = new ChatMsg.ChatMsgBuilder("607", "SERVER")
														.room_dst(aroom.getRoom_name())
														.to_whom(chatmsg.userName)
														.build();
									// Room tmp2 = new Room.RoomBuilder("607").build();
									writeOneObject(chattmp);
									break;
								}
							}
							//sendRoomListToAll();
							sendRoooooooomListToAll();
						} else if (chatmsg.code.matches("400")) { // logout message ó��
							logout();
							break;
						}
					}
					if (room != null) {
						if (room.getCode().matches("600")) { // create new room
							roomList_server.add(room);
							ChatMsg tmp = new ChatMsg.ChatMsgBuilder("607", "SERVER")
													.room_dst(room.getRoom_name())
													.to_whom(room.getMasterUser())
													.build();
							writeOneObject(tmp);
							// sendRoomListToAll();
							sendRoooooooomListToAll();
						}
					}

				} catch (IOException e) {
					appendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						logout(); // �������� ���� ��ü�� ���Ϳ��� �����
						break;
					} catch (Exception ee) {
						break;
					} // catch�� ��
				} // �ٱ� catch����
			} // while
		} // run
	}

}
