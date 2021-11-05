// JavaObjClient.java
// ObjecStream 사용하는 채팅 Client

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

class ImagePanel extends JPanel {
	private Image img;

	public ImagePanel(Image img) {
		this.img = img;
		setSize(new Dimension(img.getWidth(null), img.getHeight(null))); // null : max
		setPreferredSize(new Dimension(img.getWidth(null), img.getHeight(null))); 
		setLayout(null);
	}
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
}

public class Welcome extends JFrame {

	private static final long serialVersionUID = 1L;
	private ImagePanel contentPane;
	private JTextField txtUserName;
	private JTextField txtIpAddress;
	private JTextField txtPortNumber;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Welcome frame = new Welcome();
					frame.setTitle("Welcome to Halli Galli  !!");
					frame.setVisible(true);
					frame.pack();
					frame.setResizable(false);		// 사이즈 조정 불가
					//frame.setPreferredSize(new Dimension(840, 840/12*9));
					//frame.setSize(840, 840/12*9);
					frame.setLocationRelativeTo(null); // 자동으로 가운데에서 창을 open
					// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 종료 시 프로세스 종료
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Welcome() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1013, 740);
		contentPane = new ImagePanel(new ImageIcon("C:\\network_programming\\Halli-Galli\\JavaGameClient\\images\\welcome.jpg").getImage());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("User Name");
		lblNewLabel.setBounds(380, 351, 82, 33);
		contentPane.add(lblNewLabel);
		
		txtUserName = new JTextField();
		txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
		txtUserName.setBounds(469, 351, 116, 33);
		contentPane.add(txtUserName);
		txtUserName.setColumns(10);
		
		JLabel lblIpAddress = new JLabel("IP Address");
		lblIpAddress.setBounds(380, 412, 82, 33);
		contentPane.add(lblIpAddress);
		
		txtIpAddress = new JTextField();
		txtIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
		txtIpAddress.setText("127.0.0.1");
		txtIpAddress.setColumns(10);
		txtIpAddress.setBounds(469, 412, 116, 33);
		contentPane.add(txtIpAddress);
		
		JLabel lblPortNumber = new JLabel("Port Number");
		lblPortNumber.setBounds(380, 475, 82, 33);
		contentPane.add(lblPortNumber);
		
		txtPortNumber = new JTextField();
		txtPortNumber.setText("30000");
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setColumns(10);
		txtPortNumber.setBounds(469, 475, 116, 33);
		contentPane.add(txtPortNumber);
		
		JButton btnConnect = new JButton(new ImageIcon(((new ImageIcon(
	            "C:\\network_programming\\Halli-Galli\\JavaGameClient\\images\\Enter_btnUp.jpg").getImage()
	            .getScaledInstance(90, 45,
	                    java.awt.Image.SCALE_SMOOTH)))));
		btnConnect .setPressedIcon(new ImageIcon("C:\\network_programming\\Halli-Galli\\JavaGameClient\\images\\Enter_btnDown.jpg"));  // 눌린 버튼의 이미지
		
		btnConnect.setBounds(440, 535, 88, 38);
		contentPane.add(btnConnect);
		Myaction action = new Myaction();
		btnConnect.addActionListener(action);
		//txtUserName.addActionListener(action);
		//txtIpAddress.addActionListener(action);
		//txtPortNumber.addActionListener(action);
	}
	class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			String username = txtUserName.getText().trim();
			String ip_addr = txtIpAddress.getText().trim();
			String port_no = txtPortNumber.getText().trim();
			setVisible(false);
			WaitingRoom view = new WaitingRoom(username, ip_addr, port_no);
		}
	}
}


