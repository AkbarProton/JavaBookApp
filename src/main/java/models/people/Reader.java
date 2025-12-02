package models.people;

import models.items.Book;

import java.util.ArrayList;

public class Reader extends User{
    private ArrayList<Book> favouriteBooks;

    //No-arg constructor
    public Reader(){
        super();
        this.setUserRole("Reader");
    }

    //Parametrized constructor
    public Reader(String userName, String userRole, ArrayList<Book> favouriteBooks){
        super(userName, userRole);
        this.favouriteBooks = favouriteBooks;
        this.setUserRole("Reader");
    }

    public void addFavouriteBook(Book newFavouriteBook){
        favouriteBooks.add(newFavouriteBook);
    }
}
