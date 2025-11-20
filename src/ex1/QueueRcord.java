package ex1;

public class QueueRcord implements Comparable<QueueRcord>{
    public final Record record;
    public final int index;

    public QueueRcord(Record record, int index) {
        this.record = record;
        this.index = index;
    }

    @Override
    public int compareTo(QueueRcord other) {
        return this.record.compareTo(other.record);
    }
}
