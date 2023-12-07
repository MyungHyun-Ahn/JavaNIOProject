package Packet;

import java.io.Serializable;
import java.util.ArrayList;

import Util.SerializeUtil;

public class PacketMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private short msgCode = PacketCode.DEFAULT;
	private short statusCode = PacketCode.DEFAULT;
	private String chatMsg; 			// 채팅 메시지일 경우
	private String noteMsg; 			// 쪽지 메시지일 경우
	
	private UserInfo userInfo; 			// 누가 보냈는지
	private UserInfo targetUserInfo; 	// 쪽지를 보낼 경우 받을 사람
	private RoomInfo roomInfo;
	
	private ArrayList<UserInfo> userList; // 유저 리스트
	private ArrayList<RoomInfo> roomList; // 방 리스트
	
	private transient byte[] serializedBytes; // 직렬화가 마무리된 바이트 스트림
	
	public PacketMessage() { }
	
	public void makeChatReq(UserInfo userInfo, String msg) {
		setMsgCode(PacketCode.CHAT_REQ);
		setChatMsg(msg);
		setUserInfo(userInfo);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeChatRes(short statusCode) {
		setMsgCode(PacketCode.CHAT_RES);
		setStatusCode(statusCode);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeLoginReq(UserInfo userInfo) {
		setMsgCode(PacketCode.LOGIN_REQ);
		setUserInfo(userInfo);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeLoginRes(short statusCode, UserInfo userInfo) {
		setMsgCode(PacketCode.LOGIN_RES);
		setUserInfo(userInfo);
		setStatusCode(statusCode);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeEnterUserRes(UserInfo userInfo) {
		setMsgCode(PacketCode.ENTERUSER_RES);
		setUserInfo(userInfo);
		setStatusCode(PacketCode.SUCCESS);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeLeaveUserRes(UserInfo userInfo) {
		setMsgCode(PacketCode.LEAVEUSER_RES);
		setUserInfo(userInfo);
		setStatusCode(PacketCode.SUCCESS);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeJoinRoomReq(UserInfo userInfo, RoomInfo roomInfo) {
		setMsgCode(PacketCode.JOINROOM_REQ);
		setUserInfo(userInfo);
		setRoomInfo(roomInfo);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeJoinRoomRes(RoomInfo roomInfo, short statusCode) {
		setMsgCode(PacketCode.JOINROOM_RES);
		setRoomInfo(roomInfo);
		setStatusCode(statusCode);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeUserListRes(ArrayList<UserInfo> userList) {
		setMsgCode(PacketCode.USERLIST_RES);
		setUserList(userList);
		setStatusCode(PacketCode.SUCCESS);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeRoomListRes(ArrayList<RoomInfo> roomList) {
		setMsgCode(PacketCode.ROOMLIST_RES);
		setRoomList(roomList);
		setStatusCode(PacketCode.SUCCESS);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeNoteReq(UserInfo sendUser, UserInfo targetUser, String note) {
		setMsgCode(PacketCode.NOTE_REQ);
		setUserInfo(sendUser);
		setTargetUserInfo(targetUser);
		setNoteMsg(note);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeNoteRes(short statusCode, UserInfo sendUser, UserInfo targetUser, String note) {
		setMsgCode(PacketCode.NOTE_RES);
		setUserInfo(sendUser);
		setTargetUserInfo(targetUser);
		setNoteMsg(note);
		setStatusCode(statusCode);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	// Getter
	public short getMsgCode() {
		return this.msgCode;
	}
	
	public short getStatusCode() {
		return statusCode;
	}
	
	public String getChatMsg() {
		return this.chatMsg;
	}
	
	public String getNoteMsg() {
		return this.noteMsg;
	}
	
	public UserInfo getUserInfo() {
		return this.userInfo;
	}
	
	public RoomInfo getRoomInfo() {
		return roomInfo;
	}
	
	public ArrayList<UserInfo> getUserList() {
		return this.userList;
	}
	
	public ArrayList<RoomInfo> getRoomList() {
		return this.roomList;
	}
	
	public byte[] getSerializedBytes() {
		return serializedBytes;
	}
	
	public UserInfo getTargetUserInfo() {
		return targetUserInfo;
	}
	
	// Setter
	public void setMsgCode(short msgCode) {
		this.msgCode = msgCode;
	}
	
	public void setStatusCode(short statusCode) {
		this.statusCode = statusCode;
	}
	
	public void setChatMsg(String chatMsg) {
		this.chatMsg = chatMsg;
	}
	
	public void setNoteMsg(String noteMsg) {
		this.noteMsg = noteMsg;
	}
	
	public void setUserInfo(UserInfo user) {
		this.userInfo = user;
	}
	
	public void setRoomInfo(RoomInfo roomInfo) {
		this.roomInfo = roomInfo;
	}
	
	public void setUserList(ArrayList<UserInfo> userList) {
		this.userList = userList;
	}
	
	public void setRoomList(ArrayList<RoomInfo> roomList) {
		this.roomList = roomList;
	}
	
	public void setSerializedBytes(byte[] data) {
		this.serializedBytes = data;
	}

	public void setTargetUserInfo(UserInfo targetUserInfo) {
		this.targetUserInfo = targetUserInfo;
	}
}