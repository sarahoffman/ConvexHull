import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

//Class that implements QuickHull using a Fork/Join Pool Framework
public class QuickHullFJ extends QuickHull {

	private static final int threshold = 1;
	private ArrayList<Point> input; //input ArrayList of points
	private int numProc;
	
	//creates a QuickHullFJ objects with the given ArrayList of Points
	public QuickHullFJ( ArrayList<Point> in ) {
		super(in);
		input = in;
// 		numProc = Runtime.getRuntime().availableProcessors();
// 		System.out.println( "Number of processors: " + numProc );
	}
	
	//creates a ForkJoinPool, and uses two threads to invoke a recursive QuickHullAction
	//on points to the left and right of the calculated line segment
	public ArrayList<Point> findConvHull() {
		ForkJoinPool pool = new ForkJoinPool();
		
		if( input.size() > 0 ) {
			//find Points with the minimum and maximum x-values
			Point[] r = getExtremes(input);
			Point min = r[0];
			Point max = r[1];
			
			//create a Segment between the Points with the minimum and maximum x-values
			Segment s = new Segment( min,max );
			
			//add the endpoints of the new Segment to the convex hull, and remove them
			//from the input ArrayList
			output.add( min );
			input.remove(min);
			if( min != max ) {
				output.add( max );
				input.remove(max);
			} 
			
			//create ArrayLists of points to the left and to the right of the Segment
			ArrayList<Point> left = new ArrayList<Point>();
			ArrayList<Point> right = new ArrayList<Point>(); 
			for(int i = 0; i< input.size(); i++){
				Point p = input.get(i);
				if (s.isLeft(p) == true){
					left.add(p);
				}
				else{
					right.add(p); 
				}
			}	
			
			//invoke a QuickHullAction on the lists of Points to the left and right of
			//the calculated Segment
			pool.invoke(new QuickHullAction(left, s, output));
			pool.invoke(new QuickHullAction(right, s, output));
		}
		//return the ArrayList of the Points in the Convex Hull
        return output;
    }
    
    //Class that computes the convex hull of the initial ArrayList of Points by recursively
    //finding the Points in the input list to the left and right of the given Segment,
    //and eventually calling the sequential QuickHull algorithm when the input list is
    //small enough.
    class QuickHullAction extends RecursiveAction {
        private Segment seg;
        private ArrayList<Point> output;
        private ArrayList<Point> in;

		//creates a QuickHullAction object with the given input and output ArrayLists of
		//Points and the given line Segment.
        public QuickHullAction( ArrayList<Point> a, Segment s, ArrayList<Point> r) {
            in = a;
            seg = s;
            output = r;
        }

        @Override
        //recursively computes the points to the left and right of the given Segment,
        //until the input list is small enough, so it calls the sequential version
        //of QuickHull to finish the job.
        protected void compute() {
            if ( in.size() < threshold ) { // do it sequentially myself
            	subHull( in , seg );
            }
            else { // fork the work into two tasks for other threads
				//loop through the input ArrayList to find the point with the maximum 
				//perpendicular distance from the given line Segment
				double max = Double.MIN_VALUE;
				Point maxPoint = in.get(0);
				for(int i = 0; i< in.size(); i++){
					double dist = distance(seg, in.get(i));
					if(dist>max){
						max = dist; 
						maxPoint = in.get(i); 
					}
				}
				
				//add the maxPoint to the convex hull if it's not already in the convex hull
				if (!output.contains(maxPoint)) {
					output.add(maxPoint);
				}
				//remove the maxPoint from the input ArrayList
				in.remove(maxPoint);

				//create a segment between the maxPoint and the endpoints of the given segment
				Segment seg1 = new Segment(seg.getP1(), maxPoint);
				Segment seg2 = new Segment(seg.getP2(), maxPoint);

				ArrayList<Point> left = new ArrayList<Point>();
				ArrayList<Point> right = new ArrayList<Point>();

				//get a list of all of the points to the left and right of the given line
				//Segment. We need to handle the cases of when the y-coordinate of the
				//maxPoint is negative and when it is positive, differently because of
				//how we implemented our isLeft() method in the Segment class
		
				//if y-coordinate of maxPoint is negative (below the given Segment)
				if (seg.getP2().getY() >= maxPoint.getY()){
					for(int i = 0; i< in.size(); i++){
						Point p = in.get(i);
						if (seg2.isLeft(p) == true){
							right.add(p);
						} 
						else if (seg1.isLeft(p) == false) {
							left.add(p);
						}
					}
				}
		
				//if the maxPoint is positive (above the given Segment)
				else{
					for(int i = 0; i< in.size(); i++){
						Point p = in.get(i);
						if (seg1.isLeft(p) == true){
							left.add(p);
						} 
						else if (seg2.isLeft(p) == false) {
							right.add(p);
						}
					}
				}
            
            	//recursively create and invoke new QuickHullActions with the newly
            	//found segments and ArrayLists of Points to the left and right of the
            	//given line Segment
                QuickHullAction l = new QuickHullAction(left, seg1, output);
                QuickHullAction r = new QuickHullAction(right, seg2, output);
                invokeAll(l, r);
            }
        }
    }
    
    //creates an ArrayList of Points and finds the convex hull of the Points, using the
    //QuickHullFJ class. Prints the time the algorithm took to calculate the convex hull
    public static void main( String args[] )  {
    	//create an ArrayList of random Points
    	Random rand = new Random();
    	ArrayList<Point> in = new ArrayList<Point>();
    	for( int i = 0; i < 75000; i++) {
	        int x = rand.nextInt(100);
			int y = rand.nextInt(100)-50;
	        	in.add( new Point( x, y ) );
		}
		
// 		System.out.println( "input: " + in );
		
		//create a QuickHullFJ object to find the convex hull
		QuickHullFJ qhfj = new QuickHullFJ( in );
		long startTime = System.nanoTime();
		ArrayList<Point> output = qhfj.findConvHull();
		long endTime = System.nanoTime();
// 		System.out.println( "\nOutput: " + output + " Size: " + output.size() );
		System.out.println( "\nTime: " + (endTime - startTime) / 1000000 );
    }
}