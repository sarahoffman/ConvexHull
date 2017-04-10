// Sara Hoffman, Hannah Bossi, and Riley Karp
// CS375 - Algorithms
// 04-10-17


// import statements
import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;
import java.lang.Math;

// class for ConvexHull algorithm
public class ConvexHull {

	// variable declarations
    private ArrayList<Point> input;
    protected ArrayList<Point> output;
    private int n;
	
	// constructor
    public ConvexHull( ArrayList<Point> S ){
		//creates a QuickHull object from the given ArrayList of points
		this.input = S;
		this.output = new ArrayList<Point>();
		System.out.println( "input: " + this.input );
    }
	
    // performs the max/min calculation
    public Point getMin( ArrayList<Point> a ) {
		Point[] output = new Point[1];
		Point min = a.get(0);
		for( int i = 0; i < n; i++ ) {
			Point p = a.get(i);
			if( p.getX() < min.getX() ) {
				min = p;
			}
		}
		return min;
    }

    // checks to see if segment is part of hull
    public boolean isExtreme(Segment s, Point p2) {
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

		// if there are no points on one side
		if (left.size() == 0 || right.size() == 0) {
    		System.out.println("Adding p2 to output: "+p2);
			this.output.add(p2);
			this.input.remove(p2);
			return true;
		} else {
			this.input.remove(p2);
			return false;
		}
    }

    // Function that actually finds the convex hull
    public ArrayList<Point> getConvexHull() {
    	Point min = this.getMin(input);
    	System.out.println("Min: "+min);
		this.input.remove(min);
		this.output.add(min);
		while (this.input.size() > 0) {
			int i = 0;
			int index = 0;
			while (i < 1 && index < this.input.size()) {
				Point p2 = this.input.get(index);
    			System.out.println("Comparing min and P2: "+min+", "+p2);
				Segment s = new Segment( min, p2 );
				boolean found = this.isExtreme(s, p2);
				if (found == true) {
					i = i + 1;
					min = p2;
    				System.out.println("New min: "+min);
				}	
				index = index + 1;
			}
		}
		return this.output;
    }

	// main method
    public static void main( String args[] ) {
		ArrayList<Point> S = new ArrayList<Point>();

		S.add(new Point(0, 0));
		S.add(new Point(0, 10));
		S.add(new Point(0, 5));
		S.add(new Point(5, 10));
		S.add(new Point(5, 5));
		S.add(new Point(10, 0));
		S.add(new Point(2, 2));
		
		long startTime = System.nanoTime();
		// create QuickHull object and find convex hull
		ConvexHull CH = new ConvexHull( S );
		System.out.println( "Convex hull: " + CH.getConvexHull() );

		long endTime = System.nanoTime();
		long duration = (endTime -startTime)/1000000;
		System.out.println("Total execution time: " + (duration) );
    }
}

// end of ConvexHull.java
