import matplotlib.pyplot as plt
import numpy as np

# Dane wyciągnięte z Twojego pliku (uśrednione wartości przybliżone)
labels = ['Wyszukiwanie (Search)', 'Wstawianie (Insert)', 'Usuwanie (Delete)']

# Dane dla d=2 (N=10000, alpha=50)
d2_means = [6.1, 11.0, 13.6]
# Dane dla d=8 (N=10000, alpha=50) - obliczone na podstawie logów z sekcji "d=8"
d8_means = [4.0, 5.0, 5.0]

x = np.arange(len(labels))
width = 0.35

fig, ax = plt.subplots(figsize=(10, 6))
rects1 = ax.bar(x - width/2, d2_means, width, label='Stopień d=2', color='#4c72b0')
rects2 = ax.bar(x + width/2, d8_means, width, label='Stopień d=8', color='#55a868')

ax.set_ylabel('Średnia liczba operacji I/O')
ax.set_title('Wpływ stopnia drzewa (d) na wydajność operacji (N=10000)')
ax.set_xticks(x)
ax.set_xticklabels(labels)
ax.legend()
ax.grid(axis='y', linestyle='--', alpha=0.5)

# Dodawanie wartości
ax.bar_label(rects1, padding=3)
ax.bar_label(rects2, padding=3)

plt.tight_layout()
plt.show()