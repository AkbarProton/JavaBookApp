package DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnectorConfigurator {
    private static final String BookAppDBPath =
            "src/main/resources/databaseTables/BookApp.db";
    private static final String URL = "jdbc:sqlite:" + BookAppDBPath;

    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error with connection: " + e.getSQLState());
            System.out.println("Message of the error: " + e.getMessage());
            e.printStackTrace(); //Getting stack trace for better understanding of the error
            return null; //In case if no connection was made
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }
        return null;
    }
}
