package bluecake.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

public class ImageScanner {
	private ITesseract instance;
	private boolean init;

	public void init() {
		instance = new Tesseract(); // JNA Interface Mapping
		File tessDataFolder = (new File("tessdata")).getAbsoluteFile();
		System.out.println(tessDataFolder.getPath());
		instance.setDatapath(tessDataFolder.getPath());
		System.out.println(tessDataFolder.getParent());
		instance.setOcrEngineMode(TessOcrEngineMode.OEM_TESSERACT_CUBE_COMBINED);
	
		
		
		init = true;
	}

	public String scan(BufferedImage image) {
		if (!init)
			init();
		image = ImageHelper.scaleBy(image, 4f);
		image = ImageHelper.rescaleOp(image, 1.2f, 0);
		File outputfile = new File("test.png");
		try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			return instance.doOCR(image);
		} catch (TesseractException e) {
			e.printStackTrace();
		}

		return null;

	}
}
