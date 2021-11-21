import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

public class PlayRoom extends JFrame {
	public WaitingRoom mainview;
	public JTextPane textArea;
	private JPanel contentPane;
	private JTextField txtInput;
	private String userName;
	private JButton btnSend;
	private JLabel lblUserName;
	private JButton imgBtn;
	private ImagePanel contentPane2;
	private ImagePanel panel;
	private JPanel gamePane;
	private JPanel player1;
	private JPanel player2;
	private JPanel player3;
	private JPanel player4;
	private JButton bell;

	public enum CurrentStatus {
		WAITING, PLAYING, OBSERVING
	};

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				mainview.sendMessageFromRoom(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	// 화면에 출력
	public void appendText(String msg) {
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
	public void appendTextR(String msg) {
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

	public PlayRoom(WaitingRoom view, String room_name) {
		mainview = view;
		setVisible(true);
		setResizable(false);
		setBounds(100, 100, 1232, 772);
		setLocationRelativeTo(null); // 자동으로 가운데에서 창을 open

		setResizable(false);
		setBounds(100, 100, 1193, 772);
		// contentPane = new JPanel();
		contentPane = new ImagePanel(
				new ImageIcon("C:\\network_programming\\Halli-Galli\\JavaGameClient\\images\\background3.jpg")
						.getImage().getScaledInstance(1193, 772, DEFAULT_CURSOR));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.repaint();

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(902, 87, 263, 524);
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
		lblUserName.setBounds(1018, 37, 84, 40);
		contentPane.add(lblUserName);
		setVisible(true);

		userName = view.client_userName;
		lblUserName.setText(view.client_userName);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
		imgBtn.setBounds(902, 683, 50, 40);
		contentPane.add(imgBtn);

		JButton btnNewButton = new JButton("\uB098\uAC00\uAE30");
		btnNewButton.setFont(new Font("굴림", Font.PLAIN, 11));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// exit room
				Room room = new Room("604");
				room.setFrom_whom(userName);
				room.setRoom_name(room_name);
				view.current_entered_room = null;
				view.sendObject(room);
				setVisible(false);
				view.setVisible(true);
			}
		});
		btnNewButton.setBounds(1096, 683, 69, 40);
		contentPane.add(btnNewButton);

		JLabel lblUserName_1 = new JLabel((String) null);
		lblUserName_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName_1.setFont(new Font("굴림", Font.BOLD, 14));
		lblUserName_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName_1.setBackground(Color.WHITE);
		lblUserName_1.setBounds(902, 37, 84, 40);
		lblUserName_1.setText(room_name);
		// lblUserName_1.setText("Room : " + room_name);
		contentPane.add(lblUserName_1);

		/*
		 * bell = new JButton(new ImageIcon(((new ImageIcon(
		 * "C:\\network_programming\\Halli-Galli\\JavaGameClient\\images\\bell.png").
		 * getImage() )))); bell.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent e) { System.out.println("bell ring!!"); } });
		 * bell.setBounds(326, 265, 232, 195); contentPane.add(bell);
		 */

		try {
			BufferedImage myPicture = ImageIO
					.read(new File("C:\\network_programming\\Halli-Galli\\JavaGameClient\\images\\bell.png"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			picLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("Yay you clicked me");
				}
			});
			picLabel.setBounds(326, 265, 232, 195);
			contentPane.add(picLabel);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		gamePane = new JPanel();
		gamePane.setBounds(12, 10, 878, 713);
		contentPane.add(gamePane);
		gamePane.setLayout(null);

		player1 = new JPanel();
		player1.setBounds(12, 10, 420, 346);
		gamePane.add(player1);
		player1.setLayout(null);

		player2 = new JPanel();
		player2.setBounds(430, 10, 436, 346);
		gamePane.add(player2);
		player2.setLayout(null);

		player3 = new JPanel();
		player3.setBounds(12, 357, 420, 346);
		gamePane.add(player3);
		player3.setLayout(null);

		player4 = new JPanel();
		player4.setBounds(430, 357, 434, 346);
		gamePane.add(player4);
		player4.setLayout(null);

//		panel = new ImagePanel(new ImageIcon("C:\\network_programming\\Halli-Galli\\JavaGameClient\\images\\background2.jpg").getImage());
//		panel.setBounds(12, 60, 878, 663);
//		contentPane.add(panel);
//		panel.setLayout(null);

		TextSendAction action = new TextSendAction();
		btnSend.addActionListener(action);
		txtInput.addActionListener(action);
		txtInput.requestFocus();
		repaint();
	}
}
