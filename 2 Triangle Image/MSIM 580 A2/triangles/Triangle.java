package triangles;

import java.awt.Color;

/**
 * The triangle class includes coordinate values for the corners of the triangle and the RGB color of the triangle
 * Includes methods to randomize the corners, determine if a point is inside the triangle and return maximum and minimum x and y values
 * @author Ben Bissantz
 *
 */
public class Triangle {
	
	// Three x and y coordinates and an RGB color
	int x1, x2, x3;
	int y1, y2, y3;
	int rgb, r, g, b;
	Color color;
	
	/**
	 * Randomizes the values of the triangle points and color
	 * @param x image width (maximum value of x coordinates)
	 * @param y image height (maximum value of y coordinates)
	 */
	public void randomize (int x, int y) {
		x1 = (int) (Math.random() * x);
		x2 = (int) (Math.random() * x);
		x3 = (int) (Math.random() * x);
		y1 = (int) (Math.random() * y);
		y2 = (int) (Math.random() * y);
		y3 = (int) (Math.random() * y);
		/* Lines removed from updated submission
		r = (int) (Math.random() * 255);
		g = (int) (Math.random() * 255);
		b = (int) (Math.random() * 255);
		color = new Color(r, g, b);
		rgb = color.getRGB();
		*/
	}
	
	/**
	 * Determine if a point is inside the triangle
	 * Uses Barycentric coordinates to determine if the point is on the same side of all three edges of the triangle
	 * This method is a modified version of code from Kornel Kisielewicz
	 * https://stackoverflow.com/questions/2049582/how-to-determine-if-a-point-is-in-a-2d-triangle
	 * @param x coordinate of the point
	 * @param y coordinate of the point
	 * @return true if the point is inside the triangle
	 */
	public boolean pointInside (int x, int y) {
	    float d1, d2, d3;
	    d1 = (x - x2) * (y1 - y2) - (x1 - x2) * (y - y2);
	    d2 = (x - x3) * (y2 - y3) - (x2 - x3) * (y - y3);
	    d3 = (x - x1) * (y3 - y1) - (x3 - x1) * (y - y1);
	    // If d1, d2, and d3 are all negative or all positive the point is inside the triangle
	    return ((d1 < 0) && (d2 < 0) && (d3 < 0) || (d1 > 0) && (d2 > 0) && (d3 > 0));
	}
	
	/**
	 * Determine the minimum x value of the triangle
	 * @return minimum x value of the triangle
	 */
	public int xMin () {
		return Math.min(x1, Math.min(x2, x3));
	}
	
	/**
	 * Determine the maximum x value of the triangle
	 * @return maximum x value of the triangle
	 */
	public int xMax () {
		return Math.max(x1, Math.max(x2, x3));
	}
	
	/**
	 * Determine the minimum y value of the triangle
	 * @return minimum y value of the triangle
	 */
	public int yMin () {
		return Math.min(y1, Math.min(y2, y3));
	}
	
	/**
	 * Determine the maximum y value of the triangle
	 * @return maximum y value of the triangle
	 */
	public int yMax () {
		return Math.max(y1, Math.max(y2, y3));
	}
}