package smarthome;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.Enumeration;

/**
 * Colourful, large, easy-to-read look for the whole application.
 * No changes to Main.java are needed: Launcher.java calls Theme.install()
 * and every window that opens afterwards is styled automatically.
 */
public final class Theme {

    // ---------- palette ----------
    static final Color PAGE_BG   = new Color(0xEAF1FB);
    static final Color NAVY      = new Color(0x1E2A5E);
    static final Color TEXT      = new Color(0x1F2937);
    static final Color STRIPE    = new Color(0xF4F8FF);
    static final Color SELECT_BG = new Color(0x2563EB);
    static final Color GOLD      = new Color(0xFFD54F);

    static final Color[] TAB_COLORS = {
        new Color(0x4F46E5), new Color(0x0D9488), new Color(0xEA580C), new Color(0xBE185D)
    };

    // ---------- fonts (each part of the UI gets its own style) ----------
    static final Font F_BODY    = new Font("Segoe UI", Font.PLAIN, 16);
    static final Font F_NAME    = new Font("Georgia", Font.BOLD, 17);
    static final Font F_TYPE    = new Font("Trebuchet MS", Font.BOLD, 16);
    static final Font F_SETTING = new Font("Consolas", Font.BOLD, 16);
    static final Font F_STATUS  = new Font("Verdana", Font.BOLD, 15);
    static final Font F_HEADER  = new Font("Georgia", Font.BOLD, 18);
    static final Font F_TAB     = new Font("Trebuchet MS", Font.BOLD, 18);
    static final Font F_BUTTON  = new Font("Verdana", Font.BOLD, 15);
    static final Font F_LABEL   = new Font("Segoe UI", Font.BOLD, 16);
    static final Font F_TITLE   = new Font("Georgia", Font.BOLD | Font.ITALIC, 30);

    private Theme() {}

    // =====================================================================
    //  install(): call once, before any window is created
    // =====================================================================
    public static void install() {
        // Give every default Swing font a bigger, cleaner base.
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object k = keys.nextElement();
            if (k instanceof String && ((String) k).endsWith(".font")) {
                UIManager.put(k, new FontUIResource(F_BODY));
            }
        }
        UIManager.put("Label.font", new FontUIResource(F_LABEL));
        UIManager.put("Button.font", new FontUIResource(F_BUTTON));
        UIManager.put("TabbedPane.font", new FontUIResource(F_TAB));
        UIManager.put("TableHeader.font", new FontUIResource(F_HEADER));
        UIManager.put("OptionPane.messageFont", new FontUIResource(F_LABEL));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(F_BUTTON));
        UIManager.put("TitledBorder.font", new FontUIResource(F_HEADER));

        // Colours
        UIManager.put("Panel.background", PAGE_BG);
        UIManager.put("OptionPane.background", PAGE_BG);
        UIManager.put("Label.foreground", NAVY);
        UIManager.put("OptionPane.messageForeground", NAVY);
        UIManager.put("TabbedPane.tabAreaBackground", NAVY);
        UIManager.put("TabbedPane.selected", GOLD);
        UIManager.put("TabbedPane.contentAreaColor", PAGE_BG);
        UIManager.put("TabbedPane.tabInsets", new InsetsUIResource(10, 30, 10, 30));
        UIManager.put("TabbedPane.contentBorderInsets", new InsetsUIResource(3, 0, 0, 0));

        // Style every window the moment it opens (main window and dialogs).
        Toolkit.getDefaultToolkit().addAWTEventListener(ev -> {
            if (ev.getID() == WindowEvent.WINDOW_OPENED && ev.getSource() instanceof Window) {
                Window w = (Window) ev.getSource();
                SwingUtilities.invokeLater(() -> styleWindow(w));
            }
        }, AWTEvent.WINDOW_EVENT_MASK);
    }

    // =====================================================================
    //  Window level
    // =====================================================================
    static void styleWindow(Window w) {
        if (!(w instanceof RootPaneContainer)) return;
        JRootPane root = ((RootPaneContainer) w).getRootPane();
        if (root.getClientProperty("themed.window") != null) return;
        root.putClientProperty("themed.window", Boolean.TRUE);

        style(root);

        if (w instanceof JFrame) {
            JFrame f = (JFrame) w;
            f.setMinimumSize(new Dimension(900, 600));
            f.setExtendedState(Frame.MAXIMIZED_BOTH);   // full screen size
            addBanner(f);
        } else if (w instanceof JDialog) {
            ((JDialog) w).pack();
            w.setLocationRelativeTo(w.getOwner());
        }
        w.revalidate();
        w.repaint();
    }

    /** Adds a coloured title banner on top if the frame uses BorderLayout and NORTH is free. */
    private static void addBanner(JFrame f) {
        Container cp = f.getContentPane();
        if (!(cp.getLayout() instanceof BorderLayout)) return;
        BorderLayout bl = (BorderLayout) cp.getLayout();
        if (bl.getLayoutComponent(BorderLayout.NORTH) != null) return;

        JPanel banner = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(0x4F46E5),
                        getWidth(), 0, new Color(0x06B6D4)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel title = new JLabel("Smart Home Automation Simulator");
        title.setFont(F_TITLE);
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Lights  \u2022  Fans  \u2022  Air Conditioners  \u2022  Security");
        sub.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        sub.setForeground(new Color(0xFFF3B0));

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(title);
        text.add(sub);
        banner.add(text, BorderLayout.WEST);

        cp.add(banner, BorderLayout.NORTH);
    }

    // =====================================================================
    //  Component level (walks the whole tree)
    // =====================================================================
    static void style(Component c) {
        if (c instanceof JTabbedPane) styleTabs((JTabbedPane) c);
        else if (c instanceof JTable) styleTable((JTable) c);
        else if (c instanceof JButton) styleButton((JButton) c);
        else if (c instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) c;
            sp.getViewport().setBackground(Color.WHITE);
            sp.setBorder(new LineBorder(new Color(0x93A5CF), 2, true));
        }
        else if (c instanceof JLabel && ((JLabel) c).getClientProperty("themed") == null) {
            c.setFont(F_LABEL);
            c.setForeground(NAVY);
        }
        else if (c instanceof JTextField || c instanceof JComboBox
                || c instanceof JSpinner || c instanceof JCheckBox
                || c instanceof JRadioButton) {
            c.setFont(F_BODY);
        }
        else if (c instanceof JPanel) {
            JPanel p = (JPanel) c;
            p.setOpaque(true);
            p.setBackground(PAGE_BG);
        }

        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) style(child);
        }
    }

    // ---------- tabs ----------
    private static void styleTabs(JTabbedPane tp) {
        tp.setFont(F_TAB);
        tp.setOpaque(true);
        tp.setBackground(NAVY);
        recolorTabs(tp);
        if (tp.getClientProperty("themed") == null) {
            tp.putClientProperty("themed", Boolean.TRUE);
            ChangeListener cl = e -> recolorTabs(tp);
            tp.addChangeListener(cl);
        }
    }

    private static void recolorTabs(JTabbedPane tp) {
        for (int i = 0; i < tp.getTabCount(); i++) {
            tp.setBackgroundAt(i, TAB_COLORS[i % TAB_COLORS.length]);
            tp.setForegroundAt(i, i == tp.getSelectedIndex() ? NAVY : Color.WHITE);
        }
    }

    // ---------- buttons ----------
    private static void styleButton(JButton b) {
        if (b.getClientProperty("themed") != null) return;
        b.putClientProperty("themed", Boolean.TRUE);

        final Color base = buttonColor(b.getText());
        final Color hover = base.brighter();

        b.setUI(new BasicButtonUI());
        b.setFont(F_BUTTON);
        b.setForeground(Color.WHITE);
        b.setBackground(base);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(
                new LineBorder(base.darker(), 2, true),
                new EmptyBorder(12, 26, 12, 26)));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(base); }
        });
    }

    static Color buttonColor(String text) {
        String t = text == null ? "" : text.toLowerCase();
        if (t.contains("delete") || t.contains("remove") || t.contains("stop"))  return new Color(0xDC2626);
        if (t.contains("cancel") || t.contains("close"))                         return new Color(0x6B7280);
        if (t.contains("turn") || t.contains("on / off") || t.contains("arm"))   return new Color(0x16A34A);
        if (t.contains("add") || t.contains("new") || t.contains("save"))        return new Color(0x9333EA);
        if (t.contains("setting") || t.contains("edit") || t.contains("change")) return new Color(0x2563EB);
        if (t.contains("refresh") || t.contains("reload"))                       return new Color(0x0D9488);
        if (t.contains("clear"))                                                 return new Color(0xEA580C);
        return new Color(0x2563EB);
    }

    // ---------- tables ----------
    private static void styleTable(JTable t) {
        t.setFont(F_BODY);
        t.setRowHeight(40);
        t.setShowGrid(true);
        t.setGridColor(new Color(0xD5DEF2));
        t.setIntercellSpacing(new Dimension(1, 1));
        t.setSelectionBackground(SELECT_BG);
        t.setSelectionForeground(Color.WHITE);
        t.setFillsViewportHeight(true);
        t.setBackground(Color.WHITE);

        CellRenderer r = new CellRenderer();
        t.setDefaultRenderer(Object.class, r);
        t.setDefaultRenderer(Number.class, r);
        t.setDefaultRenderer(Date.class, r);

        JTableHeader h = t.getTableHeader();
        if (h != null) {
            h.setReorderingAllowed(false);
            h.setPreferredSize(new Dimension(100, 46));
            h.setDefaultRenderer(new HeaderRenderer());
        }
    }

    private static final class HeaderRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, false, false, row, col);
            setOpaque(true);
            setBackground(NAVY);
            setForeground(Color.WHITE);
            setFont(F_HEADER);
            setHorizontalAlignment(CENTER);
            setBorder(new MatteBorder(0, 0, 3, 1, new Color(0x4F46E5)));
            return this;
        }
    }

    private static final class CellRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, false, row, col);
            String text = v == null ? "" : v.toString().trim();
            String up = text.toUpperCase();
            String colName = t.getColumnName(col).trim().toLowerCase();

            setBorder(new EmptyBorder(0, 12, 0, 12));
            setHorizontalAlignment(LEFT);
            setFont(F_BODY);

            // zebra stripes + soft tint for the whole row based on its Status
            Color bg = (row % 2 == 0) ? Color.WHITE : STRIPE;
            String status = rowStatus(t, row);
            if (status != null) {
                switch (status) {
                    case "ON":       bg = new Color(0xDDF7E3); break;
                    case "ARMED":    bg = new Color(0xFFE1E1); break;
                    case "DISARMED": bg = new Color(0xFFF4D6); break;
                    default: break;
                }
            }
            Color fg = TEXT;

            if (colName.equals("id")) {
                setHorizontalAlignment(CENTER);
                setFont(F_TYPE);
                fg = new Color(0x6B7280);
            } else if (colName.equals("device") || colName.equals("name")) {
                setFont(F_NAME);
                fg = NAVY;
            } else if (colName.equals("type")) {
                setFont(F_TYPE);
                fg = typeColor(up);
            } else if (colName.equals("room")) {
                setFont(new Font("Segoe UI", Font.ITALIC | Font.BOLD, 16));
                fg = new Color(0x7C3AED);
            } else if (colName.equals("setting")) {
                setFont(F_SETTING);
                fg = new Color(0x0369A1);
            }

            // status words get their own colour wherever they appear
            Color sc = statusColor(up);
            if (sc != null) {
                setFont(F_STATUS);
                setHorizontalAlignment(CENTER);
                fg = sc;
            }

            if (sel) {
                setBackground(SELECT_BG);
                setForeground(Color.WHITE);
            } else {
                setBackground(bg);
                setForeground(fg);
            }
            return this;
        }
    }

    private static String rowStatus(JTable t, int row) {
        for (int i = 0; i < t.getColumnCount(); i++) {
            if (t.getColumnName(i).trim().equalsIgnoreCase("Status")) {
                Object o = t.getValueAt(row, i);
                return o == null ? null : o.toString().trim().toUpperCase();
            }
        }
        return null;
    }

    private static Color statusColor(String up) {
        switch (up) {
            case "ON":       return new Color(0x15803D);
            case "OFF":      return new Color(0xB91C1C);
            case "ARMED":    return new Color(0xDC2626);
            case "DISARMED": return new Color(0xB45309);
            default:         return null;
        }
    }

    private static Color typeColor(String up) {
        switch (up) {
            case "LIGHT":    return new Color(0xD97706);
            case "FAN":      return new Color(0x0D9488);
            case "AC":       return new Color(0x2563EB);
            case "SECURITY": return new Color(0x9333EA);
            default:         return TEXT;
        }
    }
}
