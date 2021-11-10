
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.ScrollPaneConstants;

public class WaitingRoom extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public WaitingRoom view = null;
	public CreateNewRoom createNewRoom = null;

	private JLabel lblUserName;
	// private JTextArea textArea;
	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;

	JPanel panel;
	private JLabel lblMouseEvent;
	private Graphics gc;
	private int pen_size = 2; // minimum 2
	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	private Image panelImage = null;
	private Graphics2D gc2 = null;
	public String currentMode = "Paint";
	public boolean eraser = false;
	JPanel roomListJPanel;
	Vector roomList = new Vector();

	public int old_x = -1;
	public int old_y = -1;

	public int old_x_guest = -1;
	public int old_y_guest = -1;

	/**
	 * Create the frame.
	 * 
	 * @throws BadLocationException
	 */
	public WaitingRoom(String username, String ip_addr, String port_no) {
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

		AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;
		lblUserName.setText(username);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
		imgBtn.setBounds(902, 683, 50, 40);
		contentPane.add(imgBtn);

		JButton btnNewButton = new JButton("종 료");
		btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				sendObject(msg);
				System.exit(0);
			}
		});
		btnNewButton.setBounds(1096, 683, 69, 40);
		contentPane.add(btnNewButton);

		view = this;

		JButton makeNewRoom = new JButton("\uBC29 \uB9CC\uB4E4\uAE30"); // 방 만들기 버튼
		makeNewRoom.setFont(new Font("양재블럭체", Font.PLAIN, 15));
		makeNewRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showInternalMessageDialog(null, "hi");
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

			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			sendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();
			ImageSendAction action2 = new ImageSendAction();
			imgBtn.addActionListener(action2);

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
		}

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

					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.UserName, cm.data);
					} else if (obcm instanceof Room) {
						room = (Room) obcm;
					} else
						continue;

					if (cm != null) {
						switch (cm.code) {
						case "200": // chat message
							if (cm.UserName.equals(UserName))
								AppendTextR(msg); // 내 메세지는 우측에
							else
								AppendText(msg);
							break;
						case "300": // Image 첨부
							if (cm.UserName.equals(UserName))
								AppendTextR("[" + cm.UserName + "]");
							else
								AppendText("[" + cm.UserName + "]");
							// AppendImage(cm.img);
							break;
						case "500": // Mouse Event 수신
							// DoMouseEvent(cm);
							break;
						case "501": // Mouse Event End 수신
							// DoMouseEvent(cm);
							break;
						case "502": // Screen Clear
							// ScreenClear();
							break;
						}
					} else if (room != null) {
						switch (room.code) {
						case "600":
							// TODO: makeNewEntry();
							System.out.println("fuck");
							roomList.add(room);
							JPanel roomEntry = new JPanel();
							roomEntry.setBackground(Color.LIGHT_GRAY);
							roomEntry.setBounds(12, 21 + ((roomList.size()-1) * 151 + 5), 783, 151);
							roomListJPanel.add(roomEntry);
							roomEntry.setLayout(null);
							
							//JLabel label_room_no = new JLabel("\uBC29 \uBC88\uD638");
							JLabel label_room_no = new JLabel("방 번호");
							label_room_no.setBounds(12, 10, 57, 34);
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
							room_no.setText(String.valueOf(roomList.size()));
							roomEntry.add(room_no);
							
							JButton playBtn = new JButton("\uD50C\uB808\uC774");
							playBtn.setBounds(697, 16, 74, 28);
							roomEntry.add(playBtn);
							
							JButton observeBtn = new JButton("\uAD00\uC804");
							observeBtn.setBounds(697, 60, 74, 28);
							roomEntry.add(observeBtn);
							
							JLabel room_master = new JLabel();
							room_master.setBounds(73, 54, 320, 32);
							room_master.setText(room.masterUser);
							roomEntry.add(room_master);
							
							
							JLabel room_name = new JLabel();
							room_name.setBounds(73, 107, 320, 32);
							room_name.setText(room.room_name);
							roomEntry.add(room_name);
							
							JLabel room_player = new JLabel();
							room_player.setBackground(Color.WHITE);
							room_player.setBounds(514, 12, 116, 32);
							room_player.setText(String.valueOf(room.players.size()));
							roomEntry.add(room_player);
							
							JLabel room_observer = new JLabel();
							room_observer.setBackground(Color.WHITE);
							room_observer.setBounds(514, 56, 116, 32);
							if(room.observers != null) {
								room_observer.setText(String.valueOf(room.observers.size()));
							}
							else room_observer.setText("0");
							roomEntry.add(room_observer);
							
							JLabel room_status = new JLabel();
							room_status.setBackground(Color.WHITE);
							room_status.setBounds(514, 109, 116, 32);
							room_status.setText(room.status);
							roomEntry.add(room_status);
						}
					}

					/*
					 * if(room.code != null) { System.out.println("hello"); }
					 */
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

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
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지첨부");
				fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
				// frame.setVisible(true);
				// fd.setDirectory(".\\");
				fd.setVisible(true);
				// System.out.println(fd.getDirectory() + fd.getFile());
				if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
					ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					obcm.img = img;
					sendObject(obcm);
				}
			}
		}
	}

	ImageIcon icon1 = new ImageIcon("src/icon1.jpg");
	

	public void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// 화면에 출력
	public void AppendText(String msg) {
		// textArea.append(msg + "\n");
		// AppendIcon(icon1);
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		// textArea.setCaretPosition(len);
		// textArea.replaceSelection(msg + "\n");

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
	public void AppendTextR(String msg) {
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
	public void SendMessage(String msg) {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "200", msg);
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error");
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

	public void sendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			AppendText("sendObject Error");
		}
	}
}
