package DatabaseConfig.BooksTable;

import DatabaseConfig.DataBaseConnectorConfigurator;
import models.items.Book;
import models.people.Author;

import java.sql.*;
import java.util.ArrayList;

public class BooksTableOperations {
    public static void clearDataBase() throws SQLException {
        String CLEAR_BOOKS_TABLE = "DELETE FROM booksTable";
        try(Connection connection = DataBaseConnectorConfigurator.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(CLEAR_BOOKS_TABLE);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static ArrayList<Book> getAllBooks() throws SQLException {
        ArrayList<Book> books = new ArrayList<Book>();
        String sqlQUERY = "SELECT * FROM booksTable";
        try(Connection connection = DataBaseConnectorConfigurator.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQUERY);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                books.add(mapBook(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }
    public static Book mapBook(ResultSet resultSet) throws SQLException{
        // <<< FIX 2: String-to-ArrayList Conversion >>>
        String authorsString = resultSet.getString("authors");
        ArrayList<String> authorsList = new ArrayList<>();

        if (authorsString != null && !authorsString.isEmpty()) {
            // This splits the database string back into a proper Java List.
            String[] authorArray = authorsString.split(",\\s*");
            for(String authorName : authorArray) {
                authorsList.add(authorName.trim());
            }
        }
        return new Book(
                resultSet.getString("title"),
                resultSet.getString("isbn"),
                authorsList,
                resultSet.getString("publishDate"),
                resultSet.getInt("numberInFavorites")
        );
    }

}
