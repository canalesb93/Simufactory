package itesm.mx.simufactory;

import java.util.ArrayList;

/**
 * Created by Canales on 5/3/15.
 */
public class Machine {
    String name;
    int team;
    int currentResource;
    private int timeCounter;
    private ArrayList<Long> times;

    public Machine(String name, int team) {
        this.name = name;
        this.team = team;
        this.currentResource = -1;
        this.timeCounter = 0;
        this.times = new ArrayList<Long>();
    }

    public int getTimeCounter() {
        return timeCounter;
    }

    public void setTimeCounter(int timeCounter) {
        this.timeCounter = timeCounter;
    }

    public void addTimeCounter() {
        this.timeCounter++;
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
        this.timeCounter--;
    }

    public int getCurrentResource() {
        return currentResource;
    }

    public void setCurrentResource(int currentResource) {
        this.currentResource = currentResource;
    }

    public void clearTimes(){
        this.times.clear();
        this.timeCounter = 0;
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
