import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Class representing a loan of a book to a student in the library system.
public class Loan implements Serializable {
    // Defining a serial version UID for serialization compatibility. This ensures that during deserialization,
    // the class matches the version used during serialization, preventing InvalidClassExceptions if the class structure changes.
    private static final long serialVersionUID = 1L;
    
    // Constants for loan management, including the standard loan period (14 days) and the fine amount per day of delay (R$ 2,00).
    public static final int LOAN_DAYS = 14; // Prazo padrão de empréstimo (14 dias)
    public static final double FINE_PER_DAY = 2.0; // Valor da multa por dia de atraso (R$ 2,00)

    // Atributes of the Loan class, including references to the Book and Student involved in the loan, 
    // as well as dates for the loan, due date, and return date, and a flag to indicate if any fine has been paid.
    private Book book;               // book being loaned
    private Student student;         // student who is borrowing the book
    private LocalDate loanDate;      // Date when the loan was made
    private LocalDate dueDate;       // Date when the loan is due for return
    private LocalDate returnDate;    // Date when the book was returned (null if not yet returned)
    private boolean finePaid = false; // Flag to indicate if the fine for this loan has been paid (true if paid, false if pending)

    // Constructor for creating a new loan with the current date as the loan date and a due date set to 14 days later.
    public Loan(Book book, Student student) {
        this(book, student, LocalDate.now(), LocalDate.now().plusDays(LOAN_DAYS));
    }

    // Constructor for creating a new loan with specified loan and due dates. 
    // This allows for more flexibility in testing and managing loans with custom dates.
    public Loan(Book book, Student student, LocalDate loanDate, LocalDate dueDate) {
        this.book = book;
        this.student = student;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    // GETTERS and SETTERS for the attributes of the Loan class

    // Associates a book with this loan.
    // @param book The Book object being loaned.
    public Book getBook() {
        return book;
    }

    // student associated with this loan.
    // @param student The Student object who is borrowing the book.
    public Student getStudent() {
        return student;
    }

    // Date when the loan was made.
    // @return The date of the loan.
    public LocalDate getLoanDate() {
        return loanDate;
    }

    // Due date for returning the book.
    // @return The date when the loan is due for return.
    public LocalDate getDueDate() {
        return dueDate;
    }

 
    // Define a new due date for the loan, allowing for extensions or adjustments to the original due date.
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // Date when the book was returned. This will be null if the book has not yet been returned.
    public LocalDate getReturnDate() {
        return returnDate;
    }

    // Verify if the loan is currently active, meaning the book has not yet been returned.
    // @return true if the loan is active (not yet returned); false if the book
    public boolean isActive() {
        return returnDate == null;
    }

    // Check if the loan is overdue, which means it is still active and the current date is past the due date.
    // @return true if the loan is overdue; false otherwise.
    public boolean isOverdue() {
        return isActive() && getDaysOverdue() > 0;
    }

    // Check if the fine for this loan has been paid.
    public boolean isFinePaid() {
        return finePaid;
    }

    // Define the status of the fine for this loan, allowing the system to track whether the fine has been paid or is still pending.
    public void setFinePaid(boolean finePaid) {
        this.finePaid = finePaid;
    }

    // Register the return of the book by setting the return date to the current date. 
    // This method should only be called if the book has not yet been returned (i.e., returnDate is null).
    public void markReturned() {
        if (returnDate == null) {
            returnDate = LocalDate.now();
        }
    }

    // Calculate the number of days the loan is overdue. If the book has been returned, it compares the return date with the due date.
    public long getDaysOverdue() {
        // If the book has not been returned, we compare the due date with the current date to calculate how many days it is overdue.
        LocalDate comparisonDate = returnDate == null ? LocalDate.now() : returnDate;
        long days = ChronoUnit.DAYS.between(dueDate, comparisonDate);
        // Return the number of days overdue, ensuring that it does not return a negative value (if the book is returned on time or early, 
        // it will return 0).
        return Math.max(0, days);
    }

    // Calculate the total fine for this loan based on the number of days overdue and the defined fine per day. 
    // If the fine has already been paid, it returns 0.0.
    public double calculateFine() {
        if (finePaid) {
            return 0.0;
        }
        return getDaysOverdue() * FINE_PER_DAY;
    }

    // Return a string representing the current status of the loan, 
    // which can be "EM DIA" (on time), "ATRASADO" (overdue), "DEVOLVIDO" (returned), or "DEVOLVIDO COM MULTA" (returned with fine).
    public String getStatus() {
        if (!isActive()) {
            return calculateFine() > 0 ? "DEVOLVIDO COM MULTA" : "DEVOLVIDO";
        }
        if (isOverdue()) {
            return finePaid ? "ATRASADO (MULTA RESETADA)" : "ATRASADO";
        }
        return "EM DIA";
    }

    // Return a string representation of the loan, showing the title of the book, the name of the student, and the due date.
    @Override
    public String toString() {
        return book.getTitle() + " -> " + student.getName() + " (vence em " + dueDate + ")";
    }
}