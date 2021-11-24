import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

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
	private static final long serialVersionUID = 1L;
	public WaitingRoom mainview;
	public JTextPane textArea;
	private JPanel contentPane;
	private JTextField txtInput;
	private String userName;
	private JButton btnSend;
	private JLabel user_name;
	private JButton imgBtn;
	List<String> players;
	private JPanel gamePane;
	private JLabel palyer1_up;
	private JLabel palyer2_up;
	private JLabel palyer3_up;
	private JLabel palyer4_up;
	private JLabel palyer1_down;
	private JLabel palyer2_down;
	private JLabel palyer3_down;
	private JLabel palyer4_down;
	protected JLabel startBtnLabel;
	public Player player1;
	public Player player2;
	public Player player3;
	public Player player4;
	public Vector<Card> total_cards;

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

	public void cardGenerator() {
		total_cards = new Vector<>();

		for (int i = 0; i < 4; i++) {
			String tmp = "";
			if(i == 0) tmp = "PLUM";
			else if (i == 1) tmp = "PEAR";
			else if (i == 2) tmp = "BANANA";
			else if (i == 3) tmp = "BERRY";
			
			for (int j = 0; j < 4; i++) {
				Card card1 = new Card(String.format("%s%d", tmp, i+1), mainview.current_entered_room);
				total_cards.add(card1);
			}
			for (int j = 0; j < 2; i++) {
				Card card2 = new Card(String.format("%s%d", tmp, i+1), mainview.current_entered_room);
				total_cards.add(card2);
			}
			for (int j = 0; j < 2; i++) {
				Card card3 = new Card(String.format("%s%d", tmp, i+1), mainview.current_entered_room);
				total_cards.add(card3);
			}
			for (int j = 0; j < 1; i++) {
				Card card4 = new Card(String.format("%s%d", tmp, i+1), mainview.current_entered_room);
				total_cards.add(card4);
			}
			Card card5 = new Card(String.format("%s%d", tmp, i+1), mainview.current_entered_room);
			total_cards.add(card5);
		}
	}

	// 화면에 출력
	public void appendText(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();
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

	public PlayRoom(WaitingRoom view, Room current_entered_room) {
		mainview = view;
		// view.net.interrupt();
		players = current_entered_room.players; // getPlayers();
		setVisible(true);
		setResizable(false);
		setBounds(100, 100, 1232, 772);
		setLocationRelativeTo(null); // 자동으로 가운데에서 창을 open
		setResizable(false);
		setBounds(100, 100, 1193, 772);
		contentPane = new ImagePanel(
				new ImageIcon("C:\\network_programming\\Halli-Galli\\JavaGameClient\\images\\background3.jpg")
						.getImage().getScaledInstance(1193, 772, DEFAULT_CURSOR));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		repaint();

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

		user_name = new JLabel("Name");
		user_name.setBorder(new LineBorder(new Color(0, 0, 0)));
		user_name.setBackground(Color.WHITE);
		user_name.setFont(new Font("굴림", Font.BOLD, 14));
		user_name.setHorizontalAlignment(SwingConstants.CENTER);
		user_name.setBounds(1018, 37, 84, 40);
		contentPane.add(user_name);
		setVisible(true);

		userName = view.client_userName;
		user_name.setText(view.client_userName);

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
				room.setRoom_name(current_entered_room.getRoom_name());
				view.current_entered_room = null;
				view.sendObject(room);
				setVisible(false);
				// view.net.run();
				// view.setVisible(true);
			}
		});
		btnNewButton.setBounds(1096, 683, 69, 40);
		contentPane.add(btnNewButton);

		JLabel room_name = new JLabel((String) null);
		room_name.setHorizontalAlignment(SwingConstants.CENTER);
		room_name.setFont(new Font("굴림", Font.BOLD, 14));
		room_name.setBorder(new LineBorder(new Color(0, 0, 0)));
		room_name.setBackground(Color.WHITE);
		room_name.setBounds(902, 37, 84, 40);
		room_name.setText(current_entered_room.getRoom_name());
		contentPane.add(room_name);

		gamePane = new JPanel();
		gamePane.setBounds(12, 10, 878, 713);
		contentPane.add(gamePane);
		gamePane.setLayout(null);

		TextSendAction action = new TextSendAction();
		btnSend.addActionListener(action);
		txtInput.addActionListener(action);
		txtInput.requestFocus();

		try {
			BufferedImage myPicture = ImageIO.read(new File("images/bell.png"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			picLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("Yay you clicked me");
				}
			});
			picLabel.setBounds(BellConfig.BELLX, BellConfig.BELLY, BellConfig.BELL_WIDTH, BellConfig.BELL_HEIGHT);
			gamePane.add(picLabel);

			GameEngine2 engine = new GameEngine2();
			engine.start();
		} catch (NumberFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static BufferedImage rotate(BufferedImage image, double angle) {
		double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
		int w = image.getWidth(), h = image.getHeight();
		int neww = (int) Math.floor(w * cos + h * sin), newh = (int) Math.floor(h * cos + w * sin);
		GraphicsConfiguration gc = getDefaultConfiguration();
		BufferedImage result = gc.createCompatibleImage(neww, newh, Transparency.TRANSLUCENT);
		Graphics2D g = result.createGraphics();
		g.translate((neww - w) / 2, (newh - h) / 2);
		g.rotate(angle, w / 2, h / 2);
		g.drawRenderedImage(image, null);
		g.dispose();
		return result;
	}

	private static GraphicsConfiguration getDefaultConfiguration() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		return gd.getDefaultConfiguration();
	}

	public void updatePlayers() throws IOException {
		if (players.size() >= 1 && !players.get(0).equals(null)) {
			BufferedImage myPicture = ImageIO.read(new File("images/back2.png"));
			System.out.println("1 entered");
			BufferedImage player1_down_rotated = rotate(myPicture, UserConfig.P1_DEG);
			Image player1_down_res = player1_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
			palyer1_down = new JLabel(new ImageIcon(player1_down_res));
			palyer1_down.setBounds(UserConfig.P1_DOWNX, UserConfig.P1_DOWNY, CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT);
			gamePane.add(palyer1_down);

			myPicture = ImageIO.read(new File(CardConfig.BERRY3));
			BufferedImage player1_up_image = rotate(myPicture, UserConfig.P1_DEG);
			Image player1_up_result = player1_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT,
					Image.SCALE_DEFAULT);
			palyer1_up = new JLabel(new ImageIcon(player1_up_result));
			palyer1_up.setBounds(UserConfig.P1_UPX, UserConfig.P1_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);
			if (mainview.client_userName.equals(players.get(0))) {
				palyer1_up.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						System.out.println("1111111111111111111");
					}
				});
			}

			gamePane.add(palyer1_up);

			JLabel player1_name = new JLabel((String) null);
			player1_name.setHorizontalAlignment(SwingConstants.CENTER);
			player1_name.setFont(new Font("굴림", Font.BOLD, 14));
			player1_name.setBorder(new LineBorder(new Color(0, 0, 0)));
			player1_name.setText(players.get(0));
			player1_name.setBackground(Color.WHITE);
			player1_name.setBounds(12, 10, 84, 40);
			gamePane.add(player1_name);

			repaint();
		}

		if (players.size() >= 2 && !players.get(1).equals(null)) {
			BufferedImage myPicture = ImageIO.read(new File("images/back2.png"));
			System.out.println("2 entered");
			BufferedImage player2_down_rotated = rotate(myPicture, UserConfig.P2_DEG);
			Image player2_down_res = player2_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
			palyer2_down = new JLabel(new ImageIcon(player2_down_res));
			palyer2_down.setBounds(UserConfig.P2_DOWNX, UserConfig.P2_DOWNY, CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT);
			gamePane.add(palyer2_down);

			myPicture = ImageIO.read(new File(CardConfig.PEAR1));
			BufferedImage player2_up_image = rotate(myPicture, UserConfig.P2_DEG);
			Image player2_up_result = player2_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT,
					Image.SCALE_DEFAULT);
			palyer2_up = new JLabel(new ImageIcon(player2_up_result));
			palyer2_up.setBounds(UserConfig.P2_UPX, UserConfig.P2_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);
			if (mainview.client_userName.equals(players.get(1))) {
				palyer2_up.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						System.out.println("2222222222222");
					}
				});
			}
			gamePane.add(palyer2_up);

			JLabel player2_name = new JLabel((String) null);
			player2_name.setHorizontalAlignment(SwingConstants.CENTER);
			player2_name.setFont(new Font("굴림", Font.BOLD, 14));
			player2_name.setBorder(new LineBorder(new Color(0, 0, 0)));
			player2_name.setText(players.get(1));
			player2_name.setBackground(Color.WHITE);
			player2_name.setBounds(866 - 84, 10, 84, 40);
			gamePane.add(player2_name);
			repaint();
		}

		if (players.size() >= 3 && !players.get(2).equals(null)) {
			BufferedImage myPicture = ImageIO.read(new File("images/back2.png"));
			System.out.println("3 entered");
			BufferedImage player3_down_rotated = rotate(myPicture, UserConfig.P3_DEG);
			Image player3_down_res = player3_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
			palyer3_down = new JLabel(new ImageIcon(player3_down_res));
			palyer3_down.setBounds(UserConfig.P3_DOWNX, UserConfig.P3_DOWNY, CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT);
			gamePane.add(palyer3_down);

			myPicture = ImageIO.read(new File(CardConfig.BANANA5));
			BufferedImage player3_up_image = rotate(myPicture, UserConfig.P3_DEG);
			Image player3_up_result = player3_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT,
					Image.SCALE_DEFAULT);
			palyer3_up = new JLabel(new ImageIcon(player3_up_result));
			palyer3_up.setBounds(UserConfig.P3_UPX, UserConfig.P3_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);
			if (mainview.client_userName.equals(players.get(2))) {
				palyer3_up.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						System.out.println("33333333333333333");
					}
				});
			}
			gamePane.add(palyer3_up);

			JLabel player4_name = new JLabel((String) null);
			player4_name.setBounds(866 - 84, 703 - 40, 84, 40);
			player4_name.setHorizontalAlignment(SwingConstants.CENTER);
			player4_name.setFont(new Font("굴림", Font.BOLD, 14));
			player4_name.setText(players.get(2));
			player4_name.setBorder(new LineBorder(new Color(0, 0, 0)));
			player4_name.setBackground(Color.WHITE);
			gamePane.add(player4_name);
			repaint();
		}

		if (players.size() >= 4 && !players.get(3).equals(null)) {
			BufferedImage myPicture = ImageIO.read(new File("images/back2.png"));
			System.out.println("4 entered");
			BufferedImage player4_down_rotated = rotate(myPicture, UserConfig.P4_DEG);
			Image player4_down_res = player4_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
			palyer4_down = new JLabel(new ImageIcon(player4_down_res));
			palyer4_down.setBounds(UserConfig.P4_DOWNX, UserConfig.P4_DOWNY, CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT);
			gamePane.add(palyer4_down);

			myPicture = ImageIO.read(new File(CardConfig.PLUM1));
			BufferedImage player4_up_image = rotate(myPicture, UserConfig.P4_DEG);
			Image player4_up_result = player4_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT,
					Image.SCALE_DEFAULT);
			palyer4_up = new JLabel(new ImageIcon(player4_up_result));
			palyer4_up.setBounds(UserConfig.P4_UPX, UserConfig.P4_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);
			if (mainview.client_userName.equals(players.get(3))) {
				palyer4_up.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						System.out.println("4444444444444444444444");
					}
				});
			}
			gamePane.add(palyer4_up);

			JLabel player3_name = new JLabel((String) null);
			player3_name.setBounds(12, 703 - 40, 84, 40);
			player3_name.setHorizontalAlignment(SwingConstants.CENTER);
			player3_name.setFont(new Font("굴림", Font.BOLD, 14));
			player3_name.setText(players.get(3));
			player3_name.setBorder(new LineBorder(new Color(0, 0, 0)));
			player3_name.setBackground(Color.WHITE);
			gamePane.add(player3_name);

			if (mainview.client_userName.equals(mainview.current_entered_room.getMasterUser())) {
				System.out.println("PLAY BUTTON!!");
				BufferedImage startBtn = ImageIO.read(new File("images/start-button.png"));
				Image startBtnImage = startBtn.getScaledInstance(100, 80, Image.SCALE_DEFAULT);
				startBtnLabel = new JLabel(new ImageIcon(startBtnImage));
				startBtnLabel.setBounds(ButtonsConfig.STARTX, ButtonsConfig.STARTY, 100, 80);
				gamePane.add(startBtnLabel);
				startBtnLabel.setVisible(true);

				startBtnLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						System.out.println("Start Btn Clicked");
						InGame tmp = new InGame("700", mainview.client_userName, mainview.current_entered_room);
						mainview.sendObject(tmp);
					}
				});
			}
			repaint();
		}
	}

	class GameEngine2 extends Thread {
		public void run() {
			while (true) {
			}
		}
	}
}
