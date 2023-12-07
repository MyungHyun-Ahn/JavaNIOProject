package Packet;

// Packet 의 종류 정의
public class PacketCode {
	public final static short DEFAULT = 0;				// DEFAULT
	
	// 1x 패킷 : 기본
	public final static short LOGIN_REQ = 10;			// LOGIN 요청
	public final static short LOGIN_RES = 11;			// LOGIN 응답
	public final static short ENTERUSER_RES = 110;		// 새로운 USER 입장
	public final static short LEAVEUSER_RES = 111;		// USER가 연결이 끊겼을 때
	public final static short CHAT_REQ = 12;			// 채팅 요청
	public final static short CHAT_RES = 13;			// 채팅 응답
	public final static short CHAT_NOTI = 120;			// 채팅 broadcast
	public final static short USERLIST_RES = 14;		// 유저 목록 요청
	public final static short ROOMLIST_RES = 15;		// 방 목록 요청
	
	// 2x 패킷 : Room 관련
	public final static short JOINROOM_REQ = 20;		// 방 입장 요청 - 방이 없으면 생성
	public final static short JOINROOM_RES = 21;		// 방 입장 응답 - 없는 방 혹은 중복 방일 시 실패
	public final static short ROOMUSERLIST_REQ = 22;	// 방 참여자 목록 요청
	
	// 3x 패킷 : 쪽지 관련
	public final static short NOTE_REQ = 31;			// 쪽지 요청
	public final static short NOTE_RES = 32;			// 쪽지 응답
	
	public final static short EXIT = -1;
	
	// 응답 패킷에 넣을 상태 코드
	public final static short SUCCESS = 200; 			// 성공
	public final static short FAILURE = 500; 			// 실패
	public final static short CONFLICT = 409;			// 중복
	
}
