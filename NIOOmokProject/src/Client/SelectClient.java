package Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import Packet.PacketCode;
import Packet.PacketMessage;
import Util.CastingUtil;
import Util.SerializeUtil;

public class SelectClient implements Runnable  {
	private String host;
	private int port;
	
	private Selector selector = null;
	private SocketChannel channel = null;
	
	private SocketSender sender;
	private Thread senderThread;
	
	private JList<String> User_List;
	private JList<String> Room_List;
	
	private JTextArea chatArea;
	
	private OmokClient omokClient;
	Vector<String> user_list = new Vector<>(); // 가입자 목록
	Vector<String> room_list = new Vector<>(); // 채팅방 목록
	
	public SelectClient(String host, int port) {
		try {
			selector = Selector.open();
			channel = SocketChannel.open(new InetSocketAddress(host, port));
			channel.configureBlocking(false); // non-blocking 설정
			channel.register(selector, SelectionKey.OP_READ);
			
			sender = new SocketSender(channel);
			senderThread = new Thread(sender);
			senderThread.start();
			
			System.out.println("Socket Sender is Started!");
		}
		catch (IOException e) {
            System.out.println("init()" + e);
        }
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
            while (channel.isOpen()) {
                // 셀렉터의 select() 메서드로 준비된 이벤트가 있는지 확인한다.
                selector.select();

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    
                    if (key.isReadable()) {
                        Read(key);
                    }
                } 
            }
        } catch (Exception e) {
            System.out.println("start()" + e);
        }
	}
	
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
					// Server off // 접속 끊기
					channel.close();
					return;
				}
				catch (Exception e) {
					e.printStackTrace();
					// Log("클라이언트 종료 실패! : " + address);
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
			// clientDisconnected(channel);
			return;
		}
		
		buf.flip();
		byte[] bytes = new byte[buf.limit()]; // 바이트 버퍼에 받은 것을 저장할 바이트 배열
		buf.get(bytes); // 바이트 배열에 데이터 저장
		PacketMessage msg = SerializeUtil.deserialize(bytes); // 역직렬화
		if (msg == null) return; // msg 가 null 일 시 pass
		
		switch (msg.getMsgCode())
		{
		case PacketCode.LOGIN_RES:
			handleLoginRes(msg);
			break;
		case PacketCode.ENTERUSER_RES:
			handleEnterUserRes(msg);
			break;
		case PacketCode.LEAVEUSER_RES:
			handleLeaveUserRes(msg);
			break;
		case PacketCode.NOTE_RES:
			handleNoteRes(msg);
			break;
		case PacketCode.CHAT_RES:
			break;
		case PacketCode.CHAT_NOTI:
			handleChatNoti(msg);
			break;
		case PacketCode.JOINROOM_RES:
			break;
		}
		
		if (buf != null)
		{
			buf.clear();
			buf = null;
		}
		
		bytes = null;
    }
	
	private void handleLoginRes(PacketMessage msg) {
		short statusCode = msg.getStatusCode();
		if (statusCode == PacketCode.SUCCESS) {
			System.out.println("login success!");
			user_list.add(msg.getUserInfo().getName());
			User_List.setListData(user_list);
		}
		if (statusCode == PacketCode.CONFLICT) {
			System.out.println("중복된 아이디");
			JOptionPane.showMessageDialog(omokClient, "중복된 아이디", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void handleLeaveUserRes(PacketMessage msg) {
		String deleteUser = msg.getUserInfo().getName();
		
		user_list.remove(deleteUser);
		User_List.setListData(user_list);
	}
	
	private void handleEnterUserRes(PacketMessage msg) {
		user_list.add(msg.getUserInfo().getName());
		User_List.setListData(user_list);
	}
	
	private void handleNoteRes(PacketMessage msg) {
		if (msg.getStatusCode() == PacketCode.SUCCESS) {
			String sender = msg.getUserInfo().getName();
			String note = msg.getNoteMsg();
			JOptionPane.showMessageDialog(omokClient, note, sender + "가 보낸 메시지", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(omokClient, "쪽지 전송 실패", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	private void handleChatNoti(PacketMessage msg) {
		chatArea.append(msg.getChatMsg());
	}
	
	public void sendPacket(PacketMessage msg) {
		sender.sendPacket(msg);
		System.out.println("Packet enqueue : " + msg.getUserInfo().getName());
	}

	public void stopClient() {
		try {
			selector.close();
			channel.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Getter Setter
	public JList<String> getUser_List() {
		return User_List;
	}

	public void setUser_List(JList<String> user_List) {
		this.User_List = user_List;
	}

	public JList<String> getRoom_List() {
		return Room_List;
	}

	public void setRoom_List(JList<String> room_List) {
		this.Room_List = room_List;
	}

	public void setOmokClient(OmokClient omokClient) {
		this.omokClient = omokClient;
	}

	public void setChatArea(JTextArea chatArea) {
		this.chatArea = chatArea;
	}
}
