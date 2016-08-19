package bluecake.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleSaveLoad {

	static String folder = "memory/";

	static {
		File dir = new File(folder);
		dir.mkdir();
	}

	public static void save(String file, String stuff) {

		FileWriter writer = null;
		try {
			writer = new FileWriter(folder + file);
			writer.write(stuff);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void append(String file, String stuff) {

		FileWriter writer = null;
		try {
			writer = new FileWriter(folder + file, true);
			writer.write(stuff);
			writer.write(System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static HashMap<String, List<String>> stuff = new HashMap<String, List<String>>();

	public static String loadLine(String file, int fileLine) throws IOException {

		if (stuff.containsKey(file)) {
			return stuff.get(file).get(fileLine);
		}
		List<String> list = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(folder + file))) {
			String line = br.readLine();

			while (line != null) {
				list.add(line);
				line = br.readLine();
			}
			stuff.put(file, list);
			return loadLine(file, fileLine);
		}
	}

	public static List<String> load(String file) throws IOException {

		if (stuff.containsKey(file)) {
			return stuff.get(file);
		}
		List<String> list = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(folder + file))) {
			String line = br.readLine();

			while (line != null) {
				list.add(line);
				line = br.readLine();
			}
			stuff.put(file, list);
			return list;
		}
	}

	public static void save(String file, String[] stuff2) {

		FileWriter writer = null;

		try {

			writer = new FileWriter(folder + file);
			for (String stuff : stuff2) {
				writer.append(stuff);
				writer.append(System.lineSeparator());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**Returns true if it created a file*/
	public static boolean createFileIfNoExist(String url, String string) {
		File file = new File(url);
		if(!file.exists()){
			save(url,string);
			return true;
		}
		return false;
	}
}
