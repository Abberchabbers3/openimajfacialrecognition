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
	}	
}