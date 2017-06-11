package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtil {
	// writes to a file
	public static void writeToFile(String fileName, String str) {
		File file = new File(fileName);
		try {
			FileOutputStream out = new FileOutputStream(file, false);
			out.write(str.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		ArrayList<String> lines = new ArrayList<String>();

		try {
			System.out.println("Read the file line by line");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			while ((tempString = reader.readLine()) != null) {
				lines.add(tempString + '\0');
				//System.out.println("line " + line + ": " + tempString);
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

		return lines;
	}
}
