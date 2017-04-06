import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;
import java.lang.Math;

public class QuickHull {

    private ArrayList<Point> input;
    private int n;
    private ArrayList<Point> output;
	
    public QuickHull( ArrayList<Point> S ){
	//creates a QuickHull object from the given ArrayList of points
	input = S;
	output = new ArrayList<Point>();
	n = S.size();
	System.out.println( this.input );
    }
	
    // performs the max/min calculation
    // to be used later in recursion
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
		
	Point[] result = this.getExtremes(input);
	Point min = result[0];
	Point max = result[1];

	//System.out.println( "min: " + String.valueOf(min) + "	" + 
	//"max: " + String.valueOf(max) );
		
	Segment s = new Segment( min,max );
		
	// extremes in x will be a part of the convex hull
	this.output.add( min );
	this.output.add( max );

	ArrayList<Point> left = new ArrayList<Point>();
	ArrayList<Point> right = new ArrayList<Point>(); 

	// get a list of all of the points to the left of the line
	for(int i = 0; i< this.n; i++){
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
		

	return this.output;
    }

    // write the recursive algorithm.
    public void subHull(ArrayList<Point> a, Segment s ) {

	// base case for recursion
	if (a.size() == 1){
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
	this.output.add(maxPoint); 

	//create a segment between that point and the max and min of the segment
	Segment min2dist = new Segment(s.getP1(), maxPoint);
	Segment max2dist = new Segment(s.getP2(), maxPoint);


	ArrayList<Point> left = new ArrayList<Point>();

	// get a list of all of the points to the left of the line
	for(int i = 0; i< this.n; i++){
	    Point p = this.input.get(i);
	    if (s.isLeft(p) == true){
		left.add(p);
	    }
       
	}

	// call algorithm recursively
	subHull(left, min2dist);
	subHull(left, max2dist); 

	
    }
	
    public double distance( Segment s, Point p ) {
	double[] a = s.getCo();
	double dist = Math.abs( a[0]*p.getX() + a[1]*p.getY() + a[2] )/Math.sqrt( a[0]*a[0] + a[1]*a[1] );
	return dist;
    }
	
    public static void main( String args[] ) {
	ArrayList<Point> S = new ArrayList<Point>();
	Random rand = new Random();
	for( int i = 0; i < 10; i++) {
	    int x = rand.nextInt(100);
	    int y = rand.nextInt(100);
	    S.add( new Point( x, y ) );
	}
		
	QuickHull QH = new QuickHull( S );
	System.out.println(QH.getConvexHull());
    }
}
