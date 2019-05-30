package Attendance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
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
	//breaks image into amtsections squared so the image comparer counts that  many dots 
	//and compares it to the same location in the other image, downsizes to fit smallest image
	int amtsections = 100;
	BufferedImage picture = null;
	JPanel panel;
	Webcam webcam;
	JLabel jlabel;
	JFrame window;
	int element = 0;
	String mark;
	int currx,curry,currw,currh;
	int x,y,w,h,ry,rx,ccx,ccy;
	boolean flash=false;
	boolean att= false;
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
		window = new JFrame("Attendance");
		panel = new JPanel() {
			private static final long serialVersionUID = 8313458371063493113L;

			@Override 
			public void paintComponent(Graphics g) {
				//Faces Are Off Center
				super.paintComponent(g);
				if(flash) {
//					System.out.println("flash");
					g.setColor(Color.white);
					g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
					return;
				}
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
						ry = h;
						rx = 8*(w/10);
						ccx = x;
						ccy = y;
						y++;
						h++;
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
		Timer imagetimer = new Timer(0,new ActionListener() {
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
		Timer facedetection = new Timer(0,new ActionListener() {
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
				//flash=true;
				panel.repaint();
				picture=webcam.getImage();
				flash=false;
				panel.repaint();
				ImageComparer ic = new ImageComparer(picture);
				ic.setBackground(picture,ccx,ccy,rx,ry,Color.WHITE);
				BufferedImage blehpicture = (picture.getSubimage(ccx,ccy+1,rx,ry-1));
				String s = attendanceChecker(blehpicture);
				JFrame frame = new JFrame("Recorded!");
				if (att==true) mark = "PRESENT";
				//System.out.print(mark);
				//System.out.println();
				if(!s.equals("")) JOptionPane.showMessageDialog(frame, s + " has been marked: " + mark);
			}
		});
		panel.setLayout(null);
		//fix button bounds
		picbutton.setBounds(0,d.width/2+60, d.width, d.height/5);
		panel.add(picbutton);
		window.add(panel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}

	@SuppressWarnings("unused")
	public String attendanceChecker(BufferedImage picture) {
		//File files[] = new File("./ProfilePics").listFiles(file -> !file.isHidden() && !file.isDirectory());
		File files[] = new File("./ProfilePics").listFiles();
		int best = Integer.MAX_VALUE;
		int bindex=0;
		for(int i = element ; i < files.length ; i++) {
//			System.out.println(files[i].getName());
			int result = compare(picture, files[i]);
			if(result<best) {
				best=result;
				bindex=i;
			}
		}
		File file = files[bindex];
		//varies based on lighting
		if(best <=4000) {
			int j= file.getName().indexOf("-");
			int k= file.getName().lastIndexOf("-");
			String personName = file.getName().substring(j+1,k);
			//System.out.print(personName + ": ");
			att=true;
			return personName;
		}
		else { 
			att=false;
			JFrame frame = new JFrame("Error");
			int n = JOptionPane.showConfirmDialog(null, "Student Not On Roster. Would You Like To Add Student?", "Roster",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(n==0) {
				ProfileCreator pc = new ProfileCreator();
			}
			else if(n==1) {
				//do nothing
			}
			else {
				//user closed out of window
			}
			return "";
		}
	}

	public int compare(BufferedImage takenPic, File files) {
		try {
			int totalscore = 0;
			BufferedImage i = ImageIO.read(files);
			ImageComparer e = new ImageComparer (i);
			ImageComparer d = new ImageComparer (takenPic);
			takenPic = (BufferedImage) d.detectWhite(takenPic,true);
			i=(BufferedImage) e.detectWhite(i,true);
			int d1=0;
			int e1=0;
			fixamtsections(takenPic,i);
			for(int r=0;r<amtsections;r++) {
				for(int c=0;c<amtsections;c++) {
					d1 = d.greenCount(takenPic,r*(takenPic.getWidth()/amtsections),(r+1)*(takenPic.getWidth()/amtsections),
							c*(takenPic.getHeight()/amtsections),(c+1)*(takenPic.getHeight()/amtsections));
					e1 = e.greenCount(i,r*(i.getWidth()/amtsections),(r+1)*(i.getWidth()/amtsections),
							c*(i.getHeight()/amtsections),(c+1)*(i.getHeight()/amtsections));
					totalscore+=Math.abs(d1-e1);
				}
			}
//			System.out.println(totalscore);
			return totalscore;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private void fixamtsections(BufferedImage takenPic, BufferedImage i) {
		int w1 = takenPic.getWidth();
		int h1 = takenPic.getHeight();
		int w2 = i.getWidth();
		int h2 = i.getHeight();
		double min = Math.sqrt(Math.min(Math.min(w1, w2),Math.min(h1,h2)));
		if(min<amtsections) {
			amtsections = (int) min;
		}
	}
}
