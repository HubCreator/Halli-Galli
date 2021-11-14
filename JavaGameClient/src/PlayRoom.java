import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class PlayRoom extends JFrame {
	public WaitingRoom mainview;
	private JTextPane textArea;
	private JPanel contentPane;
	private JTextField txtInput;
	private String userName;
	private JButton btnSend;
	private JLabel lblUserName;
	private JButton imgBtn;

	public PlayRoom(WaitingRoom view, String room_name) {
		mainview = view;
		setVisible(true);
		setResizable(false);
		setBounds(100, 100, 1193, 772);
		setLocationRelativeTo(null); // 자동으로 가운데에서 창을 open
		
		setResizable(false);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

		//view.AppendText("User " + view.UserName + " connecting " + ip_addr + " " + port_no);
		userName = view.userName;
		lblUserName.setText(view.userName);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
		imgBtn.setBounds(902, 683, 50, 40);
		contentPane.add(imgBtn);

		JButton btnNewButton = new JButton("\uB098\uAC00\uAE30");
		btnNewButton.setFont(new Font("굴림", Font.PLAIN, 11));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// exit room
				ChatMsg msg = new ChatMsg.ChatMsgBuilder("604", userName)
										.data(room_name)
										.build();
										
				view.sendObject(msg);
				System.exit(0);
			}
		});
		btnNewButton.setBounds(1096, 683, 69, 40);
		contentPane.add(btnNewButton);
		
		JLabel lblUserName_1 = new JLabel((String) null);
		lblUserName_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName_1.setFont(new Font("굴림", Font.BOLD, 14));
		lblUserName_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName_1.setBackground(Color.WHITE);
		lblUserName_1.setBounds(35, 87, 84, 40);
		lblUserName_1.setText(room_name);
		contentPane.add(lblUserName_1);
		repaint();
	}
	
	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { Welcome frame = new Welcome();
	 * frame.setTitle("Play !!"); frame.setVisible(true); frame.pack();
	 * frame.setResizable(false); // 사이즈 조정 불가 //frame.setPreferredSize(new
	 * Dimension(840, 840/12*9)); //frame.setSize(840, 840/12*9);
	 * frame.setLocationRelativeTo(null); // 자동으로 가운데에서 창을 open //
	 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 종료 시 프로세스 종료 } catch
	 * (Exception e) { e.printStackTrace(); } } }); }
	 */
}
