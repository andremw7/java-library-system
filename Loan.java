import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Representa um empréstimo de livro dentro do sistema da biblioteca.
// Controla os envolvidos (livro e estudante), datas de empréstimo, 
// vencimento e devolução, além de calcular multas por atraso automaticamente.
public class Loan implements Serializable {
    // Definindo o ID de serialização para persistência de dados
    private static final long serialVersionUID = 1L;
    
    // Constantes do sistema de empréstimo
    public static final int LOAN_DAYS = 14; // Prazo padrão de empréstimo (14 dias)
    public static final double FINE_PER_DAY = 2.0; // Valor da multa por dia de atraso (R$ 2,00)

    // Atributos da classe
    private Book book;               // O livro que foi emprestado
    private Student student;         // O estudante que pegou o livro
    private LocalDate loanDate;      // Data em que o empréstimo foi realizado
    private LocalDate dueDate;       // Data limite para a devolução (vencimento)
    private LocalDate returnDate;    // Data em que o livro foi efetivamente devolvido (null se ativo)
    private boolean finePaid = false; // Indica se a multa gerada já foi paga

    // Construtor padrão para novos empréstimos.
    // Define automaticamente a data de hoje como data de empréstimo e calcula
    // a data de vencimento somando os dias padrão (LOAN_DAYS).
    // @param book O livro a ser emprestado.
    // @param student O estudante que está pegando o livro.
    public Loan(Book book, Student student) {
        this(book, student, LocalDate.now(), LocalDate.now().plusDays(LOAN_DAYS));
    }

    // Construtor completo para empréstimos.
    // Permite definir manualmente todas as datas (útil para carregar dados antigos ou testes).
    // @param book O livro emprestado.
    // @param student O estudante responsável.
    // @param loanDate A data de início do empréstimo.
    // @param dueDate A data de vencimento do empréstimo.
    public Loan(Book book, Student student, LocalDate loanDate, LocalDate dueDate) {
        this.book = book;
        this.student = student;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    // --- Métodos Getters e Setters ---

    // Obtém o livro associado a este empréstimo.
    // @return O objeto Book.
    public Book getBook() {
        return book;
    }

    // Obtém o estudante associado a este empréstimo.
    // @return O objeto Student.
    public Student getStudent() {
        return student;
    }

    // Obtém a data em que o empréstimo foi feito.
    // @return A data de início.
    public LocalDate getLoanDate() {
        return loanDate;
    }

    // Obtém a data de vencimento atual do empréstimo.
    // @return A data de vencimento.
    public LocalDate getDueDate() {
        return dueDate;
    }

    // Define ou altera a data de vencimento do empréstimo.
    // Este método resolve o erro que ocorria na classe Library.java ao tentar renovar
    // ou alterar o prazo de devolução de um livro.
    // @param dueDate A nova data de vencimento.
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // Obtém a data em que o livro foi devolvido.
    // @return A data de devolução ou null se ainda não foi devolvido.
    public LocalDate getReturnDate() {
        return returnDate;
    }

    // Verifica se o empréstimo ainda está ativo (ou seja, se o livro ainda não foi devolvido).
    // @return true se o livro não foi devolvido; false caso contrário.
    public boolean isActive() {
        return returnDate == null;
    }

    // Verifica se o empréstimo está atrasado.
    // O empréstimo está atrasado se estiver ativo e a quantidade de dias de atraso for maior que zero.
    // @return true se estiver atrasado; false caso contrário.
    public boolean isOverdue() {
        return isActive() && getDaysOverdue() > 0;
    }

    // Verifica se a multa deste empréstimo foi paga.
    // @return true se a multa foi paga; false caso contrário.
    public boolean isFinePaid() {
        return finePaid;
    }

    // Define o status do pagamento da multa.
    // @param finePaid true para marcar como paga, false para pendente.
    public void setFinePaid(boolean finePaid) {
        this.finePaid = finePaid;
    }

    // Registra a devolução do livro definindo a data de retorno como a data atual (hoje).
    // Só executa a ação se o livro ainda não tiver sido devolvido.
    public void markReturned() {
        if (returnDate == null) {
            returnDate = LocalDate.now();
        }
    }

    // Calcula a quantidade de dias de atraso do empréstimo.
    // Se o livro já foi devolvido, calcula o atraso com base na data de devolução.
    // Se ainda não foi devolvido, calcula com base na data atual (hoje).
    // Retorna 0 caso não haja atraso (vencimento futuro).
    // @return Quantidade de dias após a data de vencimento.
    public long getDaysOverdue() {
        // Se já devolveu, compara com a data de retorno. Se não, compara com a data de hoje.
        LocalDate comparisonDate = returnDate == null ? LocalDate.now() : returnDate;
        long days = ChronoUnit.DAYS.between(dueDate, comparisonDate);
        // Retorna o valor de dias, ou 0 se o cálculo der negativo (em dia)
        return Math.max(0, days);
    }

    // Calcula o valor total da multa acumulada por atraso.
    // Caso a multa já tenha sido paga, o valor retornado será 0.0.
    // @return O valor em dinheiro da multa (dias de atraso multiplicado pela taxa diária).
    public double calculateFine() {
        if (finePaid) {
            return 0.0;
        }
        return getDaysOverdue() * FINE_PER_DAY;
    }

    // Retorna uma representação textual do status atual do empréstimo.
    // Os estados possíveis incluem: DEVOLVIDO COM MULTA, DEVOLVIDO, 
    // ATRASADO (MULTA RESETADA), ATRASADO ou EM DIA.
    // @return String contendo a descrição amigável do status.
    public String getStatus() {
        if (!isActive()) {
            return calculateFine() > 0 ? "DEVOLVIDO COM MULTA" : "DEVOLVIDO";
        }
        if (isOverdue()) {
            return finePaid ? "ATRASADO (MULTA RESETADA)" : "ATRASADO";
        }
        return "EM DIA";
    }

    // Representação em String do empréstimo para exibição em listas ou logs.
    // Exibe o título do livro, o nome do estudante e a data de vencimento.
    @Override
    public String toString() {
        return book.getTitle() + " -> " + student.getName() + " (vence em " + dueDate + ")";
    }
}