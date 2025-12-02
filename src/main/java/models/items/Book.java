package models.items;

import java.util.ArrayList;

public class Book {
    private String title;
    private String isbn;
    private ArrayList<String> authors;
    private String publishDate;
    private int numberInFavorites;//Number of times that this istance is in favourites by other users

    public Book(){}
    public Book(String title, String isbn, ArrayList<String> authors, String publishDate, int numberInFavorites){
        this.title = title;
        this.isbn = isbn;
        this.authors = authors;
        this.publishDate = publishDate;
        this.numberInFavorites = numberInFavorites;
    }


    //set methods

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public void setNumberInFavorites(int numberInFavorites) {
        this.numberInFavorites = numberInFavorites;
    }

    //get methods


    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public int getNumberInFavorites() {
        return numberInFavorites;
    }

    public void incrementNumberInFavorites(){
        this.numberInFavorites++;
    }

}
