package il.mgmaor.solitaire;

import static il.mgmaor.solitaire.Display.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Solitaire {

	// Constants.

	public static final char RANKS[] = { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };

	public static final char SUITS[] = { '♣', '♦', '♥', '♠' };

	public static final Card[] deckOrganized() {
		Card[] temp = new Card[52];

		for (int i = 0; i < temp.length; i++) {
			temp[i] = new Card(RANKS[i / 4], SUITS[i % 4]);
		}

		return temp;
	}
	
	public static boolean isMouseOn(Card card, int xMouse, int yMouse) {
		boolean inRegionX = xMouse >= card.getCurrentX() && xMouse <= card.getCurrentX() + Display.CARD_WIDTH;
		boolean inRegionY = yMouse >= card.getCurrentY() && yMouse <= card.getCurrentY() + Display.CARD_HEIGHT;
		
		return inRegionX && inRegionY;
	}

	// The piles.

	private Card[]			deck;

	private Stack<Card>		stock;

	private Stack<Card>		waste;

	private ArrayList<Stack<Card>>	foundation;

	private ArrayList<Stack<Card>>	tableau;

	private ArrayList<Card>		dragged;
	
	public  boolean gameEnd;

	// Constructor.

	public Solitaire() {
		this.gameEnd = false;
		createPiles();
		shuffle();
		fillPiles();
	}

	// Local methods.

	private void createPiles() {
		this.deck       = deckOrganized();
		this.foundation = new ArrayList<>(4);
		this.tableau    = new ArrayList<>(7);
		this.stock      = new Stack<>();
		this.waste      = new Stack<>();
		this.dragged    = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			this.foundation.add(new Stack<>());
		}
		for (int i = 0; i < 7; i++) {
			this.tableau.add(new Stack<>());
		}
	}

	private void shuffle() {
		Random rand = new Random();
		for (int i = 0; i < this.deck.length; i++) {
			int randomPosition = rand.nextInt(this.deck.length);
			Card temp = this.deck[i];
			this.deck[i] = this.deck[randomPosition];
			this.deck[randomPosition] = temp;
		}
	}

	private int startFrom = 0;
	
	private void fillPiles() {
		fillStock();
		fillTableau();
	}
	
	private void fillStock() {
		for (int i = 0; i < 24; i++) {
			Card card = this.deck[this.startFrom++];
			card.setLastX(Display.STOCK_X);
			card.setLastY(Display.UPPER_HALF_PILES_Y);
			card.setPile(Pile.STOCK);
			this.stock.push(card);
		}
	}
	
	private void fillTableau() {
		int x = STOCK_X;
		int y;
		for (int i = 0; i < 7; i++) {
			Stack<Card> pile = this.tableau.get(i);
			Card        top  = this.deck[(this.startFrom += i) + i];
			y                = UPPER_HALF_PILES_Y + CARD_HEIGHT + SPACE;
			for (int j = 0; j < i + 1; j++) {
				Card card = this.deck[startFrom + j];
				card.setLastX(x);
				card.setLastY(y);
				card.setPile(Pile.TABLEAU);
				card.setPreviousTableauPileIndex(i);
				pile.push(card);
				y += SPACE;
			}
			x += CARD_WIDTH + SPACE;
			top.setShown(true);
			this.tableau.add(pile);
		}
	}

	public Card[] getDeck() {
		return deck;
	}

	public void setDeck(Card[] deck) {
		this.deck = deck;
	}

	public Stack<Card> getStock() {
		return stock;
	}

	public void setStock(Stack<Card> stock) {
		this.stock = stock;
	}

	public Stack<Card> getWaste() {
		return waste;
	}

	public void setWaste(Stack<Card> waste) {
		this.waste = waste;
	}

	public ArrayList<Stack<Card>> getFoundation() {
		return foundation;
	}
	
	public Stack<Card> getFoundationPile(char suit) {
		int suitIndex = -1;
		for (int i = 0; i < 4; i++) {
			if (suit == SUITS[i]) {
				suitIndex = i;
			}
		}
		return this.foundation.get(suitIndex);
	}

	public void setFoundation(ArrayList<Stack<Card>> foundation) {
		this.foundation = foundation;
	}

	public ArrayList<Stack<Card>> getTableau() {
		return tableau;
	}
	
	public int getHiddenCards(int pileIndex) {
		int count = 0;
		for (Card card : this.tableau.get(pileIndex)) {
			if (!card.isShown()) {
				count++;
			}
		}
		return count;
	}

	public void setTableau(ArrayList<Stack<Card>> tableau) {
		this.tableau = tableau;
	}

	public ArrayList<Card> getDragged() {
		return dragged;
	}

	public void setDragged(ArrayList<Card> dragged) {
		this.dragged = dragged;
	}
}
