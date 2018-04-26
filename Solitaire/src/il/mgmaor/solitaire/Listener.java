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

	// Constructor.

	public Listener(Solitaire solitaire, Display display) {
		this.solitaire = solitaire;
		this.display   = display;
	}

	// Events.
	
	public void mouseClicked(MouseEvent event) {
		int x             = event.getX();
		int y             = event.getY();
		boolean fitStockX = x >= STOCK_X            && x <= STOCK_X            + CARD_WIDTH;
		boolean fitStockY = y >= UPPER_HALF_PILES_Y && y <= UPPER_HALF_PILES_Y + CARD_HEIGHT;
		if (fitStockX && fitStockY) {
			if (this.solitaire.getStock().isEmpty()) {
				clickedEmptyStock();
			} else {
				clickedNonEmptyStock();
			}
			this.display.repaint();
		}
	}

	public void mousePressed(MouseEvent event) {
		int     x              = event.getX();
		int     y              = event.getY();
		boolean fitUpperPilesY = y >= UPPER_HALF_PILES_Y && y <= UPPER_HALF_PILES_Y + CARD_HEIGHT;
		if (fitUpperPilesY) {
			boolean fitWasteX = x >= WASTE_X && x <= WASTE_X + CARD_WIDTH;
			if (fitWasteX) {
				pressedWaste(x, y);
			} else {
				checkPressFoundation(x, y);
			}
		} else {
			checkPressTableau(x, y);
		}
	}

	
	
//	private boolean rankFitsFoundation(Card card, Stack<Card> pile, int xPile, int yPile) {
//	char rank = card.getRank();
//	int size = pile.size();
//
//	if (rank == Solitaire.RANKS[size]) {
//		if (size > 0) {
//			Card previous = pile.peek();
//			previous.setShown(false);
//			previous.setHeading(false);
//		}
//		pile.push(card);
//		card.setLastX(xPile);
//		card.setLastY(yPile);
//		card.setPile(Pile.FOUNDATION);
//		card.setHeading(true);
//		return true;
//	}
//	return false;
//}
	
	public void mouseReleased(MouseEvent event) {
		ArrayList<Card> dragged        = this.solitaire.getDragged();
		int             x              = event.getX();
		int             y              = event.getY();
		int             pileX          = 0;
		boolean         fitUpperPilesY = y >= UPPER_HALF_PILES_Y && y <= UPPER_HALF_PILES_Y + CARD_HEIGHT;
		if (fitUpperPilesY && dragged.size() == 1) {
			pileX            = STOCK_X + 3 * CARD_WIDTH + 3 * SPACE;
			char pileSuit    = '\0';
			Stack<Card> pile = null;
			for (int i = 0; i < 4; i++) {
				pile = this.solitaire.getFoundation().get(i);
				if (pile.size() < 13) {
					if (x >= pileX && x <= pileX + CARD_WIDTH) {
						pileSuit = Solitaire.SUITS[i];
						break;
					}
				}
				pileX += CARD_WIDTH + SPACE;
			}
			if (pileSuit != '\0') {
				Card card = dragged.remove(0);
				char rank = card.getRank();
				if (rank == Solitaire.RANKS[pile.size()]) {
					Stack<Card> previousPile = null;
					if (card.getPile() == Pile.WASTE) {
						previousPile = this.solitaire.getWaste();
						previousPile.pop();
					} else {
						int previousIndex = card.getPreviousTableauPileIndex();
						previousPile      = this.solitaire.getTableau().get(previousIndex);
						previousPile.pop();
						if (!previousPile.isEmpty()) {
							previousPile.peek().setShown(true);
						}
					}
					card.setPile(Pile.FOUNDATION);
					card.setLastX(pileX);
					card.setLastY(UPPER_HALF_PILES_Y);
					pile.push(card);
				}
			}
		} else if (dragged.size() > 1) {
			pileX                 = STOCK_X;
			Stack<Card> pile      = null;
			int         pileIndex = -1;
			for (int i = 0; i < 7; i++) {
				pile = this.solitaire.getTableau().get(i);
				if (x >= pileX && x <= pileX + CARD_WIDTH) {
					pileIndex = i;
					break;
				}
				pileX += CARD_WIDTH + SPACE;
			}
			if (pileIndex != -1) {
				Card first = dragged.get(0);
				if (pile.isEmpty() && first.getRank() == 'K') {
					int tableauY = UPPER_HALF_PILES_Y + CARD_HEIGHT + SPACE;
					if (y >= tableauY && y <= tableauY + CARD_HEIGHT) {
						int size = dragged.size();
						Stack<Card> previousPile;
						if (first.getPile() == Pile.WASTE) {
							previousPile = this.solitaire.getWaste();
						} else {
							int previousIndex = first.getPreviousTableauPileIndex();
							previousPile      = this.solitaire.getTableau().get(previousIndex);
						}
						for (int i = 0; i < size; i++) {
							previousPile.pop();
							if (first.getPile() == Pile.TABLEAU && !previousPile.isEmpty()) {
								previousPile.peek().setShown(true);
							}
							first.setPile(Pile.TABLEAU);
							first.setLastX(pileX);
							first.setLastY(tableauY);
							pile.push(dragged.remove(i));
						}
					}
				}
			}
		}
		this.display.repaint();
	}
	
	
	
	
	
	
	
	
	
//	public void mousePressed(MouseEvent event) {
//		int x = event.getX();
//		int y = event.getY();
//
//		Stack<Card> waste = this.solitaire.getWaste();
//		ArrayList<Card> dragged = this.solitaire.getDragged();
//
//		boolean fitXWaste = y >= UPPER_HALF_PILES_Y && y <= UPPER_HALF_PILES_Y + CARD_HEIGHT;
//		boolean fitYWaste = x >= WASTE_X            && x <= WASTE_X            + CARD_WIDTH;
//		
//		if (fitXWaste && fitYWaste) {
//			if (waste.isEmpty()) {
//				
//			}
//		}
//
////		if (inRegionXWaste && inRegionYWaste && !wasteEmpty) {
////			pressedWaste(waste, dragged, x, y);
////		} else if (y >= tableauLocation(0, 0).y) {
////			pressedTableauY(dragged, x, y);
////		}
//	}

//	public void mouseReleased(MouseEvent event) {
//		ArrayList<Card> dragged = this.solitaire.getDragged();
//
//		if (!dragged.isEmpty()) {
//			int x = event.getX();
//			int y = event.getY();
//			this.success = false;
//
//			Pile previousPile = dragged.get(0).getPile();
//			int taPileIndex = dragged.get(0).getTableauPileIndex();
//
//			boolean droppedInFoundation = dragged.size() == 1 && y < UPPER_HALF_PILES_Y + CARD_HEIGHT;
//			
//			// More efficient to check here the y ->
//			if (droppedInFoundation) {
//				checkDropFoundation(dragged, x, y);
//			} else {
//				int pile = checkDropTableauBoundsPile(dragged, x, y);
//				if (pile != -1) {
//					this.success = checkDropTableauSuccess(dragged, x, y, pile);
//					if (this.success) {
//						dropTableauSuccessful(dragged, pile);
//					}
//				}
//			}
//
//			if (this.success) {
//				dropSuccessful(dragged, previousPile, taPileIndex);
//			} else {
//				dropUnsuccessful(dragged);
//			}
//
//			this.distanceX = 0;
//			this.distanceY = 0;
//			dragged.clear();
//			this.display.repaint();
//		}
//	}

	public void mouseDragged(MouseEvent event) {
		ArrayList<Card> dragged = this.solitaire.getDragged();
		if (!dragged.isEmpty()) {
			int x = event.getX() - this.distanceX;
			int y = event.getY() - this.distanceY;
			for (Card card : dragged) {
				card.setCurrentX(x);
				card.setCurrentY(y);
				y += 10;
			}
			this.display.repaint();
		}
	}

	// Helping methods.
	
	private void clickedEmptyStock() {
		Stack<Card> stock = this.solitaire.getStock();
		Stack<Card> waste = this.solitaire.getWaste();
		int wasteSize = waste.size();
		for (int i = 0; i < wasteSize; i++) {
			Card card = waste.pop();
			card.setShown(false);
			card.setPile(Pile.STOCK);
			card.setLastX(STOCK_X);
			card.setLastY(UPPER_HALF_PILES_Y);
			stock.push(card);
		}
	}
	
	private void clickedNonEmptyStock() {
		Stack<Card> stock = this.solitaire.getStock();
		Stack<Card> waste = this.solitaire.getWaste();
		Card card = stock.pop();
		card.setShown(true);
		card.setPile(Pile.WASTE);
		card.setLastX(WASTE_X);
		card.setLastY(UPPER_HALF_PILES_Y);
		waste.push(card);
	}
	
	private void pressedWaste(int x, int y) {
		ArrayList<Card> dragged = this.solitaire.getDragged();
		Stack<Card>     waste   = this.solitaire.getWaste();
		if (!waste.isEmpty()) {
			dragged.add(waste.peek());
			this.distanceX = x - WASTE_X;
			this.distanceY = y - UPPER_HALF_PILES_Y;
		}
	}
	
	private void checkPressFoundation(int x, int y) {
		char        pileSuit = '\0';
		int         pileX    = STOCK_X + 3 * CARD_WIDTH + 3 * SPACE;
		Stack<Card> pile     = null;
		for (int i = 0; i < 4; i++) {
			pile = this.solitaire.getFoundation().get(i);
			if (!pile.isEmpty()) {
				if (x >= pileX && x <= pileX + CARD_WIDTH) {
					pileSuit = Solitaire.SUITS[i];
					break;
				}
			}
			pileX += CARD_WIDTH + SPACE;
		}
		if (pileSuit != '\0') {
			this.solitaire.getDragged().add(pile.peek());
			this.distanceX = x - WASTE_X;
			this.distanceY = y - UPPER_HALF_PILES_Y;
		}
	}
	
	private void checkPressTableau(int x, int y) {
		int pileIndex = -1;
		int pileX     = STOCK_X;
		for (int i = 0; i < 7; i++) {
			if (!this.solitaire.getTableau().get(i).isEmpty()) {
				if (x >= pileX && x <= pileX + CARD_WIDTH) {
					pileIndex = i;
					break;
				}
			}
			pileX += CARD_WIDTH + SPACE;
		}
		if (pileIndex != -1) {
			checkPressTableau(x, y, pileIndex, pileX);
		}
	}
	
	private void checkPressTableau(int x, int y, int pileIndex, int pileX) {
		Stack<Card> pile       = this.solitaire.getTableau().get(pileIndex);
		int         hidden     = this.solitaire.getHiddenCards(pileIndex);
		int         size       = pile.size();
		int         pileY      = UPPER_HALF_PILES_Y + CARD_HEIGHT + SPACE;
		int         lastShownY = pileY + SPACE * hidden;
		int         lastY      = pileY + SPACE * (size - 1);
		if (y >= lastShownY && y <= lastY + CARD_HEIGHT) { 
			if (y >= lastY) {
				this.solitaire.getDragged().add(pile.peek());
				this.distanceX = x - pileX;
				this.distanceY = y - lastY;
			} else {
				int cardIndex = -1;
				int cardY     = lastShownY;
				for (int i = hidden; i < size - hidden + 1; i++) {
					if (y >= cardY && y <= cardY + CARD_HEIGHT) {
						cardIndex = i;
					}
					if (cardIndex == -1) {
						cardY += SPACE;
					} else {
						this.solitaire.getDragged().add(pile.peek());
						this.distanceX = x - pileX;
						this.distanceY = y - cardY;
					}
				}
			}
//			System.out.println("Started grabbing:");
//			pile.peek().fullDebug();
		}
	}
	
	
	
	
	
	
	
	

//	private void pressedWaste(Stack<Card> waste, ArrayList<Card> dragged, int x, int y) {
//		Card topWaste = waste.peek();
//		dragged.add(topWaste);
//
//		this.distanceX = x - topWaste.getCurrentX();
//		this.distanceY = y - topWaste.getCurrentY();
//	}
//
//	private void pressedTableauY(ArrayList<Card> dragged, int x, int y) {
//		for (int i = 0; i < 7; i++) {
//			Stack<Card> pile = this.solitaire.getTableau().get(i);
//
//			if (!pile.isEmpty()) {
//				Card topCard = pile.peek();
//				int bottomPileY = topCard.getCurrentY() + CARD_HEIGHT;
//
//				if (y <= bottomPileY) {
//					for (int j = pile.size() - 1; j >= 0; j--) {
//						Card current = pile.get(j);
//
//						if (Solitaire.isMouseOn(current, x, y)) {
//							if (current.isHeading()) {
//								dragged.add(current);
//							} else {
//								int index = current.getTableauCardIndex();
//								for (int k = index; k < pile.size(); k++) {
//									dragged.add(pile.get(k));
//								}
//							}
//
//							this.distanceX = x - current.getCurrentX();
//							this.distanceY = y - current.getCurrentY();
//							return;
//						}
//					}
//				}
//			}
//		}
//	}
//
//	private void clickedStockAndItIsEmpty() {
//		// TODO: Do not delete this variable!!!
//		// You got to save the size, it changes!!!
//		final int size = this.solitaire.getWaste().size();
//		
//		for (int i = 0; i < size - 1; i++) {
//			Card card = this.solitaire.getWaste().pop();
//			this.solitaire.getStock().push(card);
//		}
//		
//		Card card = this.solitaire.getWaste().pop();
//		this.solitaire.getStock().push(card);
//		card.setShown(true);
//		card.setHeading(true);
//	}
//
//	private void clickedStockAndItIsNotEmpty() {
//		if (!this.solitaire.getWaste().isEmpty()) {
//			Card topWaste = this.solitaire.getWaste().peek();
//			topWaste.setShown(false);
//			topWaste.setHeading(false);
//		}
//		
//		Card topStock = this.solitaire.getStock().pop();
//		topStock.setShown(true);
//		topStock.setHeading(true);
//		topStock.setPile(Pile.WASTE);
//		topStock.setLastX(WASTE_X);
//		topStock.setLastY(UPPER_HALF_PILES_Y);
//		this.solitaire.getWaste().push(topStock);
//	}
//
//	private void dropSuccessful(ArrayList<Card> dragged, Pile previousPile, int pileIndex) {
//		Card newHeader = null;
//
//		if (previousPile == Pile.TABLEAU) {
//			// Tableau
//			Stack<Card> taPile = this.solitaire.getTableau().get(pileIndex);
//			for (int i = 0; i < dragged.size(); i++) {
//				taPile.pop();
//			}
//			if (!taPile.isEmpty()) {
//				newHeader = taPile.peek();
//			}
//		} else {
//			// Waste
//			Stack<Card> waste = this.solitaire.getWaste();
//			waste.remove(dragged.get(0));
//			if (!waste.isEmpty()) {
//				newHeader = waste.peek();
//			}
//		}
//
//		if (newHeader != null) {
//			newHeader.setHeading(true);
//			newHeader.setShown(true);
//		}
//	}
//
//	private void dropUnsuccessful(ArrayList<Card> dragged) {
//		for (Card card : dragged) {
//			card.setCurrentX(card.getLastX());
//			card.setCurrentY(card.getLastY());
//		}
//	}
//
//	private void checkDropFoundation(ArrayList<Card> dragged, int x, int y) {
//		Card dropped = dragged.get(0);
//		char suit = dropped.getSuit();
//
//		int x1 = foundationX(suit);
//		int y1 = UPPER_HALF_PILES_Y;
//		int x2 = x1 + CARD_WIDTH;
//
//		if (x >= x1 && x <= x2 && y >= y1) {
//			this.success = rankFitsFoundation(dropped, this.solitaire.getFoundationPile(suit), x1, y1);
//		}
//	}
//
//	/**
//	 * @return The index of the clicked pile of the tableau if even, otherwise -1.
//	 */
//	private int checkDropTableauBoundsPile(ArrayList<Card> dragged, int x, int y) {
//		// Determine what pile by the x.
//		final int yPile = UPPER_HALF_PILES_Y + CARD_HEIGHT + SPACE;
//		
//		int pileIndex = -1;
//		boolean inY = false;
//		int xPile = STOCK_X;
//		
//		for (int i = 0; i < 7; i++) {
//			if (x >= xPile && x <= xPile + CARD_WIDTH) {
//				pileIndex = i;
//				break;
//			}
//			xPile += CARD_WIDTH + SPACE;
//		}
//		// If it is -1, the player hasn't clicked any pile.
//		if (pileIndex != -1) {
//			Stack<Card> pile = this.solitaire.getTableau().get(pileIndex);
//			int size = pile.size();
//			// Empty/Size 1 are the same y value.
//			if (size == 0) {
//				size = 1;
//			}
//			// Now check if the Y fits the pile.
//			int Y = yPile + (size - 1) * SPACE;
//			if (y >= Y && y <= Y + CARD_HEIGHT) {
//				inY = true;
//			}
//		}
//		return inY ? pileIndex : -1;
//	}
//	
//	private boolean checkDropTableauSuccess(ArrayList<Card> dragged, int x, int y, int pile) {
//		Card topDragged = dragged.get(0);
//		Card topPile = this.solitaire.getTableau().get(pile).peek();
//		int rankIndexDragged = topDragged.getRankIndex();
//		System.out.println("RANK INDEX DRAGGED: " + rankIndexDragged);
//		int rankIndexPile = topPile.getRankIndex();
//		System.out.println("RANK INDEX PILE: " + rankIndexPile);
//		boolean draggedBlack = topDragged.isBlack();
//		System.out.println("DRAGGED BLACK? " + draggedBlack);
//		boolean pileBlack = topPile.isBlack();
//		System.out.println("PILE BLACK? " + pileBlack);
//
//		boolean ranksFit = rankIndexDragged == rankIndexPile - 1;
//		boolean suitsFit = draggedBlack != pileBlack;
//		
//		System.out.println("RANKS FIT? " + ranksFit);
//		System.out.println("SUITS FIT? " + suitsFit);
//		System.out.println("-------");
//		return ranksFit && suitsFit;
//	}
//	
//	private void dropTableauSuccessful(ArrayList<Card> dragged, int pileIndex) {
//		Stack<Card> pile = this.solitaire.getTableau().get(pileIndex);
//		final int sizeDragged = dragged.size();
//		final int sizePile = this.solitaire.getTableau().get(pileIndex).size();
//		for (int i = 0; i < sizeDragged; i++) {
//			Point newLoc = tableauLocation(pileIndex, sizePile + sizeDragged - 1);
//			
//			Card toAdd = dragged.get(i);
//			toAdd.setLastX(newLoc.x);
//			toAdd.setLastY(newLoc.y);
//			toAdd.setPile(Pile.TABLEAU);
//			pile.push(toAdd);
//		}
//		
//	}
//
//	private boolean rankFitsFoundation(Card card, Stack<Card> pile, int xPile, int yPile) {
//		char rank = card.getRank();
//		int size = pile.size();
//
//		if (rank == Solitaire.RANKS[size]) {
//			if (size > 0) {
//				Card previous = pile.peek();
//				previous.setShown(false);
//				previous.setHeading(false);
//			}
//			pile.push(card);
//			card.setLastX(xPile);
//			card.setLastY(yPile);
//			card.setPile(Pile.FOUNDATION);
//			card.setHeading(true);
//			return true;
//		}
//		return false;
//	}
	
	
//	private void printTableau() {
//		for (Stack<Card> pile : solitaire.getTableau()){
//			System.out.println("Pile 0: " + );
//		}
//	}
}