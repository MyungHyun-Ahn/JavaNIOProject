package Server;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import Packet.PacketCode;
import Packet.PacketMessage;
import Packet.RoomInfo;
import Packet.UserInfo;
import Util.CastingUtil;
import Util.SerializeUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// 192.168.35.1
// 공유기 IP 주소

public class SelectServer implements Runnable {
	private Selector selector; // Non-Blocking 소켓 통신을 위한 Selector
	private ServerSocketChannel serverChannel;	// 클라이언트들의 접속을 승인할 서버 소켓 채널
	private InetSocketAddress serverAddress;	// 서버의 주소를 가지는 객체
	private Queue<PacketMessage> outPacketQueue; // 패킷 전송 대기열로 이용할 큐
	
	private ArrayList<UserInfo> users; // UserInfo를 저장하는 ArrayList
	private Vector<String> userVc;
	private ArrayList<RoomInfo> rooms; // RoomInfo를 저장하는 ArrayList
	private Vector<String> roomVc;
	
	private JTextArea textArea;
	private JList<String> clientList;
	private JList<String> roomList;
	private JList<String> roomMemList;
	
	private OmokServer omokServer;
	
	private JLabel conUsersLabel;
	
	private boolean useGUI = true;
	
	// private static final ExecutorService threadPool = Executors.newFixedThreadPool(4); // 스레드 풀 (스레드 개수를 4개로 제한) 
	
	// 생성자
	public SelectServer(int portNum, JTextArea textArea) {
		try {
			serverAddress = new InetSocketAddress(portNum);
			
			selector = Selector.open(); // Selector Open
			
			users = new ArrayList<UserInfo>(); // User 정보를 담는 ArrayList
			userVc = new Vector<String>();
			rooms = new ArrayList<RoomInfo>();
			roomVc = new Vector<String>();
			
			outPacketQueue = new LinkedList<PacketMessage>();
			
			this.textArea = textArea;
			
			serverChannel = ServerSocketChannel.open(); 				// ServerSocketChannel Open
			serverChannel.configureBlocking(false); 					// Set Non-blocking mode
			serverChannel.socket().bind(serverAddress); 				// 주소 정보 bind
			serverChannel.register(selector, SelectionKey.OP_ACCEPT); 	// 비동기 accept 등록
		}
		catch (Exception e) {
			e.printStackTrace();
			LogError("Server Init : " + LocalDateTime.now());
			return;
		}
	}
	
	public SelectServer(int portNum, boolean useGUI) {
		try {
			serverAddress = new InetSocketAddress(portNum);
			
			selector = Selector.open(); // Selector Open
			
			users = new ArrayList<UserInfo>(); // User 정보를 담는 ArrayList
			userVc = new Vector<String>();
			rooms = new ArrayList<RoomInfo>();
			roomVc = new Vector<String>();
			
			outPacketQueue = new LinkedList<PacketMessage>();
			
			this.useGUI = useGUI;
			
			serverChannel = ServerSocketChannel.open(); 				// ServerSocketChannel Open
			serverChannel.configureBlocking(false); 					// Set Non-blocking mode
			serverChannel.socket().bind(serverAddress); 				// 주소 정보 bind
			serverChannel.register(selector, SelectionKey.OP_ACCEPT); 	// 비동기 accept 등록
		}
		catch (Exception e) {
			e.printStackTrace();
			LogError("Server Init : " + LocalDateTime.now());
			return;
		}
	}
	
	public String getInetAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress().toString();
	}
	
	@Override
	public void run() {
		Log("Server started on port : " + serverAddress.getPort());
		
		// TODO Auto-generated method stub
		try {
			while(serverChannel.isOpen()) {
				try {
					selector.select(); // 완료된 이벤트를 선택
				}
				catch (Exception e) {
					e.printStackTrace();
					System.out.println("Select Error! : " + LocalDateTime.now());
					return;
				}
				
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator(); // 완료된 이벤트를 하나씩 처리
				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					
					if (key.isAcceptable()) // Accept는 메인 스레드에서 진행
						Accept(key);
					
					if (key.isReadable())
						Read(key);
					
					try {
						if (key.isWritable())
							Write(key);
					}
					catch (Exception e) {
						// pass
						// 유저가 접속 종료를 눌렀을 때
						// CanceledKeyException 으로 서버 Crash 방지
					}
					
					
					keys.remove();
				}
			}
		}
		catch(Exception e) { // 서버 종료 버튼 클릭
			Log("Server Shutdown");
		}
	}
	
	// Accept 처리 메소드
	private void Accept(SelectionKey key) {
		serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = null;
		
		try {
			channel = serverChannel.accept(); 					// accept 한 클라이언트 소켓 채널을 얻음
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ); 	// 비동기 Read 예약
			Log(channel.toString() + " 클라이언트가 접속했습니다.");
		}
		catch (Exception e) {
			LogError("클라이언트 접속 실패! : " + LocalDateTime.now());
		}
	}
	
	// Read 처리 메소드
	private void Read(SelectionKey key) {
		SocketChannel channel = (SocketChannel)key.channel(); // 보낸 소켓 채널 얻어오기
		ByteBuffer buf = null;
		String address = channel.socket().getInetAddress().getHostAddress().toString();
		
		int recvBytes; // 받은 바이트 수
		short packetSize; // 패킷 사이즈
		ByteBuffer bSize = ByteBuffer.allocateDirect(2); // 통신을 위한 바이트 버퍼
		byte[] size = new byte[2]; // 패킷 사이즈를 헤더로 붙이기 위한 바이트 배열
		
		try {
			recvBytes = channel.read(bSize);
			
			if (recvBytes == -1) { // 받은 크기가 -1이면 접속이 끊김을 의미
				try {
					clientDisconnected(channel); // 접속 끊기
					channel.close();
					Log("접속 종료");
					return;
				}
				catch (Exception e) {
					e.printStackTrace();
					LogError("클라이언트 종료 실패! : " + address);
					return;
				}
			}
			
			bSize.flip(); 	// 바이트 버퍼의 현재 상태에 따라 p 값과 l 값 수정
							// p 값은 바이트 버퍼의 시작 위치
							// l 값은 바이트 버퍼의 끝 위치
			bSize.get(size); // 바이트 배열에 바이트 버퍼의 값을 얻어옴
			packetSize = CastingUtil.byteArrayToShort(size); // 바이트 배열 -> short 캐스팅 
			buf = ByteBuffer.allocateDirect(packetSize); // 얻어온 크기만큼 바이트 버퍼 할당
			recvBytes = 0;
			
			while (packetSize != recvBytes) { // 패킷이 끊겨서 올 수도 있기 때문에 모두 받을 때까지 read 수행
				recvBytes += channel.read(buf);
			}
		}
		catch (Exception e) { // 클라이언트가 전송 도중 끊겼을 경우 처리
			clientDisconnected(channel);
			return;
		}
		
		buf.flip();
		byte[] bytes = new byte[buf.limit()]; // 바이트 버퍼에 받은 것을 저장할 바이트 배열
		buf.get(bytes); // 바이트 배열에 데이터 저장
		PacketMessage msg = SerializeUtil.deserialize(bytes); // 역직렬화
		if (msg == null) return; // msg 가 null 일 시 pass
		
		outPacketQueue.offer(msg); // 패킷 큐에 메시지 추가
		
		try {
			channel.register(selector, SelectionKey.OP_WRITE); // 비동기 WRITE 예약
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogError("OP_WRITE 실패");
			return;
		}	
		
		if (buf != null)
		{
			buf.clear();
			buf = null;
		}
		
		bytes = null;
	}
	
	// Write 처리 메소드 - 실제로 패킷을 보내지는 않음
	private void Write(SelectionKey key) {
		SocketChannel channel = (SocketChannel)key.channel();
		
		while (true) {
			PacketMessage msg = outPacketQueue.poll(); // 큐에서 처리할 패킷이 있으면 꺼내온다.
			
			if (msg == null) break; // 큐가 비었을 경우 메세지 전송을 종료
			
			switch (msg.getMsgCode())
			{
			// 기본 요청 패킷 처리
			case PacketCode.LOGIN_REQ:
				handleLoginReq(channel, msg);
				break;
			case PacketCode.NOTE_REQ:
				handleNoteReq(channel, msg);
				break;
			case PacketCode.CHAT_REQ:
				handleChatReq(channel, msg);
				break;
				
			// 방 관련 요청 패킷 처리
			case PacketCode.CREATEROOM_REQ:
				handleCreateRoomReq(channel, msg);
				break;
			case PacketCode.ENTERROOM_REQ:
				handleEnterRoomReq(channel, msg);
				break;
			case PacketCode.LEAVEROOM_REQ:
				handleLeaveRoomReq(channel, msg);
				break;
			case PacketCode.ROOMCHAT_REQ:
				handleRoomChatReq(channel, msg);
				break;
				
			// 게임 관련 요청 패킷 처리
			case PacketCode.GAMESTART_REQ:
				handleGameStartReq(channel, msg);
				break;
			case PacketCode.GAMEINPUT_REQ:
				handleGameInputReq(channel, msg);
				break;
			}
		}
		
		try {
			// Write를 완료했으면 다시 READ 상태로 전환
			channel.register(selector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			LogError("OP_READ 실패");
		}
	}
		

	private void handleGameInputReq(SocketChannel channel, PacketMessage msg) {
		// TODO Auto-generated method stub
		RoomInfo gameRoomInfo = getRoomInfo(msg.getRoomInfo().getName());
		UserInfo userInfo = getUserInfo(msg.getUserInfo().getName());
		
		short[] input = msg.getUserInput();
		short gameCode = gameRoomInfo.setOmokPos(msg.getColorCode(), input[0], input[1]);
		
		// Log("gameCode = " + gameCode + ", y = " + msg.getUserInput()[0] + ", x = " + msg.getUserInput()[1]);
		
		// 그럴일 없겠지만 실패한 경우
		if (!(gameCode >= 0 && gameCode < 3)) {
			PacketMessage resMsg = new PacketMessage();
			resMsg.makeGameInputRes(PacketCode.FAILURE);
			sendPacket(channel, resMsg);
			LogError("Invalid Omok Pos");
			return ;
		}
		
		PacketMessage resMsg = new PacketMessage();
		resMsg.makeGameInputRes(PacketCode.SUCCESS);
		sendPacket(channel, resMsg);
		
		UserInfo nextUser = null;
		for (UserInfo u : gameRoomInfo.getUserList()) {
			if (!u.equals(userInfo)) {
				nextUser = u;
			}
		}
		
		// 각자에게 결과 전달
		if (gameCode == PacketCode.NONE) {
			for (UserInfo u : gameRoomInfo.getUserList()) {
				PacketMessage resultMsg = new PacketMessage();
				resultMsg.makeGameResultInfo(PacketCode.NONE, gameRoomInfo, nextUser, msg.getColorCode(), input);
				sendPacket(u.getSocketChannel(), resultMsg);
			}
		}
		else if (gameCode == msg.getColorCode()) { // 패킷 보낸 사람 승리
			for (UserInfo u : gameRoomInfo.getUserList()) {
				if (u.equals(userInfo)) {
					PacketMessage resultMsg = new PacketMessage();
					resultMsg.makeGameResultInfo(PacketCode.WINNER, gameRoomInfo, u, msg.getColorCode(), input);
					sendPacket(u.getSocketChannel(), resultMsg);
					Log(gameRoomInfo.getName() +" 방의 게임 종료 : " + userInfo.getName() + " 승리");
				}
				else {
					PacketMessage resultMsg = new PacketMessage();
					resultMsg.makeGameResultInfo(PacketCode.LOSER, gameRoomInfo, u, msg.getColorCode(), msg.getUserInput());
					sendPacket(u.getSocketChannel(), resultMsg);
				}
			}
		}
	}

	private void handleGameStartReq(SocketChannel channel, PacketMessage msg) {
		// TODO Auto-generated method stub
		RoomInfo gameRoomInfo = getRoomInfo(msg.getRoomInfo().getName());
		UserInfo startUserInfo = getUserInfo(msg.getUserInfo().getName());
		
		Log(gameRoomInfo.getName() + " 방에서 게임 시작");
		
		gameRoomInfo.setGameRunning(true);
		gameRoomInfo.initOmokMap();
		
		for (UserInfo u : users) {
			if (u.equals(startUserInfo)) { // 게임 시작 유저가 흑돌
				PacketMessage startRes = new PacketMessage();
				startRes.makeGameStartRes(PacketCode.SUCCESS, gameRoomInfo, startUserInfo, PacketCode.COLOR_BLACK);
				sendPacket(channel, startRes);
			}
			else { // 백돌 - 어차피 방의 유저는 2명
				PacketMessage startRes = new PacketMessage();
				startRes.makeGameStartRes(PacketCode.SUCCESS, gameRoomInfo, startUserInfo, PacketCode.COLOR_WHITE);
				sendPacket(u.getSocketChannel(), startRes);
			}
		}
		
	}

	private void handleRoomChatReq(SocketChannel channel, PacketMessage msg) {
		// TODO Auto-generated method stub
		RoomInfo roomInfo = getRoomInfo(msg.getRoomInfo().getName());
		UserInfo userInfo = getUserInfo(msg.getUserInfo().getName());
	
		PacketMessage notiMsg = new PacketMessage();
		notiMsg.makeRoomChatNoti(PacketCode.SUCCESS, roomInfo, userInfo, msg.getChatMsg());
	
		for (UserInfo u : roomInfo.getUserList()) {
			sendResPacket(u.getSocketChannel(), notiMsg);
		}
	}

	private void handleEnterRoomReq(SocketChannel channel, PacketMessage msg) {
		// TODO Auto-generated method stub
		PacketMessage resMsg = new PacketMessage();
		RoomInfo roomInfo = getRoomInfo(msg.getRoomInfo().getName());
		UserInfo userInfo = getUserInfo(msg.getUserInfo().getName());
		
		if (roomInfo.isRoomFull()) { // 방이 가득 참
			resMsg.makeEnterRoomRes(PacketCode.ISROOMFULL, null);
			sendResPacket(channel, resMsg);
			return;
		}
		
		
		roomInfo.EnterUser(userInfo);
		Log(roomInfo.getName() + "에 " + userInfo.getName() + " 가입");
		resMsg.makeEnterRoomRes(PacketCode.SUCCESS, roomInfo);
		sendResPacket(channel, resMsg);
		
		
		// 방에 원래 있던 유저도 알려주기
		ArrayList<UserInfo> roomUserList = roomInfo.getUserList();
		for (UserInfo u : roomUserList) {
			if (!u.getName().equals(userInfo.getName())) { // 자기 자신이 아닌 경우
				PacketMessage info = new PacketMessage();
				info.makeRoomUserEnterInfo(roomInfo, userInfo); // 자기 자신이 입장했다고 알림
				sendResPacket(u.getSocketChannel(), info);
				
				PacketMessage info2 = new PacketMessage();
				info2.makeRoomUserEnterInfo(roomInfo, u); // 자기 자신에게 원래 있던 유저의 정보를 보냄
				sendResPacket(channel, info2);
			}
		}
	}

	private void handleLeaveRoomReq(SocketChannel channel, PacketMessage msg) {
		// TODO Auto-generated method stub
		RoomInfo roomInfo = getRoomInfo(msg.getRoomInfo().getName());
		UserInfo userInfo = getUserInfo(msg.getUserInfo().getName());
		
		PacketMessage info = new PacketMessage();
		info.makeRoomUserLeaveInfo(roomInfo, userInfo);
		
		roomInfo.leaveUser(userInfo);
		
		// 어떤 유저가 나갔는지 알려주기
		for (UserInfo u : roomInfo.getUserList()) {
			sendResPacket(u.getSocketChannel(), info);
		}
		
		PacketMessage resMsg = new PacketMessage();
		resMsg.makeLeaveRoomRes(PacketCode.SUCCESS, roomInfo);
		
		if (roomInfo.getUserList().size() == 0) { // 방이 비었으면 방 삭제
			deleteRoom(roomInfo);
			return; // 방이 비었으므로 뒷 작업은 필요없음
		}
	}
	
	private void deleteRoom(RoomInfo roomInfo) {
		// 모든 유저에게 방이 삭제 되었다고 알리기
		Log(roomInfo.getName() + " 방 삭제");
					
		PacketMessage removeRoomMsg = new PacketMessage();
		removeRoomMsg.makeRoomDelInfo(roomInfo);
					
		broadcast(null, removeRoomMsg);
					
		rooms.remove(roomInfo);
		roomVc.remove(roomInfo.getName());
		
		if (useGUI)
			roomList.setListData(roomVc);
	}

	private void handleLoginReq(SocketChannel channel, PacketMessage msg) {
		PacketMessage resMsg = new PacketMessage();
		UserInfo userInfo = msg.getUserInfo();
		 
		if (isConflictUser(userInfo.getName())) { // 중복 닉네임 거르기
			resMsg.makeLoginRes(PacketCode.CONFLICT, userInfo);
			sendResPacket(channel, resMsg); // response 패킷을 보낸다!
			return;
		}

		
		// 유저 입장 허락
		userInfo.setSocketChannel(channel); // 채널을 저장
		Log("Enter User : " + userInfo.getName());
		users.add(userInfo); // userInfo를 저장
		userVc.add(userInfo.getName());
		
		if (useGUI) {
			clientList.setListData(userVc);
			conUsersLabel.setText(userVc.size() + "명");	
		}
		resMsg.makeLoginRes(PacketCode.SUCCESS, userInfo);
		sendResPacket(channel, resMsg); // response 패킷을 보낸다!
			
		// 접속 중인 유저 정보 전송
		for (UserInfo u : users) {
			if (u.getName().equals(userInfo.getName()))
				continue;
				 
			PacketMessage addUser = new PacketMessage();
			addUser.makeEnterUserInfo(u);
			sendResPacket(channel, addUser);
		}
		
		// 생성되어 있는 방 정보 전송
		for (RoomInfo r : rooms) {
			PacketMessage addRoom = new PacketMessage();
			addRoom.makeRoomInfoInfo(r);
			sendResPacket(channel, addRoom);
		}
		
		
		// 새로운 유저가 입장했음을 알림 | 유저 리스트 전송
		// broadcast
		PacketMessage enterUserInfo = new PacketMessage(); 
		enterUserInfo.makeEnterUserInfo(userInfo);
		broadcast(channel, enterUserInfo);
	}
	
	private void handleNoteReq(SocketChannel channel, PacketMessage msg) {
		String sender = msg.getUserInfo().getName();
		String target = msg.getTargetUserInfo().getName();
		
		UserInfo senderInfo = null;
		UserInfo targetInfo = null;
		
		// User 소켓 채널 찾아오기 위함
		for (UserInfo u : users) {
			if (senderInfo != null && targetInfo != null)
				break;
			
			if (u.getName().equals(sender)) {
				senderInfo = u;
			}
			
			if (u.getName().equals(target)) {
				targetInfo = u;
			}
		}
		
		PacketMessage msgRes = new PacketMessage();
		
		if (senderInfo == null || targetInfo == null) {
			LogError(senderInfo.getName() + "가 보낸 쪽지 실패");
			msgRes.makeNoteRes(PacketCode.FAILURE, senderInfo, targetInfo, msg.getNoteMsg());
			return;
		}
		
		Log(senderInfo.getName() + "가 " + targetInfo.getName() + "에게 쪽지 전송");
		msgRes.makeNoteRes(PacketCode.SUCCESS, senderInfo, targetInfo, msg.getNoteMsg());
		sendResPacket(targetInfo.getSocketChannel(), msgRes);
	}
	
	private void handleChatReq(SocketChannel channel, PacketMessage msg) {
		try {
			PacketMessage resMsg = new PacketMessage();
			resMsg.makeChatRes(PacketCode.SUCCESS);
			sendResPacket(channel, resMsg);
		}
		catch (Exception e) { // 혹시나 실패하면 FAILURE 패킷
			LogError(msg.getUserInfo().getName() + "가 보낸 채팅 수신 실패");
			PacketMessage resMsg = new PacketMessage();
			resMsg.makeChatRes(PacketCode.FAILURE);
			sendResPacket(channel, resMsg);
			return;
		}
		
		Log(msg.getUserInfo().getName() + "가 보낸 채팅 수신 성공");
		PacketMessage chatPacket = new PacketMessage();
		String resStr = msg.getUserInfo().getName() + " : " + msg.getChatMsg() + "\n";
		chatPacket.makeChatNoti(resStr);
		broadcast(null, chatPacket); // 모두에게 전송
	}
	
	private void handleCreateRoomReq(SocketChannel channel, PacketMessage msg) {
		// TODO Auto-generated method stub
		PacketMessage resMsg = new PacketMessage();
		String roomName = msg.getRoomInfo().getName();
		
		if (isConflictRoom(roomName)) {
			Log(roomName + " 방 이름 중복");
			resMsg.makeCreateRoomRes(PacketCode.CONFLICT, null);
			sendResPacket(channel, resMsg);
			return;
		}
		
		// 방 생성 가능!
		RoomInfo roomInfo = new RoomInfo(roomName);
		
		resMsg.makeCreateRoomRes(PacketCode.SUCCESS, roomInfo);
		sendResPacket(channel, resMsg);
		
		rooms.add(roomInfo);
		roomVc.add(roomName);
		if (useGUI)
			roomList.setListData(roomVc);
		
		// 유저 방 입장 시키기
		UserInfo userInfo = getUserInfo(msg.getUserInfo().getName());
		roomInfo.EnterUser(userInfo);
		Log(userInfo.getName() + " : " + roomInfo.getName() + " 방 생성");
		
						
		// 모든 유저에게 방 정보 전달
		PacketMessage createRoomInfo = new PacketMessage();
		createRoomInfo.makeRoomInfoInfo(roomInfo);
		broadcast(channel, createRoomInfo);
	}
	
	private boolean isConflictUser(String userName) {
		for (UserInfo u : users) {
			if (u.getName().equals(userName)) 
				return true;
		}
		
		return false;
	}
	
	private boolean isConflictRoom(String roomName) {
		for (RoomInfo r : rooms) {
			if (r.getName().equals(roomName)) 
				return true;
		}
		
		return false;
	}
	
	// 외부 호출용
	public void sendPacket(SocketChannel channel, PacketMessage msg) {
		sendResPacket(channel, msg);
	}
	
	// 응답 패킷 전달 혹은 targetChannel에 전달
	private synchronized void sendResPacket(SocketChannel channel, PacketMessage msg) {
		ByteBuffer buf = SerializeUtil.makeFullPacket(msg);
		
		try {
			channel.write(buf); // 보낸다!
			buf.rewind(); // bytebuf 의 position 초기화 
		} 
		catch(Exception e) {
			LogError("sendResPacket");
		}
		
		buf.clear();
	}
	
	// broadcast 전송
	// socketChannel은 보낸 사람
	private void broadcast(SocketChannel channel, PacketMessage msg) {
		ByteBuffer buf = SerializeUtil.makeFullPacket(msg);
		try {
			for (UserInfo u : users) {
				if (u.getSocketChannel() == channel) // 보내는 클라이언트와 동일하면 continue
					continue;
				
				SocketChannel targetChannel = u.getSocketChannel();
				targetChannel.write(buf);
				buf.rewind();
			}
		}
		catch(Exception e) {
			LogError("broadcast");
		}
		
		buf.clear();
	}
	
	// 클라이언트 연결이 끊어지면 보내는 메시지
	private void clientDisconnected(SocketChannel channel) {
		
		UserInfo deleteUser = null;
		for (UserInfo u : users) {
			if (u.getSocketChannel() == channel) {
				deleteUser = u;
				break;
			}
		}
		
		if (deleteUser == null)
			return;
		
		// 방에서 접속 종료
		RoomInfo delRoom = deleteUser.getRoomInfo();
		if (delRoom != null) {
			PacketMessage outRoomMsg = new PacketMessage();
			outRoomMsg.makeRoomUserLeaveInfo(delRoom, deleteUser);
			
			delRoom.leaveUser(deleteUser);
			
			
			if (delRoom.getUserList().size() == 0) // 방이 0명이 되면 삭제
				deleteRoom(delRoom);
			
			for (UserInfo u : delRoom.getUserList()) {
				sendResPacket(u.getSocketChannel(), outRoomMsg); // 방 유저에게 나갔음을 알려주기
			}
		}
		
		// 접속 종료 처리
		PacketMessage msg = new PacketMessage();
		msg.makeLeaveUserInfo(deleteUser);
		Log("Leave User : " + deleteUser.getName());
		broadcast(channel, msg);
		userVc.remove(deleteUser.getName());
		
		if (useGUI) {
			clientList.setListData(userVc);
			conUsersLabel.setText(userVc.size() + "명");
		}
		
		users.remove(deleteUser);
		
		broadcast(null, msg);
	}
	
	// 로그 textArea에 추가
	public void Log(String str) {
		if (useGUI) {
			textArea.append("INFO : " + str + "\n");
			omokServer.setScrollBottom();
		}
		else {
			System.out.println("INFO : " + str);
		}
		
	}
	
	public void LogError(String str) {
		if (useGUI) {
			textArea.append("ERROR : "+ str + "\n");
			omokServer.setScrollBottom();
		}
		else {
			System.err.println("ERROR : "+ str);
		}
	}
	
	public void stopServer() {
		try {
			selector.close();
			serverChannel.close();
		} catch (IOException e) {
			LogError("서버가 비정상 종료");
		}
	}
	
	public SocketChannel getUserChannel(String id) {
		UserInfo ret = null;
		for (UserInfo u : users) {
			if (u.getName().equals(id))
				ret = u;
		}
		
		return ret.getSocketChannel();
	}
	
	public UserInfo getUserInfo(String id) {
		UserInfo ret = null;
		for (UserInfo u : users) {
			if (u.getName().equals(id))
				ret = u;
		}
		
		return ret;
	}
	
	public RoomInfo getRoomInfo(String id) {
		RoomInfo ret = null;
		for (RoomInfo r : rooms) {
			if (r.getName().equals(id))
				ret = r;
		}
		
		return ret;
	}
	
	public void setClientList(JList<String> clientList) {
		this.clientList = clientList;
	}

	public void setRoomMemList(JList<String> roomMemList) {
		this.roomMemList = roomMemList;
	}

	public void setRoomList(JList<String> roomList) {
		this.roomList = roomList;
	}

	public void setConUsersLabel(JLabel conUsersLabel) {
		this.conUsersLabel = conUsersLabel;
	}

	public void setOmokServer(OmokServer omokServer) {
		this.omokServer = omokServer;
	}
}
