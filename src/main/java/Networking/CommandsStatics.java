package Networking;
import java.util.List;

public final class CommandsStatics {
    public static final String GET_ALL_BOOKS = "GET_ALL_BOOKS";
    public static final String PUBLISH_BOOK = "PUBLISH_BOOK";
    public static final String DELETE_BOOK = "DELETE_BOOK";
    public static final String INCREMENT_FAVORITES = "INCREMENT_FAVORITES";
    public static final String GET_BOOK_BY_ISBN = "GET_BOOK_BY_ISBN";
    public static final String UPDATE_PROFILE = "UPDATE_PROFILE";
    public static final String PROMOTE_AUTHOR = "PROMOTE_AUTHOR";
    public static final String LOGIN = "LOGIN";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String FOUND = "FOUND";
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    public static final List<String> ALL_COMMANDS = List.of(
            GET_ALL_BOOKS,
            PUBLISH_BOOK,
            DELETE_BOOK,
            INCREMENT_FAVORITES,
            GET_BOOK_BY_ISBN,
            UPDATE_PROFILE,
            PROMOTE_AUTHOR,
            LOGIN
    );
    private CommandsStatics() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }
}