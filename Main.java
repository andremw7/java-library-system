import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// Classe com o metodo de entrada inicial que carrega a base de dados e lanca as telas da aplicacao
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Mantem as configuracoes graficas padrao em caso de incompatibilidade com o S.O.
            }

            Library library = DataManager.load();
            // Inicia o ciclo continuo de sessoes da aplicacao
            runSessionLoop(library);
        });
    }

    // Metodo recursivo que permite fazer logout e reabrir a tela de login infinitamente de forma limpa
    private static void runSessionLoop(Library library) {
        LoginDialog login = new LoginDialog(null, library);
        login.setVisible(true);

        if (login.isAuthenticated()) {
            LibraryUI view = new LibraryUI(login.getUserRole());
            LibraryController controller = new LibraryController(library, view, login.getUserRole());
            
            // Define o que acontece quando o usuario clica no botao de Encerrar Sessao
            controller.setLogoutAction(() -> {
                view.dispose(); // Fecha e destroi a tela principal atual
                runSessionLoop(library); // Abre uma nova tela de login utilizando a mesma base de dados
            });
        } else {
            System.exit(0); // Caso feche a janela de login sem autenticar, encerra o programa
        }
    }
}