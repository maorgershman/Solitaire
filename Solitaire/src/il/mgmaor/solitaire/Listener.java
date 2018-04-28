package il.mgmaor.solitaire;

import static il.mgmaor.solitaire.Display.*;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Stack;

public class Listener {

	private Solitaire	solitaire;
	private Display		display;
	private int		distanceX;
	private int		distanceY;
	private boolean         dropSuccessful;

	public Listener(Solitaire solitaire, Display display) {
		this.solitaire      = solitaire;
		this.display        = display;
		this.dropSuccessful = false;
	}
	
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

	public void mouseReleased(MouseEvent event) {
		ArrayList<Card> dragged        = this.solitaire.getDragged();
		int             x              = event.getX();
		int             y              = event.getY();
		int             pileX          = 0;
		boolean         fitUpperPilesY = y >= UPPER_HALF_PILES_Y && y <= UPPER_HALF_PILES_Y + CARD_HEIGHT;
		
		// Drop at the foundation
		
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
					this.dropSuccessful = true;
				}
			}
		// Drop at the tableau
			
		} else if (!fitUpperPilesY && dragged.size() != 0) {
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
				Card firstDragged = dragged.get(0);
				int tableauY = UPPER_HALF_PILES_Y + CARD_HEIGHT + SPACE;
				int lastY    = tableauY + SPACE * (pile.size() - 1);
				if (pile.isEmpty() && firstDragged.getRank() == 'K') {
					if (y >= tableauY && y <= tableauY + CARD_HEIGHT) {
						int size = dragged.size();
						Stack<Card> previousPile;
						if (firstDragged.getPile() == Pile.WASTE) {
							previousPile = this.solitaire.getWaste();
						} else {
							int previousIndex = firstDragged.getPreviousTableauPileIndex();
							previousPile      = this.solitaire.getTableau().get(previousIndex);
						}
						for (int i = 0; i < size; i++) {
							previousPile.pop();
							if (firstDragged.getPile() == Pile.TABLEAU && !previousPile.isEmpty()) {
								previousPile.peek().setShown(true);
							}
							Card current = dragged.get(i);
							current.setPile(Pile.TABLEAU);
							current.setLastX(pileX);
							current.setLastY(lastY + SPACE);
							current.setPreviousTableauPileIndex(pileIndex);
							pile.push(current);
							lastY += SPACE;
						}
						this.dropSuccessful = true;
					}
				} else if (!pile.isEmpty() && firstDragged.getRank() != 'K') {
					Card firstPile = pile.peek();
					if (y >= lastY && y <= lastY + CARD_HEIGHT) {
						int     firstDraggedRankIndex = firstDragged.getRankIndex();
						int     firstPileRankIndex    = firstPile.getRankIndex();
						boolean firstDraggedBlack     = firstDragged.isBlack();
						boolean firstPileBlack        = firstPile.isBlack();
						boolean rankFits              = firstPileRankIndex == firstDraggedRankIndex + 1;
						boolean suitFits              = firstDraggedBlack  != firstPileBlack;
						if (rankFits && suitFits) {
							int size = dragged.size();
							Stack<Card> previousPile;
							if (firstDragged.getPile() == Pile.WASTE) {
								previousPile = this.solitaire.getWaste();
							} else {
								int previousIndex = firstDragged.getPreviousTableauPileIndex();
								previousPile      = this.solitaire.getTableau().get(previousIndex);
							}
							for (int i = 0; i < size; i++) {
								previousPile.pop();
								if (firstDragged.getPile() == Pile.TABLEAU && !previousPile.isEmpty()) {
									previousPile.peek().setShown(true);
								}
								Card current = dragged.get(i);
								current.setPile(Pile.TABLEAU);
								current.setLastX(pileX);
								current.setLastY(lastY + SPACE);
								current.setPreviousTableauPileIndex(pileIndex);
								pile.push(current);
								lastY += SPACE;
							}
							this.dropSuccessful = true;
						}
					}
				}
			}
		}
		if (!this.dropSuccessful) {
			for (Card card : dragged) {
				card.setCurrentX(card.getLastX());
				card.setCurrentY(card.getLastY());
			}
		}
		dragged.clear();
		this.dropSuccessful = false;
		this.display.repaint();
	}

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
			this.distanceX = x - pileX;
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
				for (int i = hidden; i < size; i++) {
					if (y >= cardY && y <= cardY + SPACE) {
						cardIndex = i;
						break;
					} else {
						cardY += SPACE;
					}
				}
				System.out.println(cardIndex);
				for (int i = cardIndex; i < size; i++) {
					this.solitaire.getDragged().add(pile.get(i));
					this.distanceX = x - pileX;
					this.distanceY = y - cardY;
				}
			}
		}
	}
	
	private void debugTableau() {
		System.out.println("--TABLEAU--");
		for (int i = 0; i < 7; i++) {
			Stack<Card> stack = this.solitaire.getTableau().get(i);
			System.out.printf("Pile #%d: %s\n", i, stack.toString());
		}
	}
	
	private void debugFoundation() {
		System.out.println("**FOUNDATION**");
		for (int i = 0; i < 4; i++) {
			Stack<Card> stack = this.solitaire.getFoundation().get(i);
			System.out.printf("Pile #%d: %s\n", i, stack.toString());
		}
	}
}