package Networking.client;

import Networking.CommandsStatics;
import models.items.Book;
import models.people.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;

    public void updateUser(String username, String email) throws IOException {
        if (output == null) throw new IOException("Connection streams are not initialized.");

        try {
            output.writeUTF(CommandsStatics.UPDATE_PROFILE);

            output.writeUTF(username);
            output.writeUTF(email);
            output.flush();

            String response = input.readUTF();
            if (!CommandsStatics.SUCCESS.equals(response)) {
                throw new IOException("Server failed to update profile: " + response);
            }
        } catch (IOException e) {
            throw e;
        }
    }


    public Client(String serverHost, int serverPort){
        try{
            System.out.println("Connecting to BookAppServer...");

            clientSocket = new Socket(serverHost, serverPort);

            output = new DataOutputStream(clientSocket.getOutputStream());
            input = new DataInputStream(clientSocket.getInputStream());
        } catch(IOException e){
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    public ArrayList<Book> getAllBooks() {
        ArrayList<Book> books = new ArrayList<>();

        try {
            output.writeUTF(CommandsStatics.GET_ALL_BOOKS);
            output.flush();

            int bookCount = input.readInt();

            for (int i = 0; i < bookCount; i++) {
                String title = input.readUTF();
                String isbn = input.readUTF();
                String authorsString = input.readUTF();
                String publishDate = input.readUTF();
                int favorites = input.readInt();
                ArrayList<String> authors = new ArrayList<>(Arrays.asList(authorsString.split(",\\s*")));

                Book book = new Book(title, isbn, authors, publishDate, favorites);
                books.add(book);
            }

        } catch (IOException e) {
            System.err.println("Error requesting books: " + e.getMessage());
        }
        return books;
    }

    public Book receiveBook(DataInputStream in) throws IOException {
        String title = in.readUTF();
        String isbn = in.readUTF();
        String authorsString = in.readUTF();
        String publishDate = in.readUTF();
        int favorites = in.readInt();

        ArrayList<String> authors = new ArrayList<>(Arrays.asList(authorsString.split(",\\s*")));

        return new Book(title, isbn, authors, publishDate, favorites);
    }

    public void publishBook(Book book){
        try{
            output.writeUTF(CommandsStatics.PUBLISH_BOOK);

            output.writeUTF(book.getTitle());
            output.writeUTF(book.getIsbn());

            output.writeUTF(String.join(",", book.getAuthors()));

            output.writeUTF(book.getPublishDate());
            output.writeInt(book.getNumberInFavorites());

            output.flush();

            String response = input.readUTF();

            if (!CommandsStatics.SUCCESS.equals(response)) {
                System.err.println("Server rejected publication: " + response);
            }

        } catch (IOException e) {
            throw new RuntimeException("Network error during book publication: " + e.getMessage(), e);
        }
    }

    public String deleteBook(String isbn) {
        try {
            output.writeUTF(CommandsStatics.DELETE_BOOK);
            output.writeUTF(isbn);
            output.flush();
            return input.readUTF();

        } catch (IOException e) {
            System.err.println("Error requesting book deletion: " + e.getMessage());
            return "NETWORK_ERROR: " + e.getMessage();
        }
    }

    public String incrementFavorites(String isbn){
        try {
            output.writeUTF(CommandsStatics.INCREMENT_FAVORITES);
            output.writeUTF(isbn);
            output.flush();
            return input.readUTF();

        } catch (IOException e) {
            System.err.println("Error requesting incrementing favourites: " + e.getMessage());
            return "NETWORK_ERROR: " + e.getMessage();
        }
    }

    public void promoteToAuthor(String username) throws IOException {
        if (output == null) throw new IOException("Connection streams are not initialized.");

        try {
            output.writeUTF(CommandsStatics.PROMOTE_AUTHOR);
            output.writeUTF(username);
            output.flush();
            String response = input.readUTF();
            if (!CommandsStatics.SUCCESS.equals(response)) {
                throw new IOException("Server failed to promote account: " + response);
            }
        } catch (IOException e) {
            throw e;
        }
    }
    public User login(String username, String password) throws IOException {
        if (output == null) throw new IOException("Connection streams are not initialized.");

        try {
            output.writeUTF(CommandsStatics.LOGIN);
            output.writeUTF(username);
            output.writeUTF(password);
            output.flush();

            String response = input.readUTF();

            if (CommandsStatics.SUCCESS.equals(response)) {
                String loggedInUsername = input.readUTF();
                String userRole = input.readUTF();
                return new User(loggedInUsername, userRole);
            } else {
                throw new IOException("Login failed: " + response);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public Book getBookByIsbn(String isbn) throws IOException {
        try{
            output.writeUTF(CommandsStatics.GET_BOOK_BY_ISBN);
            output.writeUTF(isbn);
            output.flush();

            String status = input.readUTF();
            if(status.equals(CommandsStatics.FOUND)){
                String title = input.readUTF();
                String receivedIsbn = input.readUTF();
                String authorsString = input.readUTF();
                String publishDate = input.readUTF();
                int favorites = input.readInt();

                ArrayList<String> authors = new ArrayList<>(Arrays.asList(authorsString.split(",\\s*")));

                return new Book(title, receivedIsbn, authors, publishDate, favorites);
            }else if (status.equals(CommandsStatics.NOT_FOUND)) {
                System.out.println("Book with ISBN " + isbn + " not found.");
                return null;
            }else {
                System.err.println("Server returned an error or unexpected status: " + status);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error requesting book by isbn: " + e.getMessage());
        }
        return null;
    }
}