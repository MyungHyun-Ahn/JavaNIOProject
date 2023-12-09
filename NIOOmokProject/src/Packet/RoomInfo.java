package Packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import Omok.GameDefine;
import Omok.GameLogic;

public class RoomInfo implements Serializable {
	private static final long serialVersionUID = 1010L;
	public final static int MAX_USER_COUNT = 2; // 2명까지 입장 가능
	
	private String roomName;
	private boolean gameRunning;
	
	private transient int[][] omokMap = new int[GameDefine.SIZE][GameDefine.SIZE];
	private transient ArrayList<UserInfo> userList;
	
	// 방 생성 요청 전용
	public RoomInfo(String roomName) {
		this.roomName = roomName;
		
		userList = new ArrayList<UserInfo>();
	}
	
	public void EnterUser(UserInfo userInfo) {
		userList.add(userInfo);
		userInfo.setRoomInfo(this);
	}
	
	public void leaveUser(UserInfo userInfo) {
		userList.remove(userInfo);
		userInfo.setRoomInfo(null);
	}
	
	public void initOmokMap() {
		for (int y = 0; y < GameDefine.SIZE; y++) {
			for (int x = 0; x < GameDefine.SIZE; x++) {
				omokMap[y][x] = 0;
			}
		}
	}
	
	public short setOmokPos(short color, int y, int x) {
		omokMap[y][x] = (int)color;
		
		return (short) GameLogic.checkWinner(omokMap, color, y, x);
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
	
	public Vector<String> getUserVc() {
		Vector<String> ret = new Vector<String>();
		for (UserInfo u : userList) {
			ret.add(u.getName());
		}
		
		return ret;
	}

	public boolean isGameRunning() {
		return gameRunning;
	}

	public void setGameRunning(boolean gameRunning) {
		this.gameRunning = gameRunning;
	}
}
