package il.mgmaor.solitaire;

import static il.mgmaor.solitaire.Display.*;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Stack;

public class Listener {

	// Locals.

	private Solitaire	solitaire;

	private Display		display;

	private int		distanceX;

	private int		distanceY;

	private boolean		success;

	// Constructor.

	public Listener(Solitaire solitaire, Display display) {
		this.solitaire = solitaire;
		this.display = display;
	}

	// Events.

	public void mousePressed(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();

		Stack<Card> waste = this.solitaire.getWaste();
		ArrayList<Card> dragged = this.solitaire.getDragged();

		boolean inRegionYWaste = y >= UPPER_HALF_PILES_Y && y <= UPPER_HALF_PILES_Y + CARD_HEIGHT;
		boolean inRegionXWaste = x >= WASTE_X && x <= WASTE_X + CARD_WIDTH;
		boolean wasteEmpty = waste.isEmpty();

		if (inRegionXWaste && inRegionYWaste && !wasteEmpty) {
			pressedWaste(waste, dragged, x, y);
		} else if (y >= tableauLocation(0, 0).y) {
			pressedTableauY(dragged, x, y);
		}
	}

	public void mouseClicked(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();

		boolean inRegionX = x >= STOCK_X && x <= STOCK_X + CARD_WIDTH;
		boolean inRegionY = y >= UPPER_HALF_PILES_Y && y <= UPPER_HALF_PILES_Y + CARD_HEIGHT;

		if (inRegionX && inRegionY) {
			clickedStock();
			display.repaint();
		}
	}

	public void mouseReleased(MouseEvent event) {
		ArrayList<Card> dragged = this.solitaire.getDragged();

		if (!dragged.isEmpty()) {
			int x = event.getX();
			int y = event.getY();
			this.success = false;

			Pile previousPile = dragged.get(0).getPile();
			int taPileIndex = dragged.get(0).getTableauPileIndex();

			// More efficient to check here the y ->
			if (dragged.size() == 1 && y < UPPER_HALF_PILES_Y + CARD_HEIGHT) {
				checkDropFoundation(dragged, x, y);
			} else {
				// TODO: Check drop in the tableau.
			}

			if (this.success) {
				dropSuccessful(dragged, previousPile, taPileIndex);
			} else {
				dropUnsuccessful(dragged);
			}

			this.distanceX = 0;
			this.distanceY = 0;
			dragged.clear();
			display.repaint();
		}
	}

	public void mouseDragged(MouseEvent event) {
		ArrayList<Card> dragged = this.solitaire.getDragged();

		if (!dragged.isEmpty()) {
			int x = event.getX() - this.distanceX;
			int y = event.getY() - this.distanceY;

			for (Card card : dragged) {
				card.setCurrentX(x);
				card.setCurrentY(y += 10);
			}

			display.repaint();
		}
	}

	// Helping methods.

	private void pressedWaste(Stack<Card> waste, ArrayList<Card> dragged, int x, int y) {
		Card topWaste = waste.peek();
		dragged.add(topWaste);

		this.distanceX = x - topWaste.getCurrentX();
		this.distanceY = y - topWaste.getCurrentY();
	}

	private void pressedTableauY(ArrayList<Card> dragged, int x, int y) {
		for (int i = 0; i < 7; i++) {
			Stack<Card> pile = this.solitaire.getTableau().get(i);

			if (!pile.isEmpty()) {
				Card topCard = pile.peek();
				int bottomPileY = topCard.getCurrentY() + CARD_HEIGHT;

				if (y <= bottomPileY) {
					for (int j = pile.size() - 1; j >= 0; j--) {
						Card current = pile.get(j);

						if (Solitaire.isMouseOn(current, x, y)) {
							if (current.isHeading()) {
								dragged.add(current);
							} else {
								int index = current.getTableauCardIndex();
								for (int k = index; k < pile.size(); k++) {
									dragged.add(pile.get(k));
								}
							}

							this.distanceX = x - current.getCurrentX();
							this.distanceY = y - current.getCurrentY();
							return;
						}
					}
				}
			}
		}
	}

	private void clickedStock() {
		Stack<Card> stock = this.solitaire.getStock();
		Stack<Card> waste = this.solitaire.getWaste();

		if (stock.isEmpty()) {
			// If the stock is empty, replace the stock with the waste.
			clickedStockAndItIsEmpty(waste, stock);
		} else {
			// Otherwise, add the card from the stock to the waste.
			clickedStockAndItIsNotEmpty(waste, stock);
		}
	}

	private void clickedStockAndItIsEmpty(Stack<Card> waste, Stack<Card> stock) {
		for (int i = 0; i < waste.size(); i++) {
			Card card = waste.pop();
			stock.push(card);
		}

		Card top = stock.peek();
		top.setShown(true);
		top.setHeading(true);
	}

	private void clickedStockAndItIsNotEmpty(Stack<Card> waste, Stack<Card> stock) {
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

	private void dropSuccessful(ArrayList<Card> dragged, Pile previousPile, int pileIndex) {
		Card newHeader = null;

		if (previousPile == Pile.TABLEAU) {
			// Tableau
			Stack<Card> taPile = this.solitaire.getTableau().get(pileIndex);
			for (int i = 0; i < dragged.size(); i++) {
				taPile.pop();
			}
			if (!taPile.isEmpty()) {
				newHeader = taPile.peek();
			}
		} else {
			// Waste
			Stack<Card> waste = this.solitaire.getWaste();
			waste.remove(dragged.get(0));
			if (!waste.isEmpty()) {
				newHeader = waste.peek();
			}
		}

		if (newHeader != null) {
			newHeader.setHeading(true);
			newHeader.setShown(true);
		}
	}

	private void dropUnsuccessful(ArrayList<Card> dragged) {
		for (Card card : dragged) {
			card.setCurrentX(card.getLastX());
			card.setCurrentY(card.getLastY());
		}
	}

	private void checkDropFoundation(ArrayList<Card> dragged, int x, int y) {
		Card dropped = dragged.get(0);
		char suit = dropped.getSuit();

		int x1 = foundationX(suit);
		int y1 = UPPER_HALF_PILES_Y;
		int x2 = x1 + CARD_WIDTH;

		if (x >= x1 && x <= x2 && y >= y1) {
			this.success = rankFitsFoundation(dropped, this.solitaire.getFoundationPile(suit), x1, y1);
		}
	}

	// TODO
	private void checkDropTableau() {

	}

	private boolean rankFitsFoundation(Card card, Stack<Card> pile, int xPile, int yPile) {
		char rank = card.getRank();
		int size = pile.size();

		if (rank == Solitaire.RANKS[size]) {
			if (size > 0) {
				Card previous = pile.peek();
				previous.setShown(false);
				previous.setHeading(false);
			}
			pile.push(card);
			card.setLastX(xPile);
			card.setLastY(yPile);
			card.setPile(Pile.FOUNDATION);
			card.setHeading(true);
			return true;
		}
		return false;
	}

	// TODO
	private boolean rankFitsTableau() {

		return false;
	}
}