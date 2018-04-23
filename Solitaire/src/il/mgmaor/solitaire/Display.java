package il.mgmaor.solitaire;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JPanel;

public class Display extends JPanel {

	// Constants.

	private static final long	serialVersionUID	= 1L;

	public static final int		CARD_WIDTH		= 100;

	public static final int		CARD_HEIGHT		= 140;

	public static final int		SPACE			= 10;

	public static final int		STOCK_X			= SPACE;

	public static final int		WASTE_X			= STOCK_X + CARD_WIDTH + SPACE;

	public static final int		UPPER_HALF_PILES_Y	= SPACE;

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
		int x = SPACE;
		int y = SPACE + CARD_HEIGHT + SPACE;

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

	// private Main main;

	public Display(Main main) {
		// this.main = main;

		setPreferredSize(new Dimension(800, 600));
		setOpaque(true);
	}
}
