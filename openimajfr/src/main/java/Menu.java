import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Menu {
	
	public static void main(String[] args) {
		Menu m = new Menu();
		m.start();
	}

	private void start() {
		JFrame window = new JFrame();
		JPanel panel = new JPanel() {
			@Override 
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};
		Dimension d = new Dimension(500,450);
		window.setPreferredSize(d);
		JButton newface = new JButton("Create new Profile");
		newface.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//creates a new profile
			}
		});
		newface.setBounds(panel.getWidth()/3, 0, 50, 50);
		panel.add(newface);
		window.add(panel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
}
