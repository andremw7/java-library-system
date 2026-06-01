import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// Controlador que gerencia as regras de negócio e validações rígidas do sistema
public class LibraryController {
    private final Library library;
    private final LibraryUI view;
    private final String userRole;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private List<Loan> currentActiveLoans = new ArrayList<>();
    private List<Loan> currentReportLoans = new ArrayList<>();
    private ReportMode reportMode = ReportMode.ACTIVE;
    private String reportPatronId = "";
    
    private Runnable logoutAction;

    private enum ReportMode {
        ACTIVE,
        OVERDUE,
        ALL_HISTORY,
        PATRON_HISTORY
    }

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

    private void applyPermissions() {
        boolean isAdmin = "admin".equalsIgnoreCase(userRole);
        boolean isBibliotecario = "bibliotecario".equalsIgnoreCase(userRole);
        boolean isEstudante = userRole != null && userRole.startsWith("estudante:");

        view.getNewLoanFieldsPanel().setVisible(isAdmin || isBibliotecario);
        view.getBookForm().setVisible(isAdmin);
        view.getStudentForm().setVisible(isAdmin);
        view.setAdminControlsEnabled(isAdmin);

        if (isEstudante) {
            String ra = userRole.substring("estudante:".length());
            reportPatronId = ra;
            reportMode = ReportMode.PATRON_HISTORY;

            view.getNewLoanFieldsPanel().setVisible(false);

            JTabbedPane tabs = view.getTabbedPane();
            for (int i = 0; i < tabs.getTabCount(); i++) {
                if (tabs.getTitleAt(i).equalsIgnoreCase("Usuários")) {
                    tabs.removeTabAt(i);
                    break;
                }
            }

            view.getShowActiveLoansBtn().setVisible(false);
            view.getShowOverdueLoansBtn().setVisible(false);
            view.getShowAllHistoryBtn().setVisible(false);
            view.getShowPatronHistoryBtn().setVisible(false);
            view.getResetFineBtn().setVisible(false);
            view.getReportPatronField().setVisible(false);
            if (view.getReportPatronPanel() != null) {
                view.getReportPatronPanel().setVisible(false);
            }

            view.getCheckoutBtn().setEnabled(false);
            view.getReturnBtn().setEnabled(false);
            view.getLoanBookCombo().setEnabled(false);
            view.getLoanStudentCombo().setEnabled(false);
            view.getRenewBtn().setEnabled(true);

            view.getBookTable().setEnabled(true);
            view.getBookSearchField().setEnabled(true);
            view.getBookSearchField().setEditable(true);

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
            view.getCheckoutBtn().setEnabled(true);
            view.getReturnBtn().setEnabled(true);
            view.getRenewBtn().setEnabled(true);
            view.getLoanBookCombo().setEnabled(true);
            view.getLoanStudentCombo().setEnabled(true);

            JOptionPane.showMessageDialog(view,
                    "Você acessou como Bibliotecário.\nFunções de Adicionar, Editar e Excluir Livros/Usuários estão desativadas.",
                    "Controle de Acesso", JOptionPane.INFORMATION_MESSAGE);
        } else if (isAdmin) {
            view.getCheckoutBtn().setEnabled(true);
            view.getReturnBtn().setEnabled(true);
            view.getRenewBtn().setEnabled(true);
        }
    }

    private void initListeners() {
        view.getLogoutBtn().addActionListener(e -> {
            if (confirm("Deseja realmente encerrar a sessão atual e voltar ao login?")) {
                if (logoutAction != null) {
                    logoutAction.run();
                }
            }
        });

        // Aba de Livros
        view.getAddBookBtn().addActionListener(e -> runSafely(this::addBook));
        view.getEditBookBtn().addActionListener(e -> runSafely(this::editBook));
        view.getDeleteBookBtn().addActionListener(e -> runSafely(this::deleteBook));
        view.getClearBookBtn().addActionListener(e -> clearBookFields());
        setupSearchFilter(view.getBookSearchField(), view.getBookSorter());

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

        // Aba de Usuários
        view.getAddStudentBtn().addActionListener(e -> runSafely(this::addStudent));
        view.getEditStudentBtn().addActionListener(e -> runSafely(this::editStudent));
        view.getDeleteStudentBtn().addActionListener(e -> runSafely(this::deleteStudent));
        view.getClearStudentBtn().addActionListener(e -> clearStudentFields());
        setupSearchFilter(view.getPatronSearchField(), view.getStudentSorter());

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

        // Aba de Empréstimos
        view.getCheckoutBtn().addActionListener(e -> runSafely(this::checkoutBook));
        view.getReturnBtn().addActionListener(e -> runSafely(this::returnBook));
        view.getRenewBtn().addActionListener(e -> runSafely(this::renewBook));
        setupSearchFilter(view.getLoanSearchField(), view.getLoanSorter());

        setupDynamicComboFilter(view.getBookFilterField(), this::refreshLoanCombos);
        setupDynamicComboFilter(view.getStudentFilterField(), this::refreshLoanCombos);

        // Aba de Relatórios
        // ALTERAÇÃO: Adicionado o setup do filtro de busca para a tabela de relatórios funcionar dinamicamente ao digitar!
        setupSearchFilter(view.getReportSearchField(), view.getReportSorter());
        
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

    private void renewBook() {
        int row = view.getLoanTable().getSelectedRow();
        requireSelected(row != -1 ? row : null, "Selecione um empréstimo ativo na tabela para renovar.");
        
        int modelRow = view.getLoanTable().convertRowIndexToModel(row);
        Loan selected = currentActiveLoans.get(modelRow);
        requireSelected(selected, "Empréstimo inválido selecionado.");

        if (selected.getBook().getAvailableCopies() < 2) {
            throw new IllegalArgumentException("A renovação não é permitida para este livro, pois ele possui menos de 2 cópias disponíveis.");
        }

        selected.setDueDate(selected.getDueDate().plusDays(14));
        DataManager.save(library);
        refreshAll();
        showInfo("Empréstimo renovado com sucesso por mais 14 dias!");
    }

    private void refreshAll() {
        refreshBookTable();
        refreshStudentTable();
        refreshLoanCombos();
        refreshActiveLoansTable();
        refreshReports();
    }

    private void refreshBookTable() {
        DefaultTableModel model = view.getBookModel();
        model.setRowCount(0);
        for (Book b : library.getBooks()) {
            model.addRow(new Object[]{b.getTitle(), b.getAuthor(), b.getIsbn(), b.getGenre(), b.getYear(), b.getAvailableCopies()});
        }
    }

    private void refreshStudentTable() {
        DefaultTableModel model = view.getStudentModel();
        model.setRowCount(0);
        for (Student s : library.getStudents()) {
            model.addRow(new Object[]{s.getName(), s.getRa(), s.getContact(), s.getPassword()});
        }
    }

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

    private void addBook() {
        String title = view.getTitleField().getText().trim();
        String author = view.getAuthorField().getText().trim();
        String isbn = view.getIsbnField().getText().trim();
        String genre = view.getGenreField().getText().trim();
        
        // Validação rígida no campo ISBN para rejeitar letras, negativos e quebras
        validateStrictPositiveIntegerString(isbn, "ISBN");
        
        int year = parsePositiveInteger(view.getYearField().getText().trim(), "Ano", false);
        int qty = parsePositiveInteger(view.getQtyField().getText().trim(), "Cópias", true);

        library.addBook(new Book(title, author, isbn, genre, year, qty));
        DataManager.save(library);
        refreshAll();
        clearBookFields();
        showInfo("Livro adicionado com sucesso.");
    }

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
        
        // Validação rígida no campo ISBN também ao salvar uma edição de livro
        validateStrictPositiveIntegerString(isbn, "ISBN");
        
        int year = parsePositiveInteger(view.getYearField().getText().trim(), "Ano", false);
        int qty = parsePositiveInteger(view.getQtyField().getText().trim(), "Cópias", true);

        library.updateBook(selected, title, author, isbn, genre, year, qty);
        DataManager.save(library);
        refreshAll();
        clearBookFields();
        showInfo("Livro atualizado com sucesso.");
    }

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

    private void addStudent() {
        String name = view.getNameField().getText().trim();
        String ra = view.getRaField().getText().trim();
        String contact = view.getContactField().getText().trim();
        String password = view.getPasswordField().getText().trim();

        // Validação rígida no campo ID/RA para bloquear letras, negativos e quebras
        validateStrictPositiveIntegerString(ra, "ID/RA");

        library.addStudent(new Student(name, ra, contact, password));
        DataManager.save(library);
        refreshAll();
        clearStudentFields();
        showInfo("Usuário adicionado com sucesso.");
    }

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

        // Validação rígida no campo ID/RA também na edição do usuário
        validateStrictPositiveIntegerString(ra, "ID/RA");

        library.updateStudent(selected, name, ra, contact, password);
        DataManager.save(library);
        refreshAll();
        clearStudentFields();
        showInfo("Usuário atualizado com sucesso.");
    }

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
            showInfo("Multa updated com sucesso.");
        }
    }

    private void clearBookFields() {
        view.getTitleField().setText("");
        view.getAuthorField().setText("");
        view.getIsbnField().setText("");
        view.getGenreField().setText("");
        view.getYearField().setText("");
        view.getQtyField().setText("");
        view.getBookTable().clearSelection();
    }

    private void clearStudentFields() {
        view.getNameField().setText("");
        view.getRaField().setText("");
        view.getContactField().setText("");
        view.getPasswordField().setText("");
        view.getStudentTable().clearSelection();
    }

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

    // Método criado especificamente para blindar campos de texto que devem conter apenas números inteiros estritamente positivos (Ex: ISBN e RA)
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

    private void setupDynamicComboFilter(JTextField field, Runnable onFilterChange) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onFilterChange.run(); }
            public void removeUpdate(DocumentEvent e) { onFilterChange.run(); }
            public void changedUpdate(DocumentEvent e) { onFilterChange.run(); }
        });
    }

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

    private void requireSelected(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(view, message, "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(view, message, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private String money(double value) {
        return String.format("R$ %.2f", value);
    }

    private String formatDate(java.time.LocalDate date) {
        return date == null ? "-" : date.format(dateFormatter);
    }

    private interface Action {
        void run();
    }
}