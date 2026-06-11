import java.io.Serializable;

/**
 * Abstract class representing a library item, which can be a book, DVD, or any other type of media.
 * * @author André Watanabe
 * @author Pedro Zanutto
 * @author Isaac Ferreira
 * @version 1.0
 */
public abstract class LibraryItem implements Serializable {
   
    /**
     * Serial version UID for serialization compatibility. This ensures that during deserialization, 
     * the class matches the version used during serialization, preventing InvalidClassExceptions if the class structure changes.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Common attribute for all library items: the title. 
     * This is protected to allow direct access in subclasses while still encapsulating it from external classes.
     */
    protected String title;

    /**
     * Constructor to initialize the library item with a title. This is called by subclasses 
     * when creating specific types of library items.
     * * @param title the title of the library item
     */
    public LibraryItem(String title) {
        this.title = title;
    }

    /**
     * Getter for the title attribute, allowing subclasses and other classes to access 
     * the title of the library item as needed.
     * * @return the title of the library item
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title, allowing the title of the library item to be updated 
     * after creation if necessary.
     * * @param title the new title to set for the library item
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Abstract method to check if the library item is available for borrowing. 
     * This must be implemented by all subclasses, as the availability logic may differ 
     * based on the type of item (e.g., a book might be available if it's not currently 
     * checked out, while a DVD might have different criteria).
     * * @return true if the item is available, false otherwise
     */
    public abstract boolean isAvailable();

    /**
     * Abstract method to get the details of the library item. This must be implemented 
     * by all subclasses to provide specific information about the item, such as author 
     * for books or director for DVDs, along with any other relevant details.
     * * @return a string containing the specific details of the library item
     */
    public abstract String getDetails();
}