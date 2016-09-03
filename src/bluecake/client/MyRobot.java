package bluecake.client;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

public class MyRobot implements ClipboardOwner {
	private Robot robot;
	private final Object lock = new Object();

	public MyRobot() {
		try {
			robot = new Robot();
			robot.setAutoDelay(600);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BufferedImage getScreen(int x, int y, int x2, int y2) {
		return robot.createScreenCapture(new Rectangle(x, y, x2 - x, y2 - y));
	}

	public void click() {
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	public Point getPoint() {
		System.out.println(MouseInfo.getPointerInfo().getLocation());
		return MouseInfo.getPointerInfo().getLocation();
	}

	public void moveTo(int x, int y) {
		robot.mouseMove(x, y);
	}

	public void moveTo(Point p) {
		this.moveTo(p.x, p.y);
	}

	public void hide() {
		moveTo(0, 0);
	}

	public void type(String characters) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection(characters);
		clipboard.setContents(stringSelection, this);

		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {

	}

	public void delete() {
		robot.keyPress(KeyEvent.VK_DELETE);
	}

	public void enter() {
		robot.keyPress(KeyEvent.VK_ENTER);
	}

	public void doubleClick() {
		robot.setAutoDelay(50);
		click();
		click();
		robot.setAutoDelay(500);
	}

	public void escape() {
		robot.keyPress(KeyEvent.VK_ESCAPE);
		robot.keyRelease(KeyEvent.VK_ESCAPE);

	}

	public BufferedImage getScreen(Rectangle numberofthingsinbinder) {
		return robot.createScreenCapture(numberofthingsinbinder);
	}

	public void press(int vkBackSpace) {
		robot.keyPress(vkBackSpace);
		robot.keyRelease(vkBackSpace);
	}

	public void quickClick() {
		robot.setAutoDelay(50);
		click();
		robot.setAutoDelay(500);
	}
}
