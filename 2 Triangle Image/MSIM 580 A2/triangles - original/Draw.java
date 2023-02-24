package triangles;

import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Scanner;
import java.lang.Math;

/**
 * This program generates an image from triangles using a genetic algorithm
 * The user is prompted for several options including an input image, number of triangles, output files, and execution time
 * @author Ben Bissantz
 *
 */
public class Draw {

	public static void main(String[] args) {

		/**
		 * Declare objects needed for file input
		 */
		Scanner userInput = new Scanner(System.in);
		String inputName = null;
		File inputFile;
		BufferedImage inputImage = null;
		int triangles = 25;
		int outputFiles = 10;
		int runTime = 5;
		int generations = 0;
		
		/**
		 * Prompt user for an image file
		 */
		while (inputName == null) {
			System.out.println("Please enter the name of a .jpg/.jpeg image file.");
			inputName = userInput.next();
			try {
				inputFile = new File(inputName);
				inputImage = ImageIO.read(inputFile);
			} catch (Exception e) {
				System.out.println("Invalid file name.");
				inputName = null;
			}
		}

		/**
		 * Prompt user for number of triangles
		 */
		System.out.println("Please enter the number of triangles that should be used to generate the image (min 1, max 1,000,000).");
		triangles = userInput.nextInt();
		if (triangles > 1000000 || triangles < 1) {
			triangles = 25;
			System.out.println("Invalid selection! 25 triangles has been chosen by default.");
		}

		/**
		 * Prompt user for number of output files, does not allow more files than triangles
		 */
		System.out.println("Please enter the number of images to generate (min 1, max 100).");
		outputFiles = userInput.nextInt();
		if (outputFiles > 100 || outputFiles < 1 || outputFiles > triangles) {
			outputFiles = Math.min(triangles, 100);
			System.out.println("Invalid selection! The lesser of 100 and the number of triangles has been chosen by default.");
		}
		
		/**
		 * Prompt user for search time
		 */
		System.out.println("Please enter the amount of time in whole minutes you would like to search (min 1 minute, max 1 week).");
		runTime = userInput.nextInt();
		if (runTime < 1) {
			runTime = 1;
			System.out.println("Invalid entry, time set to 1 minute.");
		}
		if (runTime < triangles / 50) {
			runTime = triangles / 50;
			System.out.println("Invalid entry, maximum 50 triangles per minute.");
		}
		if (runTime > 10080) {
			runTime = 10080;
			System.out.println("Invalid entry, time set to 1 week.");
		}
		long endTime = System.currentTimeMillis() + runTime * 60 * 1000;
		
		/**
		 * Provide a summary of inputs
		 */
		userInput.close();
		System.out.println(inputName + " will be created using " + triangles + " triangles. ");
		System.out.println(outputFiles + " images will be created in the next " + runTime + " minutes.");
		
		/**
		 * Initialize the Linked List of Generated Images for the first generation
		 */
		LinkedList<GeneratedImage> images = new LinkedList<GeneratedImage>();
		for (int n = 0; n < 50; n++) {
			GeneratedImage i = new GeneratedImage();
			i.initialize(inputImage.getWidth(), inputImage.getHeight());
			i.setRandomTriangles(triangles);
			i.fitnessFunction(inputImage);
			images.addLast(i);
		}
		
		/**
		 * Generate triangles and provide an update after each generation
		 */
		while(System.currentTimeMillis() < endTime) {
			images = selection(images, inputImage);
			// Every 25 generations save the best Generated Image
			if (generations % 25 == 0) {
				String outputName = inputName.substring(0, inputName.lastIndexOf('.')) + "-g" + generations + "-" + images.getFirst().fitness + inputName.substring(inputName.lastIndexOf('.'));
				try {
					File outputFile = new File(outputName);
					ImageIO.write(images.getFirst().drawTriangles(triangles), "jpg", outputFile);
				} catch (Exception e) {
					System.out.println("Current image not saved");
				}
			}
			System.out.println(generations + " generations, top three and last fitness function results: " + images.getFirst().fitness + " " + images.get(1).fitness + " " + images.get(2).fitness + " ... " + images.getLast().fitness);
			generations++;
		}
		
		/**
		 * Create output images of the best image generated based on user input
		 */
		for (int i = 1; i <= outputFiles; i++) {
			String outputName = inputName.substring(0, inputName.lastIndexOf('.')) + "-t" + i + inputName.substring(inputName.lastIndexOf('.'));
			try {
				File outputFile = new File(outputName);
				ImageIO.write(images.getFirst().drawTriangles(i * triangles / outputFiles), "jpg", outputFile);
				// Provide an update as images are created
				System.out.println(outputName + " is image " + i + " / " + outputFiles + " images created with " + (i * triangles / outputFiles) + " / " + triangles + " triangles added.");
			} catch (Exception e) {
				System.out.println("Output file " + outputName + "failed  with " + i + " / " + triangles + " triangles added.");
			}
		}
		
		/**
		 * Inform user of program completion
		 */
		System.out.println("Program complete: " + inputName + " was created using " + triangles + " triangles. " + outputFiles + " images created in " + runTime + " minutes with " + generations + " generations.");
	} // End of Main
	
	/**
	 * Performs the Genetic Operation and Mutation Operations then updates the Fitness Function value for all Generated Images
	 * Sorts the images based on Fitness Function value and Selection process selects 50 images to keep
	 * @param imageList Linked list of Generated Images used to produce the next generation of Generated Images 
	 * @param inputImage Image the program is attempting to reproduce
	 * @return New Linked List of Generated Images
	 */
	public static LinkedList<GeneratedImage> selection (LinkedList<GeneratedImage> imageList, BufferedImage inputImage) {
		// Add children generated from the images in the list
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < 20; j++) {
				// Select a mate, images with better fitness function values are more likely to be chosen
				int mate = (int) (50 * Math.pow(Math.random(), 2));
				mate += (i == mate) ? 1 : 0; 
				imageList.addLast(geneticOperation(imageList.get(i), imageList.get(mate)));			
			}
		}
		// Chance of selection for mutation increases as fitness function increases
		// 5 percent chance of aspect of a triangle being mutated
		for (int i = 1; i < imageList.size(); i++) {
			int mutate = Math.min(i, 25);
			// If the image is a duplicate it is automatically mutated
			if (imageList.get(i).fitness == imageList.get(i - 1).fitness) mutate = 50;
			imageList.get(i).mutation(mutate, 50, 5, 100);
		}
		// Update Fitness Function values for all Generated Images
		for (int i = 0; i < imageList.size(); i++) imageList.get(i).fitnessFunction(inputImage);
		// Sort the images and keep the best 50 images
		LinkedList<GeneratedImage> sortedList = new LinkedList<GeneratedImage>();
		sortedList.add(imageList.getFirst());
		for (int i = 1; i < imageList.size(); i++) {
			if (imageList.get(i).fitness >= sortedList.getLast().fitness) {
				sortedList.addLast(imageList.get(i));
			} else for (int j = 0; j < sortedList.size(); j++) {
				if (imageList.get(i).fitness < sortedList.get(j).fitness) {
					sortedList.add(j, imageList.get(i));
					j = sortedList.size();
				}
			}
		}
		// Keep the first 20 images and keep every other of the next 60 images for a total of 50
		for (int i = 20; i < 50; i++) sortedList.remove(i);
		while (sortedList.size() > 50) sortedList.removeLast();
		return sortedList;
	}
	
	/**
	 * The Genetic Operation combines information from two parent Generated Images to produce a new Generated Image
	 * Traits are more likely to be chosen from the parent with a lower fitness function value 
	 * @param parentA Generated Image to act as a parent
	 * @param parentB Generated Image to act as a parent
	 * @return new Generated Image with traits from both parent images
	 */
	public static GeneratedImage geneticOperation (GeneratedImage parentA, GeneratedImage parentB) {
		// Create a new Generated Image
		GeneratedImage child = new GeneratedImage();
		child.initialize(Math.min(parentA.image.getWidth(), parentB.image.getWidth()), Math.min(parentA.image.getHeight(), parentB.image.getHeight()));
		child.triangles = new LinkedList<Triangle>();
		// Copy triangles from either of the parents
		for (int t = 0; t < Math.min(parentA.triangles.size(), parentB.triangles.size()); t++) {
			Triangle tri = new Triangle();
			if (parentA.fitness < Math.random() * (parentA.fitness + parentB.fitness)) {
				tri.x1 = parentA.triangles.get(t).x1;
				tri.x2 = parentA.triangles.get(t).x2;
				tri.x3 = parentA.triangles.get(t).x3;
				tri.y1 = parentA.triangles.get(t).y1;
				tri.y2 = parentA.triangles.get(t).y2;
				tri.y3 = parentA.triangles.get(t).y3;
				tri.r = parentA.triangles.get(t).r;
				tri.g = parentA.triangles.get(t).g;
				tri.b = parentA.triangles.get(t).b;
				tri.rgb = parentA.triangles.get(t).rgb;
				tri.color = parentA.triangles.get(t).color;
			} else {
				tri.x1 = parentB.triangles.get(t).x1;
				tri.x2 = parentB.triangles.get(t).x2;
				tri.x3 = parentB.triangles.get(t).x3;
				tri.y1 = parentB.triangles.get(t).y1;
				tri.y2 = parentB.triangles.get(t).y2;
				tri.y3 = parentB.triangles.get(t).y3;
				tri.r = parentB.triangles.get(t).r;
				tri.g = parentB.triangles.get(t).g;
				tri.b = parentB.triangles.get(t).b;
				tri.rgb = parentB.triangles.get(t).rgb;
				tri.color = parentB.triangles.get(t).color;
			}
			child.triangles.add(tri);
		}
		return child;
	}
}