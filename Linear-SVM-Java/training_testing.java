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
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;

public class training_testing {
	
	// Constants
	static private final int k = 5;
	static private final String data_folder = "../data-processing/";
	static private final String model_fn = "model.dat";
	
	// Liblinear constants
	static private final int nOutput = 2;
	static private final double C = 100.0;
	static private final double Epsilon = 1e-2;
	
	// Memory 
	static private int nSamples, nTraining, nTesting, nFeatures;
	static private double features_training[][];
	static private double features_testing[][];
	static private double labels_training[];
	static private double labels_testing[];
	
	// For checking the performance
	static private int nCorrects;
	static private int nNegatives;
	static private int nCorrectNegatives;
	static private int nPositives;
	static private int nCorrectPositives;
	
	// Randomization process
	static private Random rand;
	
	// Supporting functions
	private static void load_data(String features_training_fn, String labels_training_fn, String features_testing_fn, String labels_testing_fn) throws IOException {
		Scanner file;
		
		// Features for Training
		System.out.println("Training features file: " + features_training_fn);
		file = new Scanner(new File(features_training_fn));
		nTraining = file.nextInt();
		nFeatures = file.nextInt();
		features_training = new double [nTraining][nFeatures];
		for (int sample = 0; sample < nTraining; ++sample) {
			for (int feature = 0; feature < nFeatures; ++feature) {
				features_training[sample][feature] = file.nextDouble();
			}
		}
		file.close();
		
		// Labels for Training
		System.out.println("Training labels file: " + labels_training_fn);
		file = new Scanner(new File(labels_training_fn));
		nTraining = file.nextInt();
		labels_training = new double [nTraining];
		for (int sample = 0; sample < nTraining; ++sample) {
			labels_training[sample] = file.nextDouble();
		}
		file.close();
		
		// Features for Testing
		System.out.println("Testing features file: " + features_testing_fn);
		file = new Scanner(new File(features_testing_fn));
		nTesting = file.nextInt();
		nFeatures = file.nextInt();
		features_testing = new double [nTesting][nFeatures];
		for (int sample = 0; sample < nTesting; ++sample) {
			for (int feature = 0; feature < nFeatures; ++feature) {
				features_testing[sample][feature] = file.nextDouble();
			}
		}
		file.close();
		
		// Labels for Testing
		System.out.println("Testing labels file: " + labels_testing_fn);
		file = new Scanner(new File(labels_testing_fn));
		nTesting = file.nextInt();
		labels_testing = new double [nTesting];
		for (int sample = 0; sample < nTesting; ++sample) {
			labels_testing[sample] = file.nextDouble();
		}
		file.close();
	}
	
	private static void mixing_order_samples() {
		rand = new Random();
		for (int i = 0; i < nTraining; ++i) {
			int j = Math.abs(rand.nextInt()) % nTraining;
			
			for (int v = 0; v < nFeatures; ++v) {
				double temp = features_training[i][v];
				features_training[i][v] = features_training[j][v];
				features_training[j][v] = temp;
			}
			
			double temp = labels_training[i];
			labels_training[i] = labels_training[j];
			labels_training[j] = temp;
		}
	}
	
	private static void normalization() {
		for (int feature = 0; feature < nFeatures; ++feature) {
			double MIN = features_training[0][feature];
			double MAX = features_training[0][feature];
			for (int sample = 1; sample < nTraining; ++sample) {
				MIN = Math.min(MIN, features_training[sample][feature]);
				MAX = Math.max(MAX, features_training[sample][feature]);
			}
			for (int sample = 0; sample < nTesting; ++sample) {
				MIN = Math.min(MIN, features_testing[sample][feature]);
				MAX = Math.max(MAX, features_testing[sample][feature]);
			}
			double range = MAX - MIN;
			for (int sample = 0; sample < nTraining; ++sample) {
				features_training[sample][feature] = (features_training[sample][feature] - MIN) / range;
			}
			for (int sample = 0; sample < nTesting; ++sample) {
				features_testing[sample][feature] = (features_testing[sample][feature] - MIN) / range;
			}
		}
	}
	
	private static void feature_extraction(Feature feature[], double arr[]){
		feature[0] = new FeatureNode(1, 1);
		for (int i = 0; i < nFeatures; ++i) {
			feature[i + 1] = new FeatureNode(i + 2, arr[i]); 
		}
	}
	
	private static void training() {
		Problem problem = new Problem();
		problem.l = nTraining;
		problem.n = nFeatures + 1;
		problem.x = new FeatureNode [nTraining][nFeatures + 1];
		problem.y = new double [nTraining];
		
		int count = 0;
		for (int sample = 0; sample < nTraining; ++sample) {
			if (labels_training[sample] == 0.0) {
				feature_extraction(problem.x[count], features_training[sample]);
				problem.y[count] = -1.0;
				++count;
			}
		}
		
		for (int sample = 0; sample < nTraining; ++sample) {
			if (labels_training[sample] > 0.0) {
				feature_extraction(problem.x[count], features_training[sample]);
				problem.y[count] = 1.0;
				++count;
			}
		}
		
		SolverType solver = SolverType.L2R_LR;
		Parameter parameter = new Parameter(solver, C, Epsilon);
		Model model = Linear.train(problem, parameter);
		
		try {
			model.save(new File(model_fn));
		} catch (IOException exc) {
			System.err.println(exc.toString());
		}
	}
	
	private static void testing() {		
		Model model = null;
		try {
			model = Model.load(new File(model_fn));
		} catch (IOException exc) {
			System.err.println(exc.toString());
			return;
		}
	
		nSamples += nTesting;

		for (int sample = 0; sample < nTesting; ++sample) {
			System.out.print("Testing sample " + Integer.toString(sample) + ": ");
			
			Feature[] instance = new FeatureNode [nFeatures + 1];
			feature_extraction(instance, features_testing[sample]);
			
			double predict = Linear.predict(model, instance);
			
			double label = -1.0;
			if (labels_testing[sample] > 0.0) {
				label = 1.0;
			}
			
			if (predict == label) {
				++nCorrects;
				if (label < 0.0) {
					++nCorrectNegatives;
					++nNegatives;
				} else {
					++nCorrectPositives;
					++nPositives;
				}
				System.out.println("YES");
			} else {
				if (label < 0.0) {
					++nNegatives;
				} else {
					++nPositives;
				}
				System.out.println("NO");
			}
		}
	}

	private static void summary() {
		System.out.println("Number of correct negative samples: " + Integer.toString(nCorrectNegatives));
		System.out.println("Number of negative samples: " + Integer.toString(nNegatives));
		System.out.println("Number of correct positive samples: " + Integer.toString(nCorrectPositives));
		System.out.println("Number of positive samples: " + Integer.toString(nPositives));
	
		double accuracy = (double)(nCorrects) / (double)(nSamples) * 100.0;
		System.out.println("Number of correct samples: " + Integer.toString(nCorrects));
		System.out.println("Number of samples: " + Integer.toString(nSamples));
		System.out.println("Accuracy: " + Double.toString(accuracy));
		
		double tp = nCorrectPositives;
		double fp = nPositives - nCorrectPositives;
		double fn = nNegatives - nCorrectNegatives;
		double precision = tp / (tp + fp);
		double recall = tp / (tp + fn);
		double F1 = 2.0 * (precision * recall) / (precision + recall);
		
		System.out.println("F1 score: " + Double.toString(F1));
	}
	
	// Main Program
	public static void main(String args[]) {
		nSamples = 0;
		nCorrects = 0;
		nNegatives = 0;
		nCorrectNegatives = 0;
		nPositives = 0;
		nCorrectPositives = 0;
	
		for (int part = 1; part <= k; ++part) {
			System.out.println("Fold " + Integer.toString(part) + ":");
			
			String features_training_fn = data_folder + "features-training-part-" + Integer.toString(part) + ".dat"; 
			String features_testing_fn = data_folder + "features-testing-part-" + Integer.toString(part) + ".dat"; 
			String labels_training_fn = data_folder + "labels-training-part-" + Integer.toString(part) + ".dat"; 
			String labels_testing_fn = data_folder + "labels-testing-part-" + Integer.toString(part) + ".dat"; 
			
			try {
				load_data(features_training_fn, labels_training_fn, features_testing_fn, labels_testing_fn);
			} catch (IOException exc) {
				System.err.println(exc.toString());
				return;
			}
			
			mixing_order_samples();
			normalization();
			training();
			testing();
		}
		
		summary();
	}	
	
}
