#  External Sorting & Persistent B+ Tree

## 1. External Merge Sort (Block I/O)
An implementation of an external sorting algorithm designed to sort files that do not fit into the main memory.

### Key Features:
* **Manual Buffer Management:** Uses a custom `HandleFile` class to manage block-based reading and writing.
* **Configurable Blocking Factor:** Allows fine-tuning the number of records per disk block to observe the impact on I/O performance.
* **Multi-way Merge:** Implements a merging strategy using a **Min-Heap** to efficiently merge multiple sorted "runs" into a single output.
* **I/O Statistics:** Built-in tracking of read/write operations to analyze the efficiency of different buffer sizes.

### Technical Deep Dive:
* **Stage 1:** Initial runs are created by reading blocks of data, sorting them in-memory, and writing them back to disk.
* **Stage 2:** Multi-way merging of runs until a single sorted file is produced.

---

## 2. Disk-Oriented B+ Tree
A persistent B+ Tree implementation for efficient data indexing and retrieval, featuring manual page-based storage.

### Key Features:
* **Persistent Storage:** Data is stored in a binary format on disk using `RandomAccessFile`.
* **Binary Serialization:** Manual conversion of tree nodes and records to/from byte arrays using `java.nio.ByteBuffer`.
* **Dynamic Rebalancing:** Full implementation of node splitting and merging to maintain an $O(\log n)$ height.

### Data Structure Details:
* **Node Structure:** Supports both leaf and internal nodes with a configurable order ($d$).
* **Byte-Level Control:** Every integer and double is precisely placed in the byte buffer, ensuring compatibility with disk-based retrieval.

---

## Ensure you have **JDK 17+** installed.
