package parser.identifier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    // Change vvv responsively if doing multiple repos at once 
    private static final String DB_URL = "jdbc:sqlite:identifier_data.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS identifiers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "filename TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "datatype TEXT)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertIdentifier(String filename, String type, String name, String datatype) {
        String sql = "INSERT INTO identifiers (filename, type, name, datatype) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, filename);
            stmt.setString(2, type);
            stmt.setString(3, name);
            stmt.setString(4, datatype);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}