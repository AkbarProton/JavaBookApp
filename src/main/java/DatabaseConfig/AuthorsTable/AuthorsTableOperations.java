package DatabaseConfig.AuthorsTable;

import DatabaseConfig.DataBaseConnectorConfigurator;
import models.people.Author;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthorsTableOperations {
    public static void addNewAuthor(Author author){
        String sqlQueryToAddNewAuthor = "INSERT OR IGNORE INTO authorsTable (userName, userRole) VALUES (?, ?)";
        try(Connection connection = DataBaseConnectorConfigurator.getConnection()){
            PreparedStatement preparedSQLStatementAddAuthor = connection.prepareStatement(sqlQueryToAddNewAuthor);

            preparedSQLStatementAddAuthor.setString(1, author.getUserName());
            preparedSQLStatementAddAuthor.setString(2, author.getUserRole());

            preparedSQLStatementAddAuthor.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void clearDataBase() throws SQLException {
        String CLEAR_AUTHORS_TABLE = "DELETE FROM authorsTable";
        try(Connection connection = DataBaseConnectorConfigurator.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(CLEAR_AUTHORS_TABLE);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Author> getAllAuthors() throws SQLException {
        ArrayList<Author> authors = new ArrayList<Author>();
        String sqlQUERY = "SELECT * FROM authorsTable";
        try(Connection connection = DataBaseConnectorConfigurator.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQUERY);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                authors.add(mapAuthor(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authors;
    }
    public static Author mapAuthor(ResultSet resultSet) throws SQLException{
        return new Author(
                resultSet.getString("userName"),
                resultSet.getString("userRole"),
                null
        );
    }
}
