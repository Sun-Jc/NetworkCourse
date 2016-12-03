# NetworkCourse

1. $ python3 filter.py edges.csv 2000
    get edges_2000.csv
    
2. $ java -jar merge.jar edges_2000.csv nodes.csv

3. $ python3 csv2edges_undirected.py mergedEdges.csv
   $ python3 csv2nodes.py mergedNodes.csv
   
   get mergedNodes.csv.txt and  mergedEdges.csv
   
4. spark-shell: dblp-script.scala
   or
   spark-submit: xxxxx.jar
