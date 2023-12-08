package Packet;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

public class UserInfo implements Serializable {
	private static final long serialVersionUID = 1010L;
	
	private String name;
	private transient String room;
	private transient SocketChannel socketChannel = null; // 소켓 채널 저장 용도 - 서버에서만
	
	public UserInfo(String name) {
		this.name = name;
	}
	
	public UserInfo(String name, SocketChannel socketChannel) {
		this.name = name;
		this.setSocketChannel(socketChannel);
	}
	
	
	public String getName() {
		return name;
	}
	
	public String getRoom() {
		return room;
	}
	
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public void setRoom(String room) {
		this.room = room;
	}
}
