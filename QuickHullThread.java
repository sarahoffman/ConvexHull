// Sara Hoffman, Hannah Bossi, and Riley Karp
// 04-10-17
// CS375 - Algorithms
// Convex Hull 
// Threading version


import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;
import java.lang.Math;


// QuickHullThread class that extends thread
public class QuickHullThread extends Thread {

    private ArrayList<Point> input;
    private ArrayList<Point> output;
    private ArrayList<HullThread> threads;
    private int n;

    public static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
	
	// constructor
    public QuickHullThread( ArrayList<Point> S ){
		//creates a QuickHull object from the given ArrayList of points
		this.input = S;
		this.output = new ArrayList<Point>();
		this.n = S.size();
		this.threads = new ArrayList<HullThread>();
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
		System.out.println( "min: " + String.valueOf(min) + "	" + 
			"max: " + String.valueOf(max) );

		return output;
    }
	
	
    // Function that actually finds the convex hull
    public ArrayList<Point> getConvexHull() {
    	System.out.println("Num threads:  " + this.NUM_THREADS);
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

			// create arraylist for left and right points
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

			if (left.size() > 0) {
				HullThread lthread = new HullThread(left, s);
				this.threads.add(lthread);
				System.out.println("Starting left thread in GCH");
				lthread.start();
			} 

			if (right.size() > 0) {
				HullThread rthread = new HullThread(right, s);
				this.threads.add(rthread);
				System.out.println("Starting right thread in GCH");
				rthread.start();
			}

			// perform the recursive algorithm on the two points
			// this.subHull(left,s);
			// this.subHull(right,s); 

			//wait for all threads to finish
	        for (HullThread t : this.threads) {
	            try {
	                t.join();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }

		// return output
		return this.output;
    }

    // write the recursive algorithm.
    public void subHull(ArrayList<Point> a, Segment s ) {
    	System.out.println("In subhull; points: " + a);
		// base case for recursion
		if (a.size() < 1) {
			return; 
		}
	
		//loop through the arrayList to find the point with the max distance
		double max = Double.MIN_VALUE;
		Point maxPoint = a.get(0);
	
		//find the point with the max perpendicular distance
		for(int i = 0; i< a.size(); i++) {
			double dist = this.distance(s, a.get(i));
			if(dist>max){
				max = dist; 
				maxPoint = a.get(i); 
			}
		}

		// print point with max distance
		System.out.println( "max: " + String.valueOf(maxPoint) );

		// add the max point to the convex hull
		if (!this.output.contains(maxPoint)) {
			this.output.add(maxPoint);
		}
		// remove point from set
		a.remove(maxPoint);

		//create a segment between the maxPoint and the endpoints of the given segment
		Segment min2dist = new Segment(s.getP1(), maxPoint);
		Segment max2dist = new Segment(s.getP2(), maxPoint);

		// create arraylist for left and right points
		ArrayList<Point> left = new ArrayList<Point>();
		ArrayList<Point> right = new ArrayList<Point>();

		// get a list of all of the points to the left of the line
		for(int i = 0; i< a.size(); i++) {
			Point p = a.get(i);
			if (min2dist.isLeft(p) == true) {
				left.add(p);
			} else if (max2dist.isLeft(p) == false) {
				right.add(p);
			}
		}

		System.out.println("Thread size: " + this.threads.size());
		if (this.threads.size() <= (this.NUM_THREADS - 2)) {
			System.out.println("Inside threads loop");
			ArrayList<HullThread> subThreads = new ArrayList<HullThread>();

			if (left.size() > 0) {
				HullThread lthread = new HullThread(left, min2dist);
				this.threads.add(lthread);
				subThreads.add(lthread);
				System.out.println("Starting left thread in SH");
				lthread.start();
			}

			if (right.size() > 0) {
				HullThread rthread = new HullThread(right, max2dist);
				this.threads.add(rthread);
				subThreads.add(rthread);
				System.out.println("Starting right thread in SH");
				rthread.start();
			}

			//wait for all threads to finish
	        for (HullThread t : subThreads) {
	            try {
	                t.join();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
		} else {
			// call algorithm recursively
			subHull(left, min2dist);
			subHull(right, max2dist); 
		}
    }
	
	// find perpendicular distance of p from s
    public double distance( Segment s, Point p ) {
		double[] a = s.getCo();
		double dist = Math.abs( a[0]*p.getX() + a[1]*p.getY() + a[2] )/Math.sqrt( a[0]*a[0] + a[1]*a[1] );
		return dist;
    }

    // class for threads
    class HullThread extends Thread {
        private ArrayList<Point> points;
        private Segment seg;

        public HullThread(ArrayList<Point> pts, Segment sment) {
            this.points = pts;
            this.seg = sment;
        }

        @Override
        public void run() {
            subHull(this.points, this.seg);
        }

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
		
		QuickHullThread QH = new QuickHullThread( S );
		System.out.println( "Convex hull: " + QH.getConvexHull() );
    }
}
