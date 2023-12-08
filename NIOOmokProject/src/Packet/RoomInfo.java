package Packet;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomInfo implements Serializable {
	private static final long serialVersionUID = 1010L;
	public final static int MAX_USER_COUNT = 2; // 2명까지 입장 가능
	
	private String roomName;
	
	private transient ArrayList<UserInfo> userList;
	
	// 방 생성 요청 전용
	public RoomInfo(String roomName) {
		this.roomName = roomName;
		
		userList = new ArrayList<UserInfo>();
	}
	
	public void EnterUser(UserInfo userInfo) {
		userList.add(userInfo);
	}
	
	public void leaveUser(UserInfo userInfo) {
		userList.remove(userInfo);
	}
	
	public String getName() {
		return this.roomName;
	}
	
	public void setName(String roomName) {
		this.roomName = roomName;
	}
	
	public boolean isRoomFull() {
		return userList.size() >= 2;
	}
	
	public ArrayList<UserInfo> getUserList() {
		return userList;
	}
}
