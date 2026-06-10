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

    // Setter for the title, allowing the title of the library item to be updated after creation if necessary.
    public void setTitle(String title) {
        this.title = title;
    }

    // Abstract method to check if the library item is available for borrowing. This must be implemented by all subclasses, as the availability logic may differ based on the type of item (e.g., a book might be available if it's not currently checked out, while a DVD might have different criteria).
    public abstract boolean isAvailable();

    // Abstract method to get the details of the library item. This must be implemented by all subclasses to provide specific information about the item, such as author for books or director for DVDs, along with any other relevant details.
    public abstract String getDetails();
}
