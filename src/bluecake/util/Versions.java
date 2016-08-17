package bluecake.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Versions {
	public static List<String[]> versions;

	public static void load() throws FileNotFoundException, IOException {
		versions = new ArrayList<String[]>();
		try (BufferedReader br = new BufferedReader(new FileReader("sets.txt"))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				String l = line.split("-")[0];
				String l2 = line.split("-")[1];
				
				versions.add(new String[] { l, l2 });
				line = br.readLine();
			}
			br.close();
		}
	}
	
}
