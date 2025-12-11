package ex2_;

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

    public Record(String line) {
        String[] parts = line.split(";");
        mass = Double.parseDouble(parts[0]);
        height = Double.parseDouble(parts[1]);
        if (parts.length > 2) {
            key = Integer.parseInt(parts[2]);
        }
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

    @Override
    public String toString() {
        String m = String.format(Locale.US, "%05.2f", mass);
        String h = String.format(Locale.US, "%05.2f", height);
        return String.format("Key=%d | %s;%s", key, m, h);
    }
}
