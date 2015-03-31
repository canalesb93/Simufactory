package itesm.mx.simufactory;

/**
 * Created by canales, roel on 3/30/15.
 */
public class Session {
    private String password;
    private String name;
    public Session() {}
    public Session(String name, String password) {
        this.name = name;
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public String getName() {
        return name;
    }
}
