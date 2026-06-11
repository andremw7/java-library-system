import java.io.Serializable;

/**
 * Class representing a user in the library system, 
 * which includes attributes for the username and role (e.g., admin, librarian, student).
 * 
 * @author André Watanabe
 * @author Pedro Zanutto
 * @author Isaac Ferreira
 * @version 1.0
 */
public class User implements Serializable {
    
    /**
     * Serial version UID for serialization compatibility. This ensures that during deserialization,
     * the class matches the version used during serialization, preventing InvalidClassExceptions if the class structure changes.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The username used for user identification.
     */
    private final String username;

    /**
     * The role to determine access permissions within the library system (e.g., admin, librarian, student).
     */
    private final String role;

    /**
     * Constructor for the User class, which initializes the username and role when creating a new user object.
     * 
     * @param username the username for identification
     * @param role the access permission role assigned to the user
     */
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    /**
     * Getter for the username attribute, allowing other classes to access this information as needed for authentication and authorization purposes.
     * 
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the user's role, which can be used to determine the level of access and permissions the user has within the library system.
     * 
     * @return the role of the user
     */
    public String getRole() {
        return role;
    }
}