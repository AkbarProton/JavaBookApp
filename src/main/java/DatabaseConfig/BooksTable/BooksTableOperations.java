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

    public static void addBookToBookTableDatabase(Book book){
        String SQLQuery = "INSERT OR IGNORE INTO booksTable ( title, isbn, authors, publishDate, numberInFavorites ) VALUES (?, ?, ?, ?, ?)";
        String insertMappingSQLQuery = "INSERT OR IGNORE INTO AuthorBookMapping (authorName, bookIsbn) VALUES (?, ?)";
        if (book.getAuthors() == null) {book.setAuthors(new ArrayList<String>());}

        try(Connection connection = DataBaseConnectorConfigurator.getConnection()){
            PreparedStatement preparedSQLStatementPublishingBook = connection.prepareStatement(SQLQuery);

            preparedSQLStatementPublishingBook.setString(1, book.getTitle());
            preparedSQLStatementPublishingBook.setString(2, book.getIsbn());
            /*
             * For SQLite, you typically need to store multiple authors as a single string (e.g., separating them with a delimiter like commas or semicolons) in a TEXT column,
             * and then parse that string back into an ArrayList when reading the book data.*/
            String authorsString = String.join(", ", book.getAuthors());
            preparedSQLStatementPublishingBook.setString(3, authorsString);

            preparedSQLStatementPublishingBook.setString(4, book.getPublishDate());
            preparedSQLStatementPublishingBook.setInt(5, book.getNumberInFavorites());

            preparedSQLStatementPublishingBook.executeUpdate();
            try (PreparedStatement preparedSQLStatementMapping = connection.prepareStatement(insertMappingSQLQuery)) {
                //This loop iterates through all authors from the book author list in authorsTable and set's authorship
                for (String authorName : book.getAuthors()) {
                    preparedSQLStatementMapping.setString(1, authorName);
                    preparedSQLStatementMapping.setString(2, book.getIsbn());
                    preparedSQLStatementMapping.addBatch();
                }

                // Execute all batched insertions
                preparedSQLStatementMapping.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void deleteBookByIsbn(String isbn) {
        String SQLQuery = "DELETE FROM booksTable WHERE isbn = ?";
        try (Connection connection = DataBaseConnectorConfigurator.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery)) {

            preparedStatement.setString(1, isbn);
            int rowsAffected = preparedStatement.executeUpdate();

            //It means no row wwith this isbn is found
            if (rowsAffected == 0) {
                System.out.println("No book found to delete with ISBN: " + isbn);
            }

        } catch (SQLException e) {
            // Maintain consistency by re-throwing as RuntimeException
            throw new RuntimeException("Error deleting book: " + e.getMessage(), e);
        }
    }
    public static void incrementFavoritesByIsbn(String isbn){
        String SQLQuery = "UPDATE booksTable SET numberInFavorites = numberInFavorites + 1 WHERE isbn = ?";
        try (Connection connection = DataBaseConnectorConfigurator.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery)) {

            preparedStatement.setString(1, isbn);
            int row = preparedStatement.executeUpdate();
            if(row == 0){
                System.out.println("No book with thgat isbn is found");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error incrementing favourites of the book: " + e.getMessage(), e);
        }
    }
    public static Book getBookByIsbn(String isbn) throws SQLException {
        String SQLQuery = "SELECT * FROM booksTable WHERE isbn = ?";
        try(Connection connection = DataBaseConnectorConfigurator.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery);
            preparedStatement.setString(1, isbn);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapBook(resultSet);
            } else {
                return null;
            }
        } catch (RuntimeException e) {
            System.out.println("Error with getting  a book by isbn");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
