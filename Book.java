import java.io.Serializable;

/**
 * Representa um livro no sistema da biblioteca.
 * Estende LibraryItem para herdar o comportamento base de itens do acervo.
 */
public class Book extends LibraryItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String author;
    private String isbn;
    private String genre;
    private int year;
    private int availableCopies;

    // Construtor completo utilizado na classe de testes e no controlador
    public Book(String title, String author, String isbn, String genre, int year, int availableCopies) {
        super(title); // Passa o título para a classe mãe (LibraryItem)
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.year = year;
        this.availableCopies = availableCopies;
    }

    // Getters e Setters
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

    // Implementação dos métodos abstratos de LibraryItem
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