package DatabaseConfig.AuthorBookMappingTable;

import DatabaseConfig.DataBaseConnectorConfigurator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


//This table is needed only to connect ( relate ) tables from authors and books together
public class AuthorBookMappingTableOperations {
    public static void clearDataBase() throws SQLException {
        String CLEAR_AUTHORS_BOOKS_MAPPING_TABLE = "DELETE FROM authorBookMapping";
        try(Connection connection = DataBaseConnectorConfigurator.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(CLEAR_AUTHORS_BOOKS_MAPPING_TABLE);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
