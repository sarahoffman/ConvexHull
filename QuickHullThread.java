// Sara Hoffman, Hannah Bossi, and Riley Karp
// 04-10-17
// CS375 - Algorithms
// Convex Hull 
// Threading version


// import statements

import java.util.ArrayList;
import java.awt.Point;
import java.lang.Math;
import java.util.Random;


/**
 * QuickHull class that drives the calculation process and contains HullThread class that implements the thread
 */

public class QuickHullThread {

    // variable delcarations
    private int threadCount = 0;
    private ArrayList<Point> originalInput;

    public static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    // method to have threads wait
    public synchronized int getThreadOrWait() {

        while (threadCount >= NUM_THREADS) {
            try {
                this.wait();
                // this waits until notify, which happens when thread count is decremented
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ;

        return ++threadCount;
    }

    // decrement thread count and notify waiting thread
    public synchronized int decrementThreads() {
        int result = --threadCount;
        this.notify(); // wake up a thread waiting to do some processing
        return result;
    }


    // constructor
    public QuickHullThread(ArrayList<Point> S) {
        //creates a QuickHull object from the given ArrayList of points
        this.threadCount = 0;
        this.originalInput = S;
        //System.out.println("input: " + S);
    }

    // getConvexHull initial call
    public ArrayList<Point> getConvexHull() {
        return new HullThread(originalInput, null).getConvexHull();
    }

    // class for threads
    class HullThread extends Thread {
        // variable to hold list of points and line seg
        private ArrayList<Point> points;
        private Segment segment;

        private ArrayList<Point> output = new ArrayList<Point>();

        // constructor
        public HullThread(ArrayList<Point> pts, Segment segment) {
            this.points = pts;
            this.segment = segment;
        }

        // performs the max/min calculation
        public Point[] getExtremes(ArrayList<Point> a) {
            Point[] extremes = new Point[2];
            Point min = a.get(0);
            Point max = a.get(0);

            for (Point p : a) {
                if (p.getX() < min.getX()) {
                    min = p;
                }
                if (p.getX() > max.getX()) {
                    max = p;
                }
            }
//            System.out.println("min: " + String.valueOf(min) + "  " +
//                    "max: " + String.valueOf(max));

            extremes[0] = (min);
            extremes[1] = (max);

            return extremes;
        }

        // Function that actually finds the convex hull
        public ArrayList<Point> getConvexHull() {
            //System.out.println("Num threads:  " + QuickHullThread.NUM_THREADS);
            if (!points.isEmpty()) {
                Point[] result = this.getExtremes(points);
                Point min = result[0];
                Point max = result[1];

                this.segment = new Segment(min, max);

                // extremes in x will be a part of the convex hull
                this.output.add(min);
                this.points.remove(min);
                if (min != max) {
                    this.output.add(max);
                    this.points.remove(max);
                }

                ArrayList<Point> left = new ArrayList<Point>();
                ArrayList<Point> right = new ArrayList<Point>();

                // get a list of all of the points to the left of the line
                for(Point p : this.points) {
                    if (this.segment.isLeft(p) == true){
                        left.add(p);
                    }
                    else{
                        right.add(p);
                    }
                }

                //System.out.println("before recursion: \n\tleft: " + left + "\n\tright: " + right);
                runThreads(left, segment, right, segment);
            }

            // return output
            return this.output;
        }

        // write the recursive algorithm.
        public void subHull() {
            // base case for recursion
            if (this.points.isEmpty()) {
                return;
            }

            //loop through the arrayList to find the point with the max distance
            double max = Double.MIN_VALUE;
            Point maxPoint = this.points.get(0);

            //find the point with the max perpendicular distance
            for (Point point : this.points) {
                double dist = this.distance(this.segment, point);
                if (dist > max) {
                    max = dist;
                    maxPoint = point;
                }
            }

            // print point with max distance
            //System.out.println("max: " + String.valueOf(maxPoint));

            // add the max point to the convex hull
            if (!this.output.contains(maxPoint)) {
                this.output.add(maxPoint);
            }

            // remove point from set
            points.remove(maxPoint);

            //create a segment between the maxPoint and the endpoints of the given segment
            Segment min2dist = new Segment(this.segment.getP1(), maxPoint);
            Segment max2dist = new Segment(this.segment.getP2(), maxPoint);

            // create arraylist for left and right points
            ArrayList<Point> left = new ArrayList<Point>();
            ArrayList<Point> right = new ArrayList<Point>();

            // is maxPoint is negative
//            if (0 >= maxPoint.getY()) {
            if (segment.getP1().getY() >= maxPoint.getY() || segment.getP2().getY() >= maxPoint.getY())  {
                for (Point p : this.points) {
                    if (max2dist.isLeft(p) == true) {
                        left.add(p);
                    } else if (min2dist.isLeft(p) == false) {
                        right.add(p);
                    }
                }
            }

            // if the maxPoint is positive
            else {

                for (Point p : this.points) {
                    if (min2dist.isLeft(p) == true) {
                        left.add(p);
                    } else if (max2dist.isLeft(p) == false) {
                        right.add(p);
                    }
                }

            }

            // System.out.println("recurring: \n\tleft: " + left
            //         + "\n\tleftSegment: " + min2dist
            //         + "\n\tright: " + right
            //         + "\n\tright segment: " + max2dist);


            runThreads(left, min2dist, right, max2dist);
        }

        // runs all threads
        private void runThreads(ArrayList<Point> left, Segment leftSegment, ArrayList<Point> right, Segment rightSegment) {
            ArrayList<HullThread> myThreads = new ArrayList<HullThread>();
            if (left.size() > 0) {
                myThreads.add(runThread(left, leftSegment));
//                System.out.println("Starting left thread in GCH");
            }

            if (right.size() > 0) {
                myThreads.add(runThread(right, rightSegment));
//                System.out.println("Starting right thread in GCH");
            }

            //wait for all threads to finish
            for (HullThread t : myThreads) {
                try {
                    t.join();
                    this.output.addAll(t.output);  // gather the output from that thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // find perpendicular distance of p from s
        public double distance(Segment s, Point p) {
            double[] a = s.getCo();
            double dist = Math.abs(a[0] * p.getX() + a[1] * p.getY() + a[2]) / Math.sqrt(a[0] * a[0] + a[1] * a[1]);
            return dist;
        }

        // run individual thread
        public HullThread runThread(ArrayList<Point> pts, Segment segment) {
            HullThread rthread = new HullThread(pts, segment);
            getThreadOrWait();      // wait until threadcount is below NUM_THREADS
//            System.out.println("Starting thread in GCH");
            rthread.start();
            return rthread;
        }

        // passes points and segments to subhull
        @Override
        public void run() {
            subHull();
            decrementThreads();
        }

    }

    // main method
    public static void main(String args[]) {
        // create ArrayList of points
        ArrayList<Point> S = new ArrayList<Point>();
        Random rand = new Random();


        // S.add(new Point(0, 0));
        // S.add(new Point(0, 10));
        // S.add(new Point(0, 5));
        // S.add(new Point(5, 10));
        // S.add(new Point(5, 5));
        // S.add(new Point(10, 0));
        // S.add(new Point(2, 2));
        // S.add(new Point(5, -5));
        // S.add(new Point(4, -1));

        for( int i = 0; i < 24; i++) {
            int x = rand.nextInt(100);
            int y = rand.nextInt(100);
            S.add( new Point( x, y ) );
         }

        // create QuickHull object and find convex hull
        long startTime = System.nanoTime();

        QuickHullThread QH = new QuickHullThread(S);
        QH.getConvexHull();

        long endTime = System.nanoTime();
        long duration = (endTime -startTime)/1000000;
        System.out.println("Total execution time: " + (duration) );
        //System.out.println("Convex hull: " + QH.getConvexHull());
    }
}
