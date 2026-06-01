import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

// Interface gráfica Swing estruturada em quatro abas separadas por responsabilidades específicas
public class LibraryUI extends JFrame {
    private final String userRole;
    private final JTabbedPane tabbedPane = new JTabbedPane();

    // Painéis de formulários transformados em atributos de classe para permitir controle de visibilidade externo
    private final JPanel bookForm = new JPanel(new GridBagLayout());
    private final JPanel studentForm = new JPanel(new GridBagLayout());

    // Componentes de Livros
    private final JTextField bookSearchField = new JTextField(22);
    private final JTextField titleField = new JTextField(14);
    private final JTextField authorField = new JTextField(14);
    private final JTextField isbnField = new JTextField(10);
    private final JTextField genreField = new JTextField(10);
    private final JTextField yearField = new JTextField(5);
    private final JTextField qtyField = new JTextField(5);

    private final JButton addBookBtn = new JButton("Adicionar");
    private final JButton editBookBtn = new JButton("Editar");
    private final JButton deleteBookBtn = new JButton("Excluir");
    private final JButton clearBookBtn = new JButton("Limpar campos");

    private final DefaultTableModel bookModel = createModel(new String[]{"Título", "Autor", "ISBN", "Gênero", "Ano", "Cópias"});
    private final JTable bookTable = new JTable(bookModel);
    private final TableRowSorter<DefaultTableModel> bookSorter = new TableRowSorter<>(bookModel);

    // Componentes de Usuários
    private final JTextField patronSearchField = new JTextField(22);
    private final JTextField nameField = new JTextField(14);
    private final JTextField raField = new JTextField(14);
    private final JTextField contactField = new JTextField(14);
    private final JPasswordField passwordField = new JPasswordField(14);

    private final JButton addStudentBtn = new JButton("Adicionar");
    private final JButton editStudentBtn = new JButton("Editar");
    private final JButton deleteStudentBtn = new JButton("Excluir");
    private final JButton clearStudentBtn = new JButton("Limpar campos");

    private final DefaultTableModel studentModel = createModel(new String[]{"Nome", "ID/RA", "Contato", "Senha"});
    private final JTable studentTable = new JTable(studentModel);
    private final TableRowSorter<DefaultTableModel> studentSorter = new TableRowSorter<>(studentModel);

    // Componentes de Empréstimos
    private final JTextField loanSearchField = new JTextField(22);
    
    // AJUSTE DE UX (FOTO 3): Novos campos de texto criados para filtrar os ComboBoxes instantaneamente por digitação
    private final JTextField bookFilterField = new JTextField(14);
    private final JTextField studentFilterField = new JTextField(14);
    
    private final JComboBox<Book> loanBookCombo = new JComboBox<>();
    private final JComboBox<Student> loanStudentCombo = new JComboBox<>();
    private final JPanel newLoanFieldsPanel = new JPanel(new GridBagLayout()); 

    private final JButton checkoutBtn = new JButton("Emprestar");
    private final JButton returnBtn = new JButton("Devolver");
    private final JButton renewBtn = new JButton("Renovar Empréstimo"); 

    private final DefaultTableModel loanModel = createModel(new String[]{"Livro", "Estudante", "Empréstimo", "Vencimento", "Status"});
    private final JTable loanTable = new JTable(loanModel);
    private final TableRowSorter<DefaultTableModel> loanSorter = new TableRowSorter<>(loanModel);

    // Componentes de Relatórios
    private final JTextField reportSearchField = new JTextField(22);
    private final JTextField reportPatronField = new JTextField(10);
    private final JPanel reportPatronPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); 

    private final JButton showActiveLoansBtn = new JButton("Ativos");
    private final JButton showOverdueLoansBtn = new JButton("Atrasados");
    private final JButton showAllHistoryBtn = new JButton("Histórico Geral");
    private final JButton showPatronHistoryBtn = new JButton("Histórico do Usuário");
    private final JButton resetFineBtn = new JButton("Resetar Multa");

    private final DefaultTableModel reportModel = createModel(new String[]{"Livro", "Estudante", "ID/RA", "Empréstimo", "Vencimento", "Devolução", "Multa", "Status"});
    private final JTable reportTable = new JTable(reportModel);
    private final TableRowSorter<DefaultTableModel> reportSorter = new TableRowSorter<>(reportModel);

    private final JButton logoutBtn = new JButton("Encerrar Sessão");

    public LibraryUI(String userRole) {
        this.userRole = userRole;
        setTitle("Sistema de Gestão de Biblioteca - Perfil: " + userRole.toUpperCase());
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        setupBooksTab();
        setupStudentsTab();
        setupLoansTab();
        setupReportsTab();

        JPanel topBar = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Conectado como: " + userRole);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        topBar.setBorder(new EmptyBorder(0, 0, 10, 0));

        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

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

    private DefaultTableModel createModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
    }

    private void setupBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        bookForm.setLayout(new GridBagLayout());
        bookForm.setBorder(BorderFactory.createTitledBorder("Cadastro / Edição de Livros"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

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

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.add(addBookBtn); btnPanel.add(editBookBtn);
        btnPanel.add(deleteBookBtn); btnPanel.add(clearBookBtn);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        bookForm.add(btnPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        bookForm.add(new JPanel(), gbc);

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

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.add(addStudentBtn); btnPanel.add(editStudentBtn);
        btnPanel.add(deleteStudentBtn); btnPanel.add(clearStudentBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        studentForm.add(btnPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        studentForm.add(new JPanel(), gbc);

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

        GridBagConstraints gbcSub = new GridBagConstraints();
        gbcSub.insets = new Insets(5, 5, 5, 5);
        gbcSub.fill = GridBagConstraints.HORIZONTAL;
        
        // AJUSTE DE UX (FOTO 3): Inclusão organizada dos campos de busca dinâmica dentro do painel
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

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btnPanel.add(checkoutBtn);
        btnPanel.add(returnBtn);
        btnPanel.add(renewBtn); 

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        loanLeftPanel.add(btnPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weighty = 1.0;
        loanLeftPanel.add(new JPanel(), gbc);

        loanTable.setRowSorter(loanSorter);
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.add(new JScrollPane(loanTable), BorderLayout.CENTER);

        panel.add(loanLeftPanel, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);
        tabbedPane.addTab("Empréstimos", panel);
    }

    private void setupReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        topBar.add(new JLabel("Filtrar texto:"));
        topBar.add(reportSearchField);

        reportPatronPanel.add(new JLabel("ID/RA do Aluno:"));
        reportPatronPanel.add(reportPatronField);
        topBar.add(reportPatronPanel);

        topBar.add(showActiveLoansBtn);
        topBar.add(showOverdueLoansBtn);
        topBar.add(showAllHistoryBtn);
        topBar.add(showPatronHistoryBtn);
        topBar.add(resetFineBtn);

        reportTable.setRowSorter(reportSorter);
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        tabbedPane.addTab("Relatórios", panel);
    }

    public JPanel getBookForm() { return bookForm; }
    public JPanel getStudentForm() { return studentForm; }
    public JTabbedPane getTabbedPane() { return tabbedPane; }
    public JButton getLogoutBtn() { return logoutBtn; }

    // GETTERS DOS NOVOS COMPONENTES DE FILTRO DE UX (FOTO 3)
    public JTextField getBookFilterField() { return bookFilterField; }
    public JTextField getStudentFilterField() { return studentFilterField; }

    // GETTERS LIVROS
    public JTextField getBookSearchField() { return bookSearchField; }
    public JTextField getTitleField() { return titleField; }
    public JTextField getAuthorField() { return authorField; }
    public JTextField getIsbnField() { return isbnField; }
    public JTextField getGenreField() { return genreField; }
    public JTextField getYearField() { return yearField; }
    public JTextField getQtyField() { return qtyField; }
    public JButton getAddBookBtn() { return addBookBtn; }
    public JButton getEditBookBtn() { return editBookBtn; }
    public JButton getDeleteBookBtn() { return deleteBookBtn; }
    public JButton getClearBookBtn() { return clearBookBtn; }
    public JTable getBookTable() { return bookTable; }
    public DefaultTableModel getBookModel() { return bookModel; }
    public TableRowSorter<DefaultTableModel> getBookSorter() { return bookSorter; }

    // GETTERS USUARIOS
    public JTextField getPatronSearchField() { return patronSearchField; }
    public JTextField getNameField() { return nameField; }
    public JTextField getRaField() { return raField; }
    public JTextField getContactField() { return contactField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getAddStudentBtn() { return addStudentBtn; }
    public JButton getEditStudentBtn() { return editStudentBtn; }
    public JButton getDeleteStudentBtn() { return deleteStudentBtn; }
    public JButton getClearStudentBtn() { return clearStudentBtn; }
    public JTable getStudentTable() { return studentTable; }
    public DefaultTableModel getStudentModel() { return studentModel; }
    public TableRowSorter<DefaultTableModel> getStudentSorter() { return studentSorter; }

    // GETTERS EMPRESTIMOS
    public JTextField getLoanSearchField() { return loanSearchField; }
    public JComboBox<Book> getLoanBookCombo() { return loanBookCombo; }
    public JComboBox<Student> getLoanStudentCombo() { return loanStudentCombo; }
    public JPanel getNewLoanFieldsPanel() { return newLoanFieldsPanel; }
    public JButton getCheckoutBtn() { return checkoutBtn; }
    public JButton getReturnBtn() { return returnBtn; }
    public JButton getRenewBtn() { return renewBtn; } 
    public JTable getLoanTable() { return loanTable; }
    public DefaultTableModel getLoanModel() { return loanModel; }
    public TableRowSorter<DefaultTableModel> getLoanSorter() { return loanSorter; }

    // GETTERS RELATORIOS
    public JTextField getReportSearchField() { return reportSearchField; }
    public JTextField getReportPatronField() { return reportPatronField; }
    public JPanel getReportPatronPanel() { return reportPatronPanel; } 
    public JButton getShowActiveLoansBtn() { return showActiveLoansBtn; }
    public JButton getShowOverdueLoansBtn() { return showOverdueLoansBtn; }
    public JButton getShowAllHistoryBtn() { return showAllHistoryBtn; }
    public JButton getShowPatronHistoryBtn() { return showPatronHistoryBtn; }
    public JButton getResetFineBtn() { return resetFineBtn; }
    public JTable getReportTable() { return reportTable; }
    public DefaultTableModel getReportModel() { return reportModel; }
    public TableRowSorter<DefaultTableModel> getReportSorter() { return reportSorter; }
}