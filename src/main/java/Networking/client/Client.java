package Networking.client;

import Networking.CommandsStatics;
import models.items.Book;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    private Socket clientSocket; //Define a socket make an bridge between a client and server
    private DataInputStream input; //Object that would save input from the server
    private DataOutputStream output; //Object that would produce an output to the server

    public Client(String serverHost, int serverPort){
        try{

            //Notyfing user that we are connecting to bookapp
            System.out.println("Connecting to BookAppServer...");


            clientSocket = new Socket(serverHost, serverPort);

            input = new DataInputStream(clientSocket.getInputStream()); //Getting input from the server
            output = new DataOutputStream(clientSocket.getOutputStream()); //Getting output to the server
        } catch(IOException e){}
        catch(Exception e){}
    }
    public ArrayList<Book> getAllBooks() {
        ArrayList<Book> books = new ArrayList<>();

        try {
            output.writeUTF(CommandsStatics.GET_ALL_BOOKS);
            output.flush();

            int bookCount = input.readInt();//line 65: output.writeInt(books.size());

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

        // Convert the single authors String back to an ArrayList
        ArrayList<String> authors = new ArrayList<>(Arrays.asList(authorsString.split(",\\s*")));

        return new Book(title, isbn, authors, publishDate, favorites);
    }
    public void publishBook(Book book){
        try{
            output.writeUTF("PUBLISH_BOOK");
            output.writeUTF(book.getTitle());

            output.writeUTF(book.getIsbn());

            output.writeUTF(String.join(",", book.getAuthors()));

            output.writeUTF(book.getPublishDate());

            output.writeInt(book.getNumberInFavorites());

            output.flush();

            String response = input.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    public Book getBookByIsbn(String isbn) throws IOException {
        try{
            output.writeUTF(CommandsStatics.GET_BOOK_BY_ISBN);
            output.writeUTF(isbn);
            output.flush();

            String status = input.readUTF();
            if(status == CommandsStatics.FOUND){
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
