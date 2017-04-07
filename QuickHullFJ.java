import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.awt.Point;
import java.util.ArrayList;

public class QuickHullFJ extends QuickHull {
	
	public ArrayList<Point> findConvHull() {
		ArrayList<Point> results = new ArrayList<Point>();
		ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new QuickHullAction(0, upto, results));
        return results;
    }
    
    class QuickHullAction extends RecursiveAction {
        private static final int THRESHOLD = 2;
        private Point pt;
        private ArrayList<Point> results;
        private ArrayList<Point> input;

        public QuickHullAction( ArrayList<Point> a, Point p, ArrayList<Point> r) {
            input = a;
            pt = p;
            results = r;
        }

        @Override
        protected void compute() {
            if (input.size() <= THRESHOLD) // do it sequentially myself
            	results[x] = subHull( input, pt);
            }
            else { // fork the work into two tasks for other threads
                QuickHullAction left = new QuickHullAction(input, pt, results);
                QuickHullAction right = new QuickHullAction(input, pt, results);
                invokeAll(left, right);
            }
        }
    }
}