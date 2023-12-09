package Omok;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import Client.SelectClient;

public class MouseAction extends MouseAdapter{
	private MapManager map;
	private GameLogic gameLogic;
	private OmokGUI omokGUI;
	private SelectClient selectClient;
	private int n = 0;
	
	
	public MouseAction(MapManager map, GameLogic gameLogic, OmokGUI omokGUI, SelectClient selectClient) {
		this.map = map;
		this.gameLogic = gameLogic;
		this.omokGUI = omokGUI;
		this.selectClient = selectClient;
	}
	
	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		if (gameLogic.isMyTurn()) { // 자기 턴이 아니면 무반응
			return;
		}
		
		// 마우스 좌표 기준으로 x, y 값 계산
		// X - (보드 판 좌표) / CELL 크기 - 1
		int x = (int) Math.round((mouseEvent.getX() - 10)/ (double) GameDefine.CELL) - 1;
		int y = (int) Math.round((mouseEvent.getY() - 10) / (double) GameDefine.CELL) - 2;
		
		if (!gameLogic.checkInput(y, x)) {
			// 잘못된 위치
			return;
		}
		
		gameLogic.input(y, x);
		map.repaint();
	}
}
