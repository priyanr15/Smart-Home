package smarthome;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB {
    private static final Properties P = new Properties();
    static {
        try (InputStream in = new FileInputStream("db.properties")) {
            P.load(in);
        } catch (IOException e) {
            System.err.println("db.properties not found, using defaults");
        }
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(
            P.getProperty("url", "jdbc:mysql://localhost:3306/smarthome"),
            P.getProperty("user", "root"),
            P.getProperty("password", ""));
    }
}
