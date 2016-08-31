% Software: Spambase filtering
% Database Name: UCI Spambase Data Set
% Datbase Link: https://archive.ics.uci.edu/ml/datasets/Spambase
% Author: Hy Truong Son
% Position: PhD Student
% Institution: Department of Computer Science, The University of Chicago
% Email: sonpascal93@gmail.com, hytruongson@uchicago.edu
% Website: http://people.inf.elte.hu/hytruongson/
% Copyright 2016 (c) Hy Truong Son. All rights reserved.

function [] = training_testing()
    % Constants
    k = 5;
    data_folder = '../data-processing/';
    libsvm_folder = './liblinear-1.94/matlab';
    model_fn = 'model.mat';
    
    % Main Program
    addpath(genpath(data_folder));
    addpath(genpath(libsvm_folder));
    
    nCorrects = 0;
    nSamples = 0;
    nCorrectNegatives = 0;
    nCorrectPositives = 0;
    nNegatives = 0;
    nPositives = 0;
    
    for part = 1 : k
        fprintf('Part %d\n', part);
        fprintf('Reading data\n');
        
        features_training_fn = [data_folder, 'features-training-part-', num2str(part), '.mat'];
        load(features_training_fn, 'features_training');
        
        labels_training_fn = [data_folder, 'labels-training-part-', num2str(part), '.mat'];
        load(labels_training_fn, 'labels_training');
        
        features_testing_fn = [data_folder, 'features-testing-part-', num2str(part), '.mat'];
        load(features_testing_fn, 'features_testing');
        
        labels_testing_fn = [data_folder, 'labels-testing-part-', num2str(part), '.mat'];
        load(labels_testing_fn, 'labels_testing');
        
        nTraining = size(features_training, 1);
        nTesting = size(features_testing, 1);
        nFeatures = size(features_training, 2);
        
        % Randomization the order of training samples
        for i = 1 : nTraining
            j = randi([1 nTraining]);
            
            temp = features_training(i, :);
            features_training(i, :) = features_training(j, :);
            features_training(j, :) = temp(:);
            
            temp = labels_training(i);
            labels_training(i) = labels_training(j);
            labels_training(j) = temp;
        end
        
        % Normalization
        fprintf('Normalization\n');
        for i = 1 : nFeatures
            MIN_training = min(features_training(:, i));
            MIN_testing = min(features_testing(:, i));
            MIN = min(MIN_training, MIN_testing);
            
            MAX_training = max(features_training(:, i));
            MAX_testing = max(features_testing(:, i));
            MAX = max(MAX_training, MAX_testing);
            
            range = MAX - MIN;
            features_training(:, i) = (features_training(:, i) - MIN) / range;
            features_testing(:, i) = (features_testing(:, i) - MIN) / range;
        end
        
        % Train the SVM - Linear kernel
        labels_training(labels_training > 0) = 1;
        labels_training(labels_training == 0) = -1;
        
        labels_testing(labels_testing > 0) = 1;
        labels_testing(labels_testing == 0) = -1;
        
        fprintf('Linear SVM training\n');
        model = train(labels_training, features_training, '-c 10.0');
        save(model_fn, 'model');
        
        % Prediction
        load(model_fn, 'model');
        fprintf('Linear SVM testing\n');
        
        nSamples = nSamples + nTesting;
        for sample = 1 : nTesting
            fprintf('Sample %d: ', sample);
            
            label = labels_testing(sample, 1);
            feature = features_testing(sample, :);
            [predict] = predict(label, feature, model);
            
            if label == predict
                fprintf('YES\n');
                nCorrects = nCorrects + 1;
                if label == 1
                    nCorrectPositives = nCorrectPositives + 1;
                else
                    nCorrectNegatives = nCorrectNegatives + 1;
                end
            else
                fprintf('NO\n');
            end
            
            if label == 1
                nPositives = nPositives + 1;
            else
                nNegatives = nNegatives + 1;
            end
        end
    end
    
    fprintf('Number of correct negative samples: %d\n', nCorrectNegatives);
    fprintf('Number of negative samples: %d\n', nNegatives);
    fprintf('Number of correct positive samples: %d\n', nCorrectPositives);
    fprintf('Number of positive samples: %d\n', nPositives);
    
    accuracy = double(nCorrects) / double(nSamples) * 100.0;
    fprintf('Number of correct samples: %d\n', nCorrects);
    fprintf('Number of samples: %d\n', nSamples);
    fprintf('Accuracy: %.6f\n', accuracy);
    
    tp = nCorrectPositives;
	fp = nPositives - nCorrectPositives;
	fn = nNegatives - nCorrectNegatives;
	precision = tp / (tp + fp);
	recall = tp / (tp + fn);
	F1 = 2.0 * (precision * recall) / (precision + recall);
		
	fprintf('F1 score: %.6f\n', F1);
end