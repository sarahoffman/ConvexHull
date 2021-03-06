import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;
import java.lang.Math;

public class QuickHull {

    private ArrayList<Point> input;
    private int n;
    protected ArrayList<Point> output;
	
    public QuickHull( ArrayList<Point> S ){
		//creates a QuickHull object from the given ArrayList of points
		input = S;
		output = new ArrayList<Point>();
		n = S.size();
		System.out.println( "input: " + this.input );
    }
	
    // performs the max/min calculation
    public Point[] getExtremes( ArrayList<Point> a ) {
		Point[] output = new Point[2];
		Point min = a.get(0);
		Point max = a.get(0);
		for( int i = 0; i < n; i++ ) {
			Point p = a.get(i);
			if( p.getX() < min.getX() ) {
				min = p;
			}
			if( p.getX() > max.getX() ) {
				max = p;
			}
		}
		output[0] = min;
		output[1] = max;
	

		return output;
    }
	
	
    // Function that actually finds the convex hull
    public ArrayList<Point> getConvexHull() {
		if( n > 0 ) {
			Point[] result = this.getExtremes(input);
			Point min = result[0];
			Point max = result[1];


		
			Segment s = new Segment( min,max );
		
			// extremes in x will be a part of the convex hull
			this.output.add( min );
			this.input.remove(min);
			if( min != max ) {
				this.output.add( max );
				this.input.remove(max);
			} 

			ArrayList<Point> left = new ArrayList<Point>();
			ArrayList<Point> right = new ArrayList<Point>(); 

			// get a list of all of the points to the left of the line
			for(int i = 0; i< this.input.size(); i++){
				Point p = this.input.get(i);
				if (s.isLeft(p) == true){
					left.add(p);
				}
				else{
					right.add(p); 
				}
			}	
			// perform the recursive algorithm on the two points
			this.subHull(left,s);
			this.subHull(right,s); 
		}
		return this.output;
    }

    // write the recursive algorithm.
    public void subHull(ArrayList<Point> a, Segment s ) {
		// base case for recursion
		if (a.size() < 1){
			return; 
		}
		
	
		//loop through the arrayList to find the point with the max distance
		double max = Double.MIN_VALUE;
		Point maxPoint = a.get(0);
	
		//find the point with the max perpendicular distance
		for(int i = 0; i< a.size(); i++){
			double dist = this.distance(s, a.get(i));
			if(dist>max){
				max = dist; 
				maxPoint = a.get(i); 
			}
		}

		

		// add the max point to the convex hull
		if (!this.output.contains(maxPoint)) {
			this.output.add(maxPoint);
		}
		a.remove(maxPoint);

		//create a segment between the maxPoint and the endpoints of the given segment
		Segment min2dist = new Segment(s.getP1(), maxPoint);
		Segment max2dist = new Segment(s.getP2(), maxPoint);

		ArrayList<Point> left = new ArrayList<Point>();
		ArrayList<Point> right = new ArrayList<Point>();

		// get a list of all of the points to the left of the line
		// we need to handle the case of the max point being below the segment 
		// and the max point being above the segment differently
		// this is due to our isLeft method
		
		// is maxPoint is below segment
		if (s.getP1().getY() >= maxPoint.getY() || s.getP2().getY() >= maxPoint.getY()){
			for(int i = 0; i< a.size(); i++){
				Point p = a.get(i);
				if (max2dist.isLeft(p) == true){
					left.add(p);
				} 
				else if (min2dist.isLeft(p) == false) {
					right.add(p);
				}
			}
		}
		
		// if the maxPoint is above segment
		else{
		
			for(int i = 0; i< a.size(); i++){
				Point p = a.get(i);
				if (min2dist.isLeft(p) == true){
					left.add(p);
				} 
				else if (max2dist.isLeft(p) == false) {
					right.add(p);
				}
			}
		
		}

		// call algorithm recursively
		subHull(left, min2dist);
		subHull(right, max2dist); 
    }
	
    public double distance( Segment s, Point p ) {
		double[] a = s.getCo();
		double dist = Math.abs( a[0]*p.getX() + a[1]*p.getY() + a[2] )/Math.sqrt( a[0]*a[0] + a[1]*a[1] );
		return dist;
    }
	
    public static void main( String args[] ) {
		ArrayList<Point> S = new ArrayList<Point>();
		Random rand = new Random();
		// S.add(new Point(0, 0));
		// S.add(new Point(1, 1));
		// S.add(new Point(2, 2));
		// S.add(new Point(0, 4));
		// S.add(new Point(4, 0));
		// S.add(new Point(4, 4));

		
		S.add(new Point(12, 32));
		S.add(new Point(45, 98));
		S.add(new Point(65, 12));
		S.add(new Point(10, 30));
		S.add(new Point(0, 80));
		S.add(new Point(2, -20));
		S.add(new Point(3, -30));
		S.add(new Point(0, 0));
		S.add(new Point(7, -45));
		S.add(new Point(0, 0));
		S.add(new Point(7, -10));
		
		
		/*
		for( int i = 0; i < 1000; i++) {
	        int x = rand.nextInt(100);
			int y = rand.nextInt(100)-50;
	        	S.add( new Point( x, y ) );
		}
		*/
		
		
		
		QuickHull QH = new QuickHull( S );
		System.out.println( "Convex hull: " + QH.getConvexHull() );
    }
}
