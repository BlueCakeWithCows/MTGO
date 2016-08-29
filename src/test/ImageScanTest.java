package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

import bluecake.client.ImageScanner;

public class ImageScanTest {
	public static void main(String[] args) throws IOException{
		File file = new File("testImages");
		String[] tests = file.list(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				return (name.contains(".png"));
			}
			
		});
		ImageScanner scanner = new ImageScanner();
		scanner.init();
		for(String s:tests){
			BufferedImage image = ImageIO.read(new File("testImages/" +s));
			System.out.println(s);
			System.out.println(scanner.scan(image).trim());
		}
		
	}
}
