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
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

public class ProfileCreator {
	Image j = null;
	JPanel panel;
	int currx,curry,currw,currh;
	int x,y,w,h;
	Webcam webcam;
	private static final Stroke STROKE = new BasicStroke(10.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);
	private List<DetectedFace> faces;
	private static HaarCascadeDetector detector;
	public ProfileCreator() {
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
				super.paintComponent(g);
				g.drawImage(j, 0, 0, panel.getWidth(), (4*panel.getHeight())/5, this);
				if(faces != null && faces.size() > 0) {
					for (DetectedFace face: faces) {
						Rectangle bounds = face.getBounds();
	
						int dx = (int) (0.1 * bounds.width);
						int dy = (int) (0.2 * bounds.height);
						currx = (int) bounds.x - dx;
						curry = (int) bounds.y;
						currw = (int) bounds.width + 2 * dx;
						currh = (int) bounds.height;
						
						Graphics2D g2 = (Graphics2D) g.create();
						g2.setStroke(STROKE);
						g2.setColor(Color.GREEN);
						g2.drawRect(currx+50, curry, currw+2*dx, currh);
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
		
		JButton picbutton = new JButton("Create Profile");
		picbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				x=currx;
				y=curry;
				w=currw;
				h=currh;
				newprofile(webcam.getImage());
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
	public void newprofile(Image profileimage) {
		// this will save the image as a file in profiles after asking for a name and a conformation
		if(faces.size()>1) {
			JOptionPane.showMessageDialog(null, "Make sure there is only one face in frame!");
			return;
		}
		else if (faces.size()==0) {
			JOptionPane.showMessageDialog(null, "There are no faces in view!");
			return;
		}
		String personID=null;
		while(personID==null) {
			personID = JOptionPane.showInputDialog("What Is Your Full Name?");
		}
		JFrame frame = new JFrame("Profile Creator");
		try {
			String name = "ProfilePics/" + String.format("frcam-" + personID + "-%d.jpg", System.currentTimeMillis());
			profileimage = ((BufferedImage) profileimage).getSubimage(x,y,w,h);
			ImageIcon icon = new ImageIcon(profileimage);
			int input = JOptionPane.showConfirmDialog(null, "Is this you?", "Person Checker",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
			if(input==1) {
				JOptionPane.showMessageDialog(null, "This profile has not been saved.");
				return;
			}
			ImageIO.write((RenderedImage) profileimage, "JPG", new File(name));
			JOptionPane.showMessageDialog(frame, "Your Image Has Been Saved!");
			System.out.format("File %s has been saved\n", name);
		} 
		catch (IOException t) {
			t.printStackTrace();
		}
		
	}
	public String getName() {
		//Return Name From Saved Profile Image
		
		return null;
	}
}
