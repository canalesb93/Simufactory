package itesm.mx.simufactory;

import java.util.ArrayList;

/**
 * Created by Canales on 4/16/15.
 */
public class Simulation {
    int money;
    long time;
    ArrayList<Operation> operations;
    int usersCount;

    public Simulation(int money, long time, ArrayList<Operation> operations, int usersCount) {
        this.money = money;
        this.time = time;
        this.operations = operations;
        this.usersCount = usersCount;
    }

    public Simulation(int money, long time, ArrayList<Operation> operations) {
        this.money = money;
        this.time = time;
        this.operations = operations;
        this.usersCount = 3;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ArrayList<Operation> getOperations() {
        return operations;
    }

    public void setOperations(ArrayList<Operation> operations) {
        this.operations = operations;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }
}
