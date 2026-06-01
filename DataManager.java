import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JOptionPane;

/**
 * Responsável por salvar e carregar os dados em arquivo local.
 */
public class DataManager {
    private static final String FILE_NAME = "biblioteca_dados.dat";

    public static void save(Library library) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(library);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Erro ao salvar dados: " + e.getMessage(),
                    "Erro de arquivo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Library load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new Library();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Library library = (Library) ois.readObject();
            library.rebuildStudentHistories();
            return library;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Não foi possível carregar o arquivo de dados. O sistema será iniciado vazio.",
                    "Erro de carregamento",
                    JOptionPane.WARNING_MESSAGE);
            return new Library();
        }
    }
}
