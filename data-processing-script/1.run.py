import os
from config import config
from concurrent.futures import ProcessPoolExecutor, wait, as_completed
 
def filterAndMerge(year):
    os.system("python3 filter.py -f edges.csv -y {y}".format(y=year))
    os.system('java -jar merge.jar evolving/{y}/edges.csv nodes.csv evolving/{y}/mergedEdges.csv evolving/{y}/mergedNodes.csv'.format(y=year))
    os.system('python3 csv2edges_undirected.py evolving/{y}/mergedEdges.csv evolving/{y}/mergedEdges.txt'.format(y=year))
    os.system('python3 csv2nodes.py evolving/{y}/mergedNodes.csv evolving/{y}/mergedNodes.txt'.format(y=year))
    print(year)

numOfCores = config.numOfCores
years = config.years

pool = ProcessPoolExecutor(numOfCores)

futures = []

for i in range(0, len(years) ):
	futures.append( pool.submit(filterAndMerge, years[i] ))

wait(futures)

