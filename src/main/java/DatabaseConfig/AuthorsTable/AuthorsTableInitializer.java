package DatabaseConfig.AuthorsTable;

import DatabaseConfig.DataBaseConnectorConfigurator;
import models.people.Author;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthorsTableInitializer {
    private static final String CREATE_AUTHORS_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS authorsTable (" +
                    "   userName TEXT PRIMARY KEY," +
                    "   userRole TEXT NOT NULL" +
                    ");";

    //To organize one to many relationship from booksTable and authorsTable
    private static final String CREATE_AUTHOR_BOOK_MAPPING_SQL =
            "CREATE TABLE IF NOT EXISTS AuthorBookMapping (" +
                    "   authorName TEXT NOT NULL," +
                    "   bookIsbn TEXT NOT NULL," +
                    "   PRIMARY KEY (authorName, bookIsbn)," +
                    "   FOREIGN KEY (authorName) REFERENCES authorsTable(userName) ON DELETE CASCADE," +
                    "   FOREIGN KEY (bookIsbn) REFERENCES booksTable(isbn) ON DELETE CASCADE" +
                    ");";
    public static void initializeDatabase() {
        // Use try-with-resources to ensure Connection and Statement are closed
        try (Connection connection = DataBaseConnectorConfigurator.getConnection();
             // 1. Correct: Use a simple Statement for Data Definition Language (DDL) like CREATE TABLE
             Statement statement = connection.createStatement()) {

            // 2. Execute the CREATE TABLE SQL using the simple Statement
            statement.execute(CREATE_AUTHORS_TABLE_SQL);
            statement.execute(CREATE_AUTHOR_BOOK_MAPPING_SQL);

            System.out.println("Database connection successful and authorsTable initialized/verified.");

        } catch (SQLException e) {
            System.err.println("Error initializing the database (creating tables):");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
