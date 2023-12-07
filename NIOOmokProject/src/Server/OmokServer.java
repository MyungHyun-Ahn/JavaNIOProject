package Server;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class OmokServer extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField portTF;
	
	// JTextArea
	private JTextArea textArea = new JTextArea();
	
	// JLabel
	private JLabel conUsersLabel;
	private JLabel roomMemLabel;
	
	// JBtn
	private JButton startBtn;
	private JButton stopBtn;
	private JButton noteBtn;
	private JButton roomMemCheck;
	
	// JList
	private JList<String> clientList = new JList<String>();
	private JList<String> roomList = new JList<String>();
	private JList<String> roomMemList = new JList<String>();
	
	// Network
	private SelectServer selectServer = null;
	
	// 서버 구동용 스레드
	Thread serverThread;
	
	public OmokServer() {
		initializeGUI();
		setupActionListeners();
	}
	
	private void initializeGUI() {
		setTitle("Omok Server Application");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 520, 500);
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
		stopBtn.setEnabled(false);
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
		
		noteBtn = new JButton("쪽지 보내기");
		noteBtn.setFont(noteBtn.getFont().deriveFont(noteBtn.getFont().getStyle() | Font.BOLD, noteBtn.getFont().getSize() + 3f));
		noteBtn.setBounds(287, 125, 201, 34);
		contentPane.add(noteBtn);
		
		JLabel lblNewLabel_1_1 = new JLabel("방 목록");
		lblNewLabel_1_1.setFont(lblNewLabel_1_1.getFont().deriveFont(lblNewLabel_1_1.getFont().getStyle() | Font.BOLD, lblNewLabel_1_1.getFont().getSize() + 3f));
		lblNewLabel_1_1.setBounds(287, 169, 118, 28);
		contentPane.add(lblNewLabel_1_1);
		
		roomMemCheck = new JButton("방 인원 조회");
		roomMemCheck.setFont(roomMemCheck.getFont().deriveFont(roomMemCheck.getFont().getStyle() | Font.BOLD, roomMemCheck.getFont().getSize() + 3f));
		roomMemCheck.setBounds(287, 286, 201, 34);
		contentPane.add(roomMemCheck);
		
		roomList.setBounds(287, 196, 201, 80);
		contentPane.add(roomList);
		
		roomMemList.setBounds(287, 358, 201, 80);
		contentPane.add(roomMemList);
		
		roomMemLabel = new JLabel("**** 방 참여자 목록");
		roomMemLabel.setFont(roomMemLabel.getFont().deriveFont(roomMemLabel.getFont().getStyle() | Font.BOLD, roomMemLabel.getFont().getSize() + 3f));
		roomMemLabel.setBounds(287, 330, 201, 28);
		contentPane.add(roomMemLabel);
		
		this.setVisible(true);
	}
	
	private void setupActionListeners() { // 11-13
	    startBtn.addActionListener(this);
	    stopBtn.addActionListener(this);
	    noteBtn.addActionListener(this);
	    roomMemCheck.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	    if (e.getSource() == startBtn) {
	    	System.out.println("start button clicked");
	    	startServer();
	    } 
	    else if (e.getSource() == stopBtn) {
	    	System.out.println("stop button clicked");
	    	stopServer();
	    }
	    else if (e.getSource() == noteBtn) {
	    	System.out.println("note button clicked");
	    	// note();
	    }
	    else if (e.getSource() == roomMemCheck) {
	    	System.out.println("Room Member Check button clicked");
	    	// stopServer();
	    }
	}
	
	private void startServer() {
		int port = Integer.parseInt(portTF.getText().trim());
		selectServer = new SelectServer(port, this.textArea);
		selectServer.setClientList(clientList);
		selectServer.setRoomList(roomList);
		selectServer.setRoomMemList(roomMemList);
		selectServer.setConUsersLabel(conUsersLabel);
		serverThread = new Thread(selectServer);
		serverThread.start();
		startBtn.setEnabled(false);
		stopBtn.setEnabled(true);
	}
	
	private void stopServer() {
		selectServer.stopServer();
		startBtn.setEnabled(true);
		stopBtn.setEnabled(false);
	}
}
