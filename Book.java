import java.io.Serializable;

/**
 * Class representing a book in the library system, 
 * which extends the abstract class LibraryItem and implements Serializable for object serialization.
 * 
 * @author André Watanabe
 * @author Pedro Zanutto
 * @author Isaac Ferreira
 * @version 1.0
 */
public class Book extends LibraryItem implements Serializable {
    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The author of the book.
     */
    private String author;

    /**
     * The ISBN (International Standard Book Number) identifier for the book.
     */
    private String isbn;

    /**
     * The genre or classification of the book.
     */
    private String genre;

    /**
     * The publication year of the book.
     */
    private int year;

    /**
     * The number of available copies currently in stock in the library.
     */
    private int availableCopies;

    /**
     * Constructor for the Book class, which initializes all the attributes of the book, including the title inherited from LibraryItem.
     * 
     * @param title the title of the book
     * @param author the author of the book
     * @param isbn the ISBN code of the book
     * @param genre the genre of the book
     * @param year the publication year of the book
     * @param availableCopies the total number of available copies
     */
    public Book(String title, String author, String isbn, String genre, int year, int availableCopies) {
        super(title); // Passa o título para a classe mãe (LibraryItem)
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.year = year;
        this.availableCopies = availableCopies;
    }

    /**
     * Gets the author of the book.
     * 
     * @return the book's author
     */
    public String getAuthor() { return author; }

    /**
     * Sets the author of the book.
     * 
     * @param author the new author to set
     */
    public void setAuthor(String author) { this.author = author; }

    /**
     * Gets the ISBN of the book.
     * 
     * @return the book's ISBN
     */
    public String getIsbn() { return isbn; }

    /**
     * Sets the ISBN of the book.
     * 
     * @param isbn the new ISBN to set
     */
    public void setIsbn(String isbn) { this.isbn = isbn; }

    /**
     * Gets the genre of the book.
     * 
     * @return the book's genre
     */
    public String getGenre() { return genre; }

    /**
     * Sets the genre of the book.
     * 
     * @param genre the new genre to set
     */
    public void setGenre(String genre) { this.genre = genre; }

    /**
     * Gets the publication year of the book.
     * 
     * @return the publication year
     */
    public int getYear() { return year; }

    /**
     * Sets the publication year of the book.
     * 
     * @param year the new publication year to set
     */
    public void setYear(int year) { this.year = year; }

    /**
     * Gets the current number of available copies for this book.
     * 
     * @return the number of available copies
     */
    public int getAvailableCopies() { return availableCopies; }

    /**
     * Sets the number of available copies for this book.
     * 
     * @param availableCopies the new count of available copies
     */
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    /**
     * Implementation of the abstract method from LibraryItem, providing specific logic for checking availability for a book.
     * 
     * @return true if there is at least one copy available, false otherwise
     */
    @Override
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /**
     * Implementation of the abstract method from LibraryItem, providing specific logic for getting details for a book.
     * 
     * @return a formatted string with specific details of the book
     */
    @Override
    public String getDetails() {
        return String.format("Autor: %s | ISBN: %s | Gênero: %s | Ano: %d | Cópias: %d", 
                author, isbn, genre, year, availableCopies);
    }

    /**
     * Returns a friendly string representation of the book for clear display inside JComboBox graphical interface elements.
     * 
     * @return a string representing the book item
     */
    @Override
    public String toString() {
        return title + " (" + author + ")";
    }
}