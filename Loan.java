import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Class representing a loan of a book to a student in the library system.
 * 
 * @author André Watanabe
 * @author Pedro Zanutto
 * @author Isaac Ferreira
 * @version 1.0
 */
public class Loan implements Serializable {
    /**
     * Defining a serial version UID for serialization compatibility. This ensures that during deserialization,
     * the class matches the version used during serialization, preventing InvalidClassExceptions if the class structure changes.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Standard loan period (14 days) for loan management.
     */
    public static final int LOAN_DAYS = 14;

    /**
     * Fine amount per day of delay (R$ 2,00) for loan management.
     */
    public static final double FINE_PER_DAY = 2.0;

    /**
     * The book being loaned.
     */
    private Book book;

    /**
     * The student who is borrowing the book.
     */
    private Student student;

    /**
     * Date when the loan was made.
     */
    private LocalDate loanDate;

    /**
     * Date when the loan is due for return.
     */
    private LocalDate dueDate;

    /**
     * Date when the book was returned (null if not yet returned).
     */
    private LocalDate returnDate;

    /**
     * Flag to indicate if the fine for this loan has been paid (true if paid, false if pending).
     */
    private boolean finePaid = false;

    /**
     * Constructor for creating a new loan with the current date as the loan date and a due date set to 14 days later.
     * 
     * @param book the book being loaned
     * @param student the student who is borrowing the book
     */
    public Loan(Book book, Student student) {
        this(book, student, LocalDate.now(), LocalDate.now().plusDays(LOAN_DAYS));
    }

    /**
     * Constructor for creating a new loan with specified loan and due dates. 
     * This allows for more flexibility in testing and managing loans with custom dates.
     * 
     * @param book the book being loaned
     * @param student the student who is borrowing the book
     * @param loanDate the date when the loan was made
     * @param dueDate the date when the loan is due for return
     */
    public Loan(Book book, Student student, LocalDate loanDate, LocalDate dueDate) {
        this.book = book;
        this.student = student;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    // GETTERS and SETTERS for the attributes of the Loan class

    /**
     * Book associated with this loan.
     * 
     * @return The Book object being loaned.
     */
    public Book getBook() {
        return book;
    }

    /**
     * Student associated with this loan.
     * 
     * @return The Student object who is borrowing the book.
     */
    public Student getStudent() {
        return student;
    }

    /**
     * Date when the loan was made.
     * 
     * @return The date of the loan.
     */
    public LocalDate getLoanDate() {
        return loanDate;
    }

    /**
     * Due date for returning the book.
     * 
     * @return The date when the loan is due for return.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Define a new due date for the loan, allowing for extensions or adjustments to the original due date.
     * 
     * @param dueDate the new due date to set
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Date when the book was returned. This will be null if the book has not yet been returned.
     * 
     * @return The date when the book was returned, or null if not yet returned.
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Verify if the loan is currently active, meaning the book has not yet been returned.
     * 
     * @return true if the loan is active (not yet returned); false if the book has been returned
     */
    public boolean isActive() {
        return returnDate == null;
    }

    /**
     * Check if the loan is overdue, which means it is still active and the current date is past the due date.
     * 
     * @return true if the loan is overdue; false otherwise.
     */
    public boolean isOverdue() {
        return isActive() && getDaysOverdue() > 0;
    }

    /**
     * Check if the fine for this loan has been paid.
     * 
     * @return true if the fine has been paid, false otherwise
     */
    public boolean isFinePaid() {
        return finePaid;
    }

    /**
     * Define the status of the fine for this loan, allowing the system to track whether the fine has been paid or is still pending.
     * 
     * @param finePaid the status of the fine payment to set
     */
    public void setFinePaid(boolean finePaid) {
        this.finePaid = finePaid;
    }

    /**
     * Register the return of the book by setting the return date to the current date. 
     * This method should only be called if the book has not yet been returned (i.e., returnDate is null).
     */
    public void markReturned() {
        if (returnDate == null) {
            returnDate = LocalDate.now();
        }
    }

    /**
     * Calculate the number of days the loan is overdue. If the book has been returned, it compares the return date with the due date.
     * 
     * @return the number of days the loan is overdue
     */
    public long getDaysOverdue() {
        // If the book has not been returned, we compare the due date with the current date to calculate how many days it is overdue.
        LocalDate comparisonDate = returnDate == null ? LocalDate.now() : returnDate;
        long days = ChronoUnit.DAYS.between(dueDate, comparisonDate);
        // Return the number of days overdue, ensuring that it does not return a negative value (if the book is returned on time or early, 
        // it will return 0).
        return Math.max(0, days);
    }

    /**
     * Calculate the total fine for this loan based on the number of days overdue and the defined fine per day. 
     * If the fine has already been paid, it returns 0.0.
     * 
     * @return the total calculated fine amount
     */
    public double calculateFine() {
        if (finePaid) {
            return 0.0;
        }
        return getDaysOverdue() * FINE_PER_DAY;
    }

    /**
     * Return a string representing the current status of the loan, 
     * which can be "EM DIA" (on time), "ATRASADO" (overdue), "DEVOLVIDO" (returned), or "DEVOLVIDO COM MULTA" (returned with fine).
     * 
     * @return the status description string
     */
    public String getStatus() {
        if (!isActive()) {
            return calculateFine() > 0 ? "DEVOLVIDO COM MULTA" : "DEVOLVIDO";
        }
        if (isOverdue()) {
            return finePaid ? "ATRASADO (MULTA RESETADA)" : "ATRASADO";
        }
        return "EM DIA";
    }

    /**
     * Return a string representation of the loan, showing the title of the book, the name of the student, and the due date.
     * 
     * @return a string representation of the loan
     */
    @Override
    public String toString() {
        return book.getTitle() + " -> " + student.getName() + " (vence em " + dueDate + ")";
    }
}