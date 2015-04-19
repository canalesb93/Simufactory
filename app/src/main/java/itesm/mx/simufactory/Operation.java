package itesm.mx.simufactory;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Canales on 4/16/15.
 */
public class Operation {
    String name;
    int cost;
    int gain;
    long time;
    String[] requires;

    public Operation(String name, int cost, int gain, long time, String[] requires) {
        this.name = name;
        this.cost = cost;
        this.gain = gain;
        this.time = time;
        this.requires = requires;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getGain() {
        return gain;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String[] getRequires() {
        return requires;
    }

    public void setRequires(String[] requires) {
        this.requires = requires;
    }
}
