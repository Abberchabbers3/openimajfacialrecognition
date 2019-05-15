package justinproj;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

//obviously need to implement face detection here
public class profilecreator {
	Image j = null;
	JPanel panel;
	public profilecreator() {
		initialize();
	}

	private void initialize() {
		final Webcam webcam = Webcam.getDefault();
		webcam.open();
		JFrame window = new JFrame();
		panel = new JPanel() {
			@Override 
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(j, 0, 0, 500, 250, this);
			}
		};
		Dimension d = new Dimension(500,450);
		window.setPreferredSize(d);
		Timer imagetimer = new Timer((int) 0,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				j = webcam.getImage();
				panel.repaint();
			}		
			});
		
		imagetimer.start();
		
		JButton picbutton = new JButton("Take Picture");
		picbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String name = String.format("frcam-%d.jpg", System.currentTimeMillis());
					ImageIO.write(webcam.getImage(), "JPG", new File(name));
					System.out.format("File %s has been saved\n", name);
				} 
				catch (IOException t) {
					t.printStackTrace();
				}
			}
		});
		//DetectEyeTrial trial = new DetectEyeTrial(webcam.getImage());
		//trial.detectWhite(webcam.getImage());
		panel.setLayout(null);
		picbutton.setBounds(0, 250, 500, 200);
		panel.add(picbutton);
		window.add(panel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
	public void newprofile(String name) {
		// TODO Auto-generated method stub
		
	}

}
