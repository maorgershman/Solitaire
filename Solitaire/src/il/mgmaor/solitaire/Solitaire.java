package il.mgmaor.solitaire;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Solitaire {

	// Constants.

	public static final char	RANKS[]	= { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };

	public static final char	SUITS[]	= { '♣', '♦', '♥', '♠' };

	public static final Card[] deckOrganized() {
		Card[] temp = new Card[52];

		for (int i = 0; i < temp.length; i++) {
			temp[i] = new Card(RANKS[i / 4], SUITS[i % 4]);
		}

		return temp;
	}

	// The piles.

	private Card[]			deck;

	private Stack<Card>		stock;

	private Stack<Card>		waste;

	private ArrayList<Stack<Card>>	foundation;

	private ArrayList<Stack<Card>>	tableau;

	private ArrayList<Card>		dragged;

	// Constructor.

	public Solitaire() {
		createPiles();
		shuffle();
		fillPiles();
	}

	// Local methods.

	private void createPiles() {
		// Create the deck.
		this.deck = deckOrganized();

		// Create the foundation.
		this.foundation = new ArrayList<>(4);
		for (int i = 0; i < 4; i++) {
			this.foundation.add(new Stack<>());
		}

		// Create the tableau.
		this.tableau = new ArrayList<>(7);
		for (int i = 0; i < 7; i++) {
			this.tableau.add(new Stack<>());
		}

		// Create the stock.
		this.stock = new Stack<>();

		// Create the waste.
		this.waste = new Stack<>();

		// Create an empty dragged stack.
		this.dragged = new ArrayList<>();
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

	private void fillPiles() {
		// The index from which we start taking cards from the deck.
		int startFrom = 0;

		// Fill the stock with 24 cards, but update them first.
		for (int i = 0; i < 24; i++) {
			// Update the counter.
			Card card = this.deck[startFrom++];

			// Set the card location as the stock location.
			card.setLastX(Display.STOCK_X);
			card.setLastY(Display.UPPER_HALF_PILES_Y);

			// Set the pile to Stock.
			card.setPile(Pile.STOCK);

			// Add the card to the stock.
			this.stock.push(card);
		}

		// Fill all the piles of the tableau.
		for (int i = 0; i < 7; i++) {
			Stack<Card> pile = this.tableau.get(i);

			// Update the counter.
			startFrom += i;

			// The top card that is yet to be added.
			Card top = this.deck[startFrom + i];
			// Set the card shown
			top.setShown(true);
			// Set the card heading
			top.setHeading(true);

			// Add the hidden cards and the shown card.
			for (int j = 0; j < i + 1; j++) {
				Card card = this.deck[startFrom + j];

				// The location that we want to place the card at.
				Point location = Display.tableauLocation(i, j);

				// Update the card properties.
				card.setLastX(location.x);
				card.setLastY(location.y);
				card.setPile(Pile.TABLEAU);
				card.setTableauPileIndex(i);
				card.setTableauCardIndex(j);

				// Add the card to the pile.
				pile.push(card);
			}

			// Add the pile to the tableau.
			this.tableau.add(pile);
		}
	}

}
