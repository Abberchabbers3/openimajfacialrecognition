package Attendance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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

public class AttendanceTaker {
	Image picture = null;
	JPanel panel;
	Webcam webcam;
	JLabel jlabel;
	int element = 0;
	String mark;
	boolean att = true;
	private static final Stroke STROKE = new BasicStroke(10.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);
	private List<DetectedFace> faces;
	private static HaarCascadeDetector detector;
	public AttendanceTaker() {
		initialize();
	}

	private void initialize() {
		webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open(true);
		detector = new HaarCascadeDetector();
		JFrame window = new JFrame("Attendance");
		panel = new JPanel() {
			private static final long serialVersionUID = 8313458371063493113L;

			@Override 
			public void paintComponent(Graphics g) {
				//Faces Are Off Center
				super.paintComponent(g);
				g.drawImage(picture, 0, 0, panel.getWidth(), (4*panel.getHeight())/5, this);
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
				picture = webcam.getImage();
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
		JButton picbutton = new JButton("Take Attendance");
		picbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = attendanceChecker(picture);
				JFrame frame = new JFrame("Recorded!");
				if (att==true) mark = "PRESENT";
				else mark = "ABSENT";
				JOptionPane.showMessageDialog(frame, s + " has been marked: " + mark);
			    
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

	public String attendanceChecker(Image picture) {
		//		// Not Sure If This Will Work
		File files[] = new File("./ProfilePics").listFiles(file -> !file.isHidden() && !file.isDirectory());
		for(int i = element ; i < files.length ; i++) {
			System.out.println(files[i].getName());
			int result = compare(picture, files[i]);
			if(result <=10) {
				//replace **************** with String Name Of File
				int j= files[i].getName().indexOf("-");
				int k= files[i].getName().lastIndexOf("-");
				String personName = files[i].getName().substring(j+1,k);
				element++;
				return personName;
			}
			else { 
				att=false;
				JFrame frame = new JFrame("Error");
				JOptionPane.showMessageDialog(frame, "Student Is Not On The Roster");
				return "";
			}
		}

		//This is just to get rid of the  error message
		return "";
	}

	public int compare(Image takenPic, File files) {
		// Return Value Of Comparison
		// Same Person Should Get int To Be Close To 0

		return 0;
	}
}
