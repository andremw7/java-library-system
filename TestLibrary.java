import java.time.LocalDate;

/**
 * Teste simples de regras de negócio, sem framework externo.
 * Execute com: javac *.java && java TestLibrary
 */
public class TestLibrary {
    public static void main(String[] args) {
        Library library = new Library();
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", "Programação", 2008, 1);
        Student student = new Student("Maria Silva", "123", "maria@email.com");

        library.addBook(book);
        library.addStudent(student);

        Loan loan = library.performLoan(book, student);
        assertEquals(0, book.getAvailableCopies(), "Após empréstimo, cópias disponíveis");
        assertTrue(loan.getDueDate().equals(LocalDate.now().plusDays(Loan.LOAN_DAYS)), "Prazo automático de 14 dias");
        assertEquals(1, student.getLoanHistory().size(), "Histórico do usuário");

        library.performReturn(loan);
        assertEquals(1, book.getAvailableCopies(), "Após devolução, cópias disponíveis");
        assertTrue(!loan.isActive(), "Empréstimo devolvido deixa de estar ativo");

        Loan overdue = new Loan(book, student, LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));
        assertEquals(12.0, overdue.calculateFine(), "Multa de R$ 2 por 6 dias de atraso");
        overdue.setFinePaid(true);
        assertEquals(0.0, overdue.calculateFine(), "Reset de multa");

        System.out.println("Todos os testes passaram.");
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": esperado " + expected + ", obtido " + actual);
        }
    }

    private static void assertEquals(double expected, double actual, String message) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(message + ": esperado " + expected + ", obtido " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
