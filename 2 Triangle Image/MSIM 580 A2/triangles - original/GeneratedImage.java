package triangles;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * The Generated Image class consists of a Linked List of Triangles
 * The class contains methods to initialize an image, add random triangles,
 * convert triangles values to an image, determine fitness, and mutate triangle values
 * @author Ben Bissantz
 *
 */
public class GeneratedImage {
	
	// The image, list of triangles, and stored result of fitness function
	BufferedImage image;
	LinkedList<Triangle> triangles;
	long fitness;
	
	/**
	 * Creates a blank image of dimensions w by h
	 * @param w width of the image to generate
	 * @param h height of the image to generate
	 */
	public void initialize (int w, int h) {
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				image.setRGB(x, y, 0);
			}
		}
	}

	/**
	 * Creates a specified number of random triangles within the dimensions of the image
	 * @param n number of triangles to generate
	 */
	public void setRandomTriangles (int n) {
		triangles = new LinkedList<Triangle>();
		for(int i = 0; i < n; i++) {
			Triangle t = new Triangle();
			t.randomize(image.getWidth(), image.getHeight());
			triangles.add(t);
		}
		image = drawTriangles(triangles.size());
	}
			
	/**
	 * Generates an image with a specified number of triangles up to the total number of triangles
	 * @param n number of triangles to add to the image
	 * @return image generated with a specified number of triangles
	 */
	public BufferedImage drawTriangles (int n) {
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				image.setRGB(x, y, 0);
			}
		}
		for (int i = 0; i < n && i < triangles.size(); i++) {
			for(int y = triangles.get(i).yMin(); y <= triangles.get(i).yMax(); y++) {
				for(int x = triangles.get(i).xMin(); x <= triangles.get(i).xMax(); x++) {
					if (triangles.get(i).pointInside(x, y)) image.setRGB(x, y, (int) ((image.getRGB(x, y) + triangles.get(i).rgb) * 0.5));
				}
			}
		}
		return image;
	}
	
	/**
	 * Compares the generated image to a specified image and calculates a value representing the difference between the two images
	 * @param i image to compare to the generated image
	 * @return a value representing the difference between specified image and the generated image
	 */
	public void fitnessFunction (BufferedImage i) {
		this.image = drawTriangles(triangles.size());
		int w = Math.min(this.image.getWidth(), i.getWidth());
		int h = Math.min(this.image.getHeight(), i.getHeight());
		long rgbDiff = 0;
		for (int y = 0; y < h; y += 2) {
			for (int x = 0; x < w; x += 2) {
	            // Creating a Color object from pixel value
				Color target = new Color(i.getRGB(x, y), true);
				Color current = new Color(image.getRGB(x, y), true);
	            // Retrieving the R G B values
	            rgbDiff += Math.abs(target.getRed() - current.getRed());
	            rgbDiff += Math.abs(target.getGreen() - current.getGreen());
	            rgbDiff += Math.abs(target.getBlue() - current.getBlue());
			}
		}
		this.fitness = rgbDiff;
	}
	
	/**
	 * Causes mutations to triangles in the image
	 * Odds of performing a mutation are based on specified parameters
	 * There is a mNumerator / mDenominator chance that an image will be selected for mutation
	 * There is a tNumerator / tDenominator chance that each aspect of a triangle in an image selected for mutation will be mutated
	 * @param mNumerator Numerator of the chance that an image will be selected for mutation
	 * @param mDenominator Denominator of the chance that an image will be selected for mutation
	 * @param tNumerator Numerator of the chance that a triangle will be selected for mutation
	 * @param tDenominator Denominator of the chance that a triangle will be selected for mutation
	 */
	public void mutation (int mNumerator, int mDenominator, int tNumerator, int tDenominator) {
		if (mNumerator > Math.random() * mDenominator) {
			for (int i = 0; i < triangles.size(); i++) {
				if (tNumerator > Math.random() * tDenominator) triangles.get(i).x1 = (int) (Math.random() * image.getWidth());
				if (tNumerator > Math.random() * tDenominator) triangles.get(i).x2 = (int) (Math.random() * image.getWidth());
				if (tNumerator > Math.random() * tDenominator) triangles.get(i).x3 = (int) (Math.random() * image.getWidth());
				if (tNumerator > Math.random() * tDenominator) triangles.get(i).y1 = (int) (Math.random() * image.getHeight());
				if (tNumerator > Math.random() * tDenominator) triangles.get(i).y2 = (int) (Math.random() * image.getHeight());
				if (tNumerator > Math.random() * tDenominator) triangles.get(i).y3 = (int) (Math.random() * image.getHeight());
				if (tNumerator > Math.random() * tDenominator) triangles.get(i).r = (int) (Math.random() * 255);
				if (tNumerator > Math.random() * tDenominator) triangles.get(i).g = (int) (Math.random() * 255);
				if (tNumerator > Math.random() * tDenominator) triangles.get(i).b = (int) (Math.random() * 255);
				triangles.get(i).color = new Color(triangles.get(i).r, triangles.get(i).g, triangles.get(i).b);
				triangles.get(i).rgb = triangles.get(i).color.getRGB();
			}
		}
	}
}