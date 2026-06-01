import java.io.Serializable;

/**
 * Representa um usuário autenticável do sistema.
 * Neste trabalho, as credenciais são fixas na tela de login.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String role;

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
