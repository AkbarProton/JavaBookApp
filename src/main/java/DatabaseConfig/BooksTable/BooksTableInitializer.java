package DatabaseConfig.BooksTable;

import DatabaseConfig.DataBaseConnectorConfigurator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class BooksTableInitializer {
    private static final String CREATE_BOOK_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS booksTable (" +
                    "   isbn TEXT PRIMARY KEY," +
                    "   title TEXT NOT NULL," +
                    "   authors TEXT," +
                    "   publishDate TEXT," +
                    "   numberInFavorites INTEGER DEFAULT 0" +
                    ");";
    public static void initializeDatabase() {
        // Use try-with-resources to ensure Connection and Statement are closed
        try (Connection connection = DataBaseConnectorConfigurator.getConnection();
             // 1. Correct: Use a simple Statement for Data Definition Language (DDL) like CREATE TABLE
             Statement statement = connection.createStatement()) {

            // 2. Execute the CREATE TABLE SQL using the simple Statement
            statement.execute(CREATE_BOOK_TABLE_SQL);

            System.out.println("Database connection successful and booksTable initialized/verified.");

        } catch (SQLException e) {
            System.err.println("Error initializing the database (creating tables):");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
