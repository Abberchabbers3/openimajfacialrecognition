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
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
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

public class AttendanceTaker {
	Image picture = null;
	JPanel panel;
	Webcam webcam;
	JLabel jlabel;
	int element = 0;
	String mark;
	int currx,curry,currw,currh;
	int x,y,w,h;
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
						@SuppressWarnings("unused")
						int dy = (int) (0.2 * bounds.height);
						currx = (int) bounds.x;
						curry = (int) bounds.y;
						currw = (int) bounds.width + 2 * dx;
						currh = (int) bounds.height;
						x=currx;
						y=curry;
						w=currw;
						h=currh;
						
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
				BufferedImage blehpicture = ((BufferedImage) picture).getSubimage(x,y,w,h);
				String s = attendanceChecker(blehpicture);
				
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

	public String attendanceChecker(BufferedImage picture) {
		File files[] = new File("./ProfilePics").listFiles(file -> !file.isHidden() && !file.isDirectory());
		for(int i = element ; i < files.length ; i++) {
			System.out.println(files[i].getName());
			int result = compare(picture, files[i]);
			if(result <=48000) {
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

		return "";
	}

	public int compare(BufferedImage takenPic, File files) {
		try {
			BufferedImage i = ImageIO.read(files);
			ImageIcon icon = new ImageIcon(takenPic);
			int input = JOptionPane.showConfirmDialog(null, "Is this you?", "Person Checker",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
			ImageComparer e = new ImageComparer (i);
			ImageComparer d = new ImageComparer (takenPic);
			takenPic = (BufferedImage) d.detectWhite(takenPic);
			int d1 = d.greenCount(takenPic);
			int e1 = e.greenCount(i);
			System.out.println(d1);
			System.out.println(e1);
			return d1-e1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
