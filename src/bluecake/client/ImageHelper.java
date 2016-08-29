package bluecake.client;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;

public class ImageHelper {
	public static BufferedImage scaleBy(BufferedImage img, float scale) {
		try {
			return Thumbnails.of(img).scalingMode(ScalingMode.BICUBIC).scale(scale).asBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int scaleX = (int) (img.getWidth() * scale);
		int scaleY = (int) (img.getHeight() * scale);
		Image image = img.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
		BufferedImage buffered = new BufferedImage(scaleX, scaleY, BufferedImage.TYPE_INT_RGB);
		buffered.getGraphics().drawImage(image, 0, 0, null);
		return buffered;
		
		
	}
	
	public static BufferedImage rescaleOp(BufferedImage image,float factor, float offset){
		RescaleOp op =new RescaleOp(factor,offset,null);
		return op.filter(image,null);
	}
}
