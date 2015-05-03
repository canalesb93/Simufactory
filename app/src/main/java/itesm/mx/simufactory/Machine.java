package itesm.mx.simufactory;

/**
 * Created by Canales on 5/3/15.
 */
public class Machine {
    String name;
    int team;

    public Machine(String name, int team) {
        this.name = name;
        this.team = team;
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
