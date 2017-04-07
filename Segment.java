// Sara Hoffman
// CS375 - Project 3
// Segment class

import java.awt.Point;
import java.lang.Math;

// segment class stores information 
// about a line segment
public class Segment {
    private Point p1;
    private Point p2;
    private double slope;
    private double intercept;
    

    // constructor
    public Segment(Point point1, Point point2) {
		this.p1 = point1;
		this.p2 = point2;
		double xVal = this.p1.getX() - this.p2.getX();
		double yVal = this.p1.getY() - this.p2.getY();
		this.slope = (yVal/xVal);
		this.intercept = this.p1.getY() - this.slope*this.p1.getX(); 
    }

    // returns length of the line segment
    private double getLength() {
		double xVal = Math.abs(this.p1.getX() - this.p2.getX());
		double yVal = Math.abs(this.p1.getY() - this.p2.getY());
		double length = Math.sqrt(Math.pow(xVal, 2)+Math.pow(yVal, 2));
		return length;
    }

    // returns coefficients of line
    public double[] getCo() {
		double [] Co = new double[3];
		double A = this.p1.getY() - this.p2.getY();
		double B = this.p2.getX() - this.p1.getX();
		double C = (this.p1.getX()*this.p2.getY()) - (this.p2.getX()*this.p1.getY());
		Co[0] = A;
		Co[1] = B;
		Co[2] = C;
		return Co;
    }

    // returns point one of line segment
    public Point getP1() {
		return this.p1;
    }

    // returns point two of line segment
    public Point getP2() {
		return this.p2;
    }
    
    // function to calculate if a point is to the left of a line segment
	public Boolean isLeft(Point p) {
		if (((this.p2.getX() - this.p1.getX())*(p.getY()-
			this.p1.getY()))-((p.getX()-this.p1.getX())*(this.p2.getY()-this.p1.getY())) > 0) {
		// if (p.getY() >= this.slope*p.getX() + this.intercept) {
		    return true; 
		}
		else {
		    return false; 
		}
    }

    // main method for compilation and testing
    public static void main(String args[]) {
		System.out.println("working");
		Point p1 = new Point(2,1);
		Point p2 = new Point(5,7);
		Segment s1 = new Segment(p1, p2);
		System.out.println("Point created");
		double length1 = s1.getLength();
		System.out.println(length1);
		double[] Co = s1.getCo();
		System.out.println(Co[0]);
		System.out.println(Co[1]);
		System.out.println(Co[2]);
    }
}
