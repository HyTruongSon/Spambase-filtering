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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class k_folds_separation {
	
	// Constants
	static private final int k = 5;
	static private String features_fn = "features.dat";
	static private String labels_fn = "labels.dat";
	
	// Memory
	static private int nSamples, nFeatures, nNegatives, nPositives;
	static private double features[][];
	static private double labels[];
	static private int negative[];
	static private int positive[];
	
	static private Random rand = new Random();
	
	// Supporting functions
	private static void read_features() {
		Scanner file = null;
		try {
			file = new Scanner(new File(features_fn));
		} catch (IOException exc) {
			System.err.println(exc.toString());
			return;
		}
		
		nSamples = file.nextInt();
		nFeatures = file.nextInt();
		
		features = new double [nSamples][nFeatures];
		
		for (int sample = 0; sample < nSamples; ++sample) {
			for (int feature = 0; feature < nFeatures; ++feature) {
				features[sample][feature] = file.nextDouble();
			}
		}
		
		try {
			file.close();
		} catch (Exception exc) {
			System.err.println(exc.toString());
		}
	}
	
	private static void read_labels() {
		Scanner file = null;
		try {
			file = new Scanner(new File(labels_fn));
		} catch (IOException exc) {
			System.err.println(exc.toString());
			return;
		}
		
		nSamples = file.nextInt();
		
		labels = new double [nSamples];
		
		for (int sample = 0; sample < nSamples; ++sample) {
			labels[sample] = file.nextDouble();
		}
		
		try {
			file.close();
		} catch (Exception exc) {
			System.err.println(exc.toString());
		}
	}
	
	private static void separation() {
		// Indexing
		nPositives = 0;
		nNegatives = 0;
		
		negative = new int [nSamples];
		positive = new int [nSamples];
		
		for (int sample = 0; sample < nSamples; ++sample) {
			if (labels[sample] < 1.0) {
				negative[nNegatives] = sample;
				++nNegatives;
			} else {
				positive[nPositives] = sample;
				++nPositives;
			}
		}
		
		// Randomization the negative index
		/*
		for (int i = 0; i < nNegatives; ++i) {
			int j = Math.abs(rand.nextInt()) % nNegatives;
			int temp = negative[i];
			negative[i] = negative[j];
			negative[j] = temp;
		}
		*/
		
		// Randomization the positive index
		/*
		for (int i = 0; i < nPositives; ++i) {
			int j = Math.abs(rand.nextInt()) % nPositives;
			int temp = positive[i];
			positive[i] = positive[j];
			positive[j] = temp;
		}
		*/
		
		// k-folds separation
		int k_negative;
		if (nNegatives % k == 0) {
			k_negative = nNegatives / k;
		} else {
			k_negative = nNegatives / k + 1;
		}
		
		int k_positive;
		if (nPositives % k == 0) {
			k_positive = nPositives / k;
		} else {
			k_positive = nPositives / k + 1;
		}
		
		int left_negative[] = new int [k];
		int right_negative[] = new int [k];
		int left_positive[] = new int [k];
		int right_positive[] = new int [k];
		
		for (int part = 0; part < k; ++part) {
			System.out.println("Part " + Integer.toString(part + 1) + ":");
			
			left_negative[part] = part * k_negative;
			right_negative[part] = Math.min((part + 1) * k_negative, nNegatives);
			
			left_positive[part] = part * k_positive;
			right_positive[part] = Math.min((part + 1) * k_positive, nPositives);
			
			System.out.println("- Negative: " + Integer.toString(left_negative[part]) + " - " + Integer.toString(right_negative[part]));
			System.out.println("- Positive: " + Integer.toString(left_positive[part]) + " - " + Integer.toString(right_positive[part]));
		}
		
		for (int part = 0; part < k; ++part) {
			// +-------------+
			// | For Testing |
			// +-------------+
			
			int nTesting = (right_negative[part] - left_negative[part]) + (right_positive[part] - left_positive[part]);
			
			String features_testing_fn = "features-testing-part-" + Integer.toString(part + 1) + ".dat";
			String labels_testing_fn = "labels-testing-part-" + Integer.toString(part + 1) + ".dat";
			PrintWriter features_testing = null;
			try {
				features_testing = new PrintWriter(new FileWriter(features_testing_fn));
			} catch (IOException exc) {
				System.err.println(exc.toString());
				return;
			}
			
			PrintWriter labels_testing = null;
			try {
				labels_testing = new PrintWriter(new FileWriter(labels_testing_fn));
			} catch (IOException exc) {
				System.err.println(exc.toString());
				return;
			}
			
			features_testing.println(Integer.toString(nTesting) + " " + Integer.toString(nFeatures));
			labels_testing.println(Integer.toString(nTesting));
			
			for (int j = left_positive[part]; j < right_positive[part]; ++j) {
				int index = positive[j];
				for (int v = 0; v < nFeatures; ++v) {
					features_testing.print(Double.toString(features[index][v]) + " ");
				}
				features_testing.println();
				labels_testing.println(labels[index]);
			}
			
			for (int j = left_negative[part]; j < right_negative[part]; ++j) {
				int index = negative[j];
				for (int v = 0; v < nFeatures; ++v) {
					features_testing.print(Double.toString(features[index][v]) + " ");
				}
				features_testing.println();
				labels_testing.println(labels[index]);
			}
			
			try {
				features_testing.close();
			} catch (Exception exc) {
				System.err.println(exc.toString());
				return;
			}

			try {
				labels_testing.close();
			} catch (Exception exc) {
				System.err.println(exc.toString());
				return;
			}
			
			// +--------------+
			// | For Training |
			// +--------------+
			
			int nTraining = nSamples - nTesting;
			
			String features_training_fn = "features-training-part-" + Integer.toString(part + 1) + ".dat";
			String labels_training_fn = "labels-training-part-" + Integer.toString(part + 1) + ".dat";
			PrintWriter features_training = null;
			try {
				features_training = new PrintWriter(new FileWriter(features_training_fn));
			} catch (IOException exc) {
				System.err.println(exc.toString());
				return;
			}
			
			PrintWriter labels_training = null;
			try {
				labels_training = new PrintWriter(new FileWriter(labels_training_fn));
			} catch (IOException exc) {
				System.err.println(exc.toString());
				return;
			}
			
			features_training.println(Integer.toString(nTraining) + " " + Integer.toString(nFeatures));
			labels_training.println(Integer.toString(nTraining));
			
			for (int i = 0; i < k; ++i) {
				if (i != part) {
					for (int j = left_positive[i]; j < right_positive[i]; ++j) {
						int index = positive[j];
						for (int v = 0; v < nFeatures; ++v) {
							features_training.print(Double.toString(features[index][v]) + " ");
						}
						features_training.println();
						labels_training.println(labels[index]);
					}
				
					for (int j = left_negative[i]; j < right_negative[i]; ++j) {
						int index = negative[j];
						for (int v = 0; v < nFeatures; ++v) {
							features_training.print(Double.toString(features[index][v]) + " ");
						}
						features_training.println();
						labels_training.println(labels[index]);
					}
				}
			}
			
			try {
				features_training.close();
			} catch (Exception exc) {
				System.err.println(exc.toString());
				return;
			}

			try {
				labels_training.close();
			} catch (Exception exc) {
				System.err.println(exc.toString());
				return;
			}
		}
	}
	
	// Main Program
	public static void main(String args[]) {
		read_features();
		read_labels();
		separation();
	}	
	
}
