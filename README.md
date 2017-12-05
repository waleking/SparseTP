# SparseTP: Efficient Topic Modeling on Phrases via Sparsity

## Publication
This work is published in ICTAI 2017. If you are using this tool, please cite our paper:  
*  Efficient Topic Modeling on Phrases via Sparsity, Weijing Huang, Wei Chen, Tengjiao Wang and Shibo Tao, Proceedings of the 29th IEEE International Conference on Tools with Artifical Intelligence (ICTAI'17), Boston, USA, Nov 2017. ([slides](https://github.com/waleking/SparseTP/blob/master/ICTAI_presentation.pdf))

## Running Examples
1.The running example on the dataset [20newsgroups](http://qwone.com/~jason/20Newsgroups/)
```
bash runningExample1.sh
```

| topic 0 | graph theory(743.0),undirected graph(299.0),directed graph(279.0),planar graph(262.0),complete graph(251.0),planar graphs(249.0),chromatic number(200.0),bipartite graph(153.0),independent set(143.0),hamiltonian cycle(127.0)                                                               |
|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| topic 1 | harvard university(262.0),princeton university(240.0),stanford university(201.0),royal society(200.0),national academy(191.0),american mathematician(190.0),electrical engineering(169.0),united states(157.0),applied mathematics(156.0),american mathematical society(153.0)                |
| topic 2 | general relativity(1083.0),special relativity(697.0),gravitational field(254.0),reference frame(204.0),lorentz transformation(176.0),time dilation(167.0),inertial frame(156.0),equivalence principle(137.0),lorentz transformations(135.0),metric tensor(134.0)                              |
| topic 3 | quantum mechanics(1136.0),quantum field theory(360.0),quantum information(268.0),wave function(256.0),quantum theory(236.0),hilbert space(177.0),quantum mechanical(173.0),quantum state(162.0),particle physics(160.0),quantum computing(137.0)                                              |
| topic 4 | probability distribution(641.0),random variables(573.0),random variable(559.0),normal distribution(505.0),probability theory(320.0),probability distributions(276.0),poisson distribution(157.0),probability density function(154.0),central limit theorem(145.0),uniform distribution(140.0) |
| topic 5 | dependent variable(230.0),linear regression(228.0),data set(208.0),data points(182.0),standard deviation(180.0),independent variables(173.0),maximum likelihood(171.0),regression analysis(154.0),independent variable(144.0),null hypothesis(121.0)                                          |
| topic 6 | large number(429.0),recent years(154.0),large numbers(150.0),high degree(140.0),important role(122.0),large scale(120.0),takes place(120.0),higher level(87.0),high level(86.0),long term(83.0)                                                                                               |
| topic 7 | special case(458.0),vice versa(375.0),closely related(210.0),starting point(155.0),general case(135.0),sufficient condition(112.0),special cases(108.0),previous section(103.0),makes sense(103.0),large number(91.0)                                                                         |
| topic 8 | topological space(211.0),vector field(158.0),algebraic topology(122.0),simply connected(121.0),banach space(118.0),euclidean space(118.0),algebraic geometry(117.0),riemannian manifold(113.0),open set(109.0),metric space(108.0)                                                            |
| topic 9 | complex numbers(189.0),real numbers(151.0),power series(144.0),complex plane(139.0),trigonometric functions(109.0),complex number(104.0),taylor series(96.0),rational functions(95.0),special case(94.0),rational function(91.0)                                                              |

2.The running example on the Wikipedia articles under the [Mathematics category](https://en.wikipedia.org/wiki/Category:Mathematics).
```
bash runningExample2.sh
```

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

