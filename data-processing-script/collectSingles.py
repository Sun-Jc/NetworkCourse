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


def stat(year,filename):
	with open('evolving/{y}/{f}'.format(y=year,f=filename), "r", encoding="utf-8") as input:
		v = input.readline()
		return v

argv = sys.argv
years = config.years		
fs = open("collect_{y}.csv".format(y=argv[1]) ,'w')

for i in range(0, len(years) ):
	avg = stat(years[i],argv[1])
	fs.write("{avg},{year}\n".format(avg=avg,year=years[i]))
fs.close()