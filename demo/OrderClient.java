public class OrderClient {

    // Flagged: identical to api.endpoint in demo/application.properties --
    // if that config value changes, this code keeps calling the old one.
    private static final String API_URL = "https://api.example.com/v2/orders";

    // Flagged: identical to db.host in demo/application.yml.
    private static final String DB_HOST_PORT = "db.internal:5432";

    // Not flagged: a bare port with no host context.
    private static final int LOCAL_PORT = 8080;

    public void placeOrder() {
        System.out.println("Calling " + API_URL);
    }
}
