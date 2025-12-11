package ex2;

import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.StringTokenizer;

public class Record implements Comparable<Record>{
    private double  mass = 0;
    private double height = 0;
    private int key = 0;
    //private double potential_energy = 0;

    public Record(int key,double mass, double height) {
        this.key = key;
        this.mass = mass;
        this.height = height;
        //potential_energy = mass*height*ConstValues.g_const;
    }

    public Record(String line){
        StringTokenizer tokenizer = new StringTokenizer(line, ";");
        mass = Double.parseDouble(tokenizer.nextToken());
        height = Double.parseDouble(tokenizer.nextToken());
        //potential_energy = mass*height*ConstValues.g_const;
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

    public double getPotentialEnergy() {
        //return mass*height*ConstValues.g_const;
        return mass*height;
    }

    public int getKey() {
        return key;
    }

    public void serialize(ByteBuffer buffer) {
        buffer.putInt(this.key);
        buffer.putDouble(this.mass);
        buffer.putDouble(this.height);
    }

    public static Record deserialize(ByteBuffer buffer) {
        int key = buffer.getInt();
        double mass = buffer.getDouble();
        double height = buffer.getDouble();
        return new Record(key, mass, height);
    }

//    public void deserialize(ByteBuffer buffer){
//        this.key = buffer.getInt();
//        this.mass = buffer.getDouble();
//        this.height = buffer.getDouble();
//    }


    @Override
    public int compareTo(Record record) {
        return Double.compare(this.getKey(), record.getKey());
    }

    @Override
    public String toString(){
        String m = String.format(Locale.US, "%05.2f", mass);
        String h = String.format(Locale.US, "%05.2f", height);
        return (key + ":" + m + ";" + h);
    }
}
