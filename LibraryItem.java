import java.io.Serializable;

// Abstract class representing a library item, which can be a book, DVD, or any other type of media.
public abstract class LibraryItem implements Serializable {
   
   // Serial version UID for serialization compatibility. This ensures that during deserialization, 
   // the class matches the version used during serialization, preventing InvalidClassExceptions if the class structure changes.
    private static final long serialVersionUID = 1L;

    // Common attribute for all library items: the title. 
    // This is protected to allow direct access in subclasses while still encapsulating it from external classes.
    protected String title;

    // Constructor to initialize the library item with a title. This is called by subclasses when creating specific types of library items
    public LibraryItem(String title) {
        this.title = title;
    }

    // Getter and setter for the title attribute, allowing subclasses and other classes to access and modify the title of the library item as needed.
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public abstract boolean isAvailable();

    public abstract String getDetails();
}
