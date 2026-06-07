import java.io.Serializable;

// Class representing a user in the library system, 
// which includes attributes for the username and role (e.g., admin, librarian, student).
public class User implements Serializable {
    
    // Serial version UID for serialization compatibility. This ensures that during deserialization,
    // the class matches the version used during serialization, preventing InvalidClassExceptions if the class structure
    private static final long serialVersionUID = 1L;

    // Attributes of the User class, including the username for identification and the role to determine access permissions within the library system.
    private final String username;
    private final String role;

   // Constructor for the User class, which initializes the username and role when creating a new user object.
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    // Getters for the username and role attributes, allowing other classes to access this information as needed for authentication and authorization purposes.
    public String getUsername() {
        return username;
    }

    // Getter for the user's role, which can be used to determine the level of access and permissions the user has within the library system (e.g., admin, librarian, student).
    public String getRole() {
        return role;
    }
}
