import java.io.Serializable;

// Class representing a book in the library system, 
// which extends the abstract class LibraryItem and implements Serializable for object serialization.
public class Book extends LibraryItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // Attributes specific to the Book class, including the author, ISBN, genre, publication year, and the number of available copies.
    private String author;
    private String isbn;
    private String genre;
    private int year;
    private int availableCopies;

    // Constructor for the Book class, which initializes all the attributes of the book, including the title inherited from LibraryItem.
    public Book(String title, String author, String isbn, String genre, int year, int availableCopies) {
        super(title); // Passa o título para a classe mãe (LibraryItem)
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.year = year;
        this.availableCopies = availableCopies;
    }

    // Getters e Setters for the attributes of the Book class, allowing other classes to access and modify the details of the book as needed.
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    // Implementation of the abstract methods from LibraryItem, providing specific logic for checking availability and getting details for a book.
    @Override
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    @Override
    public String getDetails() {
        return String.format("Autor: %s | ISBN: %s | Gênero: %s | Ano: %d | Cópias: %d", 
                author, isbn, genre, year, availableCopies);
    }

    // Exibição amigável do livro dentro dos JComboBox da interface gráfica
    @Override
    public String toString() {
        return title + " (" + author + ")";
    }
}