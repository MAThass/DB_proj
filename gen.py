import random
import csv

n = 20

with open("data_20.csv", "w",newline="",encoding="utf-8") as csvfile:
    writer = csv.writer(csvfile, delimiter=';')

    for _ in range(n):
        #l1=round(random.uniform(1,100),2)
        l1 = 1
        l2=round(random.uniform(1,100),2)
        writer.writerow([l1,l2])
