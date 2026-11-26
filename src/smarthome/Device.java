package smarthome;

public class Device {
    public int id;
    public String name, type, room;
    public boolean on;
    public int level;

    public String status() {
        if (type.equals("SECURITY")) return on ? "ARMED" : "DISARMED";
        return on ? "ON" : "OFF";
    }

    public String setting() {
        switch (type) {
            case "LIGHT": return level + "% brightness";
            case "FAN": return "Speed " + level;
            case "AC": return level + " \u00B0C";
            default: return "-";
        }
    }

    @Override public String toString() { return name + " (" + room + ")"; }
}
