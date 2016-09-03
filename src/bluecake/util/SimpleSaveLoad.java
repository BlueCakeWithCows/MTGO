package bluecake.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
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

	/** Returns true if it created a file */
	public static boolean createFileIfNoExist(String urs, String string) {
		String url = folder + urs;
		File file = new File(url);
		try {
			return file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void addOrReplace(String urs, String targetKey, String propery) throws IOException {

		String newLine = targetKey + ":" + propery;
		if (createFileIfNoExist(urs, newLine))
			return;
		String url = urs;
		List<String> file = load(url);

		boolean added = false;
		for (int i = 0; i < file.size(); i++) {
			String line = file.get(i);
			String[] lineSplit = line.split(":");
			String key = lineSplit[0];
			String prop = line.replaceFirst(key + ":", "");
			if (key.equals(targetKey)) {
				file.set(i, newLine);
				added = true;
				break;
			}
		}
		if (!added)
			file.add(newLine);
		// File temp = File.createTempFile("TempFisle", ".tmp", new File("/"));

		// temp.deleteOnExit();
		File temp = new File(folder + url);
		BufferedWriter out = new BufferedWriter(new FileWriter(temp));
		for (String string : file)
			out.write(string + System.lineSeparator());
		out.close();
		// File original = new File(folder + url);
		// FileChannel src = new FileInputStream(temp).getChannel();
		// FileChannel dest = new FileOutputStream(original).getChannel();
		// dest.transferFrom(src, 0, src.size());
		// src.close();
		// dest.close();

	}

	public static String getProperty(String url, String targetKey) {
		List<String> file = null;
		try {
			file = load(url);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		for (int i = 0; i < file.size(); i++) {
			String line = file.get(i);
			String[] lineSplit = line.split(":");
			String key = lineSplit[0];
			if (key.contentEquals(targetKey))
				return line.replaceFirst(key + ":", "");
		}
		return null;
	}
}
