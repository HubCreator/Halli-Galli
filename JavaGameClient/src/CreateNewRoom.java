
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

public class CreateNewRoom extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public WaitingRoom mainview;
	
	JPanel panel;
	private JTextField textField;
	private JPasswordField passwordField;
	public String masterUser;
	public Room room = null;
	public String getRoom() {
		return room.toString();
	}

	/**
	 * Create the frame.
	 * 
	 * @throws BadLocationException
	 */
	public CreateNewRoom(String username, WaitingRoom view) {
		mainview = view;
		this.masterUser = username;
		setVisible(true);
//		setResizable(false);
		setPreferredSize(new Dimension(400, 400/12*9));
		setSize(400, 400/12*9);
		setLocationRelativeTo(null); // 자동으로 가운데에서 창을 open
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 종료 시 프로세스 종료
		//setBounds(100, 100, 1193, 772);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("\uBC29 \uB9CC\uB4E4\uAE30");
		lblNewLabel.setFont(new Font("굴림", Font.BOLD, 20));
		lblNewLabel.setBounds(12, 10, 99, 39);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("\uBC29 \uC774\uB984");
		lblNewLabel_1.setFont(new Font("굴림", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(12, 59, 85, 24);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("\uBE44\uBC00\uBC88\uD638 \uC124\uC815");
		lblNewLabel_1_1.setFont(new Font("굴림", Font.PLAIN, 15));
		lblNewLabel_1_1.setBounds(12, 133, 99, 24);
		contentPane.add(lblNewLabel_1_1);
		
		textField = new JTextField();
		textField.setToolTipText("\uBC29 \uC774\uB984\uC744 \uC785\uB825\uD558\uC138\uC694.");
		textField.setBounds(12, 83, 341, 29);
		contentPane.add(textField);
		textField.setColumns(30);
		
		JButton createBtn = new JButton("\uC0DD\uC131");
		createBtn.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				room = new Room(masterUser,"600", textField.getText(), passwordField.getPassword().toString());
				mainview.sendObject(room);
				setVisible(false);
			}
		});
		
		createBtn.setBounds(221, 204, 60, 39);
		contentPane.add(createBtn);
		
		JButton cancelBtn = new JButton("\uCDE8\uC18C");
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		cancelBtn.setBounds(293, 204, 60, 39);
		contentPane.add(cancelBtn);
		
		passwordField = new JPasswordField();
		passwordField.setToolTipText("Optional..");
		passwordField.setBounds(12, 162, 341, 29);
		contentPane.add(passwordField);

	}
}
