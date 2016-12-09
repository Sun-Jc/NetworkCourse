import os
import sys
from config import config
from concurrent.futures import ProcessPoolExecutor, wait, as_completed
 
def hist(year,bins):
    os.system('python3 degreeHist.py evolving/{y}/dblpDegrees.txt {bins} evolving/{y}/dblpDegrees.png'.format(y=year,bins=bins))
    print(year)

numOfCores = config.numOfCores
years = config.years

pool = ProcessPoolExecutor(numOfCores)

futures = []

for i in range(0, len(years) ):
	futures.append( pool.submit(hist, years[i], sys.argv[1] ))

wait(futures)