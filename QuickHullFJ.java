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
// 		System.out.println( "Number of processors: " + numProc );
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
        }

        @Override
        protected void compute() {
            if ( in.size() < threshold ) { // do it sequentially myself
            	subHull( in , seg );
            }
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
                invokeAll(l, r);
            }
        }
    }
    
    public static void main( String args[] )  {
    	Random rand = new Random();
    	ArrayList<Point> in = new ArrayList<Point>();
    	for( int i = 0; i < 75000; i++) {
	        int x = rand.nextInt(100);
			int y = rand.nextInt(100)-50;
	        	in.add( new Point( x, y ) );
		}
		
// 		System.out.println( "input: " + in );
		
		QuickHullFJ qhfj = new QuickHullFJ( in );
		long startTime = System.nanoTime();
		ArrayList<Point> output = qhfj.findConvHull();
		long endTime = System.nanoTime();
// 		System.out.println( "\nOutput: " + output + "Size: " + output.size() );
		System.out.println( "\nTime: " + (endTime - startTime) / 1000000 );
    }
}