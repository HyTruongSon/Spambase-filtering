% Software: Spambase filtering
% Database Name: UCI Spambase Data Set
% Datbase Link: https://archive.ics.uci.edu/ml/datasets/Spambase
% Author: Hy Truong Son
% Position: PhD Student
% Institution: Department of Computer Science, The University of Chicago
% Email: sonpascal93@gmail.com, hytruongson@uchicago.edu
% Website: http://people.inf.elte.hu/hytruongson/
% Copyright 2016 (c) Hy Truong Son. All rights reserved.

function [] = make_dictionary()
    % Constants
    input_fn = 'dictionary.dat';
    output_fn = 'dictionary.mat';
    
    % Reading
    fid = fopen(input_fn, 'r');
    nWords = str2num(fgets(fid));
    dictionary = [];
    for i = 1 : nWords
        word = fgets(fid);
        word = word(1, 1 : size(word, 2) - 1);
        dictionary{i, 1} = word;
    end
    
    % Save to mat file
    save(output_fn, 'dictionary');
end