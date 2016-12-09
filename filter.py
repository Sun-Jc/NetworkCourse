#!/usr/bin/python
import os
from optparse import OptionParser
parser=OptionParser()
parser.add_option("-f", action = "store", type = "string", dest = "filename")
parser.add_option("-y", action = "store", type = "int", dest = "year")
(options,args) = parser.parse_args()
wd = os.getcwd()
FN = options.filename
FN = "evolving/"+ str(options.year)+"/"+FN 
if os.path.exists(wd+'/evolving'):
	if os.path.exists(wd+'/evolving'+'/'+str(options.year)):
		wf = open(FN , 'w')
	else:
		os.makedirs(wd + "/evolving/" + str(options.year))
		wf = open(FN , 'w')
else:
	os.makedirs(wd + "/evolving/" + str(options.year))
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
			if int(l[6]) >= options.year:
				l = ','.join(l)
				wf.write(l)
f.close()
wf.close()
