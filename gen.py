import random
import csv

n = 20
test_keys = []

with open("data_20.csv", "w",newline="",encoding="utf-8") as csvfile:
    writer = csv.writer(csvfile, delimiter=' ')

    for _ in range(n):
        #l1=round(random.uniform(1,100),2)
        i = "i"
        l = random.randint(1, 1000000)
        l1 = round(random.uniform(1,1000000),2)
        l2=round(random.uniform(1,1000000),2)
        writer.writerow([i,l,l1,l2])

    for _ in range(10):
            operation = "i"
            # Losujemy klucz i od razu go zapamiętujemy
            key = random.randint(1, 1000000)
            test_keys.append(key)

            # Stałe dane dla łatwej identyfikacji
            data1 = 1
            data2 = 1

            # Format: i <klucz> 1 1
            writer.writerow([operation, key, data1, data2])

        # 3. SEKCJA TESTOWA: 10 wyszukiwań (s)
    for key in test_keys:
        operation = "s"  # 's' - Wyszukiwanie


        writer.writerow([operation, key])

#         # 4. SEKCJA TESTOWA: 10 usuwania (d)
#     for key in test_keys:
#         operation = "d"  # 'd' - Usuwanie
#
#             # Format: d <klucz>
#         writer.writerow([operation, key])
