import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a student in the library system, which includes attributes 
 * for the student's name, RA (registration number), contact information, password, and loan history.
 * * @author André Watanabe
 * @author Pedro Zanutto
 * @author Isaac Ferreira
 * @version 1.0
 */
public class Student implements Serializable {
    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The full name of the student.
     */
    private String name;

    /**
     * The RA (Academic Registration number) of the student.
     */
    private String ra;

    /**
     * The contact information (such as phone or email) of the student.
     */
    private String contact;

    /**
     * New field added to store the student's password, which will be used for authentication in the login process.
     */
    private String password;

    /**
     * List to keep track of the student's current and past loan history.
     */
    private List<Loan> loanHistory;

    /**
     * Original constructor for the Student class, which initializes the student's name, RA, and contact information,
     * and sets the password to an empty string by default. The loan history is initialized as an empty list.
     * * @param name the student's name
     * @param ra the student's registration number (RA)
     * @param contact the student's contact information
     */
    public Student(String name, String ra, String contact) {
        this.name = name;
        this.ra = ra;
        this.contact = contact;
        this.password = "";
        this.loanHistory = new ArrayList<>();
    }

    /**
     * New constructor for the Student class that includes a parameter for the password. 
     * This allows for creating student objects with a specified password, which is essential for the authentication process in the login dialog.
     * * @param name the student's name
     * @param ra the student's registration number (RA)
     * @param contact the student's contact information
     * @param password the student's password used for system authentication
     */
    public Student(String name, String ra, String contact, String password) {
        this.name = name;
        this.ra = ra;
        this.contact = contact;
        this.password = password;
        this.loanHistory = new ArrayList<>();
    }

    // Methods for accessing and modifying the attributes of the Student class

    /**
     * Gets the name of the student.
     * * @return the student's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the student.
     * * @param name the new name to set for the student
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the RA (registration number) of the student.
     * * @return the student's RA
     */
    public String getRa() {
        return ra;
    }

    /**
     * Sets the RA (registration number) of the student.
     * * @param ra the new RA to set for the student
     */
    public void setRa(String ra) {
        this.ra = ra;
    }

    /**
     * Gets the contact information of the student.
     * * @return the student's contact information
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the contact information of the student.
     * * @param contact the new contact information to set for the student
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * New method for getting the student's password, which will be used in the 
     * authentication process when the student attempts to log in to the system.
     * * @return the student's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * New method for setting the student's password, which will be used in the 
     * authentication process when the student attempts to log in to the system.
     * * @param password the new password to set for the student
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Methods for managing the student's loan history, including retrieving the loan history, 
     * clearing it, and adding new loans to the history.
     * * @return a list containing the student's loan history
     */
    public List<Loan> getLoanHistory() {
        if (loanHistory == null) {
            loanHistory = new ArrayList<>();
        }
        return loanHistory;
    }

    /**
     * Clears the entire loan history of the student.
     */
    public void clearLoanHistory() {
        getLoanHistory().clear();
    }

    /**
     * Adds a new loan to the student's history if it is not already present in the list.
     * * @param loan the Loan object to add to the history
     */
    public void addLoanToHistory(Loan loan) {
        if (!getLoanHistory().contains(loan)) {
            getLoanHistory().add(loan);
        }
    }

    /**
     * Returns a string representation of the student, showing their name and RA.
     * * @return a string representing the student
     */
    @Override
    public String toString() {
        return name + " (RA: " + ra + ")";
    }
}