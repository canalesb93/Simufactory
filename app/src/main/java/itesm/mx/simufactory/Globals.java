package itesm.mx.simufactory;

import java.util.ArrayList;

/**
 * Created by Canales on 5/4/15.
 */
public class Globals{
    private static Globals instance;

    // Global variable
    private Simulation simulation;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
