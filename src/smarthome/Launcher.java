package smarthome;

/** Start the app through this class to get the colourful, full-screen look. */
public class Launcher {
    public static void main(String[] args) throws Exception {
        Theme.install();
        Main.main(args);
    }
}
