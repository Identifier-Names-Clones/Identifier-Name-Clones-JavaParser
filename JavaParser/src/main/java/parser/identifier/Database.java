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
        String drop_sql = "DROP TABLE IF EXISTS identifiers";
        try (Connection conn = getConnection();
             PreparedStatement identifiers_stmt = conn.prepareStatement(drop_sql)) {
            identifiers_stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String project_drop_sql = "DROP TABLE IF EXISTS projects";
        try (Connection conn = getConnection();
             PreparedStatement identifiers_stmt = conn.prepareStatement(project_drop_sql)) {
            identifiers_stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String identifiers_sql = "CREATE TABLE IF NOT EXISTS identifiers (" +
                "projectID INTEGER," +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "filename TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "datatype TEXT," +
                "FOREIGN KEY (projectID) REFERENCES projects(id))";

        String projects_sql = "CREATE TABLE IF NOT EXISTS projects (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)";

        try (Connection conn = getConnection();
             PreparedStatement identifiers_stmt = conn.prepareStatement(identifiers_sql)) {
            identifiers_stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = getConnection();
             PreparedStatement proj_stmt = conn.prepareStatement(projects_sql)) {
            proj_stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertIdentifier(int projectID, String filename, String type, String name, String datatype) {
        String sql = "INSERT INTO identifiers (projectID, filename, type, name, datatype) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, projectID);
            stmt.setString(2, filename);
            stmt.setString(3, type);
            stmt.setString(4, name);
            stmt.setString(5, datatype);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int insertProject(String projectName) {
        String sql = "INSERT INTO projects (name) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, projectName);
            stmt.executeUpdate();

            // Retrieve the generated project ID
            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}