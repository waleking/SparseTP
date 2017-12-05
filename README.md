# SparseTP: Efficient Topic Modeling on Phrases via Sparsity

## Publications
This work is published in ICTAI 2017. If you are using this tool, please cite our paper:  
*  Efficient Topic Modeling on Phrases via Sparsity, Weijing Huang, Wei Chen, Tengjiao Wang and Shibo Tao, Proceedings of the 29th IEEE International Conference on Tools with Artifical Intelligence (ICTAI'17), Boston, USA, Nov 2017. ([slides](https://github.com/waleking/SparseTP/blob/master/ICTAI_presentation.pdf))

## Usage
It has been tested on MacOS 10.13 (High Sierra), and Debian GNU/Linux 8。
There are three steps to get the phrase topics on a given corpus.
  
1.Get data prepared.
`TODO`

2.Run the tool. 

Directly use the following command. `${inputfile}` is the file got in the step 1, e.g., input/20newsgroups.txt; `${TopicNumber}` is the number of topics, e.g., 100; `${IterationNumber}` is the iteration number, e.g., 1000; `${NumberOfTopPhrasesToShow}` is the number of phraes in each topic to show in result/ folder, e.g., 10.
```      
bash run.sh ${inputfile} ${TopicNumber} ${IterationNumber} ${NumberOfTopPhrasesToShow}
```

3.Check the result.

Visit `result/` folder, and get the final output in result/inputfile_K${TopicNumber}_iteration${IterationNumber}.txt

## Running Example
1.The running example on the dataset `[20newsgroups](http://qwone.com/~jason/20Newsgroups/)`
```
bash runningExample1.sh
```

2.The running example on the Wikipedia articles under the [Mathematics category](https://en.wikipedia.org/wiki/Category:Mathematics).
```
bash runningExample2.sh
```

