import matplotlib.pyplot as plt


records = [100,1000, 10000, 100000, 1000000, 10000000, 100000000]
real_ops = [2, 20,200, 4000, 60000, 600000, 8000000]
theory_ops = [2, 20,200, 4000, 60000, 600000, 8000000]

plt.figure(figsize=(10,6))
plt.plot(records, real_ops, marker='o', label="Rzeczywiste operacje")
plt.plot(records, theory_ops, marker='x', label="Teoretyczne operacje")
plt.xscale("log")
plt.yscale("log")
plt.xlabel("Liczba rekordów (log)")
plt.ylabel("Operacje dyskowe (log)")
plt.title("Operacje dyskowe(rzeczywiste oraz teoretyczne) w stosunku do liczby rekordów")
plt.legend()
plt.grid(True)
plt.show()

# # New data
# records = [777777, 8888888]
# real_ops = [31112, 533334]
# theory_ops = [31108, 533328]
#
# plt.figure(figsize=(10,6))
# plt.plot(records, real_ops, marker='o', label="Rzeczywiste operacje")
# plt.plot(records, theory_ops, marker='x', label="Teoretyczne operacje")
#
# plt.xlabel("Liczba rekordów (log)")
# plt.ylabel("Operacje dyskowe (log)")
# plt.title("Porównanie operacji dyskowych dla danych z rozbieżnościami")
# plt.legend()
# plt.grid(True)
# plt.show()

