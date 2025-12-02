package models.people;

import DatabaseConfig.DataBaseConnectorConfigurator;
import models.items.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Author extends User{
    private ArrayList<Book> publishedBooks;
    public Author(){
        this.setUserRole("Author");
    }
    // Restored: Parametrized constructor
    public Author(String userName, String userRole, ArrayList<Book> publishedBooks){
        super(userName, userRole);
        this.publishedBooks = publishedBooks;
        this.setUserRole("Author");
    }
    // Restored: Setter for publishedBooks
    public void setPublishedBooks(ArrayList<Book> publishedBooks) {
        this.publishedBooks = publishedBooks;
    }

    // Restored: Getter for publishedBooks
    public ArrayList<Book> getPublishedBooks() {
        return publishedBooks;
    }

    public void publishBook(Book book){
        String SQLQuery = "INSERT OR IGNORE INTO booksTable ( title, isbn, authors, publishDate, numberInFavorites ) VALUES (?, ?, ?, ?, ?)";
        String insertMappingSQLQuery = "INSERT OR IGNORE INTO AuthorBookMapping (authorName, bookIsbn) VALUES (?, ?)";
        if (book.getAuthors() == null) {book.setAuthors(new ArrayList<String>());}
        book.getAuthors().add(this.getUserName());

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
}
