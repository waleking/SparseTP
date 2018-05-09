import numpy as np
import scipy.sparse as sparse
from sklearn.metrics.pairwise import cosine_similarity
import networkx as nx
import matplotlib.pyplot as plt
import sys
import subprocess

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
K=int(topicNumber)
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
    return g


def loadTopics():
    d=dict()
    topicid=""
    content=[]
    with open("../result/"+filename+"_K"+str(K)+"_iteration1000.txt") as f:
        for line in f:
            line=line.strip()
            tmptopicid,_,tmpcontent=line.split("\t")
            topicid=int(tmptopicid.split("topic")[1].split(":")[0])
            content=[]
            for phrase in tmpcontent.split(",")[0:-1]:
                content.append(phrase)
            d[topicid]=content

    print(d)
    print("\n\n")
    return d


def getTopicDescription(dTopics,k,numTopWords):
    words=dTopics[k]
    description="topic"+str(k)+"\\n"
    for i in range(0,numTopWords):
        description=description+words[i]+"\\n"
    return description.strip()


def runcommand(command):
    p = subprocess.Popen(command.split(" "), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    print(out)

def outputGraphvizFile(dTopics,lower_weight):
    g=draw_correlation_graph(lower_weight)
    gvFilename="../result/"+filename+"_graphviz_"+str(lower_weight)+".gv"
    pdfFilename="../result/"+filename+"_graphviz_"+str(lower_weight)+".pdf"
    with open(gvFilename,"w") as gvWriter:
        gvWriter.write("graph {\n")
        gvWriter.write("ratio=auto;\n")
        for k in range(0,K):
            description=getTopicDescription(dTopics,k,numTopWords=5)
            gvWriter.write("\t%s [label=\"%s\"];\n" % (k,description))

        for edge in g.edges():
            source,target=edge
            gvWriter.write("\t%s -- %s;\n" % (source,target))
        gvWriter.write("}\n")

    runcommand("dot -Tpdf "+gvFilename+" -o "+pdfFilename)


dTopics=loadTopics()
outputGraphvizFile(dTopics,0.1)
outputGraphvizFile(dTopics,0.2)
outputGraphvizFile(dTopics,0.3)
