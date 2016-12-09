# NetworkCourse

# Procedures(evolving network)

0. copy every file of "data-processing-script" folder into the same folder with edges.csv and nodes.csv

make sure your "$wc -l edges.csv " reveals ~15000000 lines

1. $python3 1.run.py

every network generated inside evolving folder

2. spark-submit: xxxxx.jar

get network-analysis results

3. $python3 3.run-degreeeHist.py 200

get distribution of degrees

4. if you need to clean-up the network data, run $

# Procedures(one network)

1. $ python3 -f edges.csv -y 2000 =====>  evolving/2000/edges.csv

2. $ java -jar merge.jar evolving/2000/edges.csv nodes.csv evolving/2000/mergedEdges.csv evolving/2000/mergedNodes.csv =====> evolving/2000/mergedEdges.csv, evolving/2000/mergedNodes.csv

3. $ python3 csv2edges_undirected.py evolving/2000/mergedEdges.csv evolving/2000/mergedEdges.txt =======>  evolving/2000/mergedEdges.txt

$ python3 csv2nodes.py evolving/2000/mergedNodes.csv evolving/2000/mergedNodes.txt  =========>   evolving/2000/mergedNodes.txt

4. spark-shell: dblp-script.scala
   
   or
   
   spark-submit: xxxxx.jar
   
   get dblpRank.txt
   
5. draw histgram of degree distribution
 
  $python3 degreeHist.py evolving/2000/dblpDegrees.txt #bins evolving/2000/dblpDegrees.png
     

# Analysis

Working functions: 

1. diameter and nodes of each connect component

2. average shortest path, excluding infinities

3. pageRank of each node

4. degree distribution

5. clustering coefficient

6. assortative coefficient

streamlined and cached

TODOs:

Results