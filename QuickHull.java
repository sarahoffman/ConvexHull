// Sara Hoffman, Hannah Bossi, and Riley Karp
// CS375 - Algorithms
// 04-10-17


// import statements
import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;
import java.lang.Math;

// class for QuickHull algorithm
public class QuickHull {

	// variable declarations
    private ArrayList<Point> input;
    protected ArrayList<Point> output;
    private int n;
	
	// constructor
    public QuickHull( ArrayList<Point> S ){
		//creates a QuickHull object from the given ArrayList of points
		input = S;
		output = new ArrayList<Point>();
		n = S.size();
		//System.out.println( "input: " + this.input );
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
		// System.out.println( "min: " + String.valueOf(min) + "	" + 
		// 	"max: " + String.valueOf(max) );

		return output;
    }
	
	
    // Function that actually finds the convex hull
    public ArrayList<Point> getConvexHull() {
		if( n > 0 ) {
			Point[] result = this.getExtremes(input);
			Point min = result[0];
			Point max = result[1];

			// System.out.println( "min: " + String.valueOf(min) + "	" + 
			// "max: " + String.valueOf(max) );
		
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

		//System.out.println( "max: " + String.valueOf(maxPoint) );

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
		
		// is maxPoint is negative
		if (s.getP1().getY() >= maxPoint.getY() || s.getP2().getY() >= maxPoint.getY()) {
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
		
		// if the maxPoint is positive
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
	
	// find the perpendicular distance of a point from the segment
    public double distance( Segment s, Point p ) {
		double[] a = s.getCo();
		double dist = Math.abs( a[0]*p.getX() + a[1]*p.getY() + a[2] )/Math.sqrt( a[0]*a[0] + a[1]*a[1] );
		return dist;
    }
	
    public static void main( String args[] ) {
		ArrayList<Point> S = new ArrayList<Point>();
		Random rand = new Random();
		
		// create random set of 10 points
		for( int i = 0; i < 750000; i++) {
	        int x = rand.nextInt(100);
			int y = rand.nextInt(100)-50;
	        	S.add( new Point( x, y ) );
		}
		
		long startTime = System.nanoTime();
		// create QuickHull object and find convex hull
		QuickHull QH = new QuickHull( S );
		QH.getConvexHull();
		//System.out.println( "Convex hull: " + QH.getConvexHull() );

		long endTime = System.nanoTime();
		long duration = (endTime -startTime)/1000000;
		System.out.println("Total execution time: " + (duration) );
    }
}
