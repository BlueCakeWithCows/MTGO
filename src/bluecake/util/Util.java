package bluecake.util;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

	public static void main(String[] agrs) throws InterruptedException, FileNotFoundException, IOException {
		Versions.load();
		ArrayList<String> newCount = new ArrayList<String>();
		for (String[] ver : Versions.versions) {
			int number = 0;
			for (int i = 1; i < 600; i++) {
				TimeUnit.SECONDS.sleep(1);
				try{
				getHTML("https://www.mtgowikiprice.com/card/" + ver[0] + "/" + i);
				
				}catch(java.io.FileNotFoundException e){
					number = i;
					break;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			newCount.add(ver[0] + "-" + number);
		}
		PrintWriter writer = new PrintWriter("sets.txt", "UTF-8");
		for (String s : newCount) {
			writer.println(s);
		}
		writer.close();
	}
}
