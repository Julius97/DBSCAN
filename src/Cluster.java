import java.util.ArrayList;

public class Cluster {
	
	private ArrayList<Point> points = new ArrayList<Point>();
	
	public void addPoint(Point p){
		points.add(p);
	}
	
	public ArrayList<Point> getPoints(){
		return points;
	}
	
	public void clearCluster(){
		points.clear();
	}
	
}
