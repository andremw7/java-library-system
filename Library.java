import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the central library management system.
 *
 * <p>This class is responsible for managing the collection of library items,
 * registered students, and loan operations. It provides methods for adding,
 * updating, removing, and searching books and students, as well as handling
 * loans, returns, and data validation.</p>
 *
 * <p>The class also supports serialization, allowing the library state to be
 * saved and restored between application executions.</p>
 *
 * @author André Watanabe
 * @author Pedro Zanutto
 * @author Isaac Ferreira
 * @version 1.0
 */
public class Library implements Serializable {
    /**
     * Serial version UID for serialization compatibility. This ensures that during deserialization, 
     * the class matches the version used during serialization, preventing InvalidClassExceptions 
     * if the class structure changes.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Collection to store the library items. 
     * Initialized as an ArrayList to allow dynamic resizing as items are added or removed from the library system.
     */
    private ArrayList<LibraryItem> items = new ArrayList<>();

    /**
     * Collection to store the students.
     * Initialized as an ArrayList to allow dynamic resizing as students are added or removed from the library system.
     */
    private ArrayList<Student> students = new ArrayList<>();

    /**
     * Collection to store the loans.
     * Initialized as an ArrayList to allow dynamic resizing as loans are added or removed from the library system.
     */
    private ArrayList<Loan> loans = new ArrayList<>();

    /**
     * Returns the list of library items managed by the system.
     *
     * <p>If the collection has not yet been initialized, a new empty list is
     * created before being returned.</p>
     *
     * @return the list of library items.
     */
     public ArrayList<LibraryItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    /**
     * Returns the list of registered students.
     *
     * <p>If the collection has not yet been initialized, a new empty list is
     * created before being returned.</p>
     *
     * @return the list of students.
     */
    public ArrayList<Student> getStudents() {
        if (students == null) {
            students = new ArrayList<>();
        }
        return students;
    }

    /**
     * Returns the list of loans currently managed by the library.
     *
     * <p>If the collection has not yet been initialized, a new empty list is
     * created before being returned.</p>
     *
     * @return the list of loans.
     */
    public ArrayList<Loan> getLoans() {
        if (loans == null) {
            loans = new ArrayList<>();
        }
        return loans;
    }

    /**
     * Returns a list containing only the books stored in the library system.
     *
     * <p>This method filters the library items collection and includes only
     * objects that are instances of {@code Book}.</p>
     * * @return a list containing all books in the library.
     */
    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        for (LibraryItem item : getItems()) {
            if (item instanceof Book) {
                books.add((Book) item);
            }
        }
        return books;
    }

    /**
     * Adds a new item to the library.
     * * @param item the library item to be added.
     */
    public void addItem(LibraryItem item) {
        getItems().add(item);
    }

    /**
     * Adds a new book to the library after validating its information.
     *
     * @param book the book to be added.
     *
     * @throws IllegalArgumentException if the book contains invalid data or
     * conflicts with an existing ISBN.
     */
    public void addBook(Book book) {
        validateBook(book, null);
        getItems().add(book);
    }

    /**
     * Updates the information of an existing book.
     *
     * @param book the book to be updated.
     * @param title the new title.
     * @param author the new author.
     * @param isbn the new ISBN.
     * @param genre the new genre.
     * @param year the publication year.
     * @param availableCopies the number of available copies.
     *
     * @throws IllegalArgumentException if the new data is invalid.
     */
    public void updateBook(Book book, String title, String author, String isbn, String genre, int year, int availableCopies) {
        validateBook(new Book(title, author, isbn, genre, year, availableCopies), book);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setGenre(genre);
        book.setYear(year);
        book.setAvailableCopies(availableCopies);
    }

    /**
     * Removes a book from the library.
     *
     * <p>A book cannot be removed while it has active loans.</p>
     *
     * @param book the book to be removed.
     *
     * @throws IllegalStateException if the book has active loans.
     */
    public void removeBook(Book book) {
        for (Loan loan : getLoans()) {
            if (loan.getBook() == book && loan.isActive()) {
                throw new IllegalStateException("Nao e possivel remover um livro com emprestimo ativo.");
            }
        }
        getItems().remove(book);
    }

    /**
     * Searches for a book by its ISBN.
     *
     * @param isbn the ISBN to search for.
     * @return the corresponding book if found, or {@code null} otherwise.
     */
    public Book findBookByIsbn(String isbn) {
        for (Book b : getBooks()) {
            if (b.getIsbn().equals(isbn)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Registers a new student in the library.
     *
     * @param student the student to be added.
     *
     * @throws IllegalArgumentException if the student's information is invalid.
     */
    public void addStudent(Student student) {
        validateStudent(student, null);
        getStudents().add(student);
    }

    /**
     * Updates the information of an existing student.
     *
     * @param student the student to update.
     * @param name the new name.
     * @param ra the new registration number.
     * @param contact the new contact information.
     * @param password the new password.
     */
    public void updateStudent(Student student, String name, String ra, String contact, String password) {
        validateStudent(new Student(name, ra, contact, password), student);
        student.setName(name);
        student.setRa(ra);
        student.setContact(contact);
        student.setPassword(password);
    }

    /**
     * Removes a student from the library.
     *
     * <p>A student cannot be removed while having active loans.</p>
     *
     * @param student the student to remove.
     *
     * @throws IllegalStateException if the student has active loans.
     */
    public void removeStudent(Student student) {
        for (Loan loan : getLoans()) {
            if (loan.getStudent() == student && loan.isActive()) {
                throw new IllegalStateException("Nao e possivel remover um usuario com emprestimo ativo.");
            }
        }
        getStudents().remove(student);
    }

    /**
     * Searches for a student using the registration number (RA).
     *
     * @param ra the student's registration number.
     * @return the corresponding student if found, or {@code null} otherwise.
     */
    public Student findStudentByRa(String ra) {
        for (Student s : getStudents()) {
            if (s.getRa().equals(ra)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Performs a loan operation for a student.
     *
     * <p>The method verifies book availability and checks whether the student
     * has overdue loans before creating a new loan record.</p>
     *
     * @param book the book to be borrowed.
     * @param student the student requesting the loan.
     *
     * @return the newly created loan.
     *
     * @throws IllegalStateException if no copies are available or the student
     * has overdue loans.
     */
    public Loan performLoan(Book book, Student student) {
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("Nao ha copias disponiveis deste livro.");
        }
        for (Loan loan : getLoans()) {
            if (loan.getStudent() == student && loan.isOverdue()) {
                throw new IllegalStateException("Usuario possui emprestimos atrasados pendentes.");
            }
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Loan loan = new Loan(book, student);
        getLoans().add(loan);
        student.addLoanToHistory(loan);
        return loan;
    }

    /**
     * Processes the return of a borrowed book.
     *
     * <p>The loan is marked as returned and the available copies of the book
     * are updated accordingly.</p>
     *
     * @param loan the loan to be closed.
     *
     * @throws IllegalStateException if the loan has already been returned.
     */
    public void performReturn(Loan loan) {
        if (!loan.isActive()) {
            throw new IllegalStateException("Este emprestimo ja foi devolvido.");
        }
        loan.markReturned();
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
    }

    /**
     * Private helper method for validating the details of books before they are added or updated in the library system.
     * * @param book the book to be validated.
     * @param currentBook the current book being updated, or null if adding a new book.
     * * @throws IllegalArgumentException if required fields are missing, year is invalid, copies are negative, or ISBN is duplicated.
     */
    private void validateBook(Book book, Book currentBook) {
        validateRequired(book.getTitle(), "Titulo");
        validateRequired(book.getAuthor(), "Autor");
        validateRequired(book.getIsbn(), "ISBN");
        validateRequired(book.getGenre(), "Genero");
        if (book.getYear() < 0 || book.getYear() > 2100) {
            throw new IllegalArgumentException("Ano invalido.");
        }
        if (book.getAvailableCopies() < 0) {
            throw new IllegalArgumentException("A quantidade de copias nao pode ser negativa.");
        }
        Book existing = findBookByIsbn(book.getIsbn());
        if (existing != null && existing != currentBook) {
            throw new IllegalArgumentException("Ja existe um livro com este ISBN.");
        }
    }

    /**
     * Validation method for student details, ensuring that all required fields are filled and that the RA is unique within the system.
     * * @param student the student to be validated.
     * @param currentStudent the current student being updated, or null if adding a new student.
     * * @throws IllegalArgumentException if required fields are missing or RA is duplicated.
     */
    private void validateStudent(Student student, Student currentStudent) {
        validateRequired(student.getName(), "Nome");
        validateRequired(student.getRa(), "ID/RA");
        validateRequired(student.getContact(), "Contato");
        validateRequired(student.getPassword(), "Senha");
        Student existing = findStudentByRa(student.getRa());
        if (existing != null && existing != currentStudent) {
            throw new IllegalArgumentException("Ja existe um usuario com este ID/RA.");
        }
    }

    /**
     * Helper method to validate that a required field is not null or empty.
     * * @param value the value to be checked.
     * @param fieldName the name of the field, used in the exception message.
     * * @throws IllegalArgumentException if the value is null or empty.
     */
    private void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o campo: " + fieldName + ".");
        }
    }

    /**
     * Rebuilds the loan history of every student.
     *
     * <p>This method is typically executed after loading serialized data to
     * restore each student's loan history based on the existing loan records.</p>
     */
    public void rebuildStudentHistories() {
        // Clears the current history of all students to avoid duplicates
        for (Student s : getStudents()) {
            s.clearLoanHistory();
        }
        // Iterates through all existing loans and re-adds them to the respective student
        for (Loan loan : getLoans()) {
            if (loan.getStudent() != null) {
                loan.getStudent().addLoanToHistory(loan);
            }
        }
    }
}