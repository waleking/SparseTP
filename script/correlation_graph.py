import numpy as np
import scipy.sparse as sparse
from sklearn.metrics.pairwise import cosine_similarity
import networkx as nx
import matplotlib.pyplot as plt
import sys

filename=sys.argv[1]
topicNumber=sys.argv[2]


def fTurnToArray(line,K):
    splitted=line.split("\t")
    l=[0]*K
    for e in splitted:
        topic=int(e)
        l[topic]+=1
    return l

i=0
A=[]
K=100
with open("../result/"+filename+"_K"+topicNumber+"_iteration1000.assignments","r") as f:
    for line in f:
        line=line.strip()
        if(line!=""):
            array=fTurnToArray(line,K)
            A.append(array)
        i=i+1

document_topic_mat=np.array(A)
topic_document_mat=document_topic_mat.transpose()

B_sparse = sparse.csr_matrix(topic_document_mat) #B_sparse is a K*D matrix

similarities = cosine_similarity(B_sparse)

print('pairwise dense output:\n {}\n'.format(similarities))

K=len(similarities)

def draw_correlation_graph(lower_weight):
    g=nx.Graph()
    for i in range(K):
        for j in range(i+1,K):
            if(similarities[i][j]>lower_weight):
                g.add_edges_from([(i,j)])

    pos = nx.spring_layout(g)

    fig=plt.figure(figsize=(20,20))
    nx.draw_networkx_nodes(g, pos, cmap=plt.get_cmap('jet'),
                        node_size = 500)
    nx.draw_networkx_labels(g, pos)
    nx.draw_networkx_edges(g, pos)
    #plt.show()

    fig.savefig("../result/"+filename+"_correlation_graph_"+str(lower_weight)+".pdf",
                bbox_inches='tight')

draw_correlation_graph(0.1)
draw_correlation_graph(0.2)
draw_correlation_graph(0.3)
