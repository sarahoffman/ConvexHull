import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;
import java.lang.Math;

public class QuickHull {

	private ArrayList<Point> input;
	private int n;
	private ArrayList<Point> output;
	
	public QuickHull( ArrayList<Point> S ){
		///creates a QuickHull object from the given ArrayList of points
		input = S;
		output = new ArrayList<Point>();
		n = S.size();
		System.out.println( this.input );
	}
	
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
	
	public ArrayList<Point> getConvexHull() {
		
		Point[] result = this.getExtremes(input);
		Point min = result[0];
		Point max = result[1];

		System.out.println( "min: " + String.valueOf(min) + "	" + 
							"max: " + String.valueOf(max) );
		
		Segment s = new Segment( min,max );
		output.add( min );
		output.add( max );
		return output;
	}
	
	public void getPoints( ArrayList<Point> s, Point min, Point max ) {
		
	}
	
// 	public float distance( Segment s, Point p ) {
// 		a,b,c = s.getCo();
// 		dist = Math.abs( a*p.getX() + b*p.getY() + c )/Math.sqrt( a*a + b*b );
// 		return dist;
// 	}
	
	public static void main( String args[] ) {
		ArrayList<Point> S = new ArrayList<Point>();
		Random rand = new Random();
		for( int i = 0; i < 10; i++) {
			int x = rand.nextInt(100);
			int y = rand.nextInt(100);
			S.add( new Point( x, y ) );
		}
		
		QuickHull QH = new QuickHull( S );
		QH.getConvexHull();
	}
}