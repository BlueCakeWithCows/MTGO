package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bluecake.client.ImageScanner;

public class ImageScanTest2 {
	static ImageScanner scanner;

	public static void main(String[] args) throws IOException {

		scanner = new ImageScanner();
		scanner.init();

		BufferedImage b1 = ImageIO.read(new File("testImages/spec/test.png"));
		String s = scanner.scan(b1);
		System.out.println(s);
		// doTestWithTarget("1 . 400 items.png", "1/400 items");
		// doContainsTest("Trade Canceled Big.png", "Trade Canceled", true);
		// doContainsTest("Trade Canceled Big.png", "Halocaust", false);

	}

	private static void doContainsTest(String url, String toDiscover, boolean target) throws IOException {
		BufferedImage image = ImageIO.read(new File("testImages/spec/" + url));

		boolean result = scanner.scanFor(image, toDiscover);
		testOut((result == target), String.valueOf(target), String.valueOf(result));
	}

	private static void doTestWithTarget(String url, String target) throws IOException {
		BufferedImage image = ImageIO.read(new File("testImages/spec/" + url));

		String result = scanner.scan(image).trim();
		testOut(result.equals(target), target, result);

	}

	private static void testOut(boolean pass, String target, String info) {
		if (pass) {
			System.out.print("SUCCESS");
		} else {
			System.out.print("FAILED");
		}
		System.out.println(": [Target: " + target + "]  [Result: " + info + "]");
	}
}
