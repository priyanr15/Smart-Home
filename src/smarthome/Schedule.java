package smarthome;

import java.time.LocalTime;

public class Schedule {
    public int id, deviceId;
    public String deviceName;
    public boolean on, repeat, active;
    public LocalTime time;
}
