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

public class make_features_labels {
	
	// Constants
	static private final String input_fn = "UCI-database/spambase/spambase.data";
	static private final String features_fn = "features.dat";
	static private final String labels_fn = "labels.dat";
	
	public static void main(String args[]) {
		// Reading input
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(input_fn));
		} catch (IOException exc) {
			System.err.println(exc.toString());
			return;
		}
		
		// Writing outputs
		PrintWriter features = null;
		try {
			features = new PrintWriter(new FileWriter(features_fn));
		} catch (IOException exc) {
			System.err.println(exc.toString());
			return;
		}
		
		PrintWriter labels = null;
		try {
			labels = new PrintWriter(new FileWriter(labels_fn));
		} catch (IOException exc) {
			System.err.println(exc.toString());
			return;
		}
		
		while (true) {
			String line = null;
			try {
				line = input.readLine();
			} catch (IOException exc) {
				System.err.println(exc.toString());
				break;
			}
			if ((line == null) || (line.length() == 0)) {
				break;
			}
			ArrayList<Double> list = new ArrayList<>();
			int i = 0;
			while (i < line.length()) {
				if ((line.charAt(i) == ',') || (line.charAt(i) == ' ')) {
					++i;
					continue;
				}
				String str = new String();
				int v = i;
				for (int j = i; j < line.length(); ++j) {
					if ((line.charAt(j) == ',') || (line.charAt(j) == ' ')) {
						v = j;
						break;
					}
					str += line.charAt(j);
				}
				i = v + 1;
				list.add(Double.parseDouble(str));
			}
			System.out.println(list.size());
			for (int j = 0; j < list.size() - 1; ++j) {
				features.print(Double.toString(list.get(j)) + " ");
			}
			features.println();
			double label = list.get(list.size() - 1);
			labels.println(Double.toString(label));
		}
		
		// Closing all streams
		try {
			input.close();
		} catch (Exception exc) {
			System.err.println(exc.toString());
		}
		
		try {
			features.close();
		} catch (Exception exc) {
			System.err.println(exc.toString());
		}
		
		try {
			labels.close();
		} catch (Exception exc) {
			System.err.println(exc.toString());
		}
	}	
	
}
