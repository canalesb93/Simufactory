package itesm.mx.simufactory;

import java.util.ArrayList;

/**
 * Created by Canales on 5/3/15.
 */
public class Machine {
    String name;
    int team;
    int currentResource;
    private ArrayList<Long> times;

    public Machine(String name, int team) {
        this.name = name;
        this.team = team;
        this.currentResource = 0;
        this.times = new ArrayList<Long>();
    }

    public ArrayList<Long> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<Long> times) {
        this.times = times;
    }

    public void addTime(long time){
        this.times.add(time);
    }

    public void removeTime(int index){
        this.times.remove(index);
    }

    public int getCurrentResource() {
        return currentResource;
    }

    public void setCurrentResource(int currentResource) {
        this.currentResource = currentResource;
    }

    public void clearTimes(){
        this.times.clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}
