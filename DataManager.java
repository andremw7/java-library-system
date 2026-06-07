import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JOptionPane;

// Class responsible for managing the persistence of the library data, including saving and loading the library state to and from a file.
public class DataManager {
    private static final String FILE_NAME = "biblioteca_dados.dat";

    // Method to save the current state of the library to a file. 
    // It uses ObjectOutputStream to serialize the Library object and write it to the specified file.
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

    // Method to load the library state from a file. It uses ObjectInputStream to deserialize the Library object from the specified file.
    public static Library load() {
        File file = new File(FILE_NAME);
        
        //
        if (!file.exists()) {
            return new Library();
        }

        // Attempt to read the library data from the file. If successful, it returns the deserialized Library object. 
        // If any exceptions occur during this process (e.g., file not found, class not found, or
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Library library = (Library) ois.readObject();
            library.rebuildStudentHistories();
            return library;
        
        // If an error occurs during loading, it logs the error and shows a warning message to the user, 
        // then returns a new empty Library instance to allow the application to continue functioning.
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
