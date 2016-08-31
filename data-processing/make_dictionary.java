// Software: Spambase filtering
// Database Name: UCI Spambase Data Set
// Datbase Link: https://archive.ics.uci.edu/ml/datasets/Spambase
// Author: Hy Truong Son
// Position: PhD Student
// Institution: Department of Computer Science, The University of Chicago
// Email: sonpascal93@gmail.com, hytruongson@uchicago.edu
// Website: http://people.inf.elte.hu/hytruongson/
// Copyright 2016 (c) Hy Truong Son. All rights reserved.

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class make_dictionary {
	
	// Constants
	static private final String input_fn = "list-of-words.dat";
	static private final String output_fn = "dictionary.dat";
	
	// Memory
	static private ArrayList<String> lines;
	
	// Main Program
	public static void main(String args[]) {
		// Reading input
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(input_fn));
		} catch (IOException exc) {
			System.err.println(exc.toString());
			return;
		}
		
		lines = new ArrayList<>();
		while (true) {
			String line = null;
			try {
				line = input.readLine();
			} catch (IOException exc) {
				System.err.println(exc.toString());
			}
			
			if ((line == null) || (line.length() == 0)) {
				break;
			}
			
			lines.add(line);
			System.out.println(line);
		}
		
		try {
			input.close();
		} catch (Exception exc) {
			System.err.println(exc.toString());
		}
		
		// Writing output
		PrintWriter output = null;
		try {
			output = new PrintWriter(new FileWriter(output_fn));
		} catch (IOException exc) {
			System.err.println(exc.toString());
			return;
		}
		
		output.println(Integer.toString(lines.size()));
		for (int i = 0; i < lines.size(); ++i) {
			String str = lines.get(i);
			String word = new String();
			for (int j = 10; j < str.length(); ++j) {
				if (str.charAt(j) == ':') {
					break;
				}
				word += str.charAt(j);
			}
			output.println(word);
		}
		
		try {
			output.close();
		} catch (Exception exc) {
			System.err.println(exc.toString());
		}
	}	
	
}
