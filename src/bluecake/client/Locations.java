package bluecake.client;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bluecake.util.SimpleSaveLoad;

public class Locations {
	public static final Point BUDGET_BINDER = new Point(56, 737);
	public static final Point SELL_BINDER = new Point(72, 757);
	public static final Point TRADE_BINDER = new Point(66, 787);
	public static final Point COLLECTION_SEARCH_BAR = new Point(144, 187);
	public static final Point COLLECTION_BUTTON = new Point(206, 57);
	public static final Point TRADE_BUTTON = new Point(512, 57);
	public static final Point BUDDIES_BUTTON = new Point(66, 200);
	public static final Point TRADE_BINDERS_BUTTON = new Point(91, 727);
	public static final Point TRADE_SEARCH_BUTTON = new Point(126, 104);
	public static final Point TRADE_TRADE_BUTTON = new Point(1882, 175);
	public static final Point COLLECTION_BINDER_OPTIONS = new Point(688, 690);
	public static final Point COLLECTION_BINDER_OPTIONS_ACTIVE = new Point(1043, 362);
	public static final Point COLLECTION_BINDER_OPTIONS_OK = new Point(1313, 735);
	public static final Point COLLECTION_CARDS = new Point(26, 133);
	public static final Point COLLECTION_OTHER = new Point(97, 133);
	public static final Point COLLECTION_CLEAR_FILTER = new Point(223, 165);
	public static final Point MAXIMIZE_TRADE_WINDOW = new Point(1564, 187);
	public static final Point INTRADE_SEARCH_BAR = new Point(99, 134);
	public static final Point INTRADE_RESET_FILTER = new Point(162, 102);
	public static final Point INTRADE_CARD = new Point(44, 66);
	public static final Point INTRADE_OTHER = new Point(110, 62);
	public static final Point INTRADE_SUBMIT = new Point(1394, 993);
	public static final Point INTRADE_CONFIRM = new Point(1394, 993);
	public static final Rectangle NumberOfThingsInBinder = createRect(361, 684, 405, 704);
	public static final Rectangle NumberOfTradePeople = createRect(241, 120, 400, 138);
	public static final Rectangle Screen = createRect(0, 0, 1920, 1015);
	public static final Rectangle TradeBox = new Rectangle(759, 429, 415, 44);
	public static final Rectangle TradeCardRowTemplate = new Rectangle(214, 77, 1446, 19);
	public static final Rectangle CollectionCardRowTemplate = new Rectangle(286, 144, 1624, 19);

	public static final Point EXIT = new Point(1900, 15);
	public static final Point Collections_First = new Point(450, 155);
	public static final Point MyTakenItem = new Point(60, 750);
	public static final Rectangle ChatArea = new Rectangle(1668, 805, 240, 110);
	public static final Rectangle TradeCorner = new Rectangle(33, 5, 37, 16);
	public static final Point SELL_BINDER_OPTIONS = new Point(666, 670);
	public static Rectangle Intrade_Ticket_Check = createRect(873, 813, 921, 833);
	public static ImageMark TRADE_BUTTON_IMAGEMARK = new ImageMark("BUTTON_GLOWING_CORNER.png",
			new Rectangle(476, 42, 6, 5));

	private static Rectangle createRect(int i, int j, int k, int l) {
		return new Rectangle(i, j, k - i, l - j);
	}

	public static class ImageMark {
		String baseURL = SimpleSaveLoad.getURL() + "ImageMark/";
		String url;
		public BufferedImage image;
		public Rectangle rectangle;

		public ImageMark(String url, Rectangle location) {
			this.url = baseURL + url;
			try {
				image = ImageIO.read(new File(this.url));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
			this.rectangle = location;
		}

		public boolean getMatch(Robot rob) {
			BufferedImage screenCap = rob.createScreenCapture(rectangle);
			double total = 0;
			Raster o = image.getRaster();
			Raster c = screenCap.getRaster();
			int[] fuck = new int[3];
			for (int y = 0; y < screenCap.getHeight(); y++) {
				for (int x = 0; x < screenCap.getWidth(); x++) {
					total += Math.pow(o.getPixel(x, y, fuck)[0] - c.getPixel(x, y, fuck)[0], 2);
				}

			}
			double score = (total / (screenCap.getWidth() * screenCap.getHeight()));
			System.out.println(score);

			if (score < 5) {
				return true;
			}
			return false;
		}
	}
}
