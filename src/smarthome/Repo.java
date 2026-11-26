package smarthome;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** All SQL lives here. */
public class Repo {

    // ---------- devices ----------
    public static List<Device> devices() throws SQLException {
        List<Device> list = new ArrayList<>();
        try (Connection c = DB.get(); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT * FROM devices ORDER BY room, name")) {
            while (r.next()) {
                Device d = new Device();
                d.id = r.getInt("id"); d.name = r.getString("name");
                d.type = r.getString("type"); d.room = r.getString("room");
                d.on = r.getBoolean("is_on"); d.level = r.getInt("level");
                list.add(d);
            }
        }
        return list;
    }

    public static void addDevice(String name, String type, String room) throws SQLException {
        int level = type.equals("LIGHT") ? 100 : type.equals("FAN") ? 1 : type.equals("AC") ? 24 : 0;
        try (Connection c = DB.get(); PreparedStatement p = c.prepareStatement(
                "INSERT INTO devices(name,type,room,level) VALUES(?,?,?,?)")) {
            p.setString(1, name); p.setString(2, type); p.setString(3, room); p.setInt(4, level);
            p.executeUpdate();
        }
        log(null, "Added device: " + name);
    }

    public static void deleteDevice(Device d) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement p = c.prepareStatement("DELETE FROM devices WHERE id=?")) {
            p.setInt(1, d.id); p.executeUpdate();
        }
        log(null, "Removed device: " + d.name);
    }

    public static void setState(int id, boolean on) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement p = c.prepareStatement("UPDATE devices SET is_on=? WHERE id=?")) {
            p.setBoolean(1, on); p.setInt(2, id); p.executeUpdate();
        }
    }

    public static void setLevel(int id, int level) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement p = c.prepareStatement("UPDATE devices SET level=? WHERE id=?")) {
            p.setInt(1, level); p.setInt(2, id); p.executeUpdate();
        }
    }

    // ---------- schedules ----------
    public static void addSchedule(int deviceId, boolean on, LocalTime t, boolean repeat) throws SQLException {
        // if the time already passed today, don't fire immediately: first run is tomorrow
        Date lastRun = t.isBefore(LocalTime.now()) ? Date.valueOf(LocalDate.now()) : null;
        try (Connection c = DB.get(); PreparedStatement p = c.prepareStatement(
                "INSERT INTO schedules(device_id,action,run_time,repeat_daily,last_run) VALUES(?,?,?,?,?)")) {
            p.setInt(1, deviceId); p.setString(2, on ? "ON" : "OFF");
            p.setTime(3, Time.valueOf(t)); p.setBoolean(4, repeat); p.setDate(5, lastRun);
            p.executeUpdate();
        }
    }

    public static void deleteSchedule(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement p = c.prepareStatement("DELETE FROM schedules WHERE id=?")) {
            p.setInt(1, id); p.executeUpdate();
        }
    }

    private static List<Schedule> query(String where, Object... args) throws SQLException {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT s.*, d.name FROM schedules s JOIN devices d ON d.id=s.device_id "
                   + where + " ORDER BY s.run_time";
        try (Connection c = DB.get(); PreparedStatement p = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) p.setObject(i + 1, args[i]);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    Schedule s = new Schedule();
                    s.id = r.getInt("id"); s.deviceId = r.getInt("device_id");
                    s.deviceName = r.getString("name"); s.on = r.getString("action").equals("ON");
                    s.time = r.getTime("run_time").toLocalTime();
                    s.repeat = r.getBoolean("repeat_daily"); s.active = r.getBoolean("active");
                    list.add(s);
                }
            }
        }
        return list;
    }

    public static List<Schedule> schedules() throws SQLException { return query(""); }

    public static List<Schedule> due() throws SQLException {
        return query("WHERE s.active=1 AND s.run_time<=? AND (s.last_run IS NULL OR s.last_run<?)",
                     Time.valueOf(LocalTime.now()), Date.valueOf(LocalDate.now()));
    }

    public static void markRun(Schedule s) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement p = c.prepareStatement(
                "UPDATE schedules SET last_run=CURDATE(), active=? WHERE id=?")) {
            p.setBoolean(1, s.repeat); p.setInt(2, s.id); p.executeUpdate();
        }
    }

    // ---------- log ----------
    public static void log(Integer deviceId, String msg) {
        try (Connection c = DB.get(); PreparedStatement p = c.prepareStatement(
                "INSERT INTO activity_log(device_id,message) VALUES(?,?)")) {
            if (deviceId == null) p.setNull(1, Types.INTEGER); else p.setInt(1, deviceId);
            p.setString(2, msg); p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<String[]> logs() throws SQLException {
        List<String[]> list = new ArrayList<>();
        try (Connection c = DB.get(); Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT created_at,message FROM activity_log ORDER BY id DESC LIMIT 200")) {
            while (r.next()) list.add(new String[]{ r.getString(1), r.getString(2) });
        }
        return list;
    }
}
