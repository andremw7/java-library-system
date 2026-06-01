import java.io.Serializable;

/**
 * Classe abstrata base para itens do acervo.
 * Mantida para demonstrar herança/polimorfismo no projeto de POO.
 */
public abstract class LibraryItem implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String title;

    public LibraryItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public abstract boolean isAvailable();

    public abstract String getDetails();
}
