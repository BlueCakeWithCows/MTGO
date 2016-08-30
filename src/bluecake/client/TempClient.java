package bluecake.client;

import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import bluecake.util.SimpleSaveLoad;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

public class TempClient {
	private MyRobot robot;
	private MicroBot bot;
	private static TempClient client;
	private ImageScanner scanner;

	public static void main(String[] args) {
		client = new TempClient();
		client.start();
	}

	private void initVariables() {
		robot = new MyRobot();
		scanner = new ImageScanner();
		scanner.init();
	}

	private void start() {
		initVariables();
		
		while(true){
			bot.c
		}
		// ITesseract instance = new Tesseract1(); // JNA Direct Mapping
		// File tessDataFolder = LoadLibs.extractTessResources("tessdata"); //
		// Maven build bundles English data

	}

	public static void sleep(double time) {
		try {
			Thread.sleep((long) (time * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
