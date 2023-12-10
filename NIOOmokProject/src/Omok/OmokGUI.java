package Omok;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import Client.OmokClient;
import Client.SelectClient;
import Packet.PacketCode;
import Packet.PacketMessage;
import Packet.RoomInfo;
import Packet.UserInfo;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class OmokGUI extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField commentTf;
	private JTextField chatTf;
	
	private JLabel userCountLabel;
	private JButton sendBtn;
	private JTextArea chatArea;
	private JButton gameStartBtn;
	
	
	private JList<String> gameUsers;
	private Vector<String> gameUserVc;
	JScrollPane scrollPane;
	
	private MapManager omokMap;
	private GameLogic gameLogic;
	
	private OmokClient omokClient;
	private SelectClient selectClient;
	private MouseAction mouseAction;
	
	/**
	 * Create the frame.
	 */
	public OmokGUI(String roomName, OmokClient omokClient, SelectClient selectClient) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				handleWindowClose();
			}
		});
		setBounds(100, 100, 933, 640);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setTitle(roomName);
		setContentPane(contentPane);
		setResizable(false);
		contentPane.setLayout(null);
		gameLogic = new GameLogic();
		
		this.selectClient = selectClient;
		this.omokClient = omokClient;
		
		omokMap = new MapManager(gameLogic);
		omokMap.setBounds(10, 10, 587, 587);
		contentPane.add(omokMap);
		
		mouseAction = new MouseAction(omokMap, gameLogic, this, selectClient);
		// addMouseListener(mouseAction);
		// removeMouseListener(mouseAction); 
		
		JLabel gameInfoLabel = new JLabel("해설");
		gameInfoLabel.setFont(gameInfoLabel.getFont().deriveFont(gameInfoLabel.getFont().getStyle() | Font.BOLD, gameInfoLabel.getFont().getSize() + 3f));
		gameInfoLabel.setBounds(622, 214, 66, 25);
		contentPane.add(gameInfoLabel);
		
		commentTf = new JTextField();
		commentTf.setText("ex) xxx 차례입니다.");
		commentTf.setBounds(622, 249, 285, 26);
		commentTf.setEditable(false);
		contentPane.add(commentTf);
		commentTf.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("게임 참여자");
		lblNewLabel_1.setFont(lblNewLabel_1.getFont().deriveFont(lblNewLabel_1.getFont().getStyle() | Font.BOLD, lblNewLabel_1.getFont().getSize() + 3f));
		lblNewLabel_1.setBounds(622, 10, 141, 33);
		contentPane.add(lblNewLabel_1);
		
		gameUserVc = new Vector<String>();
		gameUsers = new JList<String>();
		gameUsers.setBounds(621, 43, 286, 111);
		contentPane.add(gameUsers);
		
		gameStartBtn = new JButton("게임 시작");
		gameStartBtn.setFont(gameStartBtn.getFont().deriveFont(gameStartBtn.getFont().getStyle() | Font.BOLD, gameStartBtn.getFont().getSize() + 3f));
		gameStartBtn.setBounds(622, 164, 285, 40);
		gameStartBtn.setEnabled(true);
		contentPane.add(gameStartBtn);
		
		scrollPane = new JScrollPane();
	    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(622, 312, 285, 245);
		chatArea = new JTextArea();
		scrollPane.setViewportView(chatArea);
		// chatArea.setBounds(622, 312, 285, 245);
		contentPane.add(scrollPane);
		chatArea.setEditable(false);
		
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
		
		addActionListeners();
	}
	
	private void addActionListeners() {
		// TODO Auto-generated method stub
		gameStartBtn.addActionListener(this); // 게임 시작 리스너
		sendBtn.addActionListener(this); // 채팅 전송 리스너
		chatTf.addKeyListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == gameStartBtn) {
			System.out.println("game start button clicked");
			handleGameStartBtnClick();
		} 
		else if (e.getSource() == sendBtn) {
			System.out.println("note button clicked");
			handleSendButtonClick();
		}
	}
	
	private void handleGameStartBtnClick() {
		// TODO Auto-generated method stub
		if (!checkCanGameStart()) {
			JOptionPane.showMessageDialog(this, "시작 인원이 충분하지 않습니다.", "게임 시작 불가", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (gameLogic.isGameRunning()) {
			return;
		}
		
		PacketMessage gameStartMsg = new PacketMessage();
		gameStartMsg.makeGameStartReq(new RoomInfo(omokClient.getRoomName()), new UserInfo(omokClient.getId()));
		selectClient.sendPacket(gameStartMsg);
		System.out.println("게임 시작 요청 전송");
	}
	
	private boolean checkCanGameStart() {
		return gameUserVc.size() >= 2;
	}

	private void handleSendButtonClick() {
		// TODO Auto-generated method stub
		sendChat();
	}
	
	private void sendChat() {
		String chatMsg = chatTf.getText().trim();
		
		if (isEmpty(chatTf))
			return;
		
		PacketMessage msg = new PacketMessage();
		msg.makeRoomChatReq(new RoomInfo(omokClient.getRoomName()), new UserInfo(omokClient.getId()), chatMsg);
		selectClient.sendPacket(msg);
		chatTf.setText("");
		chatTf.requestFocus();
	}

	private boolean isEmpty(JTextField field) {
		return field.getText().trim().isEmpty();
	}

	public void setCommentTf(String str) {
		commentTf.setText(str);
	}
	
	public void setUserCountLabel() {
		if (gameUserVc.size() == 2) {
			gameStartBtn.setEnabled(true);
		} 
		else {
			gameStartBtn.setEnabled(true);
		}
		userCountLabel.setText(gameUserVc.size() + "/2");
	}
	
	public void addUserList(String userName) {
		gameUserVc.add(userName);
		gameUsers.setListData(gameUserVc);
		setUserCountLabel();
		
		
	}
	
	public void removeUserList(String userName) {
		gameUserVc.remove(userName);
		gameUsers.setListData(gameUserVc);
		setUserCountLabel();
		
		if (gameLogic.isGameRunning()) {
			endGame();
			JOptionPane.showMessageDialog(this, "상대방의 접속 종료로 승리하셨습니다.", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void addChatArea(String msg) {
		chatArea.append(msg + "\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}
	
	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 10) {
			sendChat();
	    }
	}

	public void keyTyped(KeyEvent e) {
	}
	
	public void handleWindowClose() {
		int result = JOptionPane.showConfirmDialog(this, "방을 나가겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
		
		if (result == JOptionPane.YES_OPTION) {
			// TODO : 방 나가기 패킷 처리
			String roomName = omokClient.getRoomName();
			String userName = omokClient.getId();
			PacketMessage leaveMsg = new PacketMessage();
			System.out.println("Omok Window Close roomName = " + roomName + ", userName = " + userName);
			leaveMsg.makeLeaveRoomReq(new RoomInfo(roomName), new UserInfo(userName));
			selectClient.sendPacket(leaveMsg);
			omokClient.setExitBtnEnabled(false);
			omokClient.setJoinBtnEnabled(true);
			dispose();
		}
	}
	
	public void gameStart(short color, UserInfo userInfo) {
		addMouseListener(mouseAction);
		gameStartBtn.setEnabled(false);
		
		String startMsg = "";
		if (color == PacketCode.COLOR_BLACK) {
			startMsg = "당신은 흑돌입니다. 선공";
		}
		else if (color == PacketCode.COLOR_WHITE) {
			startMsg = "당신은 백돌입니다. 후공";
		}
		
		JOptionPane.showMessageDialog(this, startMsg, "게임 시작", JOptionPane.INFORMATION_MESSAGE);
		
		gameLogic.init(new RoomInfo(omokClient.getRoomName()), new UserInfo(omokClient.getId()), omokMap);
		omokMap.repaint();
		gameLogic.setColor(color);
		gameLogic.setSelectClient(selectClient);
		gameLogic.setGameRunning(true);
		commentTf.setText(gameLogic.getTurn() + " : " + userInfo.getName() + "님 차례입니다.");
	}
	
	public void gameResult(PacketMessage msg) {
		short[] input = msg.getUserInput();
		
		gameLogic.setPos(msg.getColorCode(), (int)input[0], (int)input[1]);
		switch (msg.getStatusCode()) {
		case PacketCode.NONE: // 게임 진행
			gameLogic.setTurn(gameLogic.getTurn() + 1);
			commentTf.setText(gameLogic.getTurn() + " : " + msg.getUserInfo().getName() + "님 차례입니다.");
			break;
		case PacketCode.WINNER: // 승리
			endGame();
			JOptionPane.showMessageDialog(this, msg.getUserInfo().getName() + "님은 승리하셨습니다!!!", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
			break;
		case PacketCode.LOSER: // 패배
			endGame();
			JOptionPane.showMessageDialog(this, msg.getUserInfo().getName() + "님은 패배하셨습니다...", "게임 종료", JOptionPane.INFORMATION_MESSAGE);
			break;
		}
	}
	
	public void endGame() {
		commentTf.setText("게임 대기중...");
		gameLogic.setGameRunning(false);
		gameStartBtn.setEnabled(true);
		removeMouseListener(mouseAction);
	}
}
