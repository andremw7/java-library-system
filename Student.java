import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Class representing a student in the library system,
//  which includes attributes for the student's name, RA (registration number), contact information, password, and loan history.
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    // Attributes of the Student class, including name, RA, contact information, password for authentication, 
    // and a list to keep track of the student's loan history.
    private String name;
    private String ra;
    private String contact;
    // New field added to store the student's password, which will be used for authentication in the login process.
    private String password;
    private List<Loan> loanHistory;

    // Original constructor for the Student class, which initializes the student's name, RA, and contact information,
    // and sets the password to an empty string by default. The loan history is initialized as an empty list.
    public Student(String name, String ra, String contact) {
        this.name = name;
        this.ra = ra;
        this.contact = contact;
        this.password = "";
        this.loanHistory = new ArrayList<>();
    }

    // New constructor for the Student class that includes a parameter for the password. 
    // This allows for creating student objects with a specified password, which is essential for the authentication process in the login dialog.
    public Student(String name, String ra, String contact, String password) {
        this.name = name;
        this.ra = ra;
        this.contact = contact;
        this.password = password;
        this.loanHistory = new ArrayList<>();
    }

    // Methods for accessing and modifying the attributes of the Student class, 
    // including getters and setters for name, RA, contact information, and password.
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

    // New methods for getting and setting the student's password, 
    // which will be used in the authentication process when the student attempts to log in to the system.
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Methods for managing the student's loan history, including retrieving the loan history, clearing it, and adding new loans to the history.
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