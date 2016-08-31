% Software: Spambase filtering
% Database Name: UCI Spambase Data Set
% Datbase Link: https://archive.ics.uci.edu/ml/datasets/Spambase
% Author: Hy Truong Son
% Position: PhD Student
% Institution: Department of Computer Science, The University of Chicago
% Email: sonpascal93@gmail.com, hytruongson@uchicago.edu
% Website: http://people.inf.elte.hu/hytruongson/
% Copyright 2016 (c) Hy Truong Son. All rights reserved.

function [] = k_folds_separation()
    % Constants
    k = 5;
    
    % Processing
    for part = 1 : k
        features_testing = load(['features-testing-part-', num2str(part), '.dat']);
        save(['features-testing-part-', num2str(part), '.mat'], 'features_testing');
        
        labels_testing = load(['labels-testing-part-', num2str(part), '.dat']);
        save(['labels-testing-part-', num2str(part), '.mat'], 'labels_testing');
        
        features_training = load(['features-training-part-', num2str(part), '.dat']);
        save(['features-training-part-', num2str(part), '.mat'], 'features_training');
        
        labels_training = load(['labels-training-part-', num2str(part), '.dat']);
        save(['labels-training-part-', num2str(part), '.mat'], 'labels_training');
    end
    
    % Checking
    f_positive = [];
    l_positive = [];
    f_negative = [];
    l_negative = [];
    
    for part = 1 : k
        load(['features-testing-part-', num2str(part), '.mat'], 'features_testing');
        load(['labels-testing-part-', num2str(part), '.mat'], 'labels_testing');
        
        if part == 1
            f_positive = features_testing(labels_testing > 0.0, :);
            f_negative = features_testing(labels_testing == 0.0, :);
            
            l_positive = labels_testing(labels_testing > 0.0);
            l_negative = labels_testing(labels_testing == 0.0);
        else
            f_positive = [f_positive; features_testing(labels_testing > 0.0, :)];
            f_negative = [f_negative; features_testing(labels_testing == 0.0, :)];
            
            l_positive = [l_positive; labels_testing(labels_testing > 0.0)];
            l_negative = [l_negative; labels_testing(labels_testing == 0.0)];
        end
    end
    
    f = [f_positive; f_negative];
    l = [l_positive; l_negative];
    
    load('features.mat', 'features');
    load('labels.mat', 'labels');
    
    fprintf('Difference (features): %.6f\n', sum(sum(features - f)));
    fprintf('Difference (labels): %.6f\n', sum(labels - l));
end