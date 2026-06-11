import javax.swing.*;
import java.awt.*;

/**
 * Graphical dialog for user login, allowing students, librarians, and administrators 
 * to authenticate themselves in the library system.
 * 
 * @author André Watanabe
 * @author Pedro Zanutto
 * @author Isaac Ferreira
 * @version 1.0
 */
public class LoginDialog extends JDialog {
    /**
     * Text field for entering the user's identification (username or RA).
     */
    private final JTextField userField = new JTextField(15);

    /**
     * Password field for entering the user's password securely.
     */
    private final JPasswordField passField = new JPasswordField(15);

    /**
     * Stores the authenticated user's role (e.g., "admin", "bibliotecario", or "estudante:RA").
     */
    private String userRole = "";

    /**
     * Flag indicating whether the user has been successfully authenticated.
     */
    private boolean authenticated = false;

    /**
     * Reference to the library system instance used for verifying student credentials.
     */
    private final Library library;

    /**
     * Constructor for the LoginDialog, which sets up the user interface components and layout.
     * It takes a parent frame and a reference to the library system to allow for student 
     * authentication based on their RA and password.
     * 
     * @param parent the parent Frame of this dialog
     * @param library the library instance used for student lookup and authentication
     */
    public LoginDialog(Frame parent, Library library) {
        super(parent, "Login do Sistema", true);
        this.library = library;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to the dialog using GridBagLayout, arranging labels and input fields for username and password, as well as a login button.
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0; // Readjusted to line 0
        add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1; // Readjusted to line 1
        add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2; // Readjusted to line 2
        gbc.gridwidth = 2;
        add(loginBtn, gbc);

        // Add an action listener to the login button that triggers the authentication process when clicked.
        loginBtn.addActionListener(e -> authenticate());

        // Set default button for the dialog to allow pressing Enter to trigger the login action.
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    /**
     * Method to authenticate the user based on the input username and password. 
     * It checks for hardcoded credentials for admin and librarian roles, 
     * and then checks the library's student records for a matching RA and password.
     */
    private void authenticate() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        if (user.equals("admin") && pass.equals("123")) {
            userRole = "admin";
            authenticated = true;
            dispose();
        } else if (user.equals("bibliotecario") && pass.equals("123")) {
            userRole = "bibliotecario";
            authenticated = true;
            dispose();
        } else {
            Student student = library.findStudentByRa(user);
            if (student != null && student.getPassword().equals(pass)) {
                userRole = "estudante:" + student.getRa();
                authenticated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Usuário ou senha incorretos.",
                        "Erro de login",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Getter method to check if the user has been successfully authenticated.
     * 
     * @return true if the user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Retrieve the role of the authenticated user, which can be "admin", "bibliotecario", 
     * or "estudante:RA" depending on the type of user that logged in.
     * 
     * @return the role of the authenticated user
     */
    public String getUserRole() {
        return userRole;
    }
}