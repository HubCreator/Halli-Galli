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

import javax.imageio.ImageIO;
import javax.swing.Icon;
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
	private JLabel user_name;
	private JButton imgBtn;
	private ImagePanel contentPane2;
	private ImagePanel panel;
	private JPanel gamePane;
	private JPanel player1;
	private JPanel player2;
	private JPanel player4;
	private JPanel player3;
	List<String> players;
	private JLabel palyer1_card;
	private JLabel palyer2_card;
	private JLabel palyer3_card;
	private JLabel palyer4_card;
	private JLabel palyer1_deck;
	private JLabel palyer2_deck;
	private JLabel palyer3_deck;
	private JLabel palyer4_deck;

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
				view.setVisible(true);
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
			gamePane.add(picLabel);
			
			myPicture = ImageIO
					.read(new File("C:\\network_programming\\Halli-Galli\\JavaGameClient\\images\\back1.png"));
			Image image = myPicture.getScaledInstance(166, 119, Image.SCALE_DEFAULT);
					
			palyer1_deck = new JLabel(new ImageIcon(image));
			palyer1_deck.setText("player1_deck");
			palyer1_deck.setBounds(27, 60, 166, 119);
			gamePane.add(palyer1_deck);
			
			palyer2_deck = new JLabel((Icon) null);
			palyer2_deck.setText("palyer2_deck");
			palyer2_deck.setBounds(678, 60, 166, 119);
			gamePane.add(palyer2_deck);
			
			palyer3_deck = new JLabel((Icon) null);
			palyer3_deck.setText("palyer3_deck");
			palyer3_deck.setBounds(678, 535, 166, 119);
			gamePane.add(palyer3_deck);
			
			palyer4_deck = new JLabel((Icon) null);
			palyer4_deck.setText("palyer4_deck");
			palyer4_deck.setBounds(27, 535, 166, 119);
			gamePane.add(palyer4_deck);
			
			myPicture = ImageIO
					.read(new File("C:\\network_programming\\Halli-Galli\\JavaGameClient\\images\\banana1.jpg"));
			BufferedImage result = rotate(myPicture, 45.0);
			Image image1 = result.getScaledInstance(166, 119, Image.SCALE_DEFAULT);
			palyer1_card = new JLabel(new ImageIcon(image1));
			palyer1_card.setText("palyer1_card");
			palyer1_card.setBounds(175, 189, 166, 119);
			gamePane.add(palyer1_card);
			
			palyer2_card = new JLabel((Icon) null);
			palyer2_card.setText("palyer2_card");
			palyer2_card.setBounds(514, 189, 166, 119);
			gamePane.add(palyer2_card);
			
			palyer3_card = new JLabel((Icon) null);
			palyer3_card.setText("palyer3_card");
			palyer3_card.setBounds(514, 403, 166, 119);
			gamePane.add(palyer3_card);
			
			palyer4_card = new JLabel((Icon) null);
			palyer4_card.setText("palyer4_card");
			palyer4_card.setBounds(193, 403, 166, 119);
			gamePane.add(palyer4_card);
			repaint();
		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		repaint();
		GameEngine engine = new GameEngine();
		engine.start();
	}
	
	public static BufferedImage rotate(BufferedImage image, double angle) {
	    double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
	    int w = image.getWidth(), h = image.getHeight();
	    int neww = (int)Math.floor(w*cos+h*sin), newh = (int) Math.floor(h * cos + w * sin);
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
	
	class GameEngine extends Thread {
		public void run() {
			while (true) {
				if (players.size() >= 1 && !players.get(0).equals(null)) {
					player1 = new JPanel();
					player1.setBounds(12, 10, 420, 346);
					gamePane.add(player1);
					player1.setLayout(null);
					
					JLabel player1_name = new JLabel((String) null);
					player1_name.setHorizontalAlignment(SwingConstants.CENTER);
					player1_name.setFont(new Font("굴림", Font.BOLD, 14));
					player1_name.setBorder(new LineBorder(new Color(0, 0, 0)));
					player1_name.setText(players.get(0));
					player1_name.setBackground(Color.WHITE);
					player1_name.setBounds(12, 10, 84, 40);
					player1.add(player1_name);
					repaint();
				}

				if (players.size() >= 2 && !players.get(1).equals(null)) {
					player2 = new JPanel();
					player2.setBounds(430, 10, 436, 346);
					gamePane.add(player2);
					player2.setLayout(null);
					
					JLabel player2_name = new JLabel((String) null);
					player2_name.setHorizontalAlignment(SwingConstants.CENTER);
					player2_name.setFont(new Font("굴림", Font.BOLD, 14));
					player2_name.setBorder(new LineBorder(new Color(0, 0, 0)));
					player2_name.setText(players.get(1));
					player2_name.setBackground(Color.WHITE);
					player2_name.setBounds(340, 10, 84, 40);
					player2.add(player2_name);
					repaint();
				}

				if (players.size() >= 3 && !players.get(2).equals(null)) {
					player4 = new JPanel();
					player4.setBounds(430, 357, 434, 346);
					gamePane.add(player4);
					player4.setLayout(null);
					
					JLabel player4_name = new JLabel((String) null);
					player4_name.setBounds(338, 296, 84, 40);
					player4_name.setHorizontalAlignment(SwingConstants.CENTER);
					player4_name.setFont(new Font("굴림", Font.BOLD, 14));
					player4_name.setText(players.get(2));
					player4_name.setBorder(new LineBorder(new Color(0, 0, 0)));
					player4_name.setBackground(Color.WHITE);
					player4.add(player4_name);
					repaint();
				}

				if (players.size() >= 4 && !players.get(3).equals(null)) {
					player3 = new JPanel();
					player3.setBounds(12, 357, 420, 346);
					gamePane.add(player3);
					player3.setLayout(null);
					
					JLabel player3_name = new JLabel((String) null);
					player3_name.setBounds(12, 296, 84, 40);
					player3_name.setHorizontalAlignment(SwingConstants.CENTER);
					player3_name.setFont(new Font("굴림", Font.BOLD, 14));
					player3_name.setText(players.get(3));
					player3_name.setBorder(new LineBorder(new Color(0, 0, 0)));
					player3_name.setBackground(Color.WHITE);
					player3.add(player3_name);
					repaint();
				}
			}
		}
	}
}
