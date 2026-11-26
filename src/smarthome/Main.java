package smarthome;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class Main extends JFrame {
    private final DefaultTableModel devModel = model("ID", "Device", "Type", "Room", "Status", "Setting");
    private final DefaultTableModel schModel = model("ID", "Device", "Action", "Time", "Repeat", "Active");
    private final DefaultTableModel logModel = model("Time", "Event");
    private final JTable devTable = new JTable(devModel), schTable = new JTable(schModel);
    private final JComboBox<Device> schDevice = new JComboBox<>();
    private List<Device> devices;

    public Main() {
        super("Smart Home Automation Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Devices", devicePanel());
        tabs.addTab("Schedules", schedulePanel());
        tabs.addTab("Activity Log", new JScrollPane(new JTable(logModel)));
        add(tabs);

        refresh();
        // scheduler: checks the DB every second, runs due schedules
        new javax.swing.Timer(1000, e -> runScheduler()).start();
        // keep UI in sync
        new javax.swing.Timer(2000, e -> refresh()).start();
    }

    private static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    // ---------- Devices tab ----------
    private JPanel devicePanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        devTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(devTable), BorderLayout.CENTER);

        JButton toggle = new JButton("Turn ON / OFF");
        JButton setting = new JButton("Change Setting");
        JButton add = new JButton("Add Device");
        JButton del = new JButton("Delete");
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(toggle); bar.add(setting); bar.add(add); bar.add(del);
        p.add(bar, BorderLayout.SOUTH);

        toggle.addActionListener(e -> {
            Device d = selectedDevice(); if (d == null) return;
            run(() -> {
                Repo.setState(d.id, !d.on);
                Repo.log(d.id, d.name + " -> " + (d.type.equals("SECURITY") ? (!d.on ? "ARMED" : "DISARMED") : (!d.on ? "ON" : "OFF")) + " (manual)");
            });
        });
        setting.addActionListener(e -> {
            Device d = selectedDevice(); if (d == null) return;
            int min, max;
            switch (d.type) {
                case "LIGHT": min = 0; max = 100; break;
                case "FAN": min = 1; max = 5; break;
                case "AC": min = 16; max = 30; break;
                default: JOptionPane.showMessageDialog(this, "No setting for this device."); return;
            }
            JSpinner sp = new JSpinner(new SpinnerNumberModel(Math.max(min, Math.min(max, d.level)), min, max, 1));
            if (JOptionPane.showConfirmDialog(this, sp, d.name + " (" + min + "-" + max + ")",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                int v = (Integer) sp.getValue();
                run(() -> { Repo.setLevel(d.id, v); Repo.log(d.id, d.name + " setting changed to " + v); });
            }
        });
        add.addActionListener(e -> {
            JTextField name = new JTextField(), room = new JTextField();
            JComboBox<String> type = new JComboBox<>(new String[]{"LIGHT", "FAN", "AC", "SECURITY"});
            JPanel f = new JPanel(new GridLayout(0, 2, 4, 4));
            f.add(new JLabel("Name")); f.add(name);
            f.add(new JLabel("Type")); f.add(type);
            f.add(new JLabel("Room")); f.add(room);
            if (JOptionPane.showConfirmDialog(this, f, "Add Device", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION
                    && !name.getText().isBlank() && !room.getText().isBlank())
                run(() -> Repo.addDevice(name.getText().trim(), (String) type.getSelectedItem(), room.getText().trim()));
        });
        del.addActionListener(e -> {
            Device d = selectedDevice(); if (d == null) return;
            if (JOptionPane.showConfirmDialog(this, "Delete " + d.name + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                run(() -> Repo.deleteDevice(d));
        });
        return p;
    }

    // ---------- Schedules tab ----------
    private JPanel schedulePanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        schTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(schTable), BorderLayout.CENTER);

        JComboBox<String> action = new JComboBox<>(new String[]{"ON", "OFF"});
        JSpinner time = new JSpinner(new SpinnerDateModel());
        time.setEditor(new JSpinner.DateEditor(time, "HH:mm"));
        JCheckBox repeat = new JCheckBox("Repeat daily");
        JButton add = new JButton("Add Schedule");
        JButton del = new JButton("Delete Selected");

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(new JLabel("Device:")); bar.add(schDevice);
        bar.add(action); bar.add(new JLabel("at")); bar.add(time);
        bar.add(repeat); bar.add(add); bar.add(del);
        p.add(bar, BorderLayout.SOUTH);

        add.addActionListener(e -> {
            Device d = (Device) schDevice.getSelectedItem();
            if (d == null) return;
            Date dt = (Date) time.getValue();
            LocalTime t = LocalTime.of(dt.getHours(), dt.getMinutes());
            if (t.isBefore(LocalTime.now()) && !repeat.isSelected()) {
                JOptionPane.showMessageDialog(this, "That time has passed today. Pick a later time or tick 'Repeat daily'.");
                return;
            }
            boolean on = action.getSelectedItem().equals("ON");
            run(() -> {
                Repo.addSchedule(d.id, on, t, repeat.isSelected());
                Repo.log(d.id, "Scheduled " + d.name + " " + (on ? "ON" : "OFF") + " at " + t);
            });
        });
        del.addActionListener(e -> {
            int row = schTable.getSelectedRow();
            if (row < 0) return;
            int id = (Integer) schModel.getValueAt(row, 0);
            run(() -> Repo.deleteSchedule(id));
        });
        return p;
    }

    // ---------- helpers ----------
    private Device selectedDevice() {
        int row = devTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a device first."); return null; }
        int id = (Integer) devModel.getValueAt(row, 0);
        return devices.stream().filter(d -> d.id == id).findFirst().orElse(null);
    }

    interface Job { void go() throws SQLException; }

    private void run(Job j) {
        try { j.go(); refresh(); }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runScheduler() {
        try {
            boolean changed = false;
            for (Schedule s : Repo.due()) {
                Repo.setState(s.deviceId, s.on);
                Repo.log(s.deviceId, "Schedule fired: " + s.deviceName + " -> " + (s.on ? "ON" : "OFF"));
                Repo.markRun(s);
                changed = true;
            }
            if (changed) refresh();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void refresh() {
        try {
            int selDev = devTable.getSelectedRow(), selSch = schTable.getSelectedRow();
            devices = Repo.devices();
            devModel.setRowCount(0);
            for (Device d : devices)
                devModel.addRow(new Object[]{d.id, d.name, d.type, d.room, d.status(), d.setting()});

            Device keep = (Device) schDevice.getSelectedItem();
            schDevice.removeAllItems();
            for (Device d : devices) schDevice.addItem(d);
            if (keep != null) for (Device d : devices) if (d.id == keep.id) schDevice.setSelectedItem(d);

            schModel.setRowCount(0);
            for (Schedule s : Repo.schedules())
                schModel.addRow(new Object[]{s.id, s.deviceName, s.on ? "ON" : "OFF", s.time,
                        s.repeat ? "Daily" : "Once", s.active ? "Yes" : "Done"});

            logModel.setRowCount(0);
            for (String[] l : Repo.logs()) logModel.addRow(l);

            if (selDev >= 0 && selDev < devModel.getRowCount()) devTable.setRowSelectionInterval(selDev, selDev);
            if (selSch >= 0 && selSch < schModel.getRowCount()) schTable.setRowSelectionInterval(selSch, selSch);
        } catch (SQLException ex) {
            setTitle("Smart Home Simulator - DB not connected: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
