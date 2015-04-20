package itesm.mx.simufactory;

/**
 * Created by canales, roel on 3/30/15.
 */
public class Session {
    private String password;
    private String name;
    private boolean active;
    private boolean started;
    public Session() {}
    public Session(String name, String password) {
        this.name = name;
        this.password = password;
        this.active = true;
        this.started = false;
    }
    public String getPassword() {
        return password;
    }
    public String getName() {
        return name;
    }
    public boolean isActive() { return active; }
    public boolean isStarted() { return started; }
}
