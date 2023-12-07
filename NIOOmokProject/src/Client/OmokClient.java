package Client;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Packet.PacketMessage;
import Packet.UserInfo;

public class OmokClient extends JFrame implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	// Login GUI 변수
	private JFrame Login_GUI = new JFrame("로그인");
	private JPanel login_pane;
	private JTextField ip_tf; // IP 택스트 필드
	private JTextField port_tf; // port 택스트 필드
	private JTextField id_tf; // ID 택스트 필드
	private JButton login_btn = new JButton("접 속"); // 접속 버튼

	// Main GUI 변수
	private JPanel contentPane;
	private JTextField msg_tf;
	private JButton notesend_btn = new JButton("쪽지보내기");
	private JButton joinroom_btn = new JButton("참여");
	private JButton exitroom_btn = new JButton("탈퇴");
	private JButton create_room_btn = new JButton("방만들기");
	private JButton send_btn = new JButton("전송");
	private JList<String> User_List = new JList(); // 전체 접속자 리스트
	private JList<String> Room_List = new JList(); // 전체 방 목록 리스트
	private JTextArea chatArea = new JTextArea(); // 채팅창 변수
	private JButton chatQuit_btn = new JButton("채팅종료");


	
	// 클라이언트 관리
	private String My_Room = ""; // 내가 참여한 채팅방
	
	private String ip = "";
	private String id = "";
	private int port;
	
	private SelectClient selectClient = null;
	private Thread clientThread = null;

	
	public OmokClient() {
		initializeLoginGUI();
		initializeMainGUI();
		addActionListeners();
	}

	private void initializeLoginGUI() {
		Login_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    Login_GUI.setBounds(300, 100, 295, 388);
	    login_pane = new JPanel();
	    login_pane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    Login_GUI.setContentPane(login_pane);
	    login_pane.setLayout(null);

	    JLabel 서버IP = new JLabel("Server IP");
	    서버IP.setBounds(12, 165, 57, 15);
	    this.login_pane.add(서버IP);

	    JLabel 서버port = new JLabel("Sever Port");
	    서버port.setBounds(12, 202, 69, 15);
	    login_pane.add(서버port);

	    JLabel 사용자ID = new JLabel("ID");
	    사용자ID.setBounds(12, 245, 57, 15);
	    login_pane.add(사용자ID);

	    ip_tf = new JTextField();
	    ip_tf.setBounds(92, 162, 116, 21);
	    login_pane.add(ip_tf);
	    ip_tf.setColumns(10);

	    port_tf = new JTextField();
	    port_tf.setBounds(92, 199, 116, 21);
	    login_pane.add(port_tf);
	    port_tf.setColumns(10);
	    id_tf = new JTextField();
	    id_tf.setBounds(92, 242, 116, 21);
	    login_pane.add(id_tf);
	    id_tf.setColumns(10);

	    login_btn.setBounds(22, 291, 227, 23);
	    login_pane.add(login_btn);
	    Login_GUI.setVisible(true);
	    this.setVisible(true);
	}

	private void initializeMainGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setBounds(600, 100, 510, 460);
	    contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    setContentPane(contentPane);
	    contentPane.setLayout(null);
	    
	    JLabel 접속자 = new JLabel("전체 접속자");
	    접속자.setBounds(12, 20, 73, 15);
	    contentPane.add(접속자);
	    User_List.setBounds(12, 45, 108, 107);
	    contentPane.add(User_List); // 접속자 목록 JLIST

	    chatQuit_btn.setBounds(12, 162, 108, 23);
	    contentPane.add(chatQuit_btn); // 채팅 종료
	    notesend_btn.setBounds(12, 192, 108, 23);
	    contentPane.add(notesend_btn); // 쪽지 보내기

	    JLabel 채팅방 = new JLabel("채팅방목록");
	    채팅방.setBounds(12, 225, 97, 15);
	    contentPane.add(채팅방);
	    Room_List.setBounds(12, 240, 108, 107);
	    contentPane.add(Room_List); // 채팅방 목록 JLIST

	    joinroom_btn.setBounds(6, 357, 60, 23);
	    contentPane.add(joinroom_btn); // 채팅방 참여
	    joinroom_btn.setEnabled(false); // 버튼 비활성화
	    exitroom_btn.setBounds(68, 357, 60, 23);
	    contentPane.add(exitroom_btn); // 채팅방 나감
	    exitroom_btn.setEnabled(false);
	    create_room_btn.setBounds(12, 386, 108, 23);
	    contentPane.add(create_room_btn); // 채팅방 생성

	    JScrollPane scrollPane = new JScrollPane();
	    scrollPane.setBounds(142, 16, 340, 363);
	    contentPane.add(scrollPane);
	    scrollPane.setColumnHeaderView(chatArea); // 채팅창
	    chatArea.setEditable(false);

	    msg_tf = new JTextField();
	    msg_tf.setBounds(144, 387, 268, 21);
	    contentPane.add(msg_tf); // 대화 입력창
	    msg_tf.setColumns(10);
	    send_btn.setBounds(412, 386, 70, 23);
	    contentPane.add(send_btn); // 메시지 전송
	    this.setVisible(true);
	}
	
	private void addActionListeners() {
		// TODO Auto-generated method stub
		 login_btn.addActionListener(this); // 로그인 리스너 연결
	     notesend_btn.addActionListener(this); // 쪽지 전송 리스너
	     joinroom_btn.addActionListener(this); // 채팅방참여 리스너
	     exitroom_btn.addActionListener(this); // 채팅방탈퇴 리스너
	     create_room_btn.addActionListener(this); // 방만들기 리스너
	     send_btn.addActionListener(this); // 전송 버튼 리스너
	     msg_tf.addKeyListener(this); // 메시지 전송 리스너
	     chatQuit_btn.addActionListener(this); // 채팅 종료 리스너
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 if (e.getSource() == login_btn) {
			 System.out.println("login button clicked");
	         handleLoginButtonClick();
	     } else if (e.getSource() == notesend_btn) {
	    	 System.out.println("note button clicked");
	         handleNoteSendButtonClick();
	     } else if (e.getSource() == joinroom_btn) {
	    	 System.out.println("join room button clicked");
	         // handleJoinRoomButtonClick();
	     } else if (e.getSource() == create_room_btn) {
	    	 System.out.println("create room button clicked");
	         // handleCreateRoomButtonClick();
	     } else if (e.getSource() == send_btn) {
	    	 System.out.println("send button clicked");
	         // handleSendButtonClick();
	     } else if (e.getSource() == chatQuit_btn) {
	    	 System.out.println("chat quit clicked");
	         handleChatQuitButtonClick();
	     } else if (e.getSource() == exitroom_btn) {
	    	 System.out.println("exit room button clicked");
	         // handleExitRoomButtonClick();
	     }
	}
	
	private void handleLoginButtonClick() {
		// TODO Auto-generated method stub
		if (isEmpty(ip_tf)) {
			setFieldText(ip_tf, "IP를 입력해주세요");
			return;
		}
		if (isEmpty(port_tf)) {
			setFieldText(port_tf, "포트 번호를 입력해주세요");
			return;
		}
		if (isEmpty(id_tf)) {
			setFieldText(id_tf, "ID를 입력하세요");
			return;
		}
		ip = ip_tf.getText().trim();
		port = Integer.parseInt(port_tf.getText().trim());
		id = id_tf.getText().trim();

		network();
		
		setTitle("사용자: " + id);
	}
	
	private void network() {
		if (selectClient == null) {
			selectClient = new SelectClient(ip, port);
			selectClient.setUser_List(User_List);
			selectClient.setRoom_List(Room_List);
			selectClient.setOmokClient(this);
			clientThread = new Thread(selectClient);
			clientThread.start();
		}
		
		PacketMessage msg = new PacketMessage();
		UserInfo userInfo = new UserInfo(id);
		msg.makeLoginReq(userInfo);
		
		selectClient.sendPacket(msg);
	}
	
	private void handleNoteSendButtonClick() {
		String user = (String) User_List.getSelectedValue();
		String note = JOptionPane.showInputDialog("보낼 메시지");
		
		if (note == null)
			return;
		
		PacketMessage msg = new PacketMessage();
		UserInfo sender = new UserInfo(id);
		UserInfo target = new UserInfo(user);
		msg.makeNoteReq(sender, target, note);
		
		selectClient.sendPacket(msg);
	}
	
	private void handleChatQuitButtonClick() {
		selectClient.stopClient();
		selectClient = null;
	}

	private boolean isEmpty(JTextField field) {
		return field.getText().trim().isEmpty();
	}
	
	private void setFieldText(JTextField field, String text) {
		field.setText(text);
	    field.requestFocus();
	}


	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 10) {
			if (!My_Room.isEmpty()) {
				// send_Msg("Chatting/" + My_Room + "/" + msg_tf.getText().trim());
	            msg_tf.setText("");
	            msg_tf.requestFocus();
	        }
	    }
	}

	public void keyTyped(KeyEvent e) {
	}

}
