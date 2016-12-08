#!/usr/bin/python
import os
from optparse import OptionParser
parser=OptionParser()
parser.add_option("-f", action = "store", type = "string", dest = "filename")
parser.add_option("-y", action = "store", type = "int", dest = "year")
(options,args) = parser.parse_args()

FN = options.filename.split('.')
FN[0] = FN[0] + "_" + str(options.year)
FN = '.'.join(FN)
wf = open(FN , 'w')
with open(options.filename) as f:
	header = f.readline()
	wf.write(header)
	while True:
		l = f.readline()
		if not l:
			break
		else:
			l = l.split(',')
			if int(l[6]) <= options.year:
				l = ','.join(l)
				wf.write(l)
f.close()
wf.close()
