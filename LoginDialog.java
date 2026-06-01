import javax.swing.*;
import java.awt.*;

// Tela gráfica de autenticação limpa com suporte para administradores, bibliotecários e alunos
public class LoginDialog extends JDialog {
    private final JTextField userField = new JTextField(15);
    private final JPasswordField passField = new JPasswordField(15);
    private String userRole = "";
    private boolean authenticated = false;
    private final Library library;

    public LoginDialog(Frame parent, Library library) {
        super(parent, "Login do Sistema", true);
        this.library = library;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ALTERAÇÃO: O JLabel antigo com as dicas de senhas foi totalmente removido para deixar o visual limpo!
        
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0; // Reajustado para começar do topo (linha 0)
        add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1; // Reajustado para a linha 1
        add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2; // Reajustado para a linha 2
        gbc.gridwidth = 2;
        add(loginBtn, gbc);

        loginBtn.addActionListener(e -> authenticate());

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void authenticate() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        if (user.equals("admin") && pass.equals("123")) {
            userRole = "admin";
            authenticated = true;
            dispose();
        } else if (user.equals("user") && pass.equals("123")) {
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

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getUserRole() {
        return userRole;
    }
}