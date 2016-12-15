python3 collectSingles.py dblpAsscociativeCoefficien.txt
python3 collectSingles.py dblpAvgClusterCoeffient.txt
python3 collectTopAvg.py dblpRanks.txt 10
python3 collectTopAvg.py dblpDegrees.txt 10

python3 drawYearFig.py collect_dblpAsscociativeCoefficien.txt.csv "Asscociative Coefficient"
python3 drawYearFig.py collect_dblpAvgClusterCoeffient.txt.csv "Cluster Coeffient(Avg)"
python3 drawYearFig.py collect_dblpDegrees.txt_avg.csv "Degree(Avg)"
python3 drawYearFig.py collect_dblpRanks.txt_avg.csv "PageRank(Avg)"