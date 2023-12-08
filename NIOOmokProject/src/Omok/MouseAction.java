package Omok;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

public class MouseAction extends MouseAdapter{
	private MapManager map;
	private GameLogic gameLogic;
	private OmokGUI omokGUI;
	private int n = 0;
	
	
	public MouseAction(MapManager map, GameLogic gameLogic, OmokGUI omokGUI) {
		this.map = map;
		this.gameLogic = gameLogic;
		this.omokGUI = omokGUI;
	}
	
	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		// 마우스 좌표 기준으로 x, y 값 계산
		// X - (보드 판 좌표) / CELL 크기 - 1
		int x = (int) Math.round((mouseEvent.getX() - 10)/ (double) MapDefine.CELL) - 1;
		int y = (int) Math.round((mouseEvent.getY() - 10) / (double) MapDefine.CELL) - 2;
		
		if (!gameLogic.checkInput(y, x)) {
			// PopUp 잘못된 위치 - 다시 놔주세요
			return;
		}
		
		gameLogic.input(y, x);
		map.repaint();
		
		int checkWin = gameLogic.checkWinner(y, x);
		
		
		if (checkWin != 0) {
			if (checkWin == 1) {
				showWin("1P Win");
			}
			if (checkWin == 2) {
				showWin("2P Win");
			}
		}
		else {
			gameLogic.isMyTurn(); // 턴 증가
		}
	}
	
	private void showWin(String msg) {
		JOptionPane.showMessageDialog(omokGUI, msg, "", JOptionPane.INFORMATION_MESSAGE);
	}
}
