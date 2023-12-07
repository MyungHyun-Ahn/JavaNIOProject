package Packet;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomInfo implements Serializable {
	private static final long serialVersionUID = 1010L;
	private int index;
	private String roomName;
	
	private ArrayList<UserInfo> userList;
	
	// 방 생성 요청 / 입장 전용
	public RoomInfo(String roomName) {
		this.roomName = roomName;
	}
	
	public RoomInfo(int index, String roomName) {
		this.index = index;
		this.roomName = roomName;
		
		userList = new ArrayList<UserInfo>();
	}
	
	public void EnterUser(UserInfo userInfo) {
		userList.add(userInfo);
	}
	
	public void leaveUser(UserInfo userInfo) {
		userList.remove(userInfo);
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public String getName() {
		return this.roomName;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setName(String roomName) {
		this.roomName = roomName;
	}
}
