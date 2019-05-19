package Attendance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

public class profilecreator {
	Image j = null;
	JPanel panel;
	Webcam webcam;
	private static final Stroke STROKE = new BasicStroke(10.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);
	private List<DetectedFace> faces;
	private static HaarCascadeDetector detector;
	public profilecreator() {
		initialize();
	}

	private void initialize() {
		webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open(true);
		detector = new HaarCascadeDetector();
		JFrame window = new JFrame();
		panel = new JPanel() {
			private static final long serialVersionUID = 8313458371063493113L;

			@Override 
			public void paintComponent(Graphics g) {
				//Faces Are Off Center
				super.paintComponent(g);
				g.drawImage(j, 0, 0, panel.getWidth(), (4*panel.getHeight())/5, this);
				if(faces != null && faces.size() > 0) {
					for (DetectedFace face: faces) {
						Rectangle bounds = face.getBounds();
	
						int dx = (int) (0.1 * bounds.width);
						int dy = (int) (0.2 * bounds.height);
						int x = (int) bounds.x - dx +50;
						int y = (int) bounds.y - dy;
						int w = (int) bounds.width + 2 * dx +50;
						int h = (int) bounds.height + dy;
						
						Graphics2D g2 = (Graphics2D) g.create();
						g2.setStroke(STROKE);
						g2.setColor(Color.GREEN);
						g2.drawRect(x, y, w, h);
						g2.dispose();
					}
				}
			}
		};
		Dimension d = new Dimension(800,600);
		window.setPreferredSize(d);
		Timer imagetimer = new Timer((int) 0,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!webcam.isOpen()) {
					return;
				}
				j = webcam.getImage();
				panel.repaint();
			}		
		});
		imagetimer.start();
		//Made This A Separate Slower Timer Because It Was Really Laggy But It Didn't Help Too Much
		Timer facedetection = new Timer(1000,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!webcam.isOpen()) {
					return;
				}
				faces = detector.detectFaces(ImageUtilities.createFImage(webcam.getImage()));
				panel.repaint();
			}		
		});
		facedetection.start();
		
		JButton picbutton = new JButton("Take Picture");
		picbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newprofile();
			}
		});
		panel.setLayout(null);
		//fix button bounds
		picbutton.setBounds(0,(3*d.width)/5, d.width, d.height/5);
		System.out.println(picbutton.getY());
		panel.add(picbutton);
		window.add(panel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
	public void newprofile() {
		// this will save the image as a file in profiles after asking for a name and a conformation
		String personID = JOptionPane.showInputDialog("What Is Your Full Name?");
		JFrame frame = new JFrame("JOptionPane showMessageDialog example");
		try {
			String name = "ProfilePics/" + String.format("frcam-" + personID + "-%d.jpg", System.currentTimeMillis());
			ImageIO.write(webcam.getImage(), "JPG", new File(name));
			JOptionPane.showMessageDialog(frame, "Your Image Has Been Saved!");
			System.out.format("File %s has been saved\n", name);
		} 
		catch (IOException t) {
			t.printStackTrace();
		}
		
	}

}
