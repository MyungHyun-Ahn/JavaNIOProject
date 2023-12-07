package Server;

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

import Packet.PacketCode;
import Packet.PacketMessage;
import Packet.RoomInfo;
import Packet.UserInfo;
import Util.CastingUtil;
import Util.SerializeUtil;

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
	
	private JLabel conUsersLabel;
	
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
			Log("Server Init Error! : " + LocalDateTime.now());
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
					
					if (key.isAcceptable())
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
		catch(Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
			Log("클라이언트 접속 실패! : " + LocalDateTime.now());
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
					return;
				}
				catch (Exception e) {
					e.printStackTrace();
					Log("클라이언트 종료 실패! : " + address);
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
			e.printStackTrace();
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
			case PacketCode.LOGIN_REQ:
				handleLoginReq(channel, msg);
				break;
			case PacketCode.NOTE_REQ:
				handleNoteReq(channel, msg);
				break;
			
			case PacketCode.CHAT_REQ:
				
				break;
			
			}
		}
		
		try {
			// Write를 완료했으면 다시 READ 상태로 전환
			channel.register(selector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void handleLoginReq(SocketChannel channel, PacketMessage msg) {
		PacketMessage resMsg = null;
		 UserInfo userInfo = msg.getUserInfo();
		 
		 for (UserInfo u : users) {
			 if (u.getName().equals(userInfo.getName())) { // 중복 닉네임 거르기
				 resMsg = new PacketMessage();
				 resMsg.makeLoginRes(PacketCode.CONFLICT, userInfo);
				 sendResPacket(channel, resMsg); // response 패킷을 보낸다!
				 break;
			 }
		 }
		 
		 if (resMsg == null) {
			 userInfo.setSocketChannel(channel); // 채널을 저장
			 Log("Enter User : " + userInfo.getName());
			 users.add(userInfo); // userInfo를 저장
			 userVc.add(userInfo.getName());
			 clientList.setListData(userVc);
			 conUsersLabel.setText(userVc.size() + "명");
			 resMsg = new PacketMessage();
			 resMsg.makeLoginRes(PacketCode.SUCCESS, userInfo);
			 sendResPacket(channel, resMsg); // response 패킷을 보낸다!
			 
			 for (UserInfo u : users) {
				 if (u.getName().equals(userInfo.getName()))
					 continue;
				 
				 PacketMessage addUser = new PacketMessage();
				 addUser.makeEnterUserRes(u);
				 sendResPacket(channel, addUser);
			 }
			 
			 // 새로운 유저가 입장했음을 알림 | 유저 리스트 전송
			 // broadcast
			 PacketMessage enterUserRes = new PacketMessage(); 
			 enterUserRes.makeEnterUserRes(userInfo);
			 broadcast(channel, enterUserRes);
		 }
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
			Log(senderInfo.getName() + "가 보낸 쪽지 실패");
			msgRes.makeNoteRes(PacketCode.FAILURE, senderInfo, targetInfo, msg.getNoteMsg());
			return;
		}
		
		Log(senderInfo.getName() + "가 " + targetInfo.getName() + "에게 쪽지 전송");
		msgRes.makeNoteRes(PacketCode.SUCCESS, senderInfo, targetInfo, msg.getNoteMsg());
		sendResPacket(targetInfo.getSocketChannel(), msgRes);
	}
	
	// 응답 패킷 전달 혹은 targetChannel에 전달
	private synchronized void sendResPacket(SocketChannel channel, PacketMessage msg) {
		ByteBuffer buf = SerializeUtil.makeFullPacket(msg);
		
		try {
			channel.write(buf); // 보낸다!
			buf.rewind(); // bytebuf 의 position 초기화 
		} 
		catch(Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		
		buf.clear();
	}
	
	// 클라이언트 연결이 끊어지면 보내는 메시지
	private void clientDisconnected(SocketChannel channel) {
		PacketMessage msg = new PacketMessage();
		
		UserInfo deleteUser = null;
		for (UserInfo u : users) {
			if (u.getSocketChannel() == channel) {
				deleteUser = u;
				break;
			}
		}
		
		if (deleteUser == null)
			return;
		
		msg.makeLeaveUserRes(deleteUser);
		Log("Leave User : " + deleteUser.getName());
		broadcast(channel, msg);
		userVc.remove(deleteUser.getName());
		clientList.setListData(userVc);
		conUsersLabel.setText(userVc.size() + "명");
		users.remove(deleteUser);
		
		// TODO : 접속 중인 방에서도 지워주기
		// room remove
		
		broadcast(null, msg);
	}
	
	// 로그 textArea에 추가
	private void Log(String str) {
		textArea.append(str + "\n");
	}
	
	public void stopServer() {
		try {
			selector.close();
			serverChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}