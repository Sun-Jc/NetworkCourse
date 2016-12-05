import sys
import matplotlib.pyplot as plt
import matplotlib
import numpy as np
import csv

def drawHist(seq,bins):
	plt.hist(seq,bins=bins)
	plt.ylabel('frequency')
	plt.xlabel('degree')
	#plt.yscale('log')
	#plt.xscale('log')
	plt.show()


with open(sys.argv[1], "r", encoding="utf-8") as input:
	print("usage: $python3 degreeHist.py dblpDegrees.txt #bins")
	reader = csv.reader(input, delimiter=',')
	degree = []
	count = 0
	for row in reader:
		count = count + 1
		if  count == 1:
			continue
		degree.append( int(row[1]) )
	drawHist(degree,int(sys.argv[2]))