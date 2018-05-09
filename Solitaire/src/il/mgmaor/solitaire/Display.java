package il.mgmaor.solitaire;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.Stack;

import javax.swing.JPanel;

public class Display extends JPanel {
	
	private static final long	    serialVersionUID	= 1L;
	public  static final int		CARD_WIDTH		    = 100;
	public  static final int		CARD_HEIGHT		    = 140;
	public  static final int		SPACE			    = 10;
	public  static final int		STOCK_X			    = 10 + SPACE;
	public  static final int		WASTE_X			    = STOCK_X + CARD_WIDTH + SPACE;
	public  static final int		UPPER_HALF_PILES_Y	= 10 + SPACE;

	private Solitaire		solitaire;
	private Listener		listener;
	private MouseAdapter		mouseAdapter;
	private MouseMotionAdapter	mouseMotionAdapter;

	public Display() {
		this.solitaire = new Solitaire();
		this.listener = new Listener(this.solitaire, this);
		
		this.mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				listener.mousePressed(event);
			}
			
			@Override
			public void mouseClicked(MouseEvent event) {
				listener.mouseClicked(event);
			}
			
			@Override
			public void mouseReleased(MouseEvent event) {
				listener.mouseReleased(event);
			}
		};
		
		this.mouseMotionAdapter = new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent event) {
				listener.mouseDragged(event);
			}
		};
		
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseMotionAdapter);
		setPreferredSize(new Dimension(800, 600));
		setOpaque(true);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		Key textAAk = RenderingHints.KEY_TEXT_ANTIALIASING;
		Object textAAv = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
		RenderingHints rh = new RenderingHints(textAAk, textAAv);
		g2.setRenderingHints(rh);
		
		drawStock(g);
		drawWaste(g);
		drawFoundation(g);
		drawTableau(g);
		drawDragged(g);
		drawGameEnd(g);
	}

	private void drawStock(Graphics g) {
		if (this.solitaire.getStock().isEmpty()) {
			drawEmptyPile(g, STOCK_X, UPPER_HALF_PILES_Y);
		} else {
			drawCardBack(g, STOCK_X, UPPER_HALF_PILES_Y);
		}
	}

	private void drawWaste(Graphics g) {
		if (this.solitaire.getWaste().isEmpty()) {
			drawEmptyPile(g, WASTE_X, UPPER_HALF_PILES_Y);
		} else {
			drawCardFront(g, this.solitaire.getWaste().peek());
		}
	}

	private void drawFoundation(Graphics g) {
		int x = STOCK_X + 3 * CARD_WIDTH + 3 * SPACE;
		for (int i = 0; i < 4; i++) {
			Stack<Card> pile = this.solitaire.getFoundation().get(i);
			if (pile.isEmpty()) {
				drawEmptyPile(g, x, UPPER_HALF_PILES_Y);
				drawFoundationPileSuit(g, i, x);
			} else {
				drawCardFront(g, pile.peek());
			}
			x += CARD_WIDTH + SPACE;
		}
	}

	private void drawTableau(Graphics g) {
		int x = STOCK_X;
		int y = UPPER_HALF_PILES_Y + CARD_HEIGHT + SPACE;
		for (int i = 0; i < 7; i++) {
			Stack<Card> pile = this.solitaire.getTableau().get(i);
			if (pile.isEmpty()) {
				drawEmptyPile(g, x, y);
			} else {
				for (Card card : pile) {
					drawCardFront(g, card);
				}
			}
			x += CARD_WIDTH + SPACE;
		}
	}

	private void drawDragged(Graphics g) {
		if (!this.solitaire.getDragged().isEmpty()) {
			for (Card card : this.solitaire.getDragged()) {
				drawCardFront(g, card);
			}
		}
	}

	private void drawEmptyPile(Graphics g, int x, int y) {
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(x, y, x + CARD_WIDTH, y + CARD_HEIGHT);
		g.drawLine(x, y + CARD_HEIGHT, x + CARD_WIDTH, y);
		g.drawRoundRect(x - 1, y - 1, CARD_WIDTH + 1, CARD_HEIGHT + 1, 15, 15);
	}

	private void drawCardBack(Graphics g, int x, int y) {
		g.setColor(Color.GRAY);
		g.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x - 1, y - 1, CARD_WIDTH + 1, CARD_HEIGHT + 1, 15, 15);
	}

	private void drawCardFront(Graphics g, Card card) {
		int x = card.getCurrentX();
		int y = card.getCurrentY();
		if (card.isShown()) {
			String rank = card.getRank() == 'T' ? "10" : String.valueOf(card.getRank());
			g.setColor(Color.WHITE);
			g.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
			g.setColor(Color.BLACK);
			g.drawString(rank, x + 10, y + 125);
			g.setColor(card.isBlack() ? Color.BLACK : Color.RED);
			g.drawString(String.valueOf(card.getSuit()), x + 45, y + 50);
		} else {
			drawCardBack(g, x, y);
		}
		g.drawRoundRect(x - 1, y - 1, CARD_WIDTH + 1, CARD_HEIGHT + 1, 15, 15);
	}

	private void drawFoundationPileSuit(Graphics g, int index, int x) {
		char suit = Solitaire.SUITS[index];
		g.setFont(new Font(Font.DIALOG, Font.BOLD, 72));
		g.setColor(Color.GRAY);
		g.drawString(String.valueOf(suit), x + 18, UPPER_HALF_PILES_Y + 95);
	}
	
	private void drawGameEnd(Graphics g) {
		if (this.solitaire.gameEnd) {
			Font font = new Font("Comic Sans MS", Font.BOLD, 48);
			g.setFont(font);
			
			g.setColor(Color.GREEN);
			
			String text = "You win!";
			Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
			int width = (int) rect.getWidth();
			int height = (int) rect.getHeight();
			
			g.drawString(text, (this.getWidth() - width) / 2, (this.getHeight() - height) / 2);
		}
	}
}
