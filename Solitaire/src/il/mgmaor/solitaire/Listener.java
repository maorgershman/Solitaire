package il.mgmaor.solitaire;

import static il.mgmaor.solitaire.Display.CARD_HEIGHT;
import static il.mgmaor.solitaire.Display.CARD_WIDTH;
import static il.mgmaor.solitaire.Display.SPACE;
import static il.mgmaor.solitaire.Display.STOCK_X;
import static il.mgmaor.solitaire.Display.WASTE_X;
import static il.mgmaor.solitaire.Display.UPPER_HALF_PILES_Y;
import static il.mgmaor.solitaire.Display.foundationX;
import static il.mgmaor.solitaire.Display.tableauLocation;

import java.awt.event.MouseEvent;
import java.util.Stack;

public class Listener {

	// Locals.

	private Solitaire	solitaire;

	private Display		display;

	private int		distanceX;

	private int		distanceY;

	// Constructor.

	public Listener(Solitaire solitaire, Display display) {
		this.solitaire = solitaire;
		this.display = display;
	}

	// Local methods.

	public void mousePressed(MouseEvent event) {

	}

	public void mouseClicked(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();

		boolean inRegionX = x >= STOCK_X && x <= STOCK_X + CARD_WIDTH;
		boolean inRegionY = y >= UPPER_HALF_PILES_Y && y <= UPPER_HALF_PILES_Y + CARD_HEIGHT;

		if (inRegionX && inRegionY) {
			Stack<Card> stock = this.solitaire.getStock();
			Stack<Card> waste = this.solitaire.getWaste();

			if (stock.isEmpty()) {
				// If the stock is empty, replace the stock with the waste.
				for (int i = 0; i < waste.size(); i++) {
					Card card = waste.pop();
					stock.push(card);
				}

				Card top = stock.peek();
				top.setShown(true);
				top.setHeading(true);
			} else {
				// Otherwise, add the card from the stock to the waste.
				Card topStock = stock.pop();
				if (!waste.isEmpty()) {
					Card topWaste = waste.peek();
					topWaste.setShown(false);
					topWaste.setHeading(false);
				}
				topStock.setShown(true);
				topStock.setHeading(true);
				topStock.setPile(Pile.WASTE);
				topStock.setLastX(WASTE_X);
				topStock.setLastY(UPPER_HALF_PILES_Y);
				waste.push(topStock);
			}

			display.repaint();
		}
	}

	public void mouseReleased(MouseEvent event) {

	}

	public void mouseDragged(MouseEvent event) {

	}
}
