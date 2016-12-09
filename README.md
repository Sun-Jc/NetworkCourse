# NetworkCourse

# Procedures(new)
python3 edges.csv  2000 =====>  evolving/2000/edges.csv

java -jar merge.jar evolving/2000/edges.csv nodes.csv evolving/2000/mergedEdges.csv evolving/2000/mergedNodes.csv =====> evolving/2000/mergedEdges.csv, evolving/2000/mergedNodes.csv

python3 csv2edges_undirected.py evolving/2000/mergedEdges.csv evolving/2000/mergedEdges.txt =======>  evolving/2000/mergedEdges.txt

python3 csv2nodes.py evolving/2000/mergedNodes.csv evolving/2000/mergedNodes.txt  =========>   evolving/2000/mergedNodes.txt

python3 degreeHist.py evolving/2000/dblpDegrees.txt #bins evolving/2000/dblpDegrees.png

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
   
   (clean: ./clean.sh)
   
5. draw histgram of degree distribution
 
  $python3 degreeHist.py dblpDegrees.txt #bins
   

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

Evolving analysis.