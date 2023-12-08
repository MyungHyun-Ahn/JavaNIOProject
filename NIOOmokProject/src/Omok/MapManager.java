package Omok;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MapManager extends JPanel{
	private static final long serialVersionUID = 1L;
	private GameLogic gameLogic;
	private final int STONE_SIZE=26; //돌 사이즈
	
	public MapManager(GameLogic gameLogic) {
		setBackground(new Color(206,167,61)); 
		setLayout(null);
		this.gameLogic = gameLogic;
	}
	
	public void init() {
		gameLogic.init(); // 초기화
	}
	
	
	// 그리기 컴포넌트
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK); 
		board(g);
		drawStone(g); 
	}
	
	// 오목판 그리기
	public void board(Graphics g) {
		for(int i = 1; i <= MapDefine.SIZE; i++) {
			g.drawLine(MapDefine.CELL, i * MapDefine.CELL, MapDefine.CELL * MapDefine.SIZE, i * MapDefine.CELL); 
			g.drawLine(i * MapDefine.CELL, MapDefine.CELL, i * MapDefine.CELL , MapDefine.CELL * MapDefine.SIZE); 
		}
	}
	
	
	// 돌 그리기
	public void drawStone(Graphics g) {
			for(int y=0; y < MapDefine.SIZE; y++){
				for(int x = 0; x < MapDefine.SIZE; x++){
					if(gameLogic.getOmokMap()[y][x]==1)
						drawBlack(g,x,y);
					else if(gameLogic.getOmokMap()[y][x]==2)
						drawWhite(g, x, y);
				}
			}
	}

	public void drawBlack(Graphics g, int x, int y) {
		g.setColor(Color.BLACK);
		g.fillOval(x * MapDefine.CELL + 14, y * MapDefine.CELL + 14, STONE_SIZE, STONE_SIZE);
	}
	
	public void drawWhite(Graphics g, int x, int y) {
		g.setColor(Color.WHITE);
		g.fillOval(x * MapDefine.CELL + 14, y * MapDefine.CELL + 14, STONE_SIZE, STONE_SIZE);
	}
	
}