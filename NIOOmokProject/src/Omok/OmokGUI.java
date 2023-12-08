package Omok;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Packet.UserInfo;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class OmokGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField commentTf;
	private JTextField chatTf;
	
	private JLabel userCountLabel;
	private JButton sendBtn;
	private JTextArea chatArea;
	private JButton gameStartBtn;
	
	private JList<String> gameUsers;
	
	private MapManager omokMap;
	private GameLogic gameLogic;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OmokGUI frame = new OmokGUI();
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
	public OmokGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 933, 640);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setTitle("Omok Game");
		setContentPane(contentPane);
		contentPane.setLayout(null);
		gameLogic = new GameLogic();
		
		omokMap = new MapManager(gameLogic);
		omokMap.setBounds(10, 10, 587, 587);
		omokMap.init();
		contentPane.add(omokMap);
		
		MouseAction mouseAction = new MouseAction(omokMap, gameLogic, this);
		addMouseListener(mouseAction);
		
		JLabel lblNewLabel = new JLabel("해설");
		lblNewLabel.setFont(lblNewLabel.getFont().deriveFont(lblNewLabel.getFont().getStyle() | Font.BOLD, lblNewLabel.getFont().getSize() + 3f));
		lblNewLabel.setBounds(622, 214, 66, 25);
		contentPane.add(lblNewLabel);
		
		commentTf = new JTextField();
		commentTf.setText("ex) xxx 차례입니다.");
		commentTf.setBounds(622, 249, 285, 26);
		contentPane.add(commentTf);
		commentTf.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("게임 참여자");
		lblNewLabel_1.setFont(lblNewLabel_1.getFont().deriveFont(lblNewLabel_1.getFont().getStyle() | Font.BOLD, lblNewLabel_1.getFont().getSize() + 3f));
		lblNewLabel_1.setBounds(622, 10, 141, 33);
		contentPane.add(lblNewLabel_1);
		
		gameUsers = new JList<String>();
		gameUsers.setBounds(621, 43, 286, 111);
		contentPane.add(gameUsers);
		
		gameStartBtn = new JButton("게임 시작");
		gameStartBtn.setFont(gameStartBtn.getFont().deriveFont(gameStartBtn.getFont().getStyle() | Font.BOLD, gameStartBtn.getFont().getSize() + 3f));
		gameStartBtn.setBounds(622, 164, 285, 40);
		contentPane.add(gameStartBtn);
		
		chatArea = new JTextArea();
		chatArea.setBounds(622, 312, 285, 245);
		contentPane.add(chatArea);
		
		chatTf = new JTextField();
		chatTf.setColumns(10);
		chatTf.setBounds(622, 567, 220, 26);
		contentPane.add(chatTf);
		
		sendBtn = new JButton("전송");
		sendBtn.setBounds(841, 567, 66, 26);
		contentPane.add(sendBtn);
		
		JLabel lblNewLabel_3 = new JLabel("채팅창");
		lblNewLabel_3.setFont(lblNewLabel_3.getFont().deriveFont(lblNewLabel_3.getFont().getStyle() | Font.BOLD, lblNewLabel_3.getFont().getSize() + 3f));
		lblNewLabel_3.setBounds(622, 285, 78, 25);
		contentPane.add(lblNewLabel_3);
		
		userCountLabel = new JLabel("0/2");
		userCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userCountLabel.setFont(userCountLabel.getFont().deriveFont(userCountLabel.getFont().getStyle() | Font.BOLD, userCountLabel.getFont().getSize() + 3f));
		userCountLabel.setBounds(823, 10, 84, 33);
		contentPane.add(userCountLabel);
	}
}
