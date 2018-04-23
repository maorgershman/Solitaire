package il.mgmaor.solitaire;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.util.Stack;

import javax.swing.JPanel;

public class Display extends JPanel {

	// Constants.

	private static final long	serialVersionUID	= 1L;

	public static final int		CARD_WIDTH		= 100;

	public static final int		CARD_HEIGHT		= 140;

	public static final int		SPACE			= 10;

	public static final int		STOCK_X			= 10 + SPACE;

	public static final int		WASTE_X			= STOCK_X + CARD_WIDTH + SPACE;

	public static final int		UPPER_HALF_PILES_Y	= 10 + SPACE;

	public static final int foundationX(char suit) {
		// X value of the first pile of the foundation.
		int x = WASTE_X + CARD_WIDTH + SPACE + CARD_WIDTH + SPACE;

		// Search the wanted suit in the suits array.
		for (char current : Solitaire.SUITS) {
			// When we find it, return the x.
			if (suit == current) {
				return x;
			}

			// If we don't find it, increase the x and keep searching.
			x += CARD_WIDTH + SPACE;
		}

		// If the suit is invalid, throw an error.
		throw new IllegalArgumentException();
	}

	public static final Point tableauLocation(int pileIndex, int cardIndex) {
		// The X and Y values of the top left card in the tableau.
		int x = STOCK_X;
		int y = UPPER_HALF_PILES_Y + CARD_HEIGHT + SPACE;

		// Every pile, move the X.
		for (int i = 0; i < pileIndex; i++) {
			x += CARD_WIDTH + SPACE;
		}

		// For every card in the pile, shift the Y a bit.
		for (int i = 0; i < cardIndex; i++) {
			y += SPACE;
		}

		// If everything is fine, return the location.
		return new Point(x, y);
	}

	// Locals.

	private Solitaire solitaire;

	// Constructor.

	public Display() {
		this.solitaire = new Solitaire();

		setPreferredSize(new Dimension(800, 600));
		setOpaque(true);
	}

	// Overridden.

	@Override
	protected void paintComponent(Graphics g) {
		// Clear the screen.
		super.paintComponent(g);

		// Enable Anti-Aliasing.
		Graphics2D g2 = (Graphics2D) g;

		Key textAAk = RenderingHints.KEY_TEXT_ANTIALIASING;
		Object textAAv = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
		RenderingHints rh = new RenderingHints(textAAk, textAAv);
		g2.setRenderingHints(rh);

		// Draw all the stuff.
		drawStock(g);
		drawWaste(g);
		drawFoundation(g);
		drawTableau(g);
		drawDragged(g);
	}

	// Local methods.

	private void drawStock(Graphics g) {
		if (this.solitaire.getStock().isEmpty()) {
			// If the stock is empty, draw an empty pile.
			drawEmptyPile(g, STOCK_X, UPPER_HALF_PILES_Y);
		} else {
			// Otherwise draw a card that is facing down.
			drawCardBack(g, STOCK_X, UPPER_HALF_PILES_Y);
		}
	}

	private void drawWaste(Graphics g) {
		if (this.solitaire.getWaste().isEmpty()) {
			// If the waste is empty, draw an empty pile.
			drawEmptyPile(g, WASTE_X, UPPER_HALF_PILES_Y);
		} else {
			// Otherwise draw the top card of the waste.
			drawCardFront(g, this.solitaire.getWaste().peek());
		}
	}

	private void drawFoundation(Graphics g) {
		for (int i = 0; i < 4; i++) {
			Stack<Card> pile = this.solitaire.getFoundation().get(i);

			if (pile.isEmpty()) {
				// If the pile is empty, draw an empty pile with the suit on top.
				int x = foundationX(Solitaire.SUITS[i]);
				drawEmptyPile(g, x, UPPER_HALF_PILES_Y);
				drawFoundationPileSuit(g, i, x);
			} else {
				// Otherwise, draw the top card.
				drawCardFront(g, pile.peek());
			}
		}
	}

	private void drawTableau(Graphics g) {
		for (int i = 0; i < 7; i++) {
			Stack<Card> pile = this.solitaire.getTableau().get(i);

			if (pile.isEmpty()) {
				// If the pile is empty, draw an empty pile.
				Point point = tableauLocation(i, 0);
				drawEmptyPile(g, point.x, point.y);
			} else {
				// Otherwise, draw all of the cards.
				for (Card card : pile) {
					drawCardFront(g, card);
				}
			}
		}
	}

	private void drawDragged(Graphics g) {
		if (!this.solitaire.getDragged().isEmpty()) {
			// If cards are currently being moved redraw them.
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
			// If the card is shown, draw its face.
			g.setColor(Color.WHITE);
			g.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 15, 15);
			
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 50));
			g.setColor(Color.BLACK);
			String rank = String.valueOf(card.getRank());
			
			rank = card.getRank() == 'T' ? "10" : rank;
			g.drawString(rank, x + 10, y + 125);
			
			boolean club = card.getSuit() == Solitaire.SUITS[0];
			boolean spade = card.getSuit() == Solitaire.SUITS[3];
			
			g.setColor(club || spade ? Color.BLACK : Color.RED);
			g.drawString(String.valueOf(card.getSuit()), x + 45, y + 50);
		} else {
			// If the card is hidden, draw its back.
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
}
