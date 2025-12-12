package org.example;
import DatabaseConfig.AuthorsTable.AuthorsTableInitializer;
import DatabaseConfig.AuthorsTable.AuthorsTableOperations;
import DatabaseConfig.BooksTable.BooksTableInitializer;
import Networking.ConnectionTestingConstants;
import Networking.client.Client;
import Networking.server.Server;
import models.items.Book;
import models.people.Author;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
//    public static ArrayList<Book> createDummyBooks() {
//        ArrayList<Book> booksFromTeam = new ArrayList<Book>();
//
//        // 1. Single Author Book
//        booksFromTeam.add(new Book(
//                "The Last Algorithm",
//                "978-0134444555",
//                new ArrayList<>(Arrays.asList("Akbarxoja")),
//                "2023-01-15",
//                120
//        ));
//
//        // 2. Multi-Author Book
//        booksFromTeam.add(new Book(
//                "Database Mastery: SQL and Beyond",
//                "978-1234567890",
//                new ArrayList<>(Arrays.asList("Akbarxoja", "Burxon")),
//                "2024-05-20",
//                350
//        ));
//
//        // 3. Book with high favorites count
//        booksFromTeam.add(new Book(
//                "Java Clean Code Patterns",
//                "978-0451524935",
//                new ArrayList<>(Arrays.asList("Burxon", "Jaxongir")),
//                "2022-11-01",
//                850
//        ));
//
//        // 4. Book with multiple authors
//        booksFromTeam.add(new Book(
//                "Mystery of the Null Pointer",
//                "978-5551112223",
//                new ArrayList<>(Arrays.asList("Akbarxoja", "Burxon", "Jaxongir", "Atabek")),
//                "2023-09-09",
//                50
//        ));
//
//        return booksFromTeam;
//    }
//    public static void setupTestDatabase() {
//        try {
//            Author author1 = new Author("Akbarxoja", "Author", new ArrayList<Book>());
//            Author author2 = new Author("Burxon", "Author", new ArrayList<Book>());
//            Author author3 = new Author("Maxkam", "Author", new ArrayList<Book>());
//            Author author4 = new Author("Atabek", "Author", new ArrayList<Book>());
//            Author author5 = new Author("Jaxongir", "Author", new ArrayList<Book>());
//
//            ArrayList<Author> allAuthors = new ArrayList<>(Arrays.asList(author1, author2, author3, author4, author5));
//
//            for (Author author : allAuthors) {
//                AuthorsTableOperations.addNewAuthor(author);
//            }
//
//            ArrayList<Book> booksToPublish = createDummyBooks();
//
//            // 4. Publish Books
//            // The Author.publishBook method iterates through the entire list of authors (book.getAuthors())
//            // and creates an entry for EACH author in the AuthorBookMapping table.
//
//            // Therefore, calling publishBook via ANY single author instance (like author1) is sufficient
//            // to correctly attribute the book to all listed authors in the database.
//            for (Book book : booksToPublish) {
//                author1.publishBook(book);
//            }
//
//            System.out.println("Successfully inserted 5 Authors and published " + booksToPublish.size() + " books.");
//
//        } catch (RuntimeException e) {
//            // Catches RuntimeException wrapped from the database operations
//            System.err.println("Failed to setup test data: " + e.getMessage());
//            throw e;
//        }
//    }
    public static void main( String[] args ) throws SQLException, IOException {
//
//        System.out.println("Starting database initialization...");
//
//        // This single line will attempt to connect and create the table.
//        BooksTableInitializer.initializeDatabase();
//        AuthorsTableInitializer.initializeDatabase();
//        System.out.println("Initialization of booksTable and authorsTable process finished.");
//
//        setupTestDatabase();
//
//        //Clearing database ( can be commented if needed)
////        AuthorsTableOperations.clearDataBase();
////        BooksTableOperations.clearDataBase();
////        AuthorBookMappingTableOperations.clearDataBase();
//
//
//
        Client thisClient = new Client(ConnectionTestingConstants.serverIP, ConnectionTestingConstants.port);
        BookAppUI.main(args);
    }
}
