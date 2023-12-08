package Omok;

public class GameLogic {
	private final int MAX_SIZE = MapDefine.SIZE;
	private final int PLAYER_CNT = 2;
		
	private int omokMap[][] = new int[MAX_SIZE][MAX_SIZE];
	private int turn = 0;
	
	public GameLogic() {
		
	}
	
	// 맵 정보 초기화 - 모두 0으로
	public void init() {
		for (int y = 0; y < MAX_SIZE; ++y) {
			for (int x = 0; x < MAX_SIZE; ++x) {
				omokMap[y][x] = 0;
			}
		}
	}
	
	// 지금은 번갈아가며 턴 계산 - 이후 서버에서 턴 정보 받아서 수행
	public boolean isMyTurn() {
		if (++turn % PLAYER_CNT == 0) {
			return true;
		}
		
		return false;
	}
	
	
	// 놓은 자리가 적합한지 판단.
	public boolean checkInput(int y, int x) {
		if (omokMap[y][x] != 0 || y < 0 || y >= MAX_SIZE || x < 0 || x >= MAX_SIZE) {
			return false;
		}
		
		return true;
	}
	
	// 1p : 1, 2p : 2
	public void input(int y, int x) {
		omokMap[y][x] = turn % 2 + 1;
	}
	
	// 승자 판별 로직 - 이후 서버로 옮길 것
	public int checkWinner(int y, int x) {
        int currentPlayer = omokMap[y][x];

        // 가로 확인
        for (int i = Math.max(0, x - 4); i <= Math.min(MAX_SIZE - 1, x + 4); i++) {
            if (omokMap[y][i] == currentPlayer &&
            	omokMap[y][i + 1] == currentPlayer &&
            	omokMap[y][i + 2] == currentPlayer &&
            	omokMap[y][i + 3] == currentPlayer &&
            	omokMap[y][i + 4] == currentPlayer) {
                return currentPlayer;
            }
        }

        // 세로 확인
        for (int i = Math.max(0, y - 4); i <= Math.min(MAX_SIZE - 1, y + 4); i++) {
            if (omokMap[i][x] == currentPlayer &&
            	omokMap[i + 1][x] == currentPlayer &&
            	omokMap[i + 2][x] == currentPlayer &&
            	omokMap[i + 3][x] == currentPlayer &&
            	omokMap[i + 4][x] == currentPlayer) {
                return currentPlayer;
            }
        }

        // 대각선(↗) 확인
        for (int i = -4; i <= 0; i++) {
            if (y + i >= 0 && y + i + 4 < MAX_SIZE && x + i >= 0 && x + i + 4 < MAX_SIZE) {
                if (omokMap[y + i][x + i] == currentPlayer &&
                	omokMap[y + i + 1][x + i + 1] == currentPlayer &&
                	omokMap[y + i + 2][x + i + 2] == currentPlayer &&
                	omokMap[y + i + 3][x + i + 3] == currentPlayer &&
                	omokMap[y + i + 4][x + i + 4] == currentPlayer) {
                    return currentPlayer;
                }
            }
        }

        // 대각선(↘) 확인
        for (int i = -4; i <= 0; i++) {
            if (y + i >= 0 && y + i + 4 < MAX_SIZE && x - i >= 4 && x - i + 4 < MAX_SIZE) {
                if (omokMap[y + i][x - i] == currentPlayer &&
                	omokMap[y + i + 1][x] == currentPlayer &&
                	omokMap[y + i + 2][x - i - 2] == currentPlayer &&
                	omokMap[y + i + 3][x - i - 3] == currentPlayer &&
                	omokMap[y + i + 4][x - i - 4] == currentPlayer) {
                    return currentPlayer;
                }
            }
        }
        
        return 0;
    }
	
	public int[][] getOmokMap() {
		return omokMap;
	}
}
