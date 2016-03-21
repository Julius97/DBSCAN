import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


public class DBSCAN {
	
	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	private ArrayList<Point> noisePoints = new ArrayList<Point>();
	
	private final double epsilon;
	private final int minPts;
	
	private enum PointStatus{
		NOISE,
		CLUSTERED,
		NOTCLUSTERED
	}
	
	private HashMap<Point,PointStatus> visited = new HashMap<Point,PointStatus>();
	private HashMap<Point,Double> minPtsNearestNeighbours = new HashMap<Point,Double>();
	private HashMap<Point,Double> minPtsNearestNeighbours2 = new HashMap<Point,Double>();
	
	public DBSCAN(double epsilon, int minPts){
		this.epsilon = epsilon;
		this.minPts = minPts;
	}
	
	public void updatePointList(ArrayList<Point> points){
		this.points = points;
		for(Point p : points)
			visited.put(p, PointStatus.NOTCLUSTERED);
		calculateCluster();
		setNoise();
	}
	
	private void calculateCluster(){
		for(Point p : points){
			if(visited.get(p) != PointStatus.NOTCLUSTERED){
				continue;
			}
			
			ArrayList<Point> neighbourList = epsilonNearestNeighbours(p);
			if(neighbourList.size() >= minPts){
				Cluster cluster = new Cluster();
				cluster = expandCluster(cluster,p,neighbourList);
				clusters.add(cluster);
			}
			else{
				visited.put(p, PointStatus.NOISE);
			}
		}
	}
	
	private Cluster expandCluster(Cluster c, Point p, ArrayList<Point> neighbours){
		visited.put(p, PointStatus.CLUSTERED);
		c.addPoint(p);
		
		for(int i=0;i<neighbours.size();i++){
			Point currentNeighbour = neighbours.get(i);
			PointStatus pointStatus = visited.get(currentNeighbour);
			
			if(pointStatus == PointStatus.NOTCLUSTERED){
				ArrayList<Point> neighbourNeighbours = epsilonNearestNeighbours(currentNeighbour);
				
				if(neighbourNeighbours.size() >= minPts){
					neighbours = merge(neighbours,neighbourNeighbours);
				}
			}
			
			if(pointStatus != PointStatus.CLUSTERED){
				visited.put(currentNeighbour, PointStatus.CLUSTERED);
				c.addPoint(currentNeighbour);
			}
		}
		
		return c;
	}
	
	private ArrayList<Point> epsilonNearestNeighbours(Point p){
		ArrayList<Point> neighbourPoints = new ArrayList<Point>();
		for(Point x : points){
			if(!x.equals(p)){
				if(epsilon > Math.sqrt((double)((double)Math.pow(Math.abs(x.getX()-p.getX()), 2) + (double)Math.pow(Math.abs(x.getY()-p.getY()), 2)))){
					neighbourPoints.add(x);
				}
			}
		}
		
		return neighbourPoints;
	}
	
	private ArrayList<Point> merge(ArrayList<Point> one, ArrayList<Point> two){
		for(Point p : two){
			if(!one.contains(p)){
				one.add(p);
			}
		}
		return one;
	}
	
	private void setNoise(){
		for(Point p : points){
			if(visited.get(p) == PointStatus.NOISE){
				noisePoints.add(p);
			}
		}
	}
	
	public ArrayList<Cluster> getClusters(){
		return clusters;
	}
	
	public ArrayList<Point> getNoise(){
		return noisePoints;
	}
	
	public void clearClusters(){
		for(Cluster c : clusters){
			c.clearCluster();
		}
		noisePoints.clear();
		clusters.clear();
	}
	
	public Entry<Point, Double> getMinPtsNearestNeighbour(Point p){
		outerLoop:for(Point x : points){
			minPtsNearestNeighbours = minPtsNearestNeighbours2;
			if(!x.equals(p)){
				if(minPtsNearestNeighbours.size() < minPts){
					minPtsNearestNeighbours2.put(x,Math.sqrt((double)((double)Math.pow(Math.abs(x.getX()-p.getX()), 2) + (double)Math.pow(Math.abs(x.getY()-p.getY()), 2))));
					minPtsNearestNeighbours.put(x,Math.sqrt((double)((double)Math.pow(Math.abs(x.getX()-p.getX()), 2) + (double)Math.pow(Math.abs(x.getY()-p.getY()), 2))));
				}
				else{
					Iterator<Map.Entry<Point, Double>> itr = minPtsNearestNeighbours2.entrySet().iterator();
					while(itr.hasNext()){
						Map.Entry<Point, Double> l = itr.next();
						if(l.getValue() > Math.sqrt((double)((double)Math.pow(Math.abs(x.getX()-p.getX()), 2) + (double)Math.pow(Math.abs(x.getY()-p.getY()), 2)))){
							minPtsNearestNeighbours2.remove(getFarestMinPtsNearestNeighbour().getKey());
							minPtsNearestNeighbours2.put(x, Math.sqrt((double)((double)Math.pow(Math.abs(x.getX()-p.getX()), 2) + (double)Math.pow(Math.abs(x.getY()-p.getY()), 2))));
							continue outerLoop;
						}
					}
				}
			}
		}
	
		minPtsNearestNeighbours = minPtsNearestNeighbours2;
		
		return getFarestMinPtsNearestNeighbour();
	}
	
	private Entry<Point, Double> getFarestMinPtsNearestNeighbour(){
		Entry<Point, Double> farestElement = minPtsNearestNeighbours2.entrySet().iterator().next();
		
		for(Entry<Point, Double> l : minPtsNearestNeighbours2.entrySet()){
			if(l.getValue() > farestElement.getValue())
				farestElement = l;
		}
		
		return farestElement;
	}
	
	public void clearMinPtsNearestNeighboursList(){
		minPtsNearestNeighbours.clear();
		minPtsNearestNeighbours2.clear();
	}
	
}
