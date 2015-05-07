package itesm.mx.simufactory;

import java.util.ArrayList;

/**
 * Created by Canales on 5/4/15.
 */
public class Globals{
    private static Globals instance;

    // Global variable
    private Simulation simulation;
    private long startTime;
    private int moneySpent;

    // Restrict the constructor from being instantiated
    private Globals(){moneySpent = 0;}

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }

    public int getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(int moneySpent) {
        this.moneySpent = moneySpent;
    }
}
