import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Central class representing the library system, which manages the collection of library items (books, DVDs, etc.), students, and loans.
public class Library implements Serializable {
   // Serial version UID for serialization compatibility. This ensures that during deserialization, the class matches the version used during serialization, preventing InvalidClassExceptions if the class structure changes.
    private static final long serialVersionUID = 1L;

    // Collections to store the library items, students, and loans. 
    // These are initialized as ArrayLists to allow dynamic resizing as items, students, and loans are added or removed from the library system.
    private ArrayList<LibraryItem> items = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Loan> loans = new ArrayList<>();

    // Getters for the collections of library items, students, and loans. 
    // These methods ensure that the collections are initialized before being accessed, 
    // preventing NullPointerExceptions when trying to access or modify these lists.
    public ArrayList<LibraryItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    // Getter for the list of students in the library system. 
    // It checks if the students list is null and initializes it if necessary before returning it.
    public ArrayList<Student> getStudents() {
        if (students == null) {
            students = new ArrayList<>();
        }
        return students;
    }

    // Getter for the list of loans in the library system. 
    // It checks if the loans list is null and initializes it if necessary before returning it.
    public ArrayList<Loan> getLoans() {
        if (loans == null) {
            loans = new ArrayList<>();
        }
        return loans;
    }

    // Getter for the list of books in the library system. 
    // It iterates through the library items and filters only those that are instances of Book, returning a new list containing only the books.
    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        for (LibraryItem item : getItems()) {
            if (item instanceof Book) {
                books.add((Book) item);
            }
        }
        return books;
    }

    // Method to add a generic library item to the collection. 
    // This allows for adding different types of items (e.g., books, DVDs) to the library system.
    public void addItem(LibraryItem item) {
        getItems().add(item);
    }

    // Method to remove a generic library item from the collection. 
    // It checks if the item is currently on loan before allowing it to be removed, ensuring
    public void addBook(Book book) {
        validateBook(book, null);
        getItems().add(book);
    }

    // Method to update the details of an existing book in the library system.
    // It takes the book to be updated and the new details as parameters, 
    // validates the new details, and then updates the book's attributes accordingly.
    public void updateBook(Book book, String title, String author, String isbn, String genre, int year, int availableCopies) {
        validateBook(new Book(title, author, isbn, genre, year, availableCopies), book);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setGenre(genre);
        book.setYear(year);
        book.setAvailableCopies(availableCopies);
    }

    // Method to remove a book from the library system. 
    // It checks if the book is currently on loan before allowing it to be removed, 
    // ensuring that books with active loans cannot be deleted from the system.
    public void removeBook(Book book) {
        for (Loan loan : getLoans()) {
            if (loan.getBook() == book && loan.isActive()) {
                throw new IllegalStateException("Nao e possivel remover um livro com emprestimo ativo.");
            }
        }
        getItems().remove(book);
    }

    // Method to find a book in the library system by its ISBN.
    public Book findBookByIsbn(String isbn) {
        for (Book b : getBooks()) {
            if (b.getIsbn().equals(isbn)) {
                return b;
            }
        }
        return null;
    }

    // Method to add a new student to the library system.
    public void addStudent(Student student) {
        validateStudent(student, null);
        getStudents().add(student);
    }

    // Method to update the details of an existing student in the library system.
    public void updateStudent(Student student, String name, String ra, String contact, String password) {
        validateStudent(new Student(name, ra, contact, password), student);
        student.setName(name);
        student.setRa(ra);
        student.setContact(contact);
        student.setPassword(password);
    }

    // Method to remove a student from the library system.
    public void removeStudent(Student student) {
        for (Loan loan : getLoans()) {
            if (loan.getStudent() == student && loan.isActive()) {
                throw new IllegalStateException("Nao e possivel remover um usuario com emprestimo ativo.");
            }
        }
        getStudents().remove(student);
    }

    // Method to find a student in the library system by their RA.
    public Student findStudentByRa(String ra) {
        for (Student s : getStudents()) {
            if (s.getRa().equals(ra)) {
                return s;
            }
        }
        return null;
    }

    // Method to perform a loan operation, which involves checking if the book is available, 
    // verifying that the student does not have overdue loans, updating the book's available copies, 
    // creating a new Loan object, and adding it to the library's list of loans and the student's loan history.
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

    // Method to perform a return operation, 
    // which involves marking the loan as returned and updating the book's available copies accordingly.
    public void performReturn(Loan loan) {
        if (!loan.isActive()) {
            throw new IllegalStateException("Este emprestimo ja foi devolvido.");
        }
        loan.markReturned();
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
    }

    // Private helper methods for validating the details of books and students before they are added or updated in the library system.
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

    // Validation method for student details, ensuring that all required fields are filled and that the RA is unique within the system.
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

    // Helper method to validate that a required field is not null or empty.
    private void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o campo: " + fieldName + ".");
        }
    }

    // Method to rebuild the loan histories of all students after loading the library data from a file.
    // This is necessary because the loan history is not directly serialized with the student objects,
    // and needs to be reconstructed based on the existing loans in the library system.
    public void rebuildStudentHistories() {
        // Limpa o historico atual de todos os estudantes para evitar duplicatas
        for (Student s : getStudents()) {
            s.clearLoanHistory();
        }
        // Percorre todos os emprestimos existentes e os readiciona ao respectivo estudante
        for (Loan loan : getLoans()) {
            if (loan.getStudent() != null) {
                loan.getStudent().addLoanToHistory(loan);
            }
        }
    }
}