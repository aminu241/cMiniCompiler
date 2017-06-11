package internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.FileUtil;

public class PreProcessor {
	public void process(String iFile, String oFile){
		ArrayList<String> lines = FileUtil.readFileByLines(iFile);
		System.out.println("Preprocessing...");
		
		//remove the block comments
		boolean inComment = false;
		for(int i = 0; i < lines.size(); i++){
			String line = lines.get(i);
			line = line.trim();
			if(inComment){
				int index = line.indexOf("*/");
				if(index >= 0){// the end found
					line = line.substring(index + 2);
					lines.set(i, line);
					inComment = false; //clear the flag
				}else{// still in block comment
					lines.set(i, "");
				}
			}
			if(line.startsWith("/*")){// the start found
				int index = line.indexOf("/*");
				line = line.substring(0, index);
				lines.set(i, line);
				inComment = true; // set the flag
			}
		}
		
		for(int i = 0; i < lines.size(); i++){
			String line = lines.get(i);
			line = line.trim();
			if(line.startsWith("//")){
				lines.set(i, "");
			}else{
				Pattern pattern = Pattern.compile("\".*\"");
				Matcher matcher = pattern.matcher(line);
				if(matcher.find()){
				line = matcher.replaceAll("");
				}
				
				int index = line.indexOf("//");
				if(index > 0){
					int index2 = line.indexOf("\"");
					if(index2 >= 0 && index2 < index){
					}else{
						line = line.substring(index);
						lines.set(i, lines.get(i).replace(line, ""));
					}
				}
			}
		}
		
		//the problem is that the line number is missing, 
		// how to fix this?
		String str = "";
		int lineNum = 1;
		for(String s: lines){
			System.out.println(s);
			if(s.length() > 0){
				str += lineNum + ":" + s +"\n";
			}
			lineNum++;
		}
		FileUtil.writeToFile(oFile, str);
	}
	
	
}
