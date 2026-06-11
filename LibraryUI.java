
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Class representing the main user interface of the library management system. 
 * It extends JFrame to create a windowed application and contains all the components and layouts for managing books, users, loans, and reports.
 * * @author André Watanabe
 * @author Pedro Zanutto
 * @author Isaac Ferreira
 * @version 1.0
 */
public class LibraryUI extends JFrame {
   
    /**
     * The role of the currently logged-in user (e.g., "admin" or "patron"). 
     * This is used to control access to certain features and functionalities within the UI, ensuring that only authorized users can perform specific actions like adding or editing books and users.
     */
    private final String userRole;
    
    /**
     * The main tabbed pane that holds different sections of the application (Books, Users, Loans, Reports).
     */
    private final JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * Panels for the different sections of the UI: one for managing books and another for managing users.
     * GridBagLayout is used to provide a flexible, grid-based alignment for the forms.
     */
    private final JPanel bookForm = new JPanel(new GridBagLayout());
    private final JPanel studentForm = new JPanel(new GridBagLayout());

    /**
     * Book management components, including text fields for entering book details, 
     * buttons for performing actions (add, edit, delete, clear), and a table to display the list of books with sorting capabilities.
     */
    private final JTextField bookSearchField = new JTextField(22);
    private final JTextField titleField = new JTextField(14);
    private final JTextField authorField = new JTextField(14);
    private final JTextField isbnField = new JTextField(10);
    private final JTextField genreField = new JTextField(10);
    private final JTextField yearField = new JTextField(5);
    private final JTextField qtyField = new JTextField(5);

    // Buttons for performing Create, Read, Update, and Delete (CRUD) operations on the book inventory.
    private final JButton addBookBtn = new JButton("Adicionar");
    private final JButton editBookBtn = new JButton("Editar");
    private final JButton deleteBookBtn = new JButton("Excluir");
    private final JButton clearBookBtn = new JButton("Limpar campos");

    // Table model and components to visualize and sort the cataloged books.
    private final DefaultTableModel bookModel = createModel(new String[]{"Título", "Autor", "ISBN", "Gênero", "Ano", "Cópias"});
    private final JTable bookTable = new JTable(bookModel);
    private final TableRowSorter<DefaultTableModel> bookSorter = new TableRowSorter<>(bookModel);

    /**
     * User management components, including text fields for entering user details, 
     * buttons for performing actions (add, edit, delete, clear), and a table to display the list of users with sorting capabilities.
     */
    private final JTextField patronSearchField = new JTextField(22);
    private final JTextField nameField = new JTextField(14);
    private final JTextField raField = new JTextField(14);
    private final JTextField contactField = new JTextField(14);
    private final JPasswordField passwordField = new JPasswordField(14);

    // Buttons for managing student/patron records within the system.
    private final JButton addStudentBtn = new JButton("Adicionar");
    private final JButton editStudentBtn = new JButton("Editar");
    private final JButton deleteStudentBtn = new JButton("Excluir");
    private final JButton clearStudentBtn = new JButton("Limpar campos");

    // Table model and components to visualize and sort registered students/users.
    private final DefaultTableModel studentModel = createModel(new String[]{"Nome", "ID/RA", "Contato", "Senha"});
    private final JTable studentTable = new JTable(studentModel);
    private final TableRowSorter<DefaultTableModel> studentSorter = new TableRowSorter<>(studentModel);

    /**
     * Loan management components, including text fields for searching and filtering loans, 
     * combo boxes for selecting books and students when creating a new loan, buttons for performing loan actions (checkout, return, renew), 
     * and a table to display the list of loans with sorting capabilities.
     */
    private final JTextField loanSearchField = new JTextField(22);
    
    /**
     * Additional text fields for filtering the book and student combo boxes in the loan management section. 
     * While the loanSearchField is for filtering the loans table, 
     * these fields allow users to quickly find and select the desired book and student when creating a new loan, 
     * enhancing the user experience by providing dynamic search capabilities within the loan creation process.
     */
    private final JTextField bookFilterField = new JTextField(14);
    private final JTextField studentFilterField = new JTextField(14);
    
    /**
     * Combo boxes for selecting a book and a student when creating a new loan.
     */
    private final JComboBox<Book> loanBookCombo = new JComboBox<>();
    private final JComboBox<Student> loanStudentCombo = new JComboBox<>();
    
    // Sub-panel specifically designed to hold the input fields for registering a new loan transaction.
    private final JPanel newLoanFieldsPanel = new JPanel(new GridBagLayout()); 

    /**
     * Buttons for performing loan actions: checking out a book, returning a book, and renewing a loan.
     */
    private final JButton checkoutBtn = new JButton("Emprestar");
    private final JButton returnBtn = new JButton("Devolver");
    private final JButton renewBtn = new JButton("Renovar Empréstimo"); 

    /**
     * Table model and table for displaying the list of loans, along with a sorter for enabling sorting functionality on the loans table.
     */
    private final DefaultTableModel loanModel = createModel(new String[]{"Livro", "Estudante", "Empréstimo", "Vencimento", "Status"});
    private final JTable loanTable = new JTable(loanModel);
    private final TableRowSorter<DefaultTableModel> loanSorter = new TableRowSorter<>(loanModel);

    /**
     * Report management components, including text fields for searching and filtering reports.
     */
    private final JTextField reportSearchField = new JTextField(22);
    private final JTextField reportPatronField = new JTextField(10);
    
    // FlowLayout panel to align the patron-specific report search components to the left side.
    private final JPanel reportPatronPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); 

    /**
     * Buttons for filtering reports based on different operational criteria.
     */
    private final JButton showActiveLoansBtn = new JButton("Ativos");
    private final JButton showOverdueLoansBtn = new JButton("Atrasados");
    private final JButton showAllHistoryBtn = new JButton("Histórico Geral");
    private final JButton showPatronHistoryBtn = new JButton("Buscar por ID/RA");
    private final JButton resetFineBtn = new JButton("Resetar Multa");

    /**
     * Table model and table for displaying the list of reports, along with a sorter for enabling sorting functionality on the reports table.
     */
    private final DefaultTableModel reportModel = createModel(new String[]{"Livro", "Estudante", "ID/RA", "Empréstimo", "Vencimento", "Devolução", "Multa", "Status"});
    private final JTable reportTable = new JTable(reportModel);
    private final TableRowSorter<DefaultTableModel> reportSorter = new TableRowSorter<>(reportModel);

    /**
     * Button for logging out of the system.
     */
    private final JButton logoutBtn = new JButton("Encerrar Sessão");

    /**
     * Constructor for the LibraryUI class, which initializes the user interface based on the role of the logged-in user. 
     * It sets up the main window, creates the different tabs for managing books, 
     * users, loans, and reports, and configures the layout and components for each section of the UI.
     * * @param userRole The role of the currently logged-in user.
     */
    public LibraryUI(String userRole) {
        this.userRole = userRole;
        
        // Sets the title of the window, dynamically appending the role of the current user.
        setTitle("Sistema de Gestão de Biblioteca - Perfil: " + userRole.toUpperCase());
        
        // Defines the initial dimensions of the application window (width, height).
        setSize(1000, 650);
        
        // Ensures the application terminates entirely when the main window is closed.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Centers the window on the screen upon startup.
        setLocationRelativeTo(null);

        // Main panel that holds all the components of the UI, using a BorderLayout to organize the top bar and the tabbed pane.
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Adds an invisible padding around the main panel to prevent inner components from touching the window edges.
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Verifying the user's role to determine which tabs and functionalities should be accessible in the UI.
        boolean isAdmin = "admin".equalsIgnoreCase(userRole);

        // Everyone has access to the Books tab (the Librarian can manage books, while the Admin can only view them).
        setupBooksTab();   
        
        // Only users with the "admin" role have access to the "Users" tab, 
        // which allows them to manage user accounts.
        if (isAdmin) {
            setupStudentsTab();
        }
        
        // Everyone has access to the Loans tab (the Librarian can manage loans, while the Admin can only view them).
        setupLoansTab();   
        
        // Everyone has access to the Reports tab (the Librarian can view reports, while the Admin can filter and reset fines).
        setupReportsTab(); 

        // Top bar that displays a welcome message with the user's role and a logout button, 
        // using a BorderLayout to position the welcome message on the left and the logout button on the right.
        JPanel topBar = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Conectado como: " + userRole);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Appends the welcome label to the left side (WEST) and the logout button to the right side (EAST).
        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        topBar.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Combines the constructed top bar and the tabbed navigation pane into the application's main panel.
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Finally, adds the assembled main panel to the JFrame window.
        add(mainPanel);
    }

    /**
     * Method to enable or disable admin controls based on the user's role.
     * * @param enabled True to enable controls, false to disable.
     */
    public void setAdminControlsEnabled(boolean enabled) {
        addBookBtn.setEnabled(enabled);
        editBookBtn.setEnabled(enabled);
        deleteBookBtn.setEnabled(enabled);
        clearBookBtn.setEnabled(enabled);
        
        titleField.setEditable(enabled);
        authorField.setEditable(enabled);
        isbnField.setEditable(enabled);
        genreField.setEditable(enabled);
        yearField.setEditable(enabled);
        qtyField.setEditable(enabled);

        addStudentBtn.setEnabled(enabled);
        editStudentBtn.setEnabled(enabled);
        deleteStudentBtn.setEnabled(enabled);
        clearStudentBtn.setEnabled(enabled);
        
        nameField.setEditable(enabled);
        raField.setEditable(enabled);
        contactField.setEditable(enabled);
        passwordField.setEditable(enabled);
    }

    /**
     * Helper method to create a non-editable table model with specified column names.
     * * @param columns The column names array.
     * @return A custom DefaultTableModel where cell editing is disabled.
     */
    private DefaultTableModel createModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            // Override the isCellEditable method to make all cells non-editable, ensuring that users cannot directly edit the contents of the tables.
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
    }

    /**
     * Method to set up the "Books" tab of the UI, which includes a form for adding and editing books,
     * a table for displaying the list of books, and a search field for filtering the books table.
     */
    private void setupBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        bookForm.setLayout(new GridBagLayout());
        bookForm.setBorder(BorderFactory.createTitledBorder("Cadastro / Edição de Livros"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set weighty to 0 for all components in the form to prevent them from stretching vertically when the window is resized.
        gbc.gridx = 0; gbc.gridy = 0; bookForm.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1; bookForm.add(titleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; bookForm.add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1; bookForm.add(authorField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; bookForm.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; bookForm.add(isbnField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; bookForm.add(new JLabel("Gênero:"), gbc);
        gbc.gridx = 1; bookForm.add(genreField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; bookForm.add(new JLabel("Ano:"), gbc);
        gbc.gridx = 1; bookForm.add(yearField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; bookForm.add(new JLabel("Cópias:"), gbc);
        gbc.gridx = 1; bookForm.add(qtyField, gbc);

        // JPanel to hold the action buttons for managing books, using a GridLayout to arrange the buttons in a 2x2 grid with spacing between them.
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.add(addBookBtn); btnPanel.add(editBookBtn);
        btnPanel.add(deleteBookBtn); btnPanel.add(clearBookBtn);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        bookForm.add(btnPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        bookForm.add(new JPanel(), gbc);

        // Set up the books table with a row sorter for enabling sorting functionality, 
        // and create a panel that includes a search field for filtering the books table and the table itself within a scroll pane.
        bookTable.setRowSorter(bookSorter);
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Filtrar texto:"));
        searchPanel.add(bookSearchField);
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        panel.add(bookForm, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);
        tabbedPane.addTab("Livros", panel);
    }

    /**
     * Method to set up the "Users" tab of the UI, which includes a form for adding and editing users,
     * a table for displaying the list of users, and a search field for filtering the users table.
     */
    private void setupStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        studentForm.setLayout(new GridBagLayout());
        studentForm.setBorder(BorderFactory.createTitledBorder("Cadastro / Edição de Usuários"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.weighty = 0.0;
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 0; studentForm.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; studentForm.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; studentForm.add(new JLabel("ID/RA:"), gbc);
        gbc.gridx = 1; studentForm.add(raField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; studentForm.add(new JLabel("Contato:"), gbc);
        gbc.gridx = 1; studentForm.add(contactField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; studentForm.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; studentForm.add(passwordField, gbc);

        // JPanel to hold the action buttons for managing users, using a GridLayout to arrange the buttons in a 2x2 grid with spacing between them.
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.add(addStudentBtn); btnPanel.add(editStudentBtn);
        btnPanel.add(deleteStudentBtn); btnPanel.add(clearStudentBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        studentForm.add(btnPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        studentForm.add(new JPanel(), gbc);

        // Set up the students table with a row sorter for enabling sorting functionality, 
        // and create a panel that includes a search field for filtering the students table and the table itself within a scroll pane.
        studentTable.setRowSorter(studentSorter);
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Filtrar texto:"));
        searchPanel.add(patronSearchField);
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        panel.add(studentForm, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);
        tabbedPane.addTab("Usuários", panel);
    }

    /**
     * Method to set up the "Loans" tab of the UI, which includes components for managing book loans, 
     * such as filtering options, combo boxes for selecting books and students, action buttons for loan operations,
     * and a table for displaying the list of loans.
     */
    private void setupLoansTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel loanLeftPanel = new JPanel(new GridBagLayout());
        loanLeftPanel.setBorder(BorderFactory.createTitledBorder("Ações de Empréstimo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 0; loanLeftPanel.add(new JLabel("Filtrar Tabela:"), gbc);
        gbc.gridx = 1; loanLeftPanel.add(loanSearchField, gbc);

        // Sub-panel for the dynamic search fields related to book and student selection in the loan management 
        // section, using a GridBagLayout for organized placement of labels and input fields.
        GridBagConstraints gbcSub = new GridBagConstraints();
        gbcSub.insets = new Insets(5, 5, 5, 5);
        gbcSub.fill = GridBagConstraints.HORIZONTAL;
        
        // Adding labels and input fields for filtering books and students, 
        // as well as combo boxes for selecting the book and student when creating a new loan.
        gbcSub.gridx = 0; gbcSub.gridy = 0; newLoanFieldsPanel.add(new JLabel("Buscar Livro:"), gbcSub);
        gbcSub.gridx = 1; newLoanFieldsPanel.add(bookFilterField, gbcSub);
        
        gbcSub.gridx = 0; gbcSub.gridy = 1; newLoanFieldsPanel.add(new JLabel("Livro Selecionado:"), gbcSub);
        gbcSub.gridx = 1; newLoanFieldsPanel.add(loanBookCombo, gbcSub);
        
        gbcSub.gridx = 0; gbcSub.gridy = 2; newLoanFieldsPanel.add(new JLabel("Buscar Aluno:"), gbcSub);
        gbcSub.gridx = 1; newLoanFieldsPanel.add(studentFilterField, gbcSub);
        
        gbcSub.gridx = 0; gbcSub.gridy = 3; newLoanFieldsPanel.add(new JLabel("Estudante Selecionado:"), gbcSub);
        gbcSub.gridx = 1; newLoanFieldsPanel.add(loanStudentCombo, gbcSub);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        loanLeftPanel.add(newLoanFieldsPanel, gbc);


        // JPanel to hold the action buttons for managing loans, 
        // using a FlowLayout to arrange the buttons in a single row with spacing between them.
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btnPanel.add(checkoutBtn);
        btnPanel.add(returnBtn);
        btnPanel.add(renewBtn); 

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        loanLeftPanel.add(btnPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weighty = 1.0;
        loanLeftPanel.add(new JPanel(), gbc);

        // Set up the loans table with a row sorter for enabling sorting functionality.
        loanTable.setRowSorter(loanSorter);
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.add(new JScrollPane(loanTable), BorderLayout.CENTER);

        panel.add(loanLeftPanel, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);
        tabbedPane.addTab("Empréstimos", panel);
    }

    /**
     * Method to set up the "Reports" tab of the UI, which includes components for filtering and displaying reports related to loans.
     */
    private void setupReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Panel to hold the controls for filtering and managing reports, 
        // using a GridLayout to arrange the components in two rows with spacing between them.
        JPanel controlsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Row 1: Components for real-time name filtering and structured student ID/RA searching.
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchRow.add(new JLabel("Filtrar por nome do aluno:"));
        searchRow.add(reportSearchField);

        reportPatronPanel.add(new JLabel(" Filtrar por ID/RA do Aluno:"));
        reportPatronPanel.add(reportPatronField);
        reportPatronPanel.add(showPatronHistoryBtn); // Moved right next to its input field for immediate action
        searchRow.add(reportPatronPanel);

        // Line 2: buttons for filtering reports based on different criteria 
        // (active loans, overdue loans, general history) and resetting fines.
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonRow.add(showActiveLoansBtn);
        buttonRow.add(showOverdueLoansBtn);
        buttonRow.add(showAllHistoryBtn);
        buttonRow.add(resetFineBtn); 

        // Adding the search row and the button row to the controls panel, 
        // which will be placed at the top of the reports tab.
        controlsPanel.add(searchRow);
        controlsPanel.add(buttonRow);

        // Set up the reports table with a row sorter for enabling sorting functionality.
        reportTable.setRowSorter(reportSorter);
        
        // Add the controls panel to the top of the reports tab and the reports table in the center within a scroll pane.
        panel.add(controlsPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        
        tabbedPane.addTab("Relatórios", panel);
    }

    /**
     * Gets the form panel responsible for book data entry and editing.
     * * @return The book form JPanel object.
     */
    public JPanel getBookForm() { return bookForm; }

    /**
     * Gets the form panel responsible for student data entry and editing.
     * * @return The student form JPanel object.
     */
    public JPanel getStudentForm() { return studentForm; }

    /**
     * Gets the main tabbed pane that holds the application's sections.
     * * @return The JTabbedPane component.
     */
    public JTabbedPane getTabbedPane() { return tabbedPane; }

    /**
     * Gets the logout trigger button component.
     * * @return The logout JButton object.
     */
    public JButton getLogoutBtn() { return logoutBtn; }

    /**
     * Gets the text filter field used to dynamically search the book combo box in the loan section.
     * * @return The book filter JTextField object.
     */
    public JTextField getBookFilterField() { return bookFilterField; }

    /**
     * Gets the text filter field used to dynamically search the student combo box in the loan section.
     * * @return The student filter JTextField object.
     */
    public JTextField getStudentFilterField() { return studentFilterField; }

    /**
     * Gets the text input field used for searching and filtering the books table.
     * * @return The book search JTextField object.
     */
    public JTextField getBookSearchField() { return bookSearchField; }

    /**
     * Gets the book title text input field.
     * * @return The title JTextField object.
     */
    public JTextField getTitleField() { return titleField; }

    /**
     * Gets the book author text input field.
     * * @return The author JTextField object.
     */
    public JTextField getAuthorField() { return authorField; }

    /**
     * Gets the book ISBN code text input field.
     * * @return The ISBN JTextField object.
     */
    public JTextField getIsbnField() { return isbnField; }

    /**
     * Gets the book genre/category text input field.
     * * @return The genre JTextField object.
     */
    public JTextField getGenreField() { return genreField; }

    /**
     * Gets the book publication year text input field.
     * * @return The publication year JTextField object.
     */
    public JTextField getYearField() { return yearField; }

    /**
     * Gets the book available copies quantity text input field.
     * * @return The quantity JTextField object.
     */
    public JTextField getQtyField() { return qtyField; }

    /**
     * Gets the action button component used to add a new book to the inventory.
     * * @return The add book JButton object.
     */
    public JButton getAddBookBtn() { return addBookBtn; }

    /**
     * Gets the action button component used to edit existing book details.
     * * @return The edit book JButton object.
     */
    public JButton getEditBookBtn() { return editBookBtn; }

    /**
     * Gets the action button component used to delete a book from the inventory.
     * * @return The delete book JButton object.
     */
    public JButton getDeleteBookBtn() { return deleteBookBtn; }

    /**
     * Gets the utility button used to clear input text boxes in the book form.
     * * @return The clear book fields JButton object.
     */
    public JButton getClearBookBtn() { return clearBookBtn; }

    /**
     * Gets the core data table element displaying cataloged library books.
     * * @return The book inventory JTable object.
     */
    public JTable getBookTable() { return bookTable; }

    /**
     * Gets the table model mapping the data structure for the books grid.
     * * @return The book table DefaultTableModel object.
     */
    public DefaultTableModel getBookModel() { return bookModel; }

    /**
     * Gets the sorting mechanism attached to the books table.
     * * @return The TableRowSorter for the book table.
     */
    public TableRowSorter<DefaultTableModel> getBookSorter() { return bookSorter; }

    /**
     * Gets the text field used for dynamically filtering the students table.
     * * @return The patron search JTextField object.
     */
    public JTextField getPatronSearchField() { return patronSearchField; }

    /**
     * Gets the student name text box element inside the user form.
     * * @return The name JTextField object.
     */
    public JTextField getNameField() { return nameField; }

    /**
     * Gets the student registration number (RA) text field.
     * * @return The RA JTextField object.
     */
    public JTextField getRaField() { return raField; }

    /**
     * Gets the student contact details text field.
     * * @return The contact JTextField object.
     */
    public JTextField getContactField() { return contactField; }

    /**
     * Gets the secure password entry container for the student.
     * * @return The password JPasswordField object.
     */
    public JPasswordField getPasswordField() { return passwordField; }

    /**
     * Gets the button that triggers the addition of a new student profile.
     * * @return The add student JButton object.
     */
    public JButton getAddStudentBtn() { return addStudentBtn; }

    /**
     * Gets the button that triggers the update of an existing student profile.
     * * @return The edit student JButton object.
     */
    public JButton getEditStudentBtn() { return editStudentBtn; }

    /**
     * Gets the button designed to delete a student profile from the system.
     * * @return The delete student JButton object.
     */
    public JButton getDeleteStudentBtn() { return deleteStudentBtn; }

    /**
     * Gets the button that resets all text inputs in the student registration form.
     * * @return The clear student fields JButton object.
     */
    public JButton getClearStudentBtn() { return clearStudentBtn; }

    /**
     * Gets the main grid table displaying registered students.
     * * @return The student JTable object.
     */
    public JTable getStudentTable() { return studentTable; }

    /**
     * Gets the underlying data structure model for the students table.
     * * @return The student DefaultTableModel object.
     */
    public DefaultTableModel getStudentModel() { return studentModel; }

    /**
     * Gets the column sorting mechanism for the students table.
     * * @return The TableRowSorter for the student table.
     */
    public TableRowSorter<DefaultTableModel> getStudentSorter() { return studentSorter; }

    /**
     * Gets the text input field used for searching and filtering the loans table.
     * * @return The loan search JTextField object.
     */
    public JTextField getLoanSearchField() { return loanSearchField; }

    /**
     * Gets the combo box drop-down that manages book selection for checkout.
     * * @return The loan book JComboBox object.
     */
    public JComboBox<Book> getLoanBookCombo() { return loanBookCombo; }

    /**
     * Gets the combo box drop-down providing pickable student records for loan assignment.
     * * @return The loan student JComboBox object.
     */
    public JComboBox<Student> getLoanStudentCombo() { return loanStudentCombo; }

    /**
     * Gets the sub-panel container holding the specific form inputs for a new loan operation.
     * * @return The new loan fields JPanel object.
     */
    public JPanel getNewLoanFieldsPanel() { return newLoanFieldsPanel; }

    /**
     * Gets the transaction button used to execute the loan checkout process.
     * * @return The checkout JButton object.
     */
    public JButton getCheckoutBtn() { return checkoutBtn; }

    /**
     * Gets the button tasked with concluding an active loan via a book return.
     * * @return The return JButton object.
     */
    public JButton getReturnBtn() { return returnBtn; }

    /**
     * Gets the button used to recalculate and extend the due date of a selected loan.
     * * @return The renew JButton object.
     */
    public JButton getRenewBtn() { return renewBtn; } 

    /**
     * Gets the core data table representing active and past loan operations.
     * * @return The loan JTable object.
     */
    public JTable getLoanTable() { return loanTable; }

    /**
     * Gets the template model defining data structures within the loans table.
     * * @return The loan DefaultTableModel object.
     */
    public DefaultTableModel getLoanModel() { return loanModel; }

    /**
     * Gets the sorting component responsible for ordering the loans table view.
     * * @return The TableRowSorter for the loan table.
     */
    public TableRowSorter<DefaultTableModel> getLoanSorter() { return loanSorter; }

    /**
     * Gets the generic text field handling overarching filtering across report lists.
     * * @return The report search JTextField object.
     */
    public JTextField getReportSearchField() { return reportSearchField; }

    /**
     * Gets the text field where a specific student RA is input to generate targeted reports.
     * * @return The report patron ID JTextField object.
     */
    public JTextField getReportPatronField() { return reportPatronField; }

    /**
     * Gets the sub-panel housing the search elements for specific student history tracking.
     * * @return The report patron JPanel object.
     */
    public JPanel getReportPatronPanel() { return reportPatronPanel; } 

    /**
     * Gets the trigger button filtering the reports to show exclusively active loans.
     * * @return The show active loans JButton object.
     */
    public JButton getShowActiveLoansBtn() { return showActiveLoansBtn; }

    /**
     * Gets the trigger button limiting the report view strictly to overdue library loans.
     * * @return The show overdue loans JButton object.
     */
    public JButton getShowOverdueLoansBtn() { return showOverdueLoansBtn; }

    /**
     * Gets the button responsible for clearing constraints and showing the system's full loan history.
     * * @return The show all history JButton object.
     */
    public JButton getShowAllHistoryBtn() { return showAllHistoryBtn; }

    /**
     * Gets the button that fetches and limits the report view to the specified patron ID/RA history.
     * * @return The show patron history JButton object.
     */
    public JButton getShowPatronHistoryBtn() { return showPatronHistoryBtn; }

    /**
     * Gets the administrative control button tasked with resetting penalty fines down to zero.
     * * @return The reset fine JButton object.
     */
    public JButton getResetFineBtn() { return resetFineBtn; }

    /**
     * Gets the main tracking data grid utilized for comprehensive reports and audits.
     * * @return The report JTable object.
     */
    public JTable getReportTable() { return reportTable; }

    /**
     * Gets the layout template model mapping the cells within the system reports grid.
     * * @return The report DefaultTableModel object.
     */
    public DefaultTableModel getReportModel() { return reportModel; }

    /**
     * Gets the sorting and ordering engine attached to the history and reports table.
     * * @return The TableRowSorter for the report table.
     */
    public TableRowSorter<DefaultTableModel> getReportSorter() { return reportSorter; }
    
}