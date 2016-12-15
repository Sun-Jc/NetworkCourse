import sys
import matplotlib.pyplot as plt
import matplotlib
import numpy as np
import csv
from config import config

def drawHist(seq,bins,obj):
	plt.hist(seq,bins=bins)
	plt.ylabel('frequency')
	plt.xlabel('pagerank')
	#plt.yscale('log')
	#plt.xscale('log')
	#plt.show()
	plt.savefig(obj)


def stat(year,filename,top):
	with open('evolving/{y}/{f}'.format(y=year,f=filename), "r", encoding="utf-8") as input:
		#print(year)
		#print("usage: $python3 degreeHist.py evolving/2000/dblpRanks.txt #bins evolving/2000/dblpRanks.png")
		reader = csv.reader(input, delimiter=',')
		v = []
		count = 0
		for row in reader:
			count = count + 1
			if  count == 1:
				continue
			v.append( (float(row[1]),row[0]))
		v.sort(reverse=True)
		return v


argv = sys.argv
years = config.years
ftop = open("collect_{y}_topx.csv".format(y=argv[1]) ,'w')
favg = open("collect_{y}_avg.csv".format(y=argv[1]) ,'w')

for i in range(0, len(years) ):
	v = stat(years[i],argv[1],argv[2])
	end = min((int)(argv[2]),len(v))
	for x in v[0:end]:
		ftop.write("{name},{value},{year}\n".format(name=x[1],value=x[0],year=years[i]))
	s = 0.0
	for x in v:
		s += x[0]
	avg = s / len(v)
	#print(years[i])
	favg.write("{avg},{year}\n".format(avg=avg,year=years[i]))
ftop.close()
favg.close()