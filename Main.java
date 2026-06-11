import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point of the library management application.
 *
 * <p>This class initializes the graphical interface, loads the persisted
 * library data, and starts the user authentication and session workflow.</p>
 * * @author André Watanabe
 * @author Pedro Zanutto
 * @author Isaac Ferreira
 * @version 1.0
 */
public class Main {
   
    /**
     * Starts the application.
     *
     * <p>The method configures the system look and feel, loads the saved
     * library data, and starts the session loop responsible for user login
     * and interface initialization.</p>
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Maintain default look and feel if system look and feel is not available or fails to load.
            }

            Library library = DataManager.load();
            // Initiate the session loop, which will handle user login and allow for logout and re-login without restarting the application.
            runSessionLoop(library);
        });
    }

    /**
     * Recursive method to manage the session loop of the application. It displays the login dialog, 
     * and upon successful authentication, it launches the main library UI. If the user logs out, 
     * it disposes of the current UI and restarts the session loop, allowing for a new login 
     * without restarting the application.
     * * @param library the active library instance containing system data
     */
    private static void runSessionLoop(Library library) {
        LoginDialog login = new LoginDialog(null, library);
        login.setVisible(true);

        // If the user successfully authenticated, we create the main library UI and controller.
        if (login.isAuthenticated()) {
            LibraryUI view = new LibraryUI(login.getUserRole());
            LibraryController controller = new LibraryController(library, view, login.getUserRole());
            
            // Define the logout action for the controller, 
            // which will dispose of the current main UI and restart the session loop to allow for a new login.
            controller.setLogoutAction(() -> {
                view.dispose(); // Close the current main UI when logging out
                runSessionLoop(library); // Open the login dialog again for a new session
            });
        } else {
            System.exit(0); // If the user closes the login dialog or fails to authenticate, we exit the application.
        }
    }
}