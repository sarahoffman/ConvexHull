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
 * QuickHullThread class that drives the calculation process and contains HullThread class that implements the thread
 */

public class QuickHullThread {

    // variable delcarations
    private int threadCount = 0;
    private ArrayList<Point> originalInput;

    public static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();


    // finds a thread
    public synchronized boolean grabAThread() {

        // Really should be using wait() and notify()
        if (threadCount >= NUM_THREADS) {
            return false;
        }

        // increment threadCount
        ++threadCount;
        return true;
    }


    // decrements thread count
    public synchronized int decrementThreads() {
        int result = --threadCount;
        return result;
    }


    // constructor
    public QuickHullThread(ArrayList<Point> S) {
        //creates a QuickHullThread object from the given ArrayList of points
        this.threadCount = 0;
        this.originalInput = S;
        //System.out.println("input: " + S);
    }


    // gets convex hull of point list
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

            extremes[0] = (min);
            extremes[1] = (max);

            return extremes;
        }


        // Function that actually finds the convex hull
        public ArrayList<Point> getConvexHull() {
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
            if (segment.getP1().getY() >= maxPoint.getY() 
                || segment.getP2().getY() >= maxPoint.getY())  {
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

            // run threads
            runThreads(left, min2dist, right, max2dist);
        }

        // runs all threads
        private void runThreads(ArrayList<Point> left, Segment leftSegment, ArrayList<Point> right, Segment rightSegment) {
            ArrayList<HullThread> myThreads = new ArrayList<HullThread>();
            if (left.size() > 0) {
                myThreads.add(runThread(left, leftSegment));
            }

            if (right.size() > 0) {
                myThreads.add(runThread(right, rightSegment));
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


        // runs individual threads
        public HullThread runThread(ArrayList<Point> pts, Segment segment) {
            HullThread rthread = new HullThread(pts, segment);
            boolean spawnThread = grabAThread();  // increment thread count or just process on this thread

            if (spawnThread) {

                rthread.start();
            }
            else {
                rthread.subHull();
            }
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
        // create random
        Random rand = new Random();

        // generate random points
        for( int i = 0; i < 1500000; i++) {
            int x = rand.nextInt(100);
            int y = rand.nextInt(100);
            S.add( new Point( x, y ) );
        }

        // create QuickHullThread object and find convex hull
        long startTime = System.nanoTime();
        QuickHullThread QH = new QuickHullThread(S);
        QH.getConvexHull();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;
        System.out.println("Duration: " + duration); 
    }
}
