// Sara Hoffman, Hannah Bossi, and Riley Karp
// CS375 - Algorithms
// 04-12-17


// import statements
import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;
import java.lang.Math;
import java.util.stream.Stream;
import java.util.stream.Collectors; 
import java.lang.Double; 


// create a class to compute the convex hull using streams
public class StreamHull {

	// variable declarations
    private ArrayList<StreamPoint> input;
    private int n;
    private ArrayList<StreamPoint> output;
	
	// constructor
    public StreamHull( ArrayList<StreamPoint> S ){
		//creates a QuickHull object from the given ArrayList of points
		this.input = S;
		this.output = new ArrayList<StreamPoint>();
		this.n = S.size();
		System.out.println(this.input); 
	
    }
 
	
    // performs the max/min calculation
    public StreamPoint[] getExtremes( ArrayList<StreamPoint> a ) {
		StreamPoint[] output = new StreamPoint[2];

		// find max and min
		StreamPoint min = a.stream().parallel().min((p1,p2)->p1.compare(p1,p2)).get(); 
		StreamPoint max = a.stream().parallel().max((p1,p2)->p1.compare(p1,p2)).get();
		
		// add min and max to output
		output[0] = min;
		output[1] = max;
		
		// return output
		return output;
    }
	
	
    // Function that actually finds the convex hull
    public ArrayList<StreamPoint> getConvexHull() {
    	// handle the case where the input contains no points
		if( n > 0 ) {
			StreamPoint[] result = this.getExtremes(input);
			StreamPoint min = result[0];
			StreamPoint max = result[1];

			// create new segment from min to max
			Segment s = new Segment( min,max );
		
			// extremes in x will be a part of the convex hull
			this.output.add( min );
			this.input.remove(min);
			if( min != max ) {
			    this.output.add( max );
			    this.input.remove(max);
			} 

			// use streams to get all points to the left of the line
			ArrayList<StreamPoint> streamleft = this.input.stream().parallel()
			    .filter(p -> s.isLeft(p)).collect(Collectors.toCollection(ArrayList::new));
			ArrayList<StreamPoint> streamright = this.input.stream().parallel()
			    .filter(p -> !s.isLeft(p)).collect(Collectors.toCollection(ArrayList::new));
		       
			// perform the recursive algorithm on the two points
			this.subHull(streamleft,s);
			this.subHull(streamright,s); 
		}

		// return output
		return this.output;
    }


    // recursive algorithm to calculate the convex hull
    public void subHull(ArrayList<StreamPoint> a, Segment s ) {
		// base case for recursion
		if (a.size() < 1){
			return; 
		}
		
	
		//find the point with the max perpendicular distance
		StreamPoint maxPoint = a.stream().parallel()
		    .max((p1,p2)->java.lang.Double.compare(this.distance(s,p1),this.distance(s,p2))).get();
		
		System.out.println("max is: " + maxPoint);

		// add the max point to the convex hull
		if (!this.output.contains(maxPoint)) {
		    this.output.add(maxPoint);
		}
		a.remove(maxPoint);

		//create a segment between the maxPoint and the endpoints of the given segment
		Segment min2dist = new Segment(s.getP1(), maxPoint);
		Segment max2dist = new Segment(s.getP2(), maxPoint);

		ArrayList<StreamPoint> left  = new ArrayList<StreamPoint>();
		ArrayList<StreamPoint> right = new ArrayList<StreamPoint>(); 

		// is maxPoint is on the bottom half of the line
		if (s.getP1().getY()>= maxPoint.getY() || s.getP2().getY() >= maxPoint.getY()){
		    
			
		    for(int i = 0; i< a.size(); i++){
				StreamPoint p = a.get(i);
				if (max2dist.isLeft(p) == true){
				    left.add(p);
				} 
				else if (min2dist.isLeft(p) == false) {
				    right.add(p);
				}
		    }
		    
		}
		
		// if the maxPoint is on the top half of the line
		else{
	      
		    for(int i = 0; i< a.size(); i++){
				StreamPoint p = a.get(i);
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
	

	// computes the distance between a point and a line 
    public double distance( Segment s, StreamPoint p ) {
		double[] a = s.getCo();
		double dist = Math.abs( a[0]*p.getX() + a[1]*p.getY() + a[2] )/Math.sqrt( a[0]*a[0] + a[1]*a[1] );
		return dist;
    }
	
	// ***************************************** MAIN CODE ************************************************
    public static void main( String args[] ) {
    	// create strem for points
		ArrayList<StreamPoint> S = new ArrayList<StreamPoint>();

		// add points to array
		S.add(new StreamPoint(12, 32));
		S.add(new StreamPoint(-45, 98));
		S.add(new StreamPoint(65, 12));
		S.add(new StreamPoint(10, 30));
		S.add(new StreamPoint(0, 80));
		S.add(new StreamPoint(1, -40));
		S.add(new StreamPoint(-3, -10));
		S.add(new StreamPoint(0, 0));
		S.add(new StreamPoint(7, -45));
		S.add(new StreamPoint(0, 0));
		S.add(new StreamPoint(7, -10));
		
		// create stream and time it
		StreamHull SH = new StreamHull( S );
		long startTime = System.nanoTime();
		ArrayList<StreamPoint> output = SH.getConvexHull(); 
	    long endTime = System.nanoTime();
		System.out.println( "Convex hull: " + output );
	    // duration time in milliseconds
	    long duration = (endTime - startTime)/1000000;
		System.out.println("Duration: " + duration); 
    }
}


// Class stream point
// needed to implement a comparator. 
class StreamPoint extends Point{

	// constructor for the stream point class
	// takes in x and y coordinates
    public StreamPoint(int x,int y){
		super(x,y); 
	
    }
	// compare two stream points based on their x values
    public int compare(StreamPoint o1, StreamPoint o2) {
		return java.lang.Double.compare(o1.getX(), o2.getX());
    }
}


// end of StreamHull.java
