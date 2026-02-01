package BPlusTree;

import java.nio.ByteBuffer;
import java.util.Locale;

class Record {
    private double mass = 0;
    private double height = 0;
    private int key;  // Natural number key

    public Record(double mass, double height, int key) {
        this.mass = mass;
        this.height = height;
        this.key = key;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public double getPotentialEnergy() {
        return mass * height;
    }

    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.putInt(key);
        buffer.putDouble(mass);
        buffer.putDouble(height);
        return buffer.array();
    }

    // Deserialize record from bytes
    public static Record deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, 20);
        int key = buffer.getInt();
        double mass = buffer.getDouble();
        double height = buffer.getDouble();
        return new Record(mass, height, key);
    }

    public static int getSerializedSize() {
        return 20; // 4 + 8 + 8 bytes
    }

    @Override
    public String toString() {
        String m = Double.toString(mass);
        String h = Double.toString(height);
        return String.format("Key=%d | %s;%s", key, m, h);
    }
}
