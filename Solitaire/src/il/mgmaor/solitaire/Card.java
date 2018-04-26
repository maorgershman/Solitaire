package il.mgmaor.solitaire;

public class Card {

	private char	rank;

	private char	suit;

	private int	lastX;

	private int	lastY;

	private int	currentX;

	private int	currentY;

	private int	previousTableauPileIndex;

	private boolean	shown;

	private Pile	pile;

	public Card(char rank, char suit) {
		this.rank = rank;
		this.suit = suit;
	}

	public char getRank() {
		return rank;
	}
	
	public int getRankIndex() {
		int index = -1;
		for (int i = 0; i < 13; i++) {
			if (rank == Solitaire.RANKS[i]) {
				index = i;
				break;
			}
		}
		return index;
	}

	public void setRank(char rank) {
		this.rank = rank;
	}

	public char getSuit() {
		return suit;
	}
	
	public boolean isBlack() {
		return suit == Solitaire.SUITS[0] || suit == Solitaire.SUITS[3];
	}

	public void setSuit(char suit) {
		this.suit = suit;
	}

	public int getLastX() {
		return lastX;
	}

	public void setLastX(int lastX) {
		this.lastX = lastX;
		this.currentX = lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public void setLastY(int lastY) {
		this.lastY = lastY;
		this.currentY = lastY;
	}

	public int getCurrentX() {
		return currentX;
	}

	public void setCurrentX(int currentX) {
		this.currentX = currentX;
	}

	public int getCurrentY() {
		return currentY;
	}

	public void setCurrentY(int currentY) {
		this.currentY = currentY;
	}

	public int getPreviousTableauPileIndex() {
		return previousTableauPileIndex;
	}

	public void setPreviousTableauPileIndex(int previousTableauPileIndex) {
		this.previousTableauPileIndex = previousTableauPileIndex;
	}

	public boolean isShown() {
		return shown;
	}

	public void setShown(boolean shown) {
		this.shown = shown;
	}

	public Pile getPile() {
		return pile;
	}

	public void setPile(Pile pile) {
		this.pile = pile;
	}

	@Override
	public String toString() {
		String rank = String.valueOf(this.rank);
		String suit = "C";
		if (this.suit == Solitaire.SUITS[1]) {
			suit = "D";
		} else if (this.suit == Solitaire.SUITS[2]) {
			suit = "H";
		} else if (this.suit == Solitaire.SUITS[3]) {
			suit = "S";
		}
		return String.format("%s:%s", rank, suit);
	}
	
	public void fullDebug() {
		System.out.println("---CARD INFO---");
		System.out.println("Name " + toString());
		System.out.println("Shown " + shown);
		System.out.println("Pile " + pile);
		System.out.println("Last X " + lastX);
		System.out.println("Last Y " + lastY);
		System.out.println("Previous Pile Index " + previousTableauPileIndex);
	}
}
