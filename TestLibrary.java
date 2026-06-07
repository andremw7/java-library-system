import java.time.LocalDate;

// Test class for the library system, which includes a main method to perform various operations such as adding books and students, 
// performing loans and returns, and calculating fines.
public class TestLibrary {

    // Main method to execute the test cases for the library system. It creates a library instance, adds a book and a student,
    // performs a loan and return operation, and tests the fine calculation for overdue loans.
    public static void main(String[] args) {
        Library library = new Library();
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", "Programação", 2008, 1);
        Student student = new Student("Maria Silva", "123", "maria@email.com");

        library.addBook(book);
        library.addStudent(student);

        // Perform a loan operation and verify that the book's available copies are updated, the due date is set correctly, and the student's loan history is updated.
        Loan loan = library.performLoan(book, student);
        assertEquals(0, book.getAvailableCopies(), "Após empréstimo, cópias disponíveis");
        assertTrue(loan.getDueDate().equals(LocalDate.now().plusDays(Loan.LOAN_DAYS)), "Prazo automático de 14 dias");
        assertEquals(1, student.getLoanHistory().size(), "Histórico do usuário");

       // Perform a return operation and verify that the book's available copies are updated and the loan is marked as returned.
        library.performReturn(loan);
        assertEquals(1, book.getAvailableCopies(), "Após devolução, cópias disponíveis");
        assertTrue(!loan.isActive(), "Empréstimo devolvido deixa de estar ativo");

        // Test the fine calculation for overdue loans.
        Loan overdue = new Loan(book, student, LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));
        assertEquals(12.0, overdue.calculateFine(), "Multa de R$ 2 por 6 dias de atraso");
        overdue.setFinePaid(true);
        assertEquals(0.0, overdue.calculateFine(), "Reset de multa");

        System.out.println("Todos os testes passaram.");
    }

    // Custom assertion methods to validate the expected outcomes of the test cases. 
    // These methods throw an AssertionError with a descriptive message if the condition is not met.
    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": esperado " + expected + ", obtido " + actual);
        }
    }

    // Overloaded method for comparing double values with a custom message.
    private static void assertEquals(double expected, double actual, String message) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(message + ": esperado " + expected + ", obtido " + actual);
        }
    }

    // Custom assertion method to validate boolean conditions with a descriptive message if the condition is not met.
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
