package il.mgmaor.solitaire;

import java.awt.Dimension;

import javax.swing.JPanel;

public class Display extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	//
	
	private Main main;
	
	public Display(Main main) {
		this.main = main;
		
		setPreferredSize(new Dimension(800, 600));
		setOpaque(true);
	}
}
