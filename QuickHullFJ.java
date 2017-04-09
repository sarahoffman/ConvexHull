import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class QuickHullFJ extends QuickHull {

	private static final int threshold = 1;
	private ArrayList<Point> input;
	private int numProc;
	private ForkJoinPool pool;
	
	public QuickHullFJ( ArrayList<Point> in ) {
		super(in);
		input = in;
		numProc = Runtime.getRuntime().availableProcessors();
		System.out.println( "Number of processors: " + numProc );
		pool = new ForkJoinPool();
	}
	
	public ArrayList<Point> findConvHull() {
		if( input.size() > 0 ) {
			Point[] r = getExtremes(input);
			Point min = r[0];
			Point max = r[1];
			Segment s = new Segment( min,max );
			output.add( min );
			input.remove(min);
			if( min != max ) {
				output.add( max );
				input.remove(max);
			} 
			
			ArrayList<Point> left = new ArrayList<Point>();
			ArrayList<Point> right = new ArrayList<Point>(); 
			// get a list of all of the points to the left of the line
			for(int i = 0; i< input.size(); i++){
				Point p = input.get(i);
				if (s.isLeft(p) == true){
					left.add(p);
				}
				else{
					right.add(p); 
				}
			}	
			pool.invoke(new QuickHullAction(left, s, output));
			pool.invoke(new QuickHullAction(right, s, output));
			System.out.println( "ASYNC: " + pool.getAsyncMode() );
		}
        return output;
    }
    
    class QuickHullAction extends RecursiveAction {
        private Segment seg;
        private ArrayList<Point> output;
        private ArrayList<Point> in;

        public QuickHullAction( ArrayList<Point> a, Segment s, ArrayList<Point> r) {
            in = a;
            seg = s;
            output = r;
            System.out.println( "\nSegment endpoints: " + seg.getP1() + " ' " + seg.getP2() );
            System.out.println( "Contents of in: " + in );
        }

        @Override
        protected void compute() {
            if ( pool.getPoolSize() >= numProc || in.size() < threshold ) { // do it sequentially myself
            	subHull( in , seg );
            }
//             else if ( pool.getStealCount() == getSurplusQueuedTaskCount() && 
//             		getSurplusQueuedTaskCount() > 0 ) {
//             		//getSurplusQueuedTaskCount() == 0 also pool.getStealCount() > 1
//             	System.out.println( "\nSURPLUS TASK COUNT: " + getSurplusQueuedTaskCount() + "\n");
//             	pool.shutdown();
//             }
            else { // fork the work into two tasks for other threads
				//loop through the arrayList to find the point with the max distance
				double max = Double.MIN_VALUE;
				Point maxPoint = in.get(0);
				//find the point with the max perpendicular distance
				for(int i = 0; i< in.size(); i++){
					double dist = distance(seg, in.get(i));
					if(dist>max){
						max = dist; 
						maxPoint = in.get(i); 
					}
				}
				
				System.out.println( "maxPoint: " + maxPoint );

				// add the max point to the convex hull
				if (!output.contains(maxPoint)) {
					output.add(maxPoint);
				}
				in.remove(maxPoint);

				//create a segment between the maxPoint and the endpoints of the given segment
				Segment seg1 = new Segment(seg.getP1(), maxPoint);
				Segment seg2 = new Segment(seg.getP2(), maxPoint);

				ArrayList<Point> left = new ArrayList<Point>();
				ArrayList<Point> right = new ArrayList<Point>();

				// get a list of all of the points to the left of the line
				// we need to handle the case of the max point being negative 
				// and the max point being positive differently
				// this is due to our isLeft method
		
				// is maxPoint is negative
				if (0 >= maxPoint.getY() || seg.getP2().getY() >= maxPoint.getY()){
					for(int i = 0; i< in.size(); i++){
						Point p = in.get(i);
						if (seg2.isLeft(p) == true){
							left.add(p);
						} 
						else if (seg1.isLeft(p) == false) {
							right.add(p);
						}
					}
				}
		
				// if the maxPoint is positive
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
            
                QuickHullAction l = new QuickHullAction(left, seg1, output);
                QuickHullAction r = new QuickHullAction(right, seg2, output);
//                 invokeAll(l, r);
                l.fork();
                r.fork();
            }
        }
    }
    
    public static void main( String args[] )  {
    	Random rand = new Random();
    	ArrayList<Point> in = new ArrayList<Point>();
//     	for( int i = 0; i < 10; i++) {
// 	        int x = rand.nextInt(100);
// 			int y = rand.nextInt(100)-50;
// 	        	in.add( new Point( x, y ) );
// 		}

// 		in.add( new Point( 15,-3 ) );
// 		in.add( new Point( 43, -14 ) );
// 		in.add( new Point( 1, 29 ) );
// 		in.add( new Point( 79, 4 ) );
// 		in.add( new Point( 67, -20 ) );
// 		in.add( new Point( 20, -23 ) );
// 		in.add( new Point( 7, -27 ) );
// 		in.add( new Point( 49, 47 ) );
// 		in.add( new Point( 24, 23 ) );
// 		in.add( new Point( 47, 7 ) );

		in.add( new Point( 43,46 ) );
		in.add( new Point( 56,39 ) );
		in.add( new Point( 48,-38 ) );
		in.add( new Point( 93,-20 ) );
		in.add( new Point( 25,0 ) );
		in.add( new Point( 84, -24 ) );
		in.add( new Point( 20,20 ) );
		in.add( new Point( 60,-32 ) );
		in.add( new Point( 66,28 ) );
		in.add( new Point( 85,-34 ) );

// 		in.add( new Point( 0, 4 ) );
// 		in.add( new Point( 0, 0 ) );
// 		in.add( new Point( 4, 0 ) );
// 		in.add( new Point( 4,4 ) );
// 		in.add( new Point( 2, 2 ) );
		
		System.out.println( "in: " + in );
		QuickHullFJ qhfj = new QuickHullFJ( in );
		ArrayList<Point> output = qhfj.findConvHull();
		System.out.println( "Output: " + output );
    }
}