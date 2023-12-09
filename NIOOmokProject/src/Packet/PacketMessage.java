package Packet;

import java.io.Serializable;
import java.util.ArrayList;

import Util.SerializeUtil;

public class PacketMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private short msgCode = PacketCode.DEFAULT;
	private short statusCode = PacketCode.DEFAULT;
	private short colorCode = PacketCode.DEFAULT;
	
	private String chatMsg; 			// 채팅 메시지일 경우
	private String noteMsg; 			// 쪽지 메시지일 경우
	
	private UserInfo userInfo; 			// 누가 보냈는지
	private UserInfo targetUserInfo; 	// 쪽지를 보낼 경우 받을 사람
	private RoomInfo roomInfo;			// 방 정보
	
	private short[] userInput;			// 오목 게임 좌표 정보
	
	private ArrayList<UserInfo> userList; // 유저 리스트
	private ArrayList<RoomInfo> roomList; // 방 리스트
	
	private transient byte[] serializedBytes; // 직렬화가 마무리된 바이트 스트림
	
	public PacketMessage() { }
	
	// 기본 패킷 생성
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
	
	public void makeEnterUserInfo(UserInfo userInfo) {
		setMsgCode(PacketCode.ENTERUSER_INFO);
		setUserInfo(userInfo);
		setStatusCode(PacketCode.SUCCESS);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeLeaveUserInfo(UserInfo userInfo) {
		setMsgCode(PacketCode.LEAVEUSER_INFO);
		setUserInfo(userInfo);
		setStatusCode(PacketCode.SUCCESS);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
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
	
	public void makeChatNoti(String msg) {
		setMsgCode(PacketCode.CHAT_NOTI);
		setChatMsg(msg);
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
	
	public void makeRoomInfoInfo(RoomInfo roomInfo) {
		setMsgCode(PacketCode.ROOMINFO_INFO);
		setRoomInfo(roomInfo);
		setStatusCode(PacketCode.SUCCESS);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeRoomDelInfo(RoomInfo roomInfo) {
		setMsgCode(PacketCode.ROOMDEL_INFO);
		setRoomInfo(roomInfo);
		setStatusCode(PacketCode.SUCCESS);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	
	// Room 관련 패킷 생성
	public void makeCreateRoomReq(RoomInfo roomInfo, UserInfo userInfo) {
		setMsgCode(PacketCode.CREATEROOM_REQ);
		setRoomInfo(roomInfo);
		setUserInfo(userInfo);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeCreateRoomRes(short statusCode, RoomInfo roomInfo) {
		setMsgCode(PacketCode.CREATEROOM_RES);
		setStatusCode(statusCode);
		setRoomInfo(roomInfo);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeEnterRoomReq(RoomInfo roomInfo, UserInfo userInfo) {
		setMsgCode(PacketCode.ENTERROOM_REQ);
		setRoomInfo(roomInfo);
		setUserInfo(userInfo);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeEnterRoomRes(short statusCode, RoomInfo roomInfo) {
		setMsgCode(PacketCode.ENTERROOM_RES);
		setRoomInfo(roomInfo);
		setStatusCode(statusCode);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeLeaveRoomReq(RoomInfo roomInfo, UserInfo userInfo) {
		setMsgCode(PacketCode.LEAVEROOM_REQ);
		setRoomInfo(roomInfo);
		setUserInfo(userInfo);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeLeaveRoomRes(short statusCode, RoomInfo roomInfo) {
		setMsgCode(PacketCode.LEAVEROOM_RES);
		setRoomInfo(roomInfo);
		setStatusCode(statusCode);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeRoomUserEnterInfo(RoomInfo roomInfo, UserInfo userInfo) {
		setMsgCode(PacketCode.ROOMUSERENTER_INFO);
		setRoomInfo(roomInfo);
		setUserInfo(userInfo);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeRoomUserLeaveInfo(RoomInfo roomInfo, UserInfo userInfo) {
		setMsgCode(PacketCode.ROOMUSERLEAVE_INFO);
		setRoomInfo(roomInfo);
		setUserInfo(userInfo);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeRoomChatReq(RoomInfo roomInfo, UserInfo userInfo, String chatMsg) {
		setMsgCode(PacketCode.ROOMCHAT_REQ);
		setRoomInfo(roomInfo);
		setUserInfo(userInfo);
		setChatMsg(chatMsg);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeRoomChatNoti(short statusCode, RoomInfo roomInfo, UserInfo userInfo, String chatMsg) {
		setMsgCode(PacketCode.ROOMCHAT_NOTI);
		setRoomInfo(roomInfo);
		setUserInfo(userInfo);
		setChatMsg(chatMsg);
		setStatusCode(statusCode);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	// 게임 관련
	public void makeGameStartReq(RoomInfo roomInfo, UserInfo userInfo) {
		setMsgCode(PacketCode.GAMESTART_REQ);
		setRoomInfo(roomInfo);
		setUserInfo(userInfo);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeGameStartRes(short statusCode, RoomInfo roomInfo, UserInfo startUser, short colorCode) {
		setMsgCode(PacketCode.GAMESTART_RES);
		setRoomInfo(roomInfo);
		setUserInfo(startUser);
		setColorCode(colorCode);
		setStatusCode(statusCode);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeGameInputReq(RoomInfo roomInfo, UserInfo userInfo, short colorCode, short[] userInput) {
		setMsgCode(PacketCode.GAMEINPUT_REQ);
		setRoomInfo(roomInfo);
		setUserInfo(userInfo);
		setColorCode(colorCode);
		setUserInput(userInput);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	public void makeGameInputRes(short statusCode) {
		setMsgCode(PacketCode.GAMEINPUT_RES);
		setStatusCode(statusCode);
		serializedBytes = SerializeUtil.serialize(this);
	}
	
	// 어디다 뒀는지 + 결과 전달
	public void makeGameResultInfo(short gameResult, RoomInfo roomInfo, UserInfo userInfo, short colorCode, short[] userInput) {
		setMsgCode(PacketCode.GAMERESULT_INFO);
		setRoomInfo(roomInfo);
		setUserInfo(userInfo);
		setColorCode(colorCode);
		setStatusCode(gameResult);
		setUserInput(userInput);
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

	public short getColorCode() {
		return colorCode;
	}

	public void setColorCode(short colorCode) {
		this.colorCode = colorCode;
	}

	public short[] getUserInput() {
		return userInput;
	}

	public void setUserInput(short[] userInput) {
		this.userInput = userInput;
	}
}
