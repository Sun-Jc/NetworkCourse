import sys
#import matplotlib.pyplot as plt
#import matplotlib
#import numpy as np
import csv
#import pickle
#from graph_tool.all import *

'''def save_object(obj, filename):
    with open(filename, 'wb') as output:
        pickle.dump(obj, output, pickle.HIGHEST_PROTOCOL)
'''

# ASUMME SIMPLE GRAPH
'''
def drawHist(seq,num):
    hist, bin_edges =  np.histogram(seq,bins=600)
    plt.figure(num)
    plt.plot(bin_edges[:-1],hist,'r^')
    plt.ylabel('frequency')
    plt.xlabel('values')
    plt.yscale('log')
    plt.xscale('log')
    plt.show()
'''

src = sys.argv[1]
obj = sys.argv[2]

with open(src, "r", encoding="utf-8") as input:
    f = open(obj,'w')
    reader = csv.reader(input, delimiter=',')
    weights = []
    tos = []
    froms = []
    count = 0
    for row in reader:
        count = count + 1
        if  count == 1:
            continue
        weight = float(row[5])
        s = int(row[0])
        t = int(row[1])
        f.write("{s} {t}\n{t} {s}\n".format(s=s,t=t))
    f.close()



        