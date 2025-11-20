package ex1;

import java.util.Locale;
import java.util.StringTokenizer;

public class Record implements Comparable<Record>{
    private double  mass = 0;
    private double height = 0;
    //private double potential_energy = 0;

    public Record(double mass, double height) {
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

    public double getPotential_energy() {
        //return mass*height*ConstValues.g_const;
        return mass*height;
    }


    @Override
    public int compareTo(Record record) {
        return Double.compare(this.getPotential_energy(), record.getPotential_energy());
    }

    @Override
    public String toString(){
        String m = String.format(Locale.US, "%05.2f", mass);
        String h = String.format(Locale.US, "%05.2f", height);
        return (m + ";" + h);
    }
}
