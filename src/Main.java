import javax.swing.JFrame;


public class Main {

	public static void main(String[] args) {
		
		DBSCAN dbscan = new DBSCAN(10.0,3);
		
		Frame frame = new Frame(dbscan);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1800, 600);
		frame.setVisible(true);
		frame.setResizable(false);
		
	}

}
