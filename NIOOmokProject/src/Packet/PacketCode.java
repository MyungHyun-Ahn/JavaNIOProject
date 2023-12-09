package Packet;

// Packet 의 종류 정의
public class PacketCode {
	public final static short DEFAULT = 0;				// DEFAULT
	public final static short EXIT = -1;				// 프로그램 종료 시
	
	// REQ  : request       - 요청 패킷 서버 -> 클라
	// RES  : response      - 응답 패킷 클라 -> 서버
	// NOTI : notification  - 알림 패킷 서버 -> 브로드캐스트
	// INFO : information   - 정보 전달 패킷 서버 -> 클라  
	
	// 1x 패킷 : 기본
	public final static short LOGIN_REQ = 10;			// LOGIN 요청
	public final static short LOGIN_RES = 11;			// LOGIN 응답
	public final static short ENTERUSER_INFO = 110;		// 새로운 USER 입장
	public final static short LEAVEUSER_INFO = 111;		// USER가 연결이 끊겼을 때
	public final static short CHAT_REQ = 12;			// 채팅 요청
	public final static short CHAT_RES = 13;			// 채팅 응답
	public final static short CHAT_NOTI = 120;			// 채팅 broadcast
	public final static short ROOMINFO_INFO = 14; 		// 방 정보 전달
	public final static short ROOMDEL_INFO = 15;		// 방 삭제 정보 전달
	
	// 2x 패킷 : Room 관련
	public final static short CREATEROOM_REQ = 21;		// 방 생성 요청
	public final static short CREATEROOM_RES = 22;		// 방 생성 응답
	public final static short ENTERROOM_REQ = 23;		// 방 입장 요청
	public final static short ENTERROOM_RES = 24;		// 방 입장 응답 - 없는 방 혹은 중복 방일 시 실패
	public final static short LEAVEROOM_REQ = 25;		// 방 퇴장 요청
	public final static short LEAVEROOM_RES = 26;		// 방 퇴장 요청 응답
	
	public final static short ROOMUSERENTER_INFO = 261; // 방 유저 입장 알림
	public final static short ROOMUSERLEAVE_INFO = 262; // 방 유저 퇴장 알림
	
	public final static short ROOMCHAT_REQ = 27;        // 방 채팅 요청
	public final static short ROOMCHAT_NOTI = 28;		// 방 채팅 응답
	
	// 3x 패킷 : 쪽지 관련
	public final static short NOTE_REQ = 31;			// 쪽지 요청
	public final static short NOTE_RES = 32;			// 쪽지 응답
	
	// 4x 패킷 : 오목 게임 패킷
	public final static short GAMESTART_REQ = 41; 		// 게임 시작 요청
	public final static short GAMESTART_RES = 42; 		// 게임 시작 응답
	public final static short GAMEINPUT_REQ = 43; 		// 바둑돌 놓기 요청
	public final static short GAMEINPUT_RES = 44; 		// 바둑돌 놓기 응답
	public final static short GAMERESULT_INFO = 45; 	// 바둑돌 정보 - 바둑돌을 놓을 때마다 전송
	
	// 응답 패킷에 넣을 상태 코드
	public final static short SUCCESS = 200; 			// 성공
	public final static short FAILURE = 500; 			// 실패
	public final static short CONFLICT = 409;			// 중복
	public final static short ISROOMFULL = 410;			// 방이 가득참
	
	// 게임 관련 상태 코드
	public final static short NONE = 0;					// 게임 진행 중인 상황
	public final static short WINNER = 800;				// 게임이 끝나면 승자에게 전송
	public final static short LOSER = 900;				// 게임이 끝나면 패자에게 전송
	
	public final static short COLOR_BLACK = 1; 			// 검은 돌
	public final static short COLOR_WHITE = 2;			// 흰 돌
}
