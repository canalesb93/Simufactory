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
    int team;
    ArrayList<Integer> requires;
    int amount;

    public Operation(String name, int cost, int gain, long time, int team, ArrayList<Integer> requires, int amount) {
        this.name = name;
        this.cost = cost;
        this.gain = gain;
        this.time = time;
        this.team = team;
        this.requires = requires;
        this.amount = amount;
    }

    public void setAmount(int amount) { this.amount = amount;  }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public int getTeam() { return team; }

    public void setTeam(int team) { this.team = team; }

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

    public ArrayList<Integer> getRequires() {
        return requires;
    }

    public void setRequires(ArrayList<Integer> requires) {
        this.requires = requires;
    }
}
