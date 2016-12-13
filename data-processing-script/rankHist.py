import sys
import matplotlib.pyplot as plt
import matplotlib
import numpy as np
import csv

def drawHist(seq,bins,obj):
	plt.hist(seq,bins=bins)
	plt.ylabel('frequency')
	plt.xlabel('pagerank')
	#plt.yscale('log')
	#plt.xscale('log')
	#plt.show()
	plt.savefig(obj)


src = sys.argv[1]
obj = sys.argv[3]

with open(sys.argv[1], "r", encoding="utf-8") as input:
	#print("usage: $python3 degreeHist.py evolving/2000/dblpRanks.txt #bins evolving/2000/dblpRanks.png")
	reader = csv.reader(input, delimiter=',')
	degree = []
	count = 0
	for row in reader:
		count = count + 1
		if  count == 1:
			continue
		degree.append( float(row[1]) )
	drawHist(degree,int(sys.argv[2]),obj)