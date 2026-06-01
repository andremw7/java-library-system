import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Classe central de dominio que concentra e gerencia as regras de negocio da biblioteca
public class Library implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<LibraryItem> items = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Loan> loans = new ArrayList<>();

    public ArrayList<LibraryItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public ArrayList<Student> getStudents() {
        if (students == null) {
            students = new ArrayList<>();
        }
        return students;
    }

    public ArrayList<Loan> getLoans() {
        if (loans == null) {
            loans = new ArrayList<>();
        }
        return loans;
    }

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        for (LibraryItem item : getItems()) {
            if (item instanceof Book) {
                books.add((Book) item);
            }
        }
        return books;
    }

    public void addItem(LibraryItem item) {
        getItems().add(item);
    }

    public void addBook(Book book) {
        validateBook(book, null);
        getItems().add(book);
    }

    public void updateBook(Book book, String title, String author, String isbn, String genre, int year, int availableCopies) {
        validateBook(new Book(title, author, isbn, genre, year, availableCopies), book);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setGenre(genre);
        book.setYear(year);
        book.setAvailableCopies(availableCopies);
    }

    public void removeBook(Book book) {
        for (Loan loan : getLoans()) {
            if (loan.getBook() == book && loan.isActive()) {
                throw new IllegalStateException("Nao e possivel remover um livro com emprestimo ativo.");
            }
        }
        getItems().remove(book);
    }

    public Book findBookByIsbn(String isbn) {
        for (Book b : getBooks()) {
            if (b.getIsbn().equals(isbn)) {
                return b;
            }
        }
        return null;
    }

    public void addStudent(Student student) {
        validateStudent(student, null);
        getStudents().add(student);
    }

    // Metodo atualizado para receber os 5 parametros necessarios incluindo a senha
    public void updateStudent(Student student, String name, String ra, String contact, String password) {
        validateStudent(new Student(name, ra, contact, password), student);
        student.setName(name);
        student.setRa(ra);
        student.setContact(contact);
        student.setPassword(password);
    }

    public void removeStudent(Student student) {
        for (Loan loan : getLoans()) {
            if (loan.getStudent() == student && loan.isActive()) {
                throw new IllegalStateException("Nao e possivel remover um usuario com emprestimo ativo.");
            }
        }
        getStudents().remove(student);
    }

    public Student findStudentByRa(String ra) {
        for (Student s : getStudents()) {
            if (s.getRa().equals(ra)) {
                return s;
            }
        }
        return null;
    }

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

    public void performReturn(Loan loan) {
        if (!loan.isActive()) {
            throw new IllegalStateException("Este emprestimo ja foi devolvido.");
        }
        loan.markReturned();
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
    }

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

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o campo: " + fieldName + ".");
        }
    }

    // Metodo adicionado para resolver o erro de compilacao do DataManager
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