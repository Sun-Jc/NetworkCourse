# NetworkCourse

# Procedures

1. $ python3 filter.py edges.csv 2000 

    get edges_2000.csv
    
2. $ java -jar merge.jar edges_2000.csv nodes.csv

3. $ python3 csv2edges_undirected.py mergedEdges.csv

   $ python3 csv2nodes.py mergedNodes.csv
   
   get mergedNodes.csv.txt and  mergedEdges.csv.txt
   
4. spark-shell: dblp-script.scala
   
   or
   
   spark-submit: xxxxx.jar
   
   get dblpRank.txt
   
5. draw histgram of degree distribution
 
  $python3 degreeHist.py dblpDegrees.txt #bins
   

# Analysis

Working functions: 

1. diameter and nodes of each connect component

2. average shortest path, excluding infinities

3. pageRank of each node

4. degree distribution


TODOs:

5. clusting coefficient