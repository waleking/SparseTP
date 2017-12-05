import json
import sys
import cPickle as pkl
import os
import re
import math


def build_inverted_index(inputfile_name):
    index_file = inputfile_name+".pkl"
    result = dict()

    #create and save it, then return it.
    index = dict()

    doc_id = 1
    with open(inputfile_name, 'r') as f:
        for line in f:
            phrases = line.split(',')
            for phrase in phrases:
                if phrase not in index:
                    index[phrase] = set()
                index[phrase].add(doc_id)
            if(doc_id%1000==0):
                print("processed %d docs" % doc_id)
            doc_id += 1

    result['doc_num'] = doc_id - 1
    result['index'] = index
    index_directory = os.path.dirname(os.path.abspath(index_file))
    if not os.path.exists(index_directory):
        os.makedirs(index_directory)

    with open(index_file, 'wb') as f:
        pkl.dump(result, f, pkl.HIGHEST_PROTOCOL)
        print 'index saved to', index_file
    return result

if __name__ == '__main__':
    inputfile_name="../"+sys.argv[1]
    doc_index = build_inverted_index(inputfile_name)
