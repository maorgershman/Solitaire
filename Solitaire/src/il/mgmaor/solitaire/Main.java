package il.mgmaor.solitaire;

import javax.swing.JFrame;

public class Main {
	
	private Display display;
	
	public Main() {
		initGraphics();
	}
	
	private void initGraphics() {
		JFrame frame = new JFrame("Solitaire");
		display = new Display(this);
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(display);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		// TODO: Add icon.
	}
	
}
 