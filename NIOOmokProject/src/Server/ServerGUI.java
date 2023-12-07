package Server;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.JList;
import javax.swing.SwingConstants;

public class ServerGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField portTF;
	
	// JTextArea
	JTextArea textArea = new JTextArea();
	
	// JLabel
	JLabel conUsersLabel;
	JLabel roomMemLabel;
	
	// JBtn
	JButton startBtn;
	JButton stopBtn;
	
	// JList
	JList<String> clientList = new JList<String>();
	JList<String> roomList = new JList<String>();
	JList<String> roomMemList = new JList<String>();
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI frame = new ServerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ServerGUI() {
		setTitle("Omok Server Application");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 502, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		portTF = new JTextField();
		portTF.setBounds(90, 391, 190, 21);
		portTF.setFont(new Font("굴림", Font.PLAIN, 16));
		contentPane.add(portTF);
		portTF.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("포트 번호");
		lblNewLabel.setBounds(12, 385, 74, 34);
		lblNewLabel.setFont(lblNewLabel.getFont().deriveFont(lblNewLabel.getFont().getStyle() | Font.BOLD, lblNewLabel.getFont().getSize() + 3f));
		contentPane.add(lblNewLabel);
		
		startBtn = new JButton("서버 실행");
		startBtn.setFont(startBtn.getFont().deriveFont(startBtn.getFont().getStyle() | Font.BOLD, startBtn.getFont().getSize() + 3f));
		startBtn.setBounds(12, 422, 128, 30);
		contentPane.add(startBtn);
		
		stopBtn = new JButton("서버 중지");
		stopBtn.setFont(stopBtn.getFont().deriveFont(stopBtn.getFont().getStyle() | Font.BOLD, stopBtn.getFont().getSize() + 3f));
		stopBtn.setBounds(152, 422, 128, 30);
		contentPane.add(stopBtn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(12, 10, 268, 359);
		contentPane.add(scrollPane);
		
		scrollPane.setViewportView(textArea);
		
		clientList.setBounds(287, 35, 201, 80);
		contentPane.add(clientList);
		
		JLabel lblNewLabel_1 = new JLabel("전체 접속자");
		lblNewLabel_1.setFont(lblNewLabel_1.getFont().deriveFont(lblNewLabel_1.getFont().getStyle() | Font.BOLD, lblNewLabel_1.getFont().getSize() + 3f));
		lblNewLabel_1.setBounds(287, 9, 118, 28);
		contentPane.add(lblNewLabel_1);
		
		conUsersLabel = new JLabel("0명");
		conUsersLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		conUsersLabel.setFont(conUsersLabel.getFont().deriveFont(conUsersLabel.getFont().getStyle() | Font.BOLD, conUsersLabel.getFont().getSize() + 3f));
		conUsersLabel.setBounds(400, 9, 88, 28);
		contentPane.add(conUsersLabel);
		
		JButton noteBtn = new JButton("쪽지 보내기");
		noteBtn.setFont(noteBtn.getFont().deriveFont(noteBtn.getFont().getStyle() | Font.BOLD, noteBtn.getFont().getSize() + 3f));
		noteBtn.setBounds(287, 125, 201, 34);
		contentPane.add(noteBtn);
		
		JLabel lblNewLabel_1_1 = new JLabel("방 목록");
		lblNewLabel_1_1.setFont(lblNewLabel_1_1.getFont().deriveFont(lblNewLabel_1_1.getFont().getStyle() | Font.BOLD, lblNewLabel_1_1.getFont().getSize() + 3f));
		lblNewLabel_1_1.setBounds(287, 169, 118, 28);
		contentPane.add(lblNewLabel_1_1);
		
		JButton btnNewButton_2_1 = new JButton("방 인원 조회");
		btnNewButton_2_1.setFont(btnNewButton_2_1.getFont().deriveFont(btnNewButton_2_1.getFont().getStyle() | Font.BOLD, btnNewButton_2_1.getFont().getSize() + 3f));
		btnNewButton_2_1.setBounds(287, 286, 201, 34);
		contentPane.add(btnNewButton_2_1);
		
		roomList.setBounds(287, 196, 201, 80);
		contentPane.add(roomList);
		
		roomMemList.setBounds(287, 358, 201, 80);
		contentPane.add(roomMemList);
		
		roomMemLabel = new JLabel("**** 방 참여자 목록");
		roomMemLabel.setFont(roomMemLabel.getFont().deriveFont(roomMemLabel.getFont().getStyle() | Font.BOLD, roomMemLabel.getFont().getSize() + 3f));
		roomMemLabel.setBounds(287, 330, 201, 28);
		contentPane.add(roomMemLabel);
	}
}
