package Attendance;
import javax.swing.JOptionPane;

public class Menu {
	
	public static void main(String[] args) {
		Menu m = new Menu();
		m.start();
	}

	@SuppressWarnings("unused")
	private void start() {
		Object[] options = {"Create Profile","Take Attendance"};
		int n = JOptionPane.showOptionDialog(null, "What would you like to do?", "Set-up", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 0);
		if(n==0) {
			//Code To Create A New Profile
			ProfileCreator pc = new ProfileCreator();
			
		}
		else if(n==1) {
			//Code For Taking Attendance
			AttendanceTaker at = new AttendanceTaker();
		}
		else {
			//user closed out of window
		}
//		JFrame window = new JFrame();
//		JPanel panel = new JPanel() {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 2162279053724239438L;
//
//			@Override 
//			public void paintComponent(Graphics g) {
//				super.paintComponent(g);
//			}
//		};
//		panel.setLayout(null);
//		Dimension d = new Dimension(500,450);
//		window.setPreferredSize(d);
//		JButton newface = new JButton("Create new Profile");
//		newface.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				System.out.println("new face");
//				//creates a new profile with an image and name in the profiles folder
//			}
//		});
//		newface.setBounds(panel.getWidth()/4, 0, 50, 50);
//		panel.add(newface);
//		JButton takeattendance = new JButton("Take attendance");
//		takeattendance.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				System.out.println("take attendance");
//				//takes attendance by opening a camera and scanning for faces;
//				//once each face is found it will compare them with those in the 
//				//profiles folder and declare they are present
//			}
//		});
//		takeattendance.setBounds(panel.getWidth()-panel.getWidth()/4, 0, 50, 50);
//		panel.add(takeattendance);
//		window.add(panel);
//		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		window.pack();
//		window.setVisible(true);
	}
}
