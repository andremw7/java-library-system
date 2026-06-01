import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Classe que representa o estudante ou usuario do sistema de biblioteca
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String ra;
    private String contact;
    // Novo campo privado criado para armazenar com seguranca a senha do estudante
    private String password;
    private List<Loan> loanHistory;

    // Construtor original mantido com senha vazia para evitar a quebra de outras classes
    public Student(String name, String ra, String contact) {
        this.name = name;
        this.ra = ra;
        this.contact = contact;
        this.password = "";
        this.loanHistory = new ArrayList<>();
    }

    // Novo construtor customizado e preparado para receber a senha criada pelo Admin
    public Student(String name, String ra, String contact, String password) {
        this.name = name;
        this.ra = ra;
        this.contact = contact;
        this.password = password;
        this.loanHistory = new ArrayList<>();
    }

    // Metodos de acesso para as propriedades basicas do estudante
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    // Novos metodos getter e setter criados para o controle seguro da senha
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Loan> getLoanHistory() {
        if (loanHistory == null) {
            loanHistory = new ArrayList<>();
        }
        return loanHistory;
    }

    public void clearLoanHistory() {
        getLoanHistory().clear();
    }

    public void addLoanToHistory(Loan loan) {
        if (!getLoanHistory().contains(loan)) {
            getLoanHistory().add(loan);
        }
    }

    @Override
    public String toString() {
        return name + " (RA: " + ra + ")";
    }
}