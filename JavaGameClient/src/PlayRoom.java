import java.awt.Color;
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

	public enum CurrentStatus {
		WAITING, PLAYING, OBSERVING
	};

	// keyboard enter key ġ�� ������ ����
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button�� �����ų� �޽��� �Է��ϰ� Enter key ġ��
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				mainview.sendMessageFromRoom(msg);
				txtInput.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
				txtInput.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
				if (msg.contains("/exit")) // ���� ó��
					System.exit(0);
			}
		}
	}
	
	// ȭ�鿡 ���
		public void AppendText(String msg) {
			// textArea.append(msg + "\n");
			// AppendIcon(icon1);
			msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.
			int len = textArea.getDocument().getLength();
			// ������ �̵�
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

		// ȭ�� ������ ���
		public void AppendTextR(String msg) {
			msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.
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
		setBounds(100, 100, 1193, 772);
		setLocationRelativeTo(null); // �ڵ����� ������� â�� open

		setResizable(false);
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		textArea.setFont(new Font("����ü", Font.PLAIN, 14));

		txtInput = new JTextField();
		txtInput.setBounds(902, 633, 182, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setFont(new Font("����", Font.PLAIN, 14));
		btnSend.setBounds(1096, 633, 69, 40);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("����", Font.BOLD, 14));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(914, 87, 84, 40);
		contentPane.add(lblUserName);
		setVisible(true);

		// view.AppendText("User " + view.UserName + " connecting " + ip_addr + " " +
		// port_no);
		userName = view.userName;
		lblUserName.setText(view.userName);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("����", Font.PLAIN, 16));
		imgBtn.setBounds(902, 683, 50, 40);
		contentPane.add(imgBtn);

		JButton btnNewButton = new JButton("\uB098\uAC00\uAE30");
		btnNewButton.setFont(new Font("����", Font.PLAIN, 11));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// exit room
				ChatMsg msg = new ChatMsg.ChatMsgBuilder("604", userName).data(room_name).build();

				view.sendObject(msg);
				setVisible(false);
				view.setVisible(true);
			}
		});
		btnNewButton.setBounds(1096, 683, 69, 40);
		contentPane.add(btnNewButton);

		JLabel lblUserName_1 = new JLabel((String) null);
		lblUserName_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName_1.setFont(new Font("����", Font.BOLD, 14));
		lblUserName_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName_1.setBackground(Color.WHITE);
		lblUserName_1.setBounds(35, 87, 84, 40);
		lblUserName_1.setText(room_name);
		contentPane.add(lblUserName_1);

		TextSendAction action = new TextSendAction();
		btnSend.addActionListener(action);
		txtInput.addActionListener(action);
		txtInput.requestFocus();
		repaint();
	}

}
