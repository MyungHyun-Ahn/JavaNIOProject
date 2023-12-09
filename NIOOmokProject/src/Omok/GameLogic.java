package Omok;

import Client.SelectClient;
import Packet.PacketMessage;
import Packet.RoomInfo;
import Packet.UserInfo;

public class GameLogic {
	private final static int MAX_SIZE = GameDefine.SIZE;
	private final static int PLAYER_CNT = 2;
	
	// 오목 승리 판정에 사용할 방향 값
	private final static int DIR[][] = { { 1, 0 }, { 0, 1 }, { 1, 1 }, { 1, -1 } };
		
	private int omokMap[][] = new int[MAX_SIZE][MAX_SIZE];
	private int turn = 0;
	private int color = -1;
	
	private boolean gameRunning = false;
	private SelectClient selectClient;
	private RoomInfo myRoom;
	private UserInfo myUser;
	private MapManager map;

	public GameLogic() {
	}
	
	// 맵 정보 초기화 - 모두 0으로
	public void init(RoomInfo roomInfo, UserInfo userInfo, MapManager map) {
		this.setMyRoom(roomInfo);
		this.setMyUser(userInfo);
		this.setMap(map);
		
		setTurn(1);
		for (int y = 0; y < MAX_SIZE; ++y) {
			for (int x = 0; x < MAX_SIZE; ++x) {
				omokMap[y][x] = 0;
			}
		}
	}

	// 턴 계산
	public boolean isMyTurn() { // 1 2 1 2
		if ((turn % PLAYER_CNT + 1) == color) {
			return true;
		}
		
		return false;
	}
	
	
	// 놓은 자리가 적합한지 판단.
	public boolean checkInput(int y, int x) {
		if (y < 0 || y >= MAX_SIZE || x < 0 || x >= MAX_SIZE || omokMap[y][x] != 0)
			return false;
		
		return true;
	}
	
	// 1p : 1, 2p : 2
	public void input(int y, int x) {
		PacketMessage sendMyInput = new PacketMessage();
		sendMyInput.makeGameInputReq(myRoom, myUser, (short)color, new short[]{(short)y, (short)x});
		selectClient.sendPacket(sendMyInput);
		omokMap[y][x] = color;
	}
	
	// 승자 판별 로직 - 서버에서만 판정 진행
	public static int checkWinner(int[][] omokMap, short color, int y, int x) {
		int nowColor = (int)color;
		
		for (int i = 0; i < 4; i++) {
			if (checkDirection(omokMap, nowColor, y, x, DIR[i][1], DIR[i][0])) {
				return nowColor;
			}
		}
		
		return 0;
        
    }
	
	private static boolean checkDirection(int[][] omokMap, int color, int y, int x, int dy, int dx) {
		int count = 1; // 현재 위치의 돌부터 시작하므로 1로 초기화

        // 오른쪽 또는 아래 방향으로 확인
        for (int i = 1; i <= 19; i++) {
            int ny = y + i * dy;
            int nx = x + i * dx;

            if (ny < 0 || ny >= MAX_SIZE || nx < 0 || nx >= MAX_SIZE || omokMap[ny][nx] != color) {
                break; // 범위를 벗어나거나 검은 돌이 아닌 경우 중단
            }

            count++;
        }

        // 왼쪽 또는 위쪽 방향으로 확인
        for (int i = 1; i <= 19; i++) {
            int ny = y - i * dy;
            int nx = x - i * dx;

            if (ny < 0 || ny >= MAX_SIZE || nx < 0 || nx >= MAX_SIZE || omokMap[ny][nx] != color) {
                break; // 범위를 벗어나거나 검은 돌이 아닌 경우 중단
            }

            count++;
        }

        return count == 5; // 5개 이상의 돌이 연속으로 놓였는지 확인
	}
	
	public int[][] getOmokMap() {
		return omokMap;
	}
	
	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public RoomInfo getMyRoom() {
		return myRoom;
	}

	public void setMyRoom(RoomInfo myRoom) {
		this.myRoom = myRoom;
	}

	public UserInfo getMyUser() {
		return myUser;
	}

	public void setMyUser(UserInfo myUser) {
		this.myUser = myUser;
	}
	
	public void setSelectClient(SelectClient selectClient) {
		this.selectClient = selectClient;
	}
	
	public void setPos(short color, int y, int x) {
		omokMap[y][x] = (int) color;
		map.repaint();
	}
	
	public void setMap(MapManager map) {
		this.map = map;
	}

	public boolean isGameRunning() {
		return gameRunning;
	}

	public void setGameRunning(boolean gameRunning) {
		this.gameRunning = gameRunning;
	}
}
