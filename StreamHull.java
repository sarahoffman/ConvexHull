import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;
import java.lang.Math;
import java.util.stream.Stream;
import java.lang.Double; 

public class StreamHull {

    private ArrayList<StreamPoint> input;
    private int n;
    private ArrayList<StreamPoint> output;
	
    public StreamHull( ArrayList<StreamPoint> S ){
		//creates a QuickHull object from the given ArrayList of points
		input = S;
		output = new ArrayList<StreamPoint>();
		n = S.size();
		System.out.println( "input: " + this.input );
    }

 
	
    // performs the max/min calculation
    public StreamPoint[] getExtremes( ArrayList<StreamPoint> a ) {
		StreamPoint[] output = new StreamPoint[2];
		StreamPoint max = a.stream().max((p1,p2)->p1.compare(p1,p2)).get();
		StreamPoint min = a.stream().min((p1,p2)->p1.compare(p1,p2)).get(); 
		
		output[0] = min;
		output[1] = max;
		//System.out.println( "min: " + String.valueOf(min) + "	" + 
		//"max: " + String.valueOf(max) );

		return output;
    }
	
	
    // Function that actually finds the convex hull
    public ArrayList<StreamPoint> getConvexHull() {
		if( n > 0 ) {
			StreamPoint[] result = this.getExtremes(input);
			StreamPoint min = result[0];
			StreamPoint max = result[1];

// 			System.out.println( "min: " + String.valueOf(min) + "	" + 
// 			"max: " + String.valueOf(max) );
		
			Segment s = new Segment( min,max );
		
			// extremes in x will be a part of the convex hull
			this.output.add( min );
			this.input.remove(min);
			if( min != max ) {
				this.output.add( max );
				this.input.remove(max);
			} 

			ArrayList<StreamPoint> left = new ArrayList<StreamPoint>();
			ArrayList<StreamPoint> right = new ArrayList<StreamPoint>(); 

			// get a list of all of the points to the left of the line
			for(int i = 0; i< this.input.size(); i++){
				StreamPoint p = this.input.get(i);
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
    public void subHull(ArrayList<StreamPoint> a, Segment s ) {
		// base case for recursion
		if (a.size() < 1){
			return; 
		}
		// System.out.println("Recursing with size: " + a.size()); 
		//System.out.println( "Contents of a: " + a );
		// System.out.println("Input size: " + this.input.size()); 
	
		//loop through the arrayList to find the point with the max distance
		double max = Double.MIN_VALUE;
		StreamPoint maxPoint = a.get(0);
	
		//find the point with the max perpendicular distance
		for(int i = 0; i< a.size(); i++){
			double dist = this.distance(s, a.get(i));
			if(dist>max){
				max = dist; 
				maxPoint = a.get(i); 
			}
		}

		System.out.println( "max: " + String.valueOf(maxPoint) );

		// add the max point to the convex hull
		if (!this.output.contains(maxPoint)) {
			this.output.add(maxPoint);
		}
		a.remove(maxPoint);

		//create a segment between the maxPoint and the endpoints of the given segment
		Segment min2dist = new Segment(s.getP1(), maxPoint);
		Segment max2dist = new Segment(s.getP2(), maxPoint);

		ArrayList<StreamPoint> left = new ArrayList<StreamPoint>();
		ArrayList<StreamPoint> right = new ArrayList<StreamPoint>();

		// get a list of all of the points to the left of the line
		for(int i = 0; i< a.size(); i++){
			StreamPoint p = a.get(i);
			if (min2dist.isLeft(p) == true){
				left.add(p);
			} else if (max2dist.isLeft(p) == false) {
				right.add(p);
			}
		}

		// call algorithm recursively
		subHull(left, min2dist);
		subHull(right, max2dist); 
    }
	
    public double distance( Segment s, StreamPoint p ) {
		double[] a = s.getCo();
		double dist = Math.abs( a[0]*p.getX() + a[1]*p.getY() + a[2] )/Math.sqrt( a[0]*a[0] + a[1]*a[1] );
		return dist;
    }
	
    public static void main( String args[] ) {
		ArrayList<StreamPoint> S = new ArrayList<StreamPoint>();
		Random rand = new Random();



		// Stream Hull is working provided that 
		/*
		S.add(new StreamPoint(0, 0));
		S.add(new StreamPoint(1, 1));
		S.add(new StreamPoint(2, 2));
		S.add(new StreamPoint(0, 4));
		S.add(new StreamPoint(4, 0));
		S.add(new StreamPoint(4, 4));
		*/

		/*
		S.add(new StreamPoint(12, 32));
		S.add(new StreamPoint(45, 98));
		S.add(new StreamPoint(65, 12));
		S.add(new StreamPoint(10, 30));
		S.add(new StreamPoint(0, 80));
		S.add(new StreamPoint(1, -40));
		S.add(new StreamPoint(3, -30));
		S.add(new StreamPoint(0, 0));
		S.add(new StreamPoint(7, -45));
		S.add(new StreamPoint(0, 0));
		S.add(new StreamPoint(7, -10));
		*/

		// for( int i = 0; i < 10; i++) {
		// 	int x = rand.nextInt(100);
		// 	int y = rand.nextInt(100);
		// 	S.add( new Point( x, y ) );
		// }
		long startTime = System.nanoTime();
		StreamHull SH = new StreamHull( S );
	    	long endTime = System.nanoTime();
		System.out.println( "Convex hull: " + SH.getConvexHull() );
	    	// duration time in milliseconds
	    	long duration = (endTime - startTime)/1000000;
    }
}


class StreamPoint extends Point{

    public StreamPoint(int x,int y){
	super(x,y); 
	
    }

    public int compare(StreamPoint o1, StreamPoint o2) {
	return java.lang.Double.compare(o1.getX(), o2.getX());
    }


}
