package bluecake.util;

import bluecake.Main;
import bluecake.Table;

public class Log {
	public static final int WIKI = 1;
	public static final int HOT = 2;

	public static void log(int i, String x) {
		try {
			String log = getHeader(i) + x + "\n";
			System.out.println(log);
			Table t = Main.frame;
			t.allConsole.append(log);
			switch (i) {
			case WIKI:
				t.priceWikiConsole.append(log);
				break;
			case HOT:
				t.hotListConsole.append(log);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getHeader(int x) {
		if (x == WIKI)
			return "[Wiki]";
		if (x == HOT)
			return "[HotList]";
		return "[NaN]";
	}
}
