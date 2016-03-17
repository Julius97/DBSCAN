import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class Frame extends JFrame {
	
	private Screen screen;
	private DBSCAN dbscan;
	
	private JLabel pointlabel;
	private JTextField pointtextfield;
	private JButton pointbutton;
	private JButton clearlabelbutton;
	private JButton startscanbutton;
	private JButton createNoiseButton;
	private JButton editClusterButton;
	
	private ArrayList<Point> points = new ArrayList<Point>();
	
	private int pointsPerClick = 100;
	private int randOffsetX = 0;
	private int randOffsetY = 0;
	private int maxOffsetSpreadRadius = 50;
	private int noiseAmount = 20;
	
	private boolean clustered = false;
	
	public Frame(DBSCAN dbscan){
		super("DBSCAN Algorithm");
		
		this.dbscan = dbscan;
		
		screen = new Screen();
		screen.setBounds(400,0,600,600);
		add(screen);
		
		screen.addMouseListener(new MouseHandler());
	}
	
	public void repaintScreen(){
		screen.repaint();
	}
	
	private class Screen extends JLabel{
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.drawLine(400, 0, 400, 600);
			
			pointlabel = new JLabel("Punkte pro Klick");
			pointlabel.setBounds(20,5,150,20);
			add(pointlabel);
			
			pointtextfield = new JTextField();
			pointtextfield.setBounds(20,30,200,40);
			add(pointtextfield);
			
			pointbutton = new JButton("Übernehmen");
			pointbutton.setBounds(250,30,100,40);
			pointbutton.setMargin(new Insets(1,1,1,1));
			pointbutton.addActionListener(new ClearPointListButtonListener());
			add(pointbutton);
			
			clearlabelbutton = new JButton("Zurücksetzen");
			clearlabelbutton.setBounds(20,100,130,40);
			clearlabelbutton.setMargin(new Insets(1,1,1,1));
			clearlabelbutton.addActionListener(new ClearPointListButtonListener());
			add(clearlabelbutton);
			
			createNoiseButton = new JButton("Noise einfügen");
			createNoiseButton.setBounds(220,100,130,40);
			createNoiseButton.setMargin(new Insets(1,1,1,1));
			createNoiseButton.addActionListener(new CreateNoiseButtonListener());
			add(createNoiseButton);
			
			editClusterButton = new JButton("Ergänzen");
			editClusterButton.setBounds(20,160,130,40);
			editClusterButton.setMargin(new Insets(1,1,1,1));
			editClusterButton.addActionListener(new EditClusterButtonListener());
			add(editClusterButton);
			
			startscanbutton = new JButton("Starte DBSCAN");
			startscanbutton.setBounds(220,160,130,40);
			startscanbutton.setMargin(new Insets(1,1,1,1));
			startscanbutton.addActionListener(new StartScanButtonListener());
			add(startscanbutton);
			
			if(!clustered){
				for(int i=0;i<points.size();i++){
					Point p = points.get(i);
					g.fillOval(p.getX()-1, p.getY()-1, 2, 2);
				}
			}
			else{
				ArrayList<Cluster> clusters = dbscan.getClusters();
				for(int i=0; i< clusters.size(); i++){
					switch(i){
						case 0:
							g.setColor(Color.ORANGE);
							break;
						case 1:
							g.setColor(Color.BLUE);
							break;
						case 2:
							g.setColor(Color.GREEN);
							break;
						case 3:
							g.setColor(Color.PINK);
							break;
						case 4:
							g.setColor(Color.YELLOW);
							break;
						case 5:
							g.setColor(Color.MAGENTA);
							break;
						case 6:
							g.setColor(Color.CYAN);
							break;
						case 7:
							g.setColor(Color.GRAY);
							break;
						default:
							g.setColor(Color.LIGHT_GRAY);
							break;
					}
					
					for(Point p : clusters.get(i).getPoints()){
						g.fillOval(p.getX()-1, p.getY()-1, 2, 2);
					}
					
					g.setColor(Color.BLACK);
					
					for(Point p : dbscan.getNoise()){
						g.fillOval(p.getX()-1, p.getY()-1, 2, 2);
					}
				}
			}
		}
	}
	
	private class ClearPointListButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {			
			points.clear();
			dbscan.clearClusters();
			clustered = false;
			repaintScreen();
		}
		
	}
	
	private class EditClusterButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			clustered = false;
			dbscan.clearClusters();
			repaintScreen();
		}
		
	}
	
	private class StartScanButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			dbscan.updatePointList(points);
			clustered = true;
			repaintScreen();
		}
		
	}
	
	private class CreateNoiseButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			for(int i=0; i<noiseAmount;i++){
				Random randPos = new Random();
				int randXpos = 430 + randPos.nextInt(550);
				int randYpos = 30 + randPos.nextInt(550);
				Point p = new Point(randXpos,randYpos);
				points.add(p);
			}
			clustered = false;
			dbscan.clearClusters();
			repaintScreen();
		}
		
	}
	
	private class MouseHandler implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getX() >= 435 && e.getY() >= 35 && e.getX() <= 965 && e.getY() <= 560){
				for(int i=0; i<pointsPerClick;i++){
					Random randOffset = new Random();
					randOffsetX = (maxOffsetSpreadRadius / 2) - randOffset.nextInt(maxOffsetSpreadRadius);
					randOffsetY = (maxOffsetSpreadRadius / 2) - randOffset.nextInt(maxOffsetSpreadRadius);
					Point p = new Point(e.getX() + randOffsetX,e.getY() + randOffsetY);
					points.add(p);
				}
				repaintScreen();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}
		
	}

}
