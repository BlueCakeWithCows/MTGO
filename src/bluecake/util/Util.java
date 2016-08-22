package bluecake.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	public static boolean logging = false;

	public static String getHTML(String url) throws IOException {
		URL webpage;
		InputStream is = null;
		BufferedReader br;
		String line = null;
		StringBuilder completedDoc = new StringBuilder();

		try {
			webpage = new URL(url);
			is = webpage.openStream(); // throws an IOException
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				completedDoc.append(line + "\n");
			}

			if (logging) {
				Date d = new Date();
				String name = getTimeStamp() + " " + url;
				name = name.replaceAll("/", "");
				name = name.replaceAll(":", "");
				name = name.replaceAll("https", "");
				name = name.replaceAll("www.	", "");
				name = name.replaceAll(".com", " ");
				File file = new File("html_logs");
				file.mkdir();
				try (PrintWriter out = new PrintWriter("html_logs/" + name + ".txt")) {
					out.println(completedDoc);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
				// nothing to see here
			}
		}

		return completedDoc.toString();
	}

	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

	public static String getTimeStamp() {
		Date date = new Date();
		String s = dateFormat.format(date);
		return s;
	}

	public static int getAgeFromCreation(long oT) {
		long cT = System.currentTimeMillis();
		long nT = cT - oT;
		nT = nT / 1000;
		int minutes = (int) (nT / 60);
		return minutes;
	}
}
