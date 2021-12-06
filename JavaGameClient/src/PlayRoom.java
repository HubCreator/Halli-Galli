import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
	MyKeyListener my_key_listener;
	List<String> players;
	protected JPanel gamePane;
	private JLabel palyer1_up;
	private JLabel palyer2_up;
	private JLabel palyer3_up;
	private JLabel palyer4_up;
	private JLabel palyer1_down;
	private JLabel palyer2_down;
	private JLabel palyer3_down;
	private JLabel palyer4_down;
	protected JLabel startBtnLabel;
	public Player player1 = new Player();
	public Player player2 = new Player();
	public Player player3 = new Player();
	public Player player4 = new Player();
	public Vector<Card> myUpCards = new Vector<Card>();
	public Vector<Card> myDownCards = new Vector<Card>();
	// 게임이 시작돼야 players_inGame_info 생성
	public Vector<Player> players_inGame_info = new Vector<Player>();
	int whose_turn = 0;
	private JLabel total_up_cards_cnt;
	public boolean didOtherHIt = false;
	public boolean amIdead = false;
	public Vector<Player> ranking = new Vector<Player>();

//	public GameEngine2 engine;

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

	class MyKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();

			if (keyCode == 32 && didOtherHIt == false) { // space를 누르면 ...
				try {
					InGame tmp = new InGame("800", mainview.client_userName, mainview.current_entered_room);
					mainview.sendObject(tmp);
				} catch (Error e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	// 화면에 출력
	public synchronized void appendText(String msg) {
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
	
	public PlayRoom(WaitingRoom view, Room current_entered_room) {
		mainview = view;
		players = current_entered_room.players; // getPlayers();
		setVisible(true);
		setResizable(false);
		setBounds(100, 100, 1232, 772);
		// setLocationRelativeTo(null); // 자동으로 가운데에서 창을 open
		setResizable(false);
		setBounds(100, 100, 1193, 772);
		contentPane = new ImagePanel(
				new ImageIcon(BackgroundConfig.BACKGROUND_PANEL)
						.getImage().getScaledInstance(1193, 772, DEFAULT_CURSOR));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Component c = (Component) e.getSource(); // 마우스가 클릭된 컴포넌트
				c.setFocusable(true);
				c.requestFocus();
			}
		});
		
		my_key_listener = new MyKeyListener();
		contentPane.addKeyListener(my_key_listener);
		contentPane.setFocusable(true);
		contentPane.requestFocus();
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

		gamePane = new ImagePanel(
				new ImageIcon(BackgroundConfig.BACKGROUND)
				.getImage().getScaledInstance(878, 713 , DEFAULT_CURSOR));
		gamePane.setBounds(12, 10, 878, 713);
		contentPane.add(gamePane);
		repaint();
		gamePane.setLayout(null);
		
		TextSendAction action = new TextSendAction();
		btnSend.addActionListener(action);
		txtInput.addActionListener(action);
		// txtInput.requestFocus();

	}
	
	public void hitted() throws IOException {
		BufferedImage boom = ImageIO.read(new File("images/boom.png"));
		Image boom_res = boom.getScaledInstance(133, 88, Image.SCALE_DEFAULT);
		JLabel hitted_image = new JLabel(new ImageIcon(boom_res));
		hitted_image.setBounds(350, 150, 180, 150);
		gamePane.add(hitted_image);
		repaint();
		try {
			// TODO :  벨을 치더라도 소용 없게 해야 함
			didOtherHIt = true; // 1초 동안은 종을 못침
			Thread.sleep(1000);
			didOtherHIt = false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		gamePane.removeAll();
		if (players.size() >= 1 && !players.get(0).equals(null)) {
			BufferedImage myPicture = ImageIO.read(new File(CardConfig.BACK));
			player1.setPlayer_name(mainview.client_userName);
			player1.setCurrent_room(mainview.current_entered_room);
			BufferedImage player1_down_rotated = rotate(myPicture, UserConfig.P1_DEG);
			Image player1_down_res = player1_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
			palyer1_down = new JLabel(new ImageIcon(player1_down_res));
			palyer1_down.setBounds(UserConfig.P1_DOWNX, UserConfig.P1_DOWNY, CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT);
			gamePane.add(palyer1_down);

			BufferedImage player1_up_image;
			if (player1.front.isEmpty())
				player1_up_image = rotate(ImageIO.read(new File(CardConfig.BLANK)), UserConfig.P1_DEG);
			else {
				player1_up_image = rotate(
						ImageIO.read(new File(player1.front.get(player1.front.size() - 1).getCard_info())),
						UserConfig.P1_DEG);
			}
			Image player1_up_result = player1_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT,
					Image.SCALE_DEFAULT);
			palyer1_up = new JLabel(new ImageIcon(player1_up_result));
			palyer1_up.setBounds(UserConfig.P1_UPX, UserConfig.P1_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);

			gamePane.add(palyer1_up);

			JLabel player1_name = new JLabel((String) null);
			player1_name.setHorizontalAlignment(SwingConstants.CENTER);
			player1_name.setFont(new Font("굴림", Font.BOLD, 14));
			player1_name.setBorder(new LineBorder(new Color(0, 0, 0)));
			player1_name.setText(players.get(0));
			player1_name.setBackground(Color.WHITE);
			if(players.get(0).equals(mainview.client_userName))
				player1_name.setForeground(Color.BLUE);
			player1_name.setBounds(12, 10, 84, 40);
			gamePane.add(player1_name);
		}

		if (players.size() >= 2 && !players.get(1).equals(null)) {
			player2.setPlayer_name(mainview.client_userName);
			player2.setCurrent_room(mainview.current_entered_room);

			BufferedImage myPicture = ImageIO.read(new File(CardConfig.BACK));
			BufferedImage player2_down_rotated = rotate(myPicture, UserConfig.P2_DEG);
			Image player2_down_res = player2_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
			palyer2_down = new JLabel(new ImageIcon(player2_down_res));
			palyer2_down.setBounds(UserConfig.P2_DOWNX, UserConfig.P2_DOWNY, CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT);
			gamePane.add(palyer2_down);

			BufferedImage player2_up_image;
			if (player2.front.isEmpty())
				player2_up_image = rotate(ImageIO.read(new File(CardConfig.BLANK)), UserConfig.P2_DEG);
			else {
				player2_up_image = rotate(
						ImageIO.read(new File(player2.front.get(player2.front.size() - 1).getCard_info())),
						UserConfig.P2_DEG);
			}

			Image player2_up_result = player2_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT,
					Image.SCALE_DEFAULT);
			palyer2_up = new JLabel(new ImageIcon(player2_up_result));
			palyer2_up.setBounds(UserConfig.P2_UPX, UserConfig.P2_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);
			gamePane.add(palyer2_up);

			JLabel player2_name = new JLabel((String) null);
			player2_name.setHorizontalAlignment(SwingConstants.CENTER);
			player2_name.setFont(new Font("굴림", Font.BOLD, 14));
			player2_name.setBorder(new LineBorder(new Color(0, 0, 0)));
			player2_name.setText(players.get(1));
			player2_name.setBackground(Color.WHITE);
			if(players.get(1).equals(mainview.client_userName))
				player2_name.setForeground(Color.BLUE);
			player2_name.setBounds(866 - 84, 10, 84, 40);
			gamePane.add(player2_name);
		}

		if (players.size() >= 3 && !players.get(2).equals(null)) {
			BufferedImage myPicture = ImageIO.read(new File(CardConfig.BACK));
			player3.setPlayer_name(mainview.client_userName);
			player3.setCurrent_room(mainview.current_entered_room);

			BufferedImage player3_down_rotated = rotate(myPicture, UserConfig.P3_DEG);
			Image player3_down_res = player3_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
			palyer3_down = new JLabel(new ImageIcon(player3_down_res));
			palyer3_down.setBounds(UserConfig.P3_DOWNX, UserConfig.P3_DOWNY, CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT);
			gamePane.add(palyer3_down);

			BufferedImage player3_up_image;
			if (player3.front.isEmpty())
				player3_up_image = rotate(ImageIO.read(new File(CardConfig.BLANK)), UserConfig.P3_DEG);
			else {
				player3_up_image = rotate(
						ImageIO.read(new File(player3.front.get(player3.front.size() - 1).getCard_info())),
						UserConfig.P3_DEG);
			}

			Image player3_up_result = player3_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT,
					Image.SCALE_DEFAULT);
			palyer3_up = new JLabel(new ImageIcon(player3_up_result));
			palyer3_up.setBounds(UserConfig.P3_UPX, UserConfig.P3_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);
			gamePane.add(palyer3_up);

			JLabel player4_name = new JLabel((String) null);
			player4_name.setBounds(866 - 84, 703 - 40, 84, 40);
			player4_name.setHorizontalAlignment(SwingConstants.CENTER);
			player4_name.setFont(new Font("굴림", Font.BOLD, 14));
			player4_name.setText(players.get(2));
			player4_name.setBorder(new LineBorder(new Color(0, 0, 0)));
			player4_name.setBackground(Color.WHITE);
			if(players.get(2).equals(mainview.client_userName))
				player4_name.setForeground(Color.BLUE);
			gamePane.add(player4_name);
		}

		if (players.size() >= 4 && !players.get(3).equals(null)) {
			BufferedImage myPicture = ImageIO.read(new File(CardConfig.BACK));
			player4.setPlayer_name(mainview.client_userName);
			player4.setCurrent_room(mainview.current_entered_room);

			BufferedImage player4_down_rotated = rotate(myPicture, UserConfig.P4_DEG);
			Image player4_down_res = player4_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
			palyer4_down = new JLabel(new ImageIcon(player4_down_res));
			palyer4_down.setBounds(UserConfig.P4_DOWNX, UserConfig.P4_DOWNY, CardConfig.CARD_WIDTH,
					CardConfig.CARD_HEIGHT);
			gamePane.add(palyer4_down);

			BufferedImage player4_up_image;
			if (player4.front.isEmpty())
				player4_up_image = rotate(ImageIO.read(new File(CardConfig.BLANK)), UserConfig.P4_DEG);
			else
				player4_up_image = rotate(
						ImageIO.read(new File(player4.front.get(player4.front.size() - 1).getCard_info())),
						UserConfig.P4_DEG);
			Image player4_up_result = player4_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT,
					Image.SCALE_DEFAULT);
			palyer4_up = new JLabel(new ImageIcon(player4_up_result));
			palyer4_up.setBounds(UserConfig.P4_UPX, UserConfig.P4_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);
			gamePane.add(palyer4_up);

			JLabel player3_name = new JLabel((String) null);
			player3_name.setBounds(12, 703 - 40, 84, 40);
			player3_name.setHorizontalAlignment(SwingConstants.CENTER);
			player3_name.setFont(new Font("굴림", Font.BOLD, 14));
			player3_name.setText(players.get(3));
			player3_name.setBorder(new LineBorder(new Color(0, 0, 0)));
			player3_name.setBackground(Color.WHITE);
			if(players.get(3).equals(mainview.client_userName))
				player3_name.setForeground(Color.BLUE);
			gamePane.add(player3_name);

			if (mainview.client_userName.equals(mainview.current_entered_room.getMasterUser())) {
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
						// current_entered_room에는 플레이어 이름 정보 있음
//						tmp.players = mainview.current_entered_room.players;
//						tmp.observers = mainview.current_entered_room.observers;
						mainview.sendObject(tmp);
					}
				});
			}
		}
		repaint();
	}

	public void updateScreen() throws IOException {
		gamePane.removeAll();
		contentPane.remove(gamePane);
		if(amIdead) {
			contentPane.removeKeyListener(my_key_listener);
		}
		// 차례에 따른 배경 변화
		if(whose_turn % 4 == 0)
			gamePane = new ImagePanel(
					new ImageIcon(BackgroundConfig.BACKGROUND_P1)
					.getImage().getScaledInstance(878, 713 , DEFAULT_CURSOR));
		else if(whose_turn % 4 == 1)
			gamePane = new ImagePanel(
					new ImageIcon(BackgroundConfig.BACKGROUND_P2)
					.getImage().getScaledInstance(878, 713 , DEFAULT_CURSOR));
		else if(whose_turn % 4 == 2)
			gamePane = new ImagePanel(
					new ImageIcon(BackgroundConfig.BACKGROUND_P3)
					.getImage().getScaledInstance(878, 713 , DEFAULT_CURSOR));
		else
			gamePane = new ImagePanel(
					new ImageIcon(BackgroundConfig.BACKGROUND_P4)
					.getImage().getScaledInstance(878, 713 , DEFAULT_CURSOR));
		gamePane.setBounds(12, 10, 878, 713);
		contentPane.add(gamePane);
		
		
		if (players.size() >= 1 && !players.get(0).equals(null)) {
			if(player1.getIsDead() == true) {
				// player1 is dead
				JLabel player1_name = new JLabel((String) null);
				player1_name.setHorizontalAlignment(SwingConstants.CENTER);
				player1_name.setFont(new Font("굴림", Font.BOLD, 14));
				player1_name.setBorder(new LineBorder(new Color(0, 0, 0)));
				// player1_name.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
				player1_name.setText(players.get(0));
				player1_name.setBackground(Color.WHITE);
				if(players.get(0).equals(mainview.client_userName))
					player1_name.setForeground(Color.BLUE);
				player1_name.setBounds(12, 10, 84, 40);
				gamePane.add(player1_name);
				
				JLabel player1_dead = new JLabel(new ImageIcon(((new ImageIcon(player1.getRank()).getImage().getScaledInstance(168, 163,java.awt.Image.SCALE_SMOOTH)))));
				player1_dead.setBounds(113, 102, 168, 163);
				
				gamePane.add(player1_dead);
			} else {
				player1.setPlayer_name(mainview.client_userName);
				player1.setCurrent_room(mainview.current_entered_room);
				if (!player1.back.isEmpty()) {
					BufferedImage myPicture = ImageIO.read(new File(CardConfig.BACK));
					BufferedImage player1_down_rotated = rotate(myPicture, UserConfig.P1_DEG);
					Image player1_down_res = player1_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
							CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
					palyer1_down = new JLabel(new ImageIcon(player1_down_res));
					palyer1_down.setBounds(UserConfig.P1_DOWNX, UserConfig.P1_DOWNY, CardConfig.CARD_WIDTH,
							CardConfig.CARD_HEIGHT);
					gamePane.add(palyer1_down);
				}

				BufferedImage player1_up_image;
				
				if (player1.front.isEmpty())
					player1_up_image = rotate(ImageIO.read(new File(CardConfig.BLANK)), UserConfig.P1_DEG);
				else {
					player1_up_image = rotate(ImageIO.read(new File(player1.front.get(player1.front.size() - 1).getCard_info())), UserConfig.P1_DEG);
				}
				Image player1_up_result = player1_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
				palyer1_up = new JLabel(new ImageIcon(player1_up_result));
				palyer1_up.setBounds(UserConfig.P1_UPX, UserConfig.P1_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);

				if (mainview.client_userName.equals(players.get(0)) && whose_turn % 4 == 0) {
					palyer1_down.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e) {
							InGame result = new InGame("701", player1.getPlayer_name(), player1.getCurrent_room());
							mainview.sendObject(result);
						}
					});
				}

				gamePane.add(palyer1_up);

				JLabel player1_name = new JLabel((String) null);
				player1_name.setHorizontalAlignment(SwingConstants.CENTER);
				player1_name.setFont(new Font("굴림", Font.BOLD, 14));
				player1_name.setBorder(new LineBorder(new Color(0, 0, 0)));
				// player1_name.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
				player1_name.setText(players.get(0));
				player1_name.setBackground(Color.WHITE);
				if(players.get(0).equals(mainview.client_userName))
					player1_name.setForeground(Color.BLUE);
				player1_name.setBounds(12, 10, 84, 40);
				gamePane.add(player1_name);

				JLabel player1_down_cnt = new JLabel(Integer.toString(player1.back.size()));
				player1_down_cnt.setBounds(115, 235, 66, 27);
				gamePane.add(player1_down_cnt);
			}
		}

		if (players.size() >= 2 && !players.get(1).equals(null)) {
			if(player2.getIsDead() == true) {
				JLabel player2_name = new JLabel((String) null);
				player2_name.setHorizontalAlignment(SwingConstants.CENTER);
				player2_name.setFont(new Font("굴림", Font.BOLD, 14));
				player2_name.setBorder(new LineBorder(new Color(0, 0, 0)));
				player2_name.setText(players.get(1));
				player2_name.setBackground(Color.WHITE);
				if(players.get(1).equals(mainview.client_userName))
					player2_name.setForeground(Color.BLUE);
				player2_name.setBounds(866 - 84, 10, 84, 40);
				gamePane.add(player2_name);
				
				// player2 is dead
				JLabel player2_dead = new JLabel(new ImageIcon(((new ImageIcon(player2.getRank()).getImage().getScaledInstance(168, 163,java.awt.Image.SCALE_SMOOTH)))));
				player2_dead.setBounds(593, 102, 168, 163);
				gamePane.add(player2_dead);
				
			} else {
				player2.setPlayer_name(mainview.client_userName);
				player2.setCurrent_room(mainview.current_entered_room);
				if (!player2.back.isEmpty()) {
					BufferedImage myPicture = ImageIO.read(new File(CardConfig.BACK));
					BufferedImage player2_down_rotated = rotate(myPicture, UserConfig.P2_DEG);
					Image player2_down_res = player2_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
							CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
					palyer2_down = new JLabel(new ImageIcon(player2_down_res));
					palyer2_down.setBounds(UserConfig.P2_DOWNX, UserConfig.P2_DOWNY, CardConfig.CARD_WIDTH,
							CardConfig.CARD_HEIGHT);
					gamePane.add(palyer2_down);
				}

				BufferedImage player2_up_image;
				if (player2.front.isEmpty())
					player2_up_image = rotate(ImageIO.read(new File(CardConfig.BLANK)), UserConfig.P2_DEG);
				else {
					player2_up_image = rotate(ImageIO.read(new File(player2.front.get(player2.front.size() - 1).getCard_info())), UserConfig.P2_DEG);
				}

				Image player2_up_result = player2_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
				palyer2_up = new JLabel(new ImageIcon(player2_up_result));
				palyer2_up.setBounds(UserConfig.P2_UPX, UserConfig.P2_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);
				if (mainview.client_userName.equals(players.get(1)) && whose_turn % 4 == 1) {
					palyer2_down.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e) {
							InGame tmp = new InGame("701", mainview.client_userName, mainview.current_entered_room);
							mainview.sendObject(tmp);
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
				if(players.get(1).equals(mainview.client_userName))
					player2_name.setForeground(Color.BLUE);
				player2_name.setBounds(866 - 84, 10, 84, 40);
				gamePane.add(player2_name);

				JLabel player2_down_cnt = new JLabel(Integer.toString(player2.back.size()));
				player2_down_cnt.setBounds(755, 235, 66, 27);
				gamePane.add(player2_down_cnt);
			}
		}

		if (players.size() >= 3 && !players.get(2).equals(null)) {
			if(player3.getIsDead() == true) {
				JLabel player4_name = new JLabel((String) null);
				player4_name.setBounds(866 - 84, 703 - 40, 84, 40);
				player4_name.setHorizontalAlignment(SwingConstants.CENTER);
				player4_name.setFont(new Font("굴림", Font.BOLD, 14));
				player4_name.setText(players.get(2));
				player4_name.setBorder(new LineBorder(new Color(0, 0, 0)));
				player4_name.setBackground(Color.WHITE);
				if(players.get(2).equals(mainview.client_userName))
					player4_name.setForeground(Color.BLUE);
				gamePane.add(player4_name);
				
				// player3 is dead
				JLabel player3_dead = new JLabel(new ImageIcon(((new ImageIcon(player3.getRank()).getImage().getScaledInstance(168, 163,java.awt.Image.SCALE_SMOOTH)))));
				player3_dead.setBounds(593, 435, 168, 163);
				gamePane.add(player3_dead);
				
			} else {
				player3.setPlayer_name(mainview.client_userName);
				player3.setCurrent_room(mainview.current_entered_room);
				if (!player3.back.isEmpty()) {
					BufferedImage myPicture = ImageIO.read(new File(CardConfig.BACK));
					BufferedImage player3_down_rotated = rotate(myPicture, UserConfig.P3_DEG);
					Image player3_down_res = player3_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
							CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
					palyer3_down = new JLabel(new ImageIcon(player3_down_res));
					palyer3_down.setBounds(UserConfig.P3_DOWNX, UserConfig.P3_DOWNY, CardConfig.CARD_WIDTH,
							CardConfig.CARD_HEIGHT);
					gamePane.add(palyer3_down);
				}

				BufferedImage player3_up_image;
				if (player3.front.isEmpty())
					player3_up_image = rotate(ImageIO.read(new File(CardConfig.BLANK)), UserConfig.P3_DEG);
				else {
					player3_up_image = rotate(
							ImageIO.read(new File(player3.front.get(player3.front.size() - 1).getCard_info())),
							UserConfig.P3_DEG);
				}

				Image player3_up_result = player3_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT,
						Image.SCALE_DEFAULT);
				palyer3_up = new JLabel(new ImageIcon(player3_up_result));
				palyer3_up.setBounds(UserConfig.P3_UPX, UserConfig.P3_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);
				if (mainview.client_userName.equals(players.get(2)) && whose_turn % 4 == 2) {
					palyer3_down.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e) {
							InGame tmp = new InGame("701", mainview.client_userName, mainview.current_entered_room);
							mainview.sendObject(tmp);
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
				if(players.get(2).equals(mainview.client_userName))
					player4_name.setForeground(Color.BLUE);
				gamePane.add(player4_name);

				JLabel player3_down_cnt = new JLabel(Integer.toString(player3.back.size()));
				player3_down_cnt.setBounds(755, 480, 66, 27);
				gamePane.add(player3_down_cnt);
			}
		}

		if (players.size() >= 4 && !players.get(3).equals(null)) {
			if(player4.getIsDead() == true) {
				JLabel player3_name = new JLabel((String) null);
				player3_name.setBounds(12, 703 - 40, 84, 40);
				player3_name.setHorizontalAlignment(SwingConstants.CENTER);
				player3_name.setFont(new Font("굴림", Font.BOLD, 14));
				player3_name.setText(players.get(3));
				player3_name.setBorder(new LineBorder(new Color(0, 0, 0)));
				player3_name.setBackground(Color.WHITE);
				if(players.get(3).equals(mainview.client_userName))
					player3_name.setForeground(Color.BLUE);
				gamePane.add(player3_name);
				
				// player4 is dead
				JLabel player4_dead = new JLabel(new ImageIcon(((new ImageIcon(player4.getRank()).getImage().getScaledInstance(168, 163,java.awt.Image.SCALE_SMOOTH)))));
				player4_dead.setBounds(113, 435, 168, 163);
				gamePane.add(player4_dead);
			} else {
				player4.setPlayer_name(mainview.client_userName);
				player4.setCurrent_room(mainview.current_entered_room);
				if (!player4.back.isEmpty()) {
					BufferedImage myPicture = ImageIO.read(new File(CardConfig.BACK));
					BufferedImage player4_down_rotated = rotate(myPicture, UserConfig.P4_DEG);
					Image player4_down_res = player4_down_rotated.getScaledInstance(CardConfig.CARD_WIDTH,
							CardConfig.CARD_HEIGHT, Image.SCALE_DEFAULT);
					palyer4_down = new JLabel(new ImageIcon(player4_down_res));
					palyer4_down.setBounds(UserConfig.P4_DOWNX, UserConfig.P4_DOWNY, CardConfig.CARD_WIDTH,
							CardConfig.CARD_HEIGHT);
					gamePane.add(palyer4_down);
				}

				BufferedImage player4_up_image;
				if (player4.front.isEmpty())
					player4_up_image = rotate(ImageIO.read(new File(CardConfig.BLANK)), UserConfig.P4_DEG);
				else
					player4_up_image = rotate(
							ImageIO.read(new File(player4.front.get(player4.front.size() - 1).getCard_info())),
							UserConfig.P4_DEG);
				Image player4_up_result = player4_up_image.getScaledInstance(CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT,
						Image.SCALE_DEFAULT);
				palyer4_up = new JLabel(new ImageIcon(player4_up_result));
				palyer4_up.setBounds(UserConfig.P4_UPX, UserConfig.P4_UPY, CardConfig.CARD_WIDTH, CardConfig.CARD_HEIGHT);
				if (mainview.client_userName.equals(players.get(3)) && whose_turn % 4 == 3) {
					palyer4_down.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e) {
							InGame tmp = new InGame("701", mainview.client_userName, mainview.current_entered_room);
							mainview.sendObject(tmp);
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
				if(players.get(3).equals(mainview.client_userName))
					player3_name.setForeground(Color.BLUE);
				gamePane.add(player3_name);
				
				JLabel player4_down_cnt = new JLabel(Integer.toString(player4.back.size()));
				player4_down_cnt.setBounds(115, 480, 66, 27);
				gamePane.add(player4_down_cnt);


				// master user에게 start버튼 show
				if (mainview.client_userName.equals(mainview.current_entered_room.getMasterUser()) && players_inGame_info == null) {
					BufferedImage startBtn = ImageIO.read(new File("images/start-button.png"));
					Image startBtnImage = startBtn.getScaledInstance(100, 80, Image.SCALE_DEFAULT);
					startBtnLabel = new JLabel(new ImageIcon(startBtnImage));
					startBtnLabel.setBounds(ButtonsConfig.STARTX, ButtonsConfig.STARTY, 100, 80);
					gamePane.add(startBtnLabel);
					startBtnLabel.setVisible(true);
				}

				BufferedImage bellImage = ImageIO.read(new File("images/bell.png"));
				JLabel picLabel = new JLabel(new ImageIcon(bellImage));
				int totalCnt = 0;
				for (int i = 0; i < players_inGame_info.size(); i++) {
					totalCnt += players_inGame_info.get(i).front.size();
				}
				
				if(totalCnt != 0) {
					total_up_cards_cnt = new JLabel(String.format("Total : %d !!", totalCnt));
					total_up_cards_cnt.setBounds(425, 279, 60, 30);
					gamePane.add(total_up_cards_cnt);
				}
				
				picLabel.setBounds(BellConfig.BELLX, BellConfig.BELLY, BellConfig.BELL_WIDTH, BellConfig.BELL_HEIGHT);
				gamePane.add(picLabel);
			}
			
			repaint();
		}
	}
}
