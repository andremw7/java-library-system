import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// This class connects the Library data with the LibraryUI screen. 
// It controls the business logic, actions, and validates user inputs.
public class LibraryController {
    private final Library library;
    private final LibraryUI view;
    private final String userRole;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Lists to temporarily hold active loans and report history
    private List<Loan> currentActiveLoans = new ArrayList<>();
    private List<Loan> currentReportLoans = new ArrayList<>();
    private ReportMode reportMode = ReportMode.ACTIVE;
    private String reportPatronId = "";
    

    // Action to handle user logout
    private Runnable logoutAction;

    // Enum representing different filtering modes for library reports
    private enum ReportMode {
        ACTIVE,
        OVERDUE,
        ALL_HISTORY,
        PATRON_HISTORY
    }

    // Constructor: Connects the components, sets permissions, and loads initial data
    public LibraryController(Library library, LibraryUI view, String userRole) {
        this.library = library;
        this.view = view;
        this.userRole = userRole;
        applyPermissions();
        initListeners();
        refreshAll();
        this.view.setVisible(true);
    }


    public void setLogoutAction(Runnable logoutAction) {
        this.logoutAction = logoutAction;
    }

    // Access Control Method: Shows or hides UI elements based on the user logged in
    private void applyPermissions() {
        boolean isAdmin = "admin".equalsIgnoreCase(userRole);
        boolean isBibliotecario = "bibliotecario".equalsIgnoreCase(userRole);
        boolean isEstudante = userRole != null && userRole.startsWith("estudante:");

        // Admin and Librarian can view loan panels; Admin can view form inputs
        view.getNewLoanFieldsPanel().setVisible(isAdmin || isBibliotecario);
        view.getBookForm().setVisible(isAdmin);
        view.getStudentForm().setVisible(isAdmin);
        view.setAdminControlsEnabled(isAdmin);

        // UI adjustments if the logged-in user is a Student
        if (isEstudante) {
            String ra = userRole.substring("estudante:".length());
            reportPatronId = ra;
            reportMode = ReportMode.PATRON_HISTORY;

            view.getNewLoanFieldsPanel().setVisible(false);

            // Remove the Users tab for standard student views
            JTabbedPane tabs = view.getTabbedPane();
            for (int i = 0; i < tabs.getTabCount(); i++) {
                if (tabs.getTitleAt(i).equalsIgnoreCase("Usuários")) {
                    tabs.removeTabAt(i);
                    break;
                }
            }

            // Hide specific management buttons from student view
            view.getShowActiveLoansBtn().setVisible(false);
            view.getShowOverdueLoansBtn().setVisible(false);
            view.getShowAllHistoryBtn().setVisible(false);
            view.getShowPatronHistoryBtn().setVisible(false);
            view.getResetFineBtn().setVisible(false);
            view.getReportPatronField().setVisible(false);
            if (view.getReportPatronPanel() != null) {
                view.getReportPatronPanel().setVisible(false);
            }

            // Students can only renew loans, they cannot checkout or return books themselves
            view.getCheckoutBtn().setEnabled(false);
            view.getReturnBtn().setEnabled(false);
            view.getLoanBookCombo().setEnabled(false);
            view.getLoanStudentCombo().setEnabled(false);
            view.getRenewBtn().setEnabled(true);

            view.getBookTable().setEnabled(true);
            view.getBookSearchField().setEnabled(true);
            view.getBookSearchField().setEditable(true);

            // Restrict student selection box to show only this student
            JComboBox<Student> studentCombo = view.getLoanStudentCombo();
            studentCombo.removeAllItems();
            Student currentStudent = library.findStudentByRa(ra);
            if (currentStudent != null) {
                studentCombo.addItem(currentStudent);
            }
            studentCombo.setEnabled(false); 

            JOptionPane.showMessageDialog(view,
                    "Bem-vindo! Você acessou como Estudante.\nSua conta permite visualizar os livros e solicitar a renovação dos seus empréstimos ativos.",
                    "Controle de Acesso", JOptionPane.INFORMATION_MESSAGE);
        } else if (isBibliotecario) {
            
            // UI adjustments if the logged-in user is a Librarian
            view.getCheckoutBtn().setEnabled(true);
            view.getReturnBtn().setEnabled(true);
            view.getRenewBtn().setEnabled(true);
            view.getLoanBookCombo().setEnabled(true);
            view.getLoanStudentCombo().setEnabled(true);

            JOptionPane.showMessageDialog(view,
                    "Você acessou como Bibliotecário.\nFunções de Adicionar, Editar e Excluir Livros/Usuários estão desativadas.",
                    "Controle de Acesso", JOptionPane.INFORMATION_MESSAGE);
        } else if (isAdmin) {

            // UI adjustments if the logged-in user is an Admin
            view.getCheckoutBtn().setEnabled(true);
            view.getReturnBtn().setEnabled(true);
            view.getRenewBtn().setEnabled(true);
        }
    }

    // Interaction Setup Method: Binds button clicks and selection changes to specific actions
    private void initListeners() {
        view.getLogoutBtn().addActionListener(e -> {
            if (confirm("Deseja realmente encerrar a sessão atual e voltar ao login?")) {
                if (logoutAction != null) {
                    logoutAction.run();
                }
            }
        });

        // Book Management Actions
        view.getAddBookBtn().addActionListener(e -> runSafely(this::addBook));
        view.getEditBookBtn().addActionListener(e -> runSafely(this::editBook));
        view.getDeleteBookBtn().addActionListener(e -> runSafely(this::deleteBook));
        view.getClearBookBtn().addActionListener(e -> clearBookFields());
        setupSearchFilter(view.getBookSearchField(), view.getBookSorter());

        // Event listener to auto-fill input fields when a book is selected in the table, but only if the user has admin permissions to edit books
        view.getBookTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getBookTable().getSelectedRow();
                if (row != -1) {
                    int modelRow = view.getBookTable().convertRowIndexToModel(row);
                    String isbn = (String) view.getBookModel().getValueAt(modelRow, 2);
                    Book selected = library.findBookByIsbn(isbn);
                    if (selected != null && "admin".equalsIgnoreCase(userRole)) {
                        view.getTitleField().setText(selected.getTitle());
                        view.getAuthorField().setText(selected.getAuthor());
                        view.getIsbnField().setText(selected.getIsbn());
                        view.getGenreField().setText(selected.getGenre());
                        view.getYearField().setText(String.valueOf(selected.getYear()));
                        view.getQtyField().setText(String.valueOf(selected.getAvailableCopies()));
                    }
                }
            }
        });

        // Student Management Actions
        view.getAddStudentBtn().addActionListener(e -> runSafely(this::addStudent));
        view.getEditStudentBtn().addActionListener(e -> runSafely(this::editStudent));
        view.getDeleteStudentBtn().addActionListener(e -> runSafely(this::deleteStudent));
        view.getClearStudentBtn().addActionListener(e -> clearStudentFields());
        setupSearchFilter(view.getPatronSearchField(), view.getStudentSorter());

        // Event listener to auto-fill input fields when a student is selected in the table, but only if the user has admin permissions to edit users
        view.getStudentTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getStudentTable().getSelectedRow();
                if (row != -1) {
                    int modelRow = view.getStudentTable().convertRowIndexToModel(row);
                    String ra = (String) view.getStudentModel().getValueAt(modelRow, 1);
                    Student selected = library.findStudentByRa(ra);
                    if (selected != null && "admin".equalsIgnoreCase(userRole)) {
                        view.getNameField().setText(selected.getName());
                        view.getRaField().setText(selected.getRa());
                        view.getContactField().setText(selected.getContact());
                        view.getPasswordField().setText(selected.getPassword());
                    }
                }
            }
        });

        // Loan Management Actions
        view.getCheckoutBtn().addActionListener(e -> runSafely(this::checkoutBook));
        view.getReturnBtn().addActionListener(e -> runSafely(this::returnBook));
        view.getRenewBtn().addActionListener(e -> runSafely(this::renewBook));
        setupSearchFilter(view.getLoanSearchField(), view.getLoanSorter());

        // Updates dropdown selection lists while typing filters in the loan registration tab, ensuring that the options are always relevant to the search query and available inventory
        setupDynamicComboFilter(view.getBookFilterField(), this::refreshLoanCombos);
        setupDynamicComboFilter(view.getStudentFilterField(), this::refreshLoanCombos);

        // Report Filtering Actions
        // Setup search filters for the reporting module, allowing dynamic filtering of the report table based on user input, and ensuring that the search is case-insensitive and treats special characters as literals
        setupSearchFilter(view.getReportSearchField(), view.getReportSorter());
        
        // Report display mode selection buttons, allowing users to switch between different views of the loan history and active loans, with specific handling for student users to automatically filter their own loan history
        view.getShowActiveLoansBtn().addActionListener(e -> { reportMode = ReportMode.ACTIVE; refreshReports(); });
        view.getShowOverdueLoansBtn().addActionListener(e -> { reportMode = ReportMode.OVERDUE; refreshReports(); });
        view.getShowAllHistoryBtn().addActionListener(e -> { reportMode = ReportMode.ALL_HISTORY; refreshReports(); });
        view.getShowPatronHistoryBtn().addActionListener(e -> {
            String ra = view.getReportPatronField().getText().trim();
            if (ra.isEmpty()) {
                showError("Digite o ID/RA do usuário para buscar o histórico específico.");
                return;
            }
            reportPatronId = ra;
            reportMode = ReportMode.PATRON_HISTORY;
            refreshReports();
        });
        view.getResetFineBtn().addActionListener(e -> runSafely(this::resetFine));
    }

    // Handles extending a book return deadline by 14 days, but only if the book has at least 2 copies available in the library to ensure that other users can still borrow it. This function is accessible to both Admin and Librarian roles, but not to Students.
    private void renewBook() {
        int row = view.getLoanTable().getSelectedRow();
        requireSelected(row != -1 ? row : null, "Selecione um empréstimo ativo na tabela para renovar.");
        
        int modelRow = view.getLoanTable().convertRowIndexToModel(row);
        Loan selected = currentActiveLoans.get(modelRow);
        requireSelected(selected, "Empréstimo inválido selecionado.");
        
        // Rule: Can only renew if the library has extra copies left
        if (selected.getBook().getAvailableCopies() < 2) {
            throw new IllegalArgumentException("A renovação não é permitida para este livro, pois ele possui menos de 2 cópias disponíveis.");
        }

        selected.setDueDate(selected.getDueDate().plusDays(14));
        DataManager.save(library);
        refreshAll();
        showInfo("Empréstimo renovado com sucesso por mais 14 dias!");
    }

    // Main update method to synchronize all interface tables with database changes, ensuring that any addition, modification, or deletion of books, students, or loans is immediately reflected in the user interface. This method is called after every operation that changes the library's data to maintain consistency and provide real-time feedback to the user.
    private void refreshAll() {
        refreshBookTable();
        refreshStudentTable();
        refreshLoanCombos();
        refreshActiveLoansTable();
        refreshReports();
    }

    // Reloads the table showing books information, clearing existing rows and repopulating it with the current list of books from the library. 
    // This method ensures that any changes to the book inventory, 
    // such as additions, edits, or deletions, are immediately visible to the user in the interface.
    private void refreshBookTable() {
        DefaultTableModel model = view.getBookModel();
        model.setRowCount(0);
        for (Book b : library.getBooks()) {
            model.addRow(new Object[]{b.getTitle(), b.getAuthor(), b.getIsbn(), b.getGenre(), b.getYear(), b.getAvailableCopies()});
        }
    }

    // Reloads the table showing student information, clearing existing rows and repopulating it with the current list of students from the library. 
    // This method ensures that any changes to the student database, such as additions, edits, or deletions, are immediately visible to the user in the interface.
    private void refreshStudentTable() {
        DefaultTableModel model = view.getStudentModel();
        model.setRowCount(0);
        for (Student s : library.getStudents()) {
            model.addRow(new Object[]{s.getName(), s.getRa(), s.getContact(), s.getPassword()});
        }
    }
    
    // Refreshes the dropdown selection components based on quick filters typed by the user in the loan registration tab, 
    // ensuring that the options for books and students are always relevant to the search query and reflect the current inventory and student database. 
    // This method is called whenever the user types in the filter fields, providing a dynamic and responsive selection experience.
    private void refreshLoanCombos() {
        String bookQuery = view.getBookFilterField().getText().trim().toLowerCase();
        JComboBox<Book> bookCombo = view.getLoanBookCombo();
        bookCombo.removeAllItems();
        
        for (Book b : library.getBooks()) {
            if (b.getAvailableCopies() > 0) {
                if (bookQuery.isEmpty() || 
                    b.getTitle().toLowerCase().contains(bookQuery) || 
                    b.getAuthor().toLowerCase().contains(bookQuery) || 
                    b.getIsbn().toLowerCase().contains(bookQuery)) {
                    bookCombo.addItem(b);
                }
            }
        }
        
        if (userRole != null && userRole.startsWith("estudante:")) {
            return;
        }

        String studentQuery = view.getStudentFilterField().getText().trim().toLowerCase();
        JComboBox<Student> studentCombo = view.getLoanStudentCombo();
        studentCombo.removeAllItems();
        
        for (Student s : library.getStudents()) {
            if (studentQuery.isEmpty() || 
                s.getName().toLowerCase().contains(studentQuery) || 
                s.getRa().toLowerCase().contains(studentQuery)) {
                studentCombo.addItem(s);
            }
        }
    }

    // Reloads data inside the current open loans layout table, filtering only active loans and, if the logged-in user is a student, 
    // showing only their own active loans.
    private void refreshActiveLoansTable() {
        DefaultTableModel model = view.getLoanModel();
        model.setRowCount(0);
        currentActiveLoans.clear();
        for (Loan l : library.getLoans()) {
            if (l.isActive()) {
                if (userRole != null && userRole.startsWith("estudante:")) {
                    String studentRa = userRole.substring("estudante:".length());
                    if (l.getStudent() == null || !l.getStudent().getRa().equalsIgnoreCase(studentRa)) {
                        continue;
                    }
                }
                currentActiveLoans.add(l);
                model.addRow(new Object[]{
                        l.getBook().getTitle(),
                        l.getStudent().getName(),
                        formatDate(l.getLoanDate()),
                        formatDate(l.getDueDate()),
                        l.getStatus()
                });
            }
        }
    }

    // Handles loading and filtering records in the historical report section, 
    // allowing users to view different subsets of loan history based on their selection (active loans, overdue loans, complete history, 
    // or specific patron history). 
    // This method is called whenever the user changes the report filter mode or performs a search in the report section, 
    // ensuring that the displayed data is always relevant to the user's current query and selection criteria.
    private void refreshReports() {
        DefaultTableModel model = view.getReportModel();
        model.setRowCount(0);
        currentReportLoans.clear();
        List<Loan> source = library.getLoans();

        if (userRole != null && userRole.startsWith("estudante:")) {
            reportMode = ReportMode.PATRON_HISTORY;
            reportPatronId = userRole.substring("estudante:".length());
        }

        for (Loan l : source) {
            boolean include = false;
            switch (reportMode) {
                case ACTIVE:
                    include = l.isActive();
                    break;
                case OVERDUE:
                    include = l.isOverdue();
                    break;
                case ALL_HISTORY:
                    include = true;
                    break;
                case PATRON_HISTORY:
                    include = l.getStudent() != null && l.getStudent().getRa().equalsIgnoreCase(reportPatronId);
                    break;
            }
            if (include) {
                currentReportLoans.add(l);
                model.addRow(new Object[]{
                        l.getBook().getTitle(),
                        l.getStudent().getName(),
                        l.getStudent().getRa(),
                        formatDate(l.getLoanDate()),
                        formatDate(l.getDueDate()),
                        formatDate(l.getReturnDate()),
                        money(l.calculateFine()),
                        l.getStatus()
                });
            }
        }
    }

    // Reads inputs, processes validations, and saves a new book record to the library. 
    // This method is triggered when the user clicks the "Add Book" button, and it ensures that all required fields are filled out correctly, 
    // that the ISBN is valid, and that the year and quantity are positive integers. 
    // If any validation fails, an appropriate error message is shown to the user. Upon successful addition, the book list is refreshed to reflect the new entry.
    private void addBook() {
        String title = view.getTitleField().getText().trim();
        String author = view.getAuthorField().getText().trim();
        String isbn = view.getIsbnField().getText().trim();
        String genre = view.getGenreField().getText().trim();
        
        // Validates ISBN format directly
        validateStrictPositiveIntegerString(isbn, "ISBN");
        
        int year = parsePositiveInteger(view.getYearField().getText().trim(), "Ano", false);
        int qty = parsePositiveInteger(view.getQtyField().getText().trim(), "Cópias", true);

        library.addBook(new Book(title, author, isbn, genre, year, qty));
        DataManager.save(library);
        refreshAll();
        clearBookFields();
        showInfo("Livro adicionado com sucesso.");
    }

    // Updates properties of an already existing book record based on user input
    private void editBook() {
        int row = view.getBookTable().getSelectedRow();
        requireSelected(row != -1 ? row : null, "Selecione um livro na tabela para editar.");
        int modelRow = view.getBookTable().convertRowIndexToModel(row);
        String isbnOriginal = (String) view.getBookModel().getValueAt(modelRow, 2);
        Book selected = library.findBookByIsbn(isbnOriginal);
        requireSelected(selected, "Livro não encontrado.");

        String title = view.getTitleField().getText().trim();
        String author = view.getAuthorField().getText().trim();
        String isbn = view.getIsbnField().getText().trim();
        String genre = view.getGenreField().getText().trim();
        
        // Validation for the ISBN field to ensure it contains only valid characters and formats, 
        // preventing invalid data entry and ensuring consistency in the database.
        validateStrictPositiveIntegerString(isbn, "ISBN");
        
        int year = parsePositiveInteger(view.getYearField().getText().trim(), "Ano", false);
        int qty = parsePositiveInteger(view.getQtyField().getText().trim(), "Cópias", true);

        library.updateBook(selected, title, author, isbn, genre, year, qty);
        DataManager.save(library);
        refreshAll();
        clearBookFields();
        showInfo("Livro atualizado com sucesso.");
    }

    // Removes a book completely from the catalog configuration
    private void deleteBook() {
        int row = view.getBookTable().getSelectedRow();
        requireSelected(row != -1 ? row : null, "Selecione um livro na tabela para excluir.");
        int modelRow = view.getBookTable().convertRowIndexToModel(row);
        String isbn = (String) view.getBookModel().getValueAt(modelRow, 2);
        Book selected = library.findBookByIsbn(isbn);
        requireSelected(selected, "Livro não encontrado.");

        if (confirm("Tem certeza que deseja excluir o livro: " + selected.getTitle() + "?")) {
            library.removeBook(selected);
            DataManager.save(library);
            refreshAll();
            clearBookFields();
            showInfo("Livro removed com sucesso.");
        }
    }

    // Validates inputs and creates a new Student registration profile in the library system, 
    // allowing them to borrow books and view their loan history.
    private void addStudent() {
        String name = view.getNameField().getText().trim();
        String ra = view.getRaField().getText().trim();
        String contact = view.getContactField().getText().trim();
        String password = view.getPasswordField().getText().trim();

        // Validation for the ID/RA field to ensure it contains only valid characters and formats,
        validateStrictPositiveIntegerString(ra, "ID/RA");

        library.addStudent(new Student(name, ra, contact, password));
        DataManager.save(library);
        refreshAll();
        clearStudentFields();
        showInfo("Usuário adicionado com sucesso.");
    }

    // Updates properties of an already existing user profile
    private void editStudent() {
        int row = view.getStudentTable().getSelectedRow();
        requireSelected(row != -1 ? row : null, "Selecione um usuário na tabela para editar.");
        int modelRow = view.getStudentTable().convertRowIndexToModel(row);
        String raOriginal = (String) view.getStudentModel().getValueAt(modelRow, 1);
        Student selected = library.findStudentByRa(raOriginal);
        requireSelected(selected, "Usuário não encontrado.");

        String name = view.getNameField().getText().trim();
        String ra = view.getRaField().getText().trim();
        String contact = view.getContactField().getText().trim();
        String password = view.getPasswordField().getText().trim();

        // Validation for the ID/RA field to ensure it contains only valid characters and formats, 
        // preventing invalid data entry and ensuring consistency in the database.
        validateStrictPositiveIntegerString(ra, "ID/RA");

        library.updateStudent(selected, name, ra, contact, password);
        DataManager.save(library);
        refreshAll();
        clearStudentFields();
        showInfo("Usuário atualizado com sucesso.");
    }

    // Deletes a student profile registration permanently
    private void deleteStudent() {
        int row = view.getStudentTable().getSelectedRow();
        requireSelected(row != -1 ? row : null, "Selecione um usuário na tabela para excluir.");
        int modelRow = view.getStudentTable().convertRowIndexToModel(row);
        String ra = (String) view.getStudentModel().getValueAt(modelRow, 1);
        Student selected = library.findStudentByRa(ra);
        requireSelected(selected, "Usuário não encontrado.");

        if (confirm("Tem certeza que deseja remover o usuário: " + selected.getName() + "?")) {
            library.removeStudent(selected);
            DataManager.save(library);
            refreshAll();
            clearStudentFields();
            showInfo("Usuário removido com sucesso.");
        }
    }

    // Connects a chosen book to a student to register a new system loan
    private void checkoutBook() {
        Book book = (Book) view.getLoanBookCombo().getSelectedItem();
        Student student = (Student) view.getLoanStudentCombo().getSelectedItem();
        requireSelected(book, "Selecione um livro disponível.");
        requireSelected(student, "Selecione um usuário válido.");

        library.performLoan(book, student);
        DataManager.save(library);
        refreshAll();
        showInfo("Empréstimo registrado com sucesso.");
    }

    // Finishes an open transaction loan, calculating overdue fine statuses automatically
    private void returnBook() {
        int row = view.getLoanTable().getSelectedRow();
        requireSelected(row != -1 ? row : null, "Selecione um empréstimo na tabela para realizar a devolução.");
        int modelRow = view.getLoanTable().convertRowIndexToModel(row);
        Loan selected = currentActiveLoans.get(modelRow);

        double fine = selected.calculateFine();
        String msg = "Confirmar devolução de '" + selected.getBook().getTitle() + "'?";
        if (fine > 0) {
            msg += "\nAtenção: Existe uma multa acumulada de " + money(fine) + " por atraso.";
        }

        if (confirm(msg)) {
            library.performReturn(selected);
            DataManager.save(library);
            refreshAll();
            if (fine > 0) {
                showInfo("Devolução registrada. Vá até a aba de relatórios caso precise zerar ou gerenciar a multa.");
            } else {
                showInfo("Livro devolvido com sucesso.");
            }
        }
    }

    // Waives or flags an existing fee balance statement as paid
    private void resetFine() {
        int row = view.getReportTable().getSelectedRow();
        requireSelected(row != -1 ? row : null, "Selecione um registro na tabela de relatórios para perdoar a multa.");
        int modelRow = view.getReportTable().convertRowIndexToModel(row);
        Loan selected = currentReportLoans.get(modelRow);

        if (selected.calculateFine() <= 0 && selected.isFinePaid()) {
            showInfo("Este registro não possui multa pendente.");
            return;
        }

        if (confirm("Deseja perdoar/marcar como paga a multa deste registro?")) {
            selected.setFinePaid(true);
            DataManager.save(library);
            refreshReports();
            showInfo("Multa atualizada com sucesso.");
        }
    }

    // Clears all entry boxes related to the book input form section
    private void clearBookFields() {
        view.getTitleField().setText("");
        view.getAuthorField().setText("");
        view.getIsbnField().setText("");
        view.getGenreField().setText("");
        view.getYearField().setText("");
        view.getQtyField().setText("");
        view.getBookTable().clearSelection();
    }

    // Clears all entry boxes related to the student registration profile section
    private void clearStudentFields() {
        view.getNameField().setText("");
        view.getRaField().setText("");
        view.getContactField().setText("");
        view.getPasswordField().setText("");
        view.getStudentTable().clearSelection();
    }

    // Parses string values into safe integer values, verifying format requirements
    private int parsePositiveInteger(String text, String fieldName, boolean allowZero) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo '" + fieldName + "' não pode estar em branco.");
        }
        if (text.contains(".") || text.contains(",")) {
            throw new IllegalArgumentException("O campo '" + fieldName + "' deve ser um número inteiro (não use pontos ou vírgulas decimais).");
        }
        try {
            int value = Integer.parseInt(text);
            if (allowZero && value < 0) {
                throw new IllegalArgumentException("O campo '" + fieldName + "' não pode ser um número negativo.");
            }
            if (!allowZero && value <= 0) {
                throw new IllegalArgumentException("O campo '" + fieldName + "' deve ser um número inteiro estritamente maior que zero.");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo '" + fieldName + "' deve conter apenas algarismos numéricos válidos.");
        }
    }

    // Strictly enforces that text values contain only absolute positive numbers (No letters, negative signs, or symbols)
    private void validateStrictPositiveIntegerString(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo '" + fieldName + "' não pode estar em branco.");
        }
        if (text.contains("-")) {
            throw new IllegalArgumentException("O campo '" + fieldName + "' não permite números negativos.");
        }
        if (text.contains(".") || text.contains(",")) {
            throw new IllegalArgumentException("O campo '" + fieldName + "' deve ser um número inteiro (não use pontos ou vírgulas).");
        }
        // Validação via Regex: garante que a string tenha exclusivamente algarismos de 0 a 9
        if (!text.matches("\\d+")) {
            throw new IllegalArgumentException("O campo '" + fieldName + "' deve conter apenas algarismos numéricos (letras ou caracteres especiais não são permitidos).");
        }
        try {
            long val = Long.parseLong(text);
            if (val <= 0) {
                throw new IllegalArgumentException("O campo '" + fieldName + "' deve ser um número maior que zero.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo '" + fieldName + "' excede o limite numérico máximo permitido.");
        }
    }

    // Links a live filtering tool to search components for instant updates as you type
    private void setupSearchFilter(JTextField field, TableRowSorter<DefaultTableModel> sorter) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = field.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
                }
            }
        });
    }

    // Monitors text input updates to refresh linked dropdown elements dynamically
    private void setupDynamicComboFilter(JTextField field, Runnable onFilterChange) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onFilterChange.run(); }
            public void removeUpdate(DocumentEvent e) { onFilterChange.run(); }
            public void changedUpdate(DocumentEvent e) { onFilterChange.run(); }
        });
    }

    // Wraps runtime operations in safety blocks to catch errors and display warnings gracefully
    private void runSafely(Action action) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro inesperado: " + e.getMessage());
        }
    }

    // Validates that an objective collection context instance row selection is valid and not null, 
    // throwing an error with a custom message if the validation fails.
    private void requireSelected(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    // Displays an absolute verification prompt modal overlay window interface
    private boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(view, message, "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // Displays informational alert panel overlays to users with a custom message and an "OK" button to acknowledge
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(view, message, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    // Displays error notification popups to users with a custom message and an "OK" button to acknowledge, using a warning icon to indicate issues
    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    // Formats plain double data numbers into currency patterns (Brazilian Real)
    private String money(double value) {
        return String.format("R$ %.2f", value);
    }

    // Safely transforms dates into readable strings or fallback hyphens if the date is null, 
    // ensuring that the interface displays consistent and user-friendly date information without showing raw null values.
    private String formatDate(java.time.LocalDate date) {
        return date == null ? "-" : date.format(dateFormatter);
    }

    // Simple functional utility blueprint used for safe runnable command pattern tasks
    private interface Action {
        void run();
    }
}