package Networking.server;

import DatabaseConfig.BooksTable.BooksTableOperations;
import Networking.CommandsStatics;
import models.items.Book;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

class ThreadTask implements Runnable{
    private Socket clientSocket; //Getting socket of client to connect
    private DataInputStream input;
    private DataOutputStream output;

    public ThreadTask(Socket clientSocket) throws IOException{
        this.clientSocket = clientSocket;
        //Connecting input of client and output of client to server
        input = new DataInputStream(clientSocket.getInputStream());
        output = new DataOutputStream(clientSocket.getOutputStream());

    }

    //Running the server for clients
    @Override
    public void run(){
        while(true){
            try {
                String command = input.readUTF(); //Needed to read command from input of the client
                System.out.println("Received command: " + command);
                switch (command) {
                    case CommandsStatics.GET_ALL_BOOKS:
                        handleGetAllBooks();
                        break;
                    case CommandsStatics.PUBLISH_BOOK:
                        handlePublishBook();
                        break;
                    case CommandsStatics.DELETE_BOOK:
                        handleDeleteBook();
                        break;
                    case CommandsStatics.INCREMENT_FAVORITES:
                        handleIncrementFavorites();
                        break;
                    case CommandsStatics.GET_BOOK_BY_ISBN:
                        handGetBookByIsbn();
                        break;
                    default:
                        output.writeUTF("ERROR: Unknown command.");
                        break;
                }
                output.flush();

            } catch (IOException e) {
                // Handle client disconnection
                System.out.println("Client disconnected or error: " + e.getMessage());
            }
        }

    }

    private void handGetBookByIsbn() throws IOException {
        String isbnToGetBook;
        try{
            isbnToGetBook = input.readUTF();
            Book foundBook = BooksTableOperations.getBookByIsbn(isbnToGetBook);
            if(foundBook != null){
                output.writeUTF(CommandsStatics.FOUND);

                output.writeUTF(foundBook.getTitle());
                output.writeUTF(foundBook.getIsbn());
                output.writeUTF(String.join(", ", foundBook.getAuthors()));
                output.writeUTF(foundBook.getPublishDate());
                output.writeInt(foundBook.getNumberInFavorites());
            }else{
                output.writeUTF(CommandsStatics.NOT_FOUND);
            }
        } catch (IOException e) {
            System.err.printf("DB ERROR on %s: %s", CommandsStatics.GET_BOOK_BY_ISBN, e.getMessage());
            output.writeUTF(CommandsStatics.ERROR + ": Failed to retrieve book.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleIncrementFavorites() throws IOException {
        String isbnToIncrement;
        try{
            isbnToIncrement = input.readUTF();
            BooksTableOperations.incrementFavoritesByIsbn(isbnToIncrement);
            output.writeUTF(CommandsStatics.SUCCESS);

        } catch (IOException e) {
            System.err.printf("DB ERROR on %s: " + e.getMessage(), CommandsStatics.INCREMENT_FAVORITES);
            output.writeUTF("ERROR: Failed to increment favourites of the book: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void handleGetAllBooks() throws IOException {
        try {
            ArrayList<Book> books = BooksTableOperations.getAllBooks();
            output.writeInt(books.size());

            for (Book book : books) {
                output.writeUTF(book.getTitle());
                output.writeUTF(book.getIsbn());
                String authorsString = String.join(", ", book.getAuthors());
                output.writeUTF(authorsString);
                output.writeUTF(book.getPublishDate());
                output.writeInt(book.getNumberInFavorites());
            }

        } catch (SQLException e) {
            System.err.printf("DB ERROR on %s: " + e.getMessage(), CommandsStatics.GET_ALL_BOOKS);
        }
    }
    public void sendBook(Book book, DataOutputStream out) throws IOException {
        out.writeUTF(book.getTitle());
        out.writeUTF(book.getIsbn());
        out.writeUTF(String.join(",", book.getAuthors())); // Convert List to a single String!
        out.writeUTF(book.getPublishDate());
        out.writeInt(book.getNumberInFavorites());
    }

    public void handlePublishBook() throws IOException {
        String title = input.readUTF();

        String isbn = input.readUTF();

        String authorsString = input.readUTF();

        String publishDate = input.readUTF();

        int favorites = input.readInt();

        ArrayList<String> authors = new ArrayList<>(Arrays.asList(authorsString.split(",\\s*")));

        Book newBook = new Book(title, isbn, authors, publishDate, favorites);
        try {
            BooksTableOperations.addBookToBookTableDatabase(newBook);
            output.writeUTF("SUCCESS");

        } catch (RuntimeException e) { // Catch the RuntimeException thrown by the DB method
            System.err.println("DB ERROR on PUBLISH_BOOK: " + e.getMessage());
            output.writeUTF("ERROR: Failed to publish the book");
        }
    }
    public void handleDeleteBook() throws IOException {
        String isbnToDelete;
        try {
            isbnToDelete = input.readUTF();
            BooksTableOperations.deleteBookByIsbn(isbnToDelete);
            output.writeUTF(CommandsStatics.SUCCESS);

        } catch (RuntimeException e) {
            System.err.printf("DB ERROR on %s: " + e.getMessage(), CommandsStatics.DELETE_BOOK);
            output.writeUTF("ERROR: Failed to delete book: " + e.getMessage());
        }
    }
}
public class Server {

    public void runServer(int portNumber) throws IOException{

        //Creating a socket for server and setting a particular portNumber for connection
        ServerSocket server = new ServerSocket(portNumber);
        while(true){
            try{
                System.out.println("Waiting for client connection...");
                ThreadTask threadTask = new ThreadTask(server.accept()); //Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.

                Thread thread = new Thread(threadTask); //A task of the thread  that is to be executed simultaneously;

                thread.start();//Start is needed to start the simultaneous interaction between users of client server aplpication BookApp
            }catch(IOException e){System.out.println("Client disconnected");};

        }

    }

}
