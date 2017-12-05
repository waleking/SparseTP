import os
import cPickle as pkl
import re
import math
import json

def get_inverted_index():
    # index_file = '/Users/huangwaleking/git/topicxonomy/data_invertIndex/index.pkl'
    index_file = '/Users/huangwaleking/git/sparseTP/input/20newsgroups.txt.pkl'
    result = dict()

    # if index file exists, return it.
    if os.path.isfile(index_file):
        with open(index_file, 'rb') as f:
            print 'load index from ', index_file
            return pkl.load(f)


def mpi_evaluate(doc_index, topk=10):
    topic_files=[('topicToTest',0),('topicToTest',1),
                 ('topicToTest',2),('topicToTest',3),
                 ('topicToTest',4),('topicToTest',5),
                 ('topicToTest',6),('topicToTest',7),
                 ('topicToTest',8),('topicToTest',9),
                 ('topicToTest',10)]


    doc_num = doc_index['doc_num'] * 1.0
    index = doc_index['index']
    topic_num = 1
    smooth_factor = 1#todo
    # smooth_factor = 0.001
    result = list()
    for topic_file, start in topic_files:
        pair_num = 0
        mpi_sum = 0.0
        with open(topic_file, 'r') as f:
            lines = f.readlines()
            for line in lines[start:start + topic_num]:
                if(":" in line):
                    topic, phrases = line.split(':')
                else:
                    phrases=line

                phrases, num = re.subn(r'\([0-9]+\.?[0-9]*\)', '', phrases.strip())  # remove frequency number
                # phrases = phrases.split('\t')
                phrases=phrases.split(",")
                phrases=[phrase.strip() for phrase in phrases]
                end = min(topk, len(phrases))
                for p1 in range(0, end):
                    phrase1 = phrases[p1]
                    if(phrase1 in index):
                        doc_id_set1 = set(index[phrase1])
                    else:
                        doc_id_set1 = set()
                    freq1 = len(doc_id_set1) + smooth_factor
                    for p2 in range(p1 + 1, end):
                        phrase2 = phrases[p2]
                        pair_num += 1
                        if(phrase2 in index):
                            doc_id_set2 = set(index[phrase2])
                        else:
                            doc_id_set2 = set()
                        freq2 = len(doc_id_set2) + smooth_factor
                        freq3 = len(doc_id_set1 & doc_id_set2) + smooth_factor
                        #todo
                        # if(freq1==0 or freq2==0 or freq3==0):
                        #     mpi_sum += 0
                        # else:
                        mpi_sum += math.log(freq3 * doc_num / (freq1 * freq2))
                        # print("phrase1="+phrase1+",phrase2="+phrase2+",pmi="+str(math.log(freq3 * doc_num / (freq1 * freq2))))
        print topic_file, mpi_sum, pair_num
        result.append((topic_file, mpi_sum / pair_num))
    return result


if __name__ == '__main__':
    doc_index = get_inverted_index()
    # print(doc_index['doc_num'])
    # print(doc_index['index']['the museum'])
    print mpi_evaluate(doc_index)

