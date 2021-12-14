
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class WaitingRoom extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	public String client_userName;
	private JButton btnSend;
	public Socket socket; // 연결소켓

	public ObjectInputStream ois;
	public ObjectOutputStream oos;

	public WaitingRoom view = null;
	public CreateNewRoom createNewRoom = null;
	public PlayRoom playRoom = null;
	public Room current_entered_room = null;

	private JLabel lblUserName;
	private JTextPane textArea;


	JPanel panel;
	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	public JPanel roomListJPanel;
	public List<Room> roomList_client = new ArrayList<>();
	ListenNetwork net;

	public WaitingRoom(String username, String ip_addr, String port_no) {
		System.out.println("WaitingRoom : " + username);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1193, 772);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(902, 178, 263, 433);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(true);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));

		txtInput = new JTextField();
		txtInput.setBounds(902, 633, 182, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		btnSend.setBounds(1096, 633, 69, 40);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(914, 87, 84, 40);
		contentPane.add(lblUserName);
		setVisible(true);

		appendText("User " + username + " connecting " + ip_addr + " " + port_no);
		client_userName = username;
		lblUserName.setText(username);

		JButton btnNewButton = new JButton("\uC885\uB8CC"); // 종료
		btnNewButton.setFont(new Font("굴림", Font.PLAIN, 12));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg.ChatMsgBuilder(Protocol.LOGOUT, client_userName).build();
				sendObject(msg);
				System.exit(0);
			}
		});
		btnNewButton.setBounds(1096, 683, 69, 40);
		contentPane.add(btnNewButton);

		view = this;

		JLabel makeNewRoom = new JLabel(new ImageIcon(((new ImageIcon("images/room.png").getImage().getScaledInstance(147, 90,java.awt.Image.SCALE_SMOOTH)))));
		makeNewRoom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				createNewRoom = new CreateNewRoom(username, view);
			}
		});
		makeNewRoom.setBounds(1018, 61, 147, 90);
		contentPane.add(makeNewRoom);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(12, 61, 850, 662);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JScrollPane roomList_scroller = new JScrollPane();
		roomList_scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		roomList_scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		roomList_scroller.setBounds(12, 10, 826, 578);
		panel_1.add(roomList_scroller);

		roomListJPanel = new JPanel();
		roomList_scroller.setViewportView(roomListJPanel);
		roomListJPanel.setLayout(null);

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			ChatMsg obcm = new ChatMsg.ChatMsgBuilder(Protocol.LOGIN, client_userName).data("Hello").build();
			sendObject(obcm);

			net = new ListenNetwork();
			net.start();
			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			appendText("connect error");
		}
	}

	public void showRoomList(List<Room> list) {
		roomListJPanel.removeAll();
		roomListJPanel.repaint();
		for (int i = 0; i < list.size(); i++) {
			// System.out.println("room name " + list.get(i).getRoom_name());
			JPanel roomEntry = new JPanel();
			Border blackline = BorderFactory.createLineBorder(Color.black);
			roomEntry.setBackground(Color.LIGHT_GRAY);
			roomEntry.setBorder(blackline);
			// roomEntry.setBounds(12, 21 + ((roomList_client.size() - 1) * 151 + 5), 783,
			// 151);
			roomEntry.setBounds(12, 21 + (i * 151), 783, 151);
			roomEntry.setLayout(null);

			// JLabel label_room_no = new JLabel("\uBC29 \uBC88\uD638");
			JLabel label_room_no = new JLabel("방 번호");
			label_room_no.setBounds(22, 10, 57, 34);
			roomEntry.add(label_room_no);

			JLabel label_room_name = new JLabel("\uBC29 \uC774\uB984");
			label_room_name.setBounds(12, 107, 49, 34);
			roomEntry.add(label_room_name);

			JLabel label_room_master = new JLabel("\uBC29\uC7A5");
			label_room_master.setBounds(12, 54, 37, 34);
			roomEntry.add(label_room_master);

			JLabel label_room_player = new JLabel("\uCC38\uC5EC \uC778\uC6D0");
			label_room_player.setBounds(445, 10, 57, 34);
			roomEntry.add(label_room_player);

			JLabel label_room_observer = new JLabel("\uAD00\uC804 \uC778\uC6D0");
			label_room_observer.setBounds(445, 54, 57, 34);
			roomEntry.add(label_room_observer);

			JLabel label_room_status = new JLabel("\uC0C1\uD0DC");
			label_room_status.setBounds(445, 107, 57, 34);
			roomEntry.add(label_room_status);

			JLabel room_no = new JLabel();
			room_no.setBounds(73, 10, 320, 32);
			room_no.setText(String.valueOf(i + 1));
			roomEntry.add(room_no);

			JLabel room_master = new JLabel();
			room_master.setBounds(73, 54, 320, 32);
			room_master.setText(list.get(i).getMasterUser());
			roomEntry.add(room_master);

			JLabel room_name = new JLabel();
			room_name.setBounds(73, 107, 320, 32);
			room_name.setText(list.get(i).getRoom_name());
			roomEntry.add(room_name);

			JLabel room_player = new JLabel();
			room_player.setBackground(Color.WHITE);
			room_player.setBounds(514, 12, 116, 32);
			room_player.setText(Integer.toString(list.get(i).players.size()));
			roomEntry.add(room_player);

			JLabel room_observer = new JLabel();
			room_observer.setBackground(Color.WHITE);
			room_observer.setBounds(514, 56, 116, 32);
			room_observer.setText(Integer.toString(list.get(i).observers.size()));
			roomEntry.add(room_observer);

			JLabel room_status = new JLabel();
			room_status.setBackground(Color.WHITE);
			room_status.setBounds(514, 109, 116, 32);
			room_status.setText(list.get(i).getStatus());
			roomEntry.add(room_status);

			// join btn
			JButton playBtn = new JButton("\uD50C\uB808\uC774");
			playBtn.setBounds(697, 16, 74, 28);
			playBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Room room = new Room(Protocol.ENTER_ROOM);
					room.setRoom_name(room_name.getText());
					room.setFrom_whom(client_userName);
					sendObject(room);
				}
			});
			roomEntry.add(playBtn);
			if(Integer.parseInt(room_player.getText()) == 4) {
				playBtn.setEnabled(false);
			}

			// observe btn
			JButton observeBtn = new JButton("\uAD00\uC804");
			observeBtn.setBounds(697, 60, 74, 28);
			observeBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Room room = new Room(Protocol.OBSERVE_ROOM);
					room.setFrom_whom(client_userName);
					room.setRoom_name(room_name.getText());
					sendObject(room);
				}
			});
			roomEntry.add(observeBtn);
			if(Integer.parseInt(room_observer.getText()) == 4) {
				observeBtn.setEnabled(false);
			}
			
			roomListJPanel.add(roomEntry);
			repaint();
		}
	}
	
	public void reload(InGame ingame) throws IOException {
		playRoom.players_inGame_info = ingame.players;
		playRoom.player1 = ingame.players.get(0);
		playRoom.player2 = ingame.players.get(1);
		playRoom.player3 = ingame.players.get(2);
		playRoom.player4 = ingame.players.get(3);
		playRoom.updateScreen();
	}

	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm = null;
					Room room = null;
					InGame ingame = null;

					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.userName, cm.data);
					} else if (obcm instanceof Room) {
						room = (Room) obcm;
					} else if (obcm instanceof InGame) {
						ingame = (InGame) obcm;
					} else
						continue;

					if (cm != null) {
						switch (cm.code) {
						case Protocol.MSG_WAITING: // chat message
							if (cm.userName.equals(client_userName))
								appendTextR(msg); // 내 메세지는 우측에
							else
								appendText(msg);
							break;
						case Protocol.MSG_ROOM: // chat message from room
							if (current_entered_room != null
									&& cm.room_dst.equals(current_entered_room.getRoom_name())) {
								if (cm.userName.equals(client_userName))
									playRoom.appendTextR(msg); // 내 메세지는 우측에
								else
									playRoom.appendText(msg);
							}
							break;
						}
					} else if (room != null) {
						if (room.getCode().matches(Protocol.ROOM_LIST)) {
							List<Room> list = room.getRoomList();
							roomList_client.clear();
							roomList_client = (ArrayList<Room>) room.getRoomList();
							showRoomList(list);
						} else if (room.getCode().matches(Protocol.ENTERING_MASTER)) {
							current_entered_room = room; // current_entered_room : 전달 받은 Room의 정보
							setVisible(false);
							playRoom = new PlayRoom(view, current_entered_room); // playRoom : client가 만든 새로운 룸
							playRoom.updatePlayers();
						} else if (room.getCode().matches(Protocol.EXIT_PLAYER)) {
							current_entered_room = room;
							System.out.println("Got someone's out");
							playRoom.updatePlayers();
						} else if (room.getCode().matches(Protocol.ENTER_ROOM)) {
							current_entered_room = null;
							current_entered_room = room;
							// if문 분기 다시 보기
							if (playRoom == null) {
								System.out.println("playroom null");
								setVisible(false);
								playRoom = new PlayRoom(view, current_entered_room);
								playRoom.updatePlayers();
							} else {
								System.out.println("updated");
								playRoom.players = room.players; // 이미 방에 들어가 있다면 플레이어 정보를 update
								playRoom.updatePlayers();
							}
						} else if (room.getCode().matches(Protocol.OBSERVE_ROOM)) {
							System.out.println("Observing allowed");
							current_entered_room = room;
							setVisible(false);
							playRoom = new PlayRoom(view, current_entered_room);
							playRoom.updatePlayers();
						}
					} else if (ingame != null) {
						if (ingame.getCode().matches(Protocol.RELOAD)) {
							playRoom.whose_turn = ingame.getWhose_turn();
							reload(ingame);
						}
						else if (ingame.getCode().matches(Protocol.GAME_START)) {
							playRoom.appendTextFromServer("[SERVER] Game starts!!");
							playRoom.removeStartButton();
							reload(ingame);
						} else if (ingame.getCode().matches(Protocol.GAME_RESTART)) {
							playRoom.appendTextFromServer("[SERVER] Game re-starts!!");
							playRoom.whose_turn = ingame.getWinner_index();
							playRoom.reStart();
							playRoom.removeStartButton();
							reload(ingame);
						} else if (ingame.getCode().matches(Protocol.CARD_CLICKED)) { // 카드 뒤집기
							playRoom.whose_turn = ingame.getWhose_turn();
							reload(ingame);
						} else if (ingame.getCode().matches(Protocol.BELL_HIT)) { // 종을 침
							System.out.println("GOTGOTGOT");
							playRoom.hitted();
							playRoom.whose_turn = ingame.getWhose_turn();
							if(!ingame.ranking.isEmpty())  {
								playRoom.ranking = ingame.ranking;
							}
							for(Player player : ingame.ranking) {
								if(player.getPlayer_name().equals(client_userName)) {
									playRoom.amIdead = true;
									break;
								}
							}
							reload(ingame);
						}  else if (ingame.getCode().matches(Protocol.PLAYER_DEAD)) { // 누군가가 죽음
							playRoom.hitted();
							playRoom.whose_turn = ingame.getWhose_turn();
							if(!ingame.ranking.isEmpty())  {
								playRoom.ranking = ingame.ranking;
								playRoom.appendTextFromServer("[SERVER] " + ingame.ranking.get(ingame.ranking.size()-1).getPlayer_name() + "님이 탈락했습니다!!");
							}
							for(Player player : ingame.ranking) {
								if(player.getPlayer_name().equals(client_userName)) {
									playRoom.amIdead = true;
									break;
								}
									
							}
							reload(ingame);
						} else if (ingame.getCode().matches(Protocol.GAME_OVER)) { // 게임 종료
							if(!ingame.ranking.isEmpty())  {
								playRoom.ranking = ingame.ranking;
								playRoom.appendTextFromServer("[SERVER] " + ingame.ranking.get(ingame.ranking.size()-2).getPlayer_name() + "님이 탈락했습니다!!");
								playRoom.appendTextFromServer("[SERVER] " + ingame.ranking.get(ingame.ranking.size()-1).getPlayer_name() + "님이 1등입니다!!");
								playRoom.winner_index = ingame.players.indexOf(ingame.ranking.get(ingame.ranking.size()-1));
								playRoom.appendTextFromServer("[SERVER] 게임을 종료합니다");
							}
							for(Player player : ingame.ranking) {
								if(player.getPlayer_name().equals(client_userName)) {
									playRoom.amIdead = true;
									break;
								}
									
							}
							reload(ingame);
							playRoom.setCurrentStatusEnd();
							// TODO : 게임 다시 시작 버튼
						}
					}
				} catch (IOException e) {
					appendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				msg = txtInput.getText();
				sendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	// 화면에 출력
	public synchronized void appendText(String msg) {
		// textArea.append(msg + "\n");
		// AppendIcon(icon1);
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", left);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 화면 우측에 출력
	public synchronized void appendTextR(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(right, Color.BLUE);
		doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", right);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Server에게 network으로 전송
	public synchronized void sendMessage(String msg) {
		try {
			ChatMsg obcm = new ChatMsg.ChatMsgBuilder(Protocol.MSG_WAITING, client_userName).data(msg).build();
			oos.writeObject(obcm);
			oos.reset();
		} catch (IOException e) {
			appendText("oos.writeObject() error");
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public synchronized void sendMessageFromRoom(String msg) {
		try {
			ChatMsg obcm = new ChatMsg.ChatMsgBuilder(Protocol.MSG_ROOM, client_userName).data(msg)
					.room_dst(current_entered_room.getRoom_name()).build();
			oos.writeObject(obcm);
			oos.reset();
		} catch (IOException e) {
			appendText("oos.writeObject() error");
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public synchronized void sendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
			oos.reset();
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			appendText("sendObject Error");
		}
	}
}
