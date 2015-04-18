package itesm.mx.simufactory;

/**
 * Created by canales, roel on 3/30/15.
 */
public class Session {
    private String password;
    private String name;
    private boolean active;
    public Session() {}
    public Session(String name, String password) {
        this.name = name;
        this.password = password;
        this.active = true;
    }
    public String getPassword() {
        return password;
    }
    public String getName() {
        return name;
    }
    public boolean isActive() { return active; }
}
