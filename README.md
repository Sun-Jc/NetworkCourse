# NetworkCourse

# Procedures(evolving network)
0) copy every file of "data-processing-script" folder into the same folder with edges.csv and nodes.csv

make sure your "$wc -l edges.csv " reveals ~15000000 lines

edit config.py and input the wanted years and executing cores

1) $python3 1.run.py

every network generated inside evolving folder

2) spark-submit: xxxxx.jar

get network-analysis results

3) $python3 3.run-hist.py 200

get distribution of both degrees and pageranks

4) spark-shell with "dblp-neighborhood.scala"

get some($who)'s k-hop neighborhood: "neighborhoodEdgesXXX.csv" and "neighborhoodNoedsXXX.csv", which can be used by Gephi

5) $./4.4.run-collect_hist.sh

get figure along years

collect_dblpRanks.txt_avg.csv.png
collect_dblpDegrees.txt_avg.csv.png
collect_dblpAsscociativeCoefficien.txt.csv.png
collect_dblpAvgClusterCoeffient.txt.csv.png

and top ranks

collect_dblpRanks.txt_topx.csv

# Procedures(one network)

1. $ python3 -f edges.csv -y 2000 =====>  evolving/2000/edges.csv

2. $ java -jar merge.jar evolving/2000/edges.csv nodes.csv evolving/2000/mergedEdges.csv evolving/2000/mergedNodes.csv =====> evolving/2000/mergedEdges.csv, evolving/2000/mergedNodes.csv

3. $ python3 csv2edges_undirected.py evolving/2000/mergedEdges.csv evolving/2000/mergedEdges.txt =======>  evolving/2000/mergedEdges.txt

$ python3 csv2nodes.py evolving/2000/mergedNodes.csv evolving/2000/mergedNodes.txt  =========>   evolving/2000/mergedNodes.txt

4. spark-shell: dblp-script.scala
   
   or
   
   $ ./bin/spark-submit simple-project_2.11-1.0.jar &
   
   get dblpRank.txt
   
5. draw histgram of degree distribution
 
  $python3 degreeHist.py evolving/2000/dblpDegrees.txt #bins evolving/2000/dblpDegrees.png
     

# Analysis

Working functions: 

%1. diameter and nodes of each connect component

%2. average shortest path, excluding infinities

3. pageRank of each node

4. degree distribution

5. clustering coefficient

6. assortative coefficient

streamlined and cached

# initial parsing

put "dblp.xml", "dblp.dtd", "parse.jar" same folder, and then run:

$java -jar parse.jar dblp.xml 
