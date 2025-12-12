import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import Networking.client.Client;
import Networking.ConnectionTestingConstants;
import models.items.Book;


public class ShikiStyleUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContent;

    // Networking Client
    private Client client;

    // Components to update
    private JPanel booksGrid;
    private JTextField searchField;

    // Map to hold dynamic book lists (e.g., "Planned" -> [Book1, Book2])
    private Map<String, ArrayList<Book>> userLists;


    // Colors Shikimori-like
    Color bgMain = new Color(245, 247, 250);
    Color sidebarColor = new Color(250, 251, 253);
    Color navColor = new Color(255, 255, 255);
    Color borderColor = new Color(220, 223, 230);
    Color accent = new Color(70, 140, 200);
    Color textPrimary = new Color(40, 40, 50);
    Color btnHover = new Color(230, 234, 245);
    Color btnAdd = new Color(60, 180, 100);
    Color btnAddHover = new Color(75, 200, 115);

    // Back button
    JButton backButton;

    public ShikiStyleUI() {
        setTitle("Library Explorer");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize user lists
        userLists = new HashMap<>();
        String[] menuItems = {"Completed", "Planned", "Favorites", "Reading", "On Hold"};
        for (String item : menuItems) {
            userLists.put(item, new ArrayList<>());
        }
        // Example: Add a dummy book to Planned list for testing
        // userLists.get("Planned").add(new Book("Test Planned Book", "999", new ArrayList<>(Arrays.asList("Author")), "2025", 0));


        // CLIENT INITIALIZATION
        try {
            this.client = new Client(ConnectionTestingConstants.serverIP, ConnectionTestingConstants.port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Client initialization failed.");
        }


        // ===== TOP NAVIGATION BAR (UNMODIFIED) =====
        JPanel topNav = new JPanel(new BorderLayout());
        topNav.setPreferredSize(new Dimension(0, 60));
        topNav.setBackground(navColor);
        topNav.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

        JLabel logo = new JLabel("Library Explorer");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        logo.setForeground(textPrimary);

        JButton userIcon = new JButton("👤");
        userIcon.setFocusPainted(false);
        userIcon.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        userIcon.setBackground(navColor);
        userIcon.setFont(new Font("SansSerif", Font.PLAIN, 22));
        userIcon.addActionListener(e -> openProfilePage());

        backButton = new JButton("← Back");
        backButton.setVisible(false);
        backButton.setFocusPainted(false);
        backButton.setBackground(navColor);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.addActionListener(e -> showMainPage());

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightTop.setBackground(navColor);
        rightTop.add(backButton);
        rightTop.add(userIcon);

        topNav.add(logo, BorderLayout.WEST);
        topNav.add(rightTop, BorderLayout.EAST);
        add(topNav, BorderLayout.NORTH);

        // ===== LEFT MENU (NEWS PANEL DELETED) =====
        JPanel leftMenu = new JPanel();
        leftMenu.setLayout(new BorderLayout());
        leftMenu.setPreferredSize(new Dimension(250, 0));
        leftMenu.setBackground(sidebarColor);
        leftMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, borderColor));

        // Menu buttons
        JPanel menuButtons = new JPanel();
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));
        menuButtons.setBackground(sidebarColor);
        menuButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // ITEMS MODIFIED: Dropped is removed
        String[] items = {"Completed", "Planned", "Favorites", "Reading", "On Hold"};

        for (String s : items) {
            JButton btn = new JButton(s);
            styleMenuButton(btn);
            btn.addActionListener(e -> openCategoryPage(s));
            menuButtons.add(Box.createRigidArea(new Dimension(0, 8)));
            menuButtons.add(btn);
        }

        // Add glue to push buttons to the top, filling the remaining space
        menuButtons.add(Box.createVerticalGlue());

        leftMenu.add(menuButtons, BorderLayout.NORTH); // Use NORTH to allow glue to work

        // NEWS PANEL DELETED

        add(leftMenu, BorderLayout.WEST);

        // ===== MAIN CONTENT (CardLayout) =====
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);

        mainContent.add(createMainCenterPanel(), "MAIN");

        for (String s : items) {
            // Category page creation remains but uses new dynamic loading
            mainContent.add(createCategoryPage(s), s);
        }

        add(mainContent, BorderLayout.CENTER);

        showMainPage();
    }

    // ... (rest of the helper methods: styleMenuButton, etc.) ...
    private JPanel createMainCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBackground(bgMain);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Search bar + button
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(bgMain);

        searchField = new JTextField(); // Assigned to class field
        searchField.setPreferredSize(new Dimension(0, 32));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        searchField.setToolTipText("Search books by ISBN...");

        JButton searchButton = new JButton("🔍");
        searchButton.setPreferredSize(new Dimension(50, 32));
        searchButton.setFocusPainted(false);
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchButton.setBackground(navColor);
        searchButton.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        searchButton.addActionListener(e -> searchBookAction(searchField.getText()));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Initialize booksGrid as a class field
        booksGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        booksGrid.setBackground(bgMain);
        booksGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Load books on startup
        loadMainBooks();

        centerPanel.add(new JScrollPane(booksGrid), BorderLayout.CENTER);

        return centerPanel;
    }

    private void loadMainBooks() {
        if (client == null) {
            booksGrid.removeAll();
            booksGrid.add(new JLabel("Server connection failed. Cannot load books.", SwingConstants.CENTER));
            booksGrid.revalidate();
            booksGrid.repaint();
            return;
        }

        ArrayList<Book> books = client.getAllBooks();

        booksGrid.removeAll();

        if (books.isEmpty()) {
            booksGrid.add(new JLabel("No books found in the library.", SwingConstants.CENTER));
        } else {
            for (Book book : books) {
                booksGrid.add(createBookCard(book));
            }
        }

        booksGrid.revalidate();
        booksGrid.repaint();
    }

    private void searchBookAction(String query) {
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Client not connected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (query.trim().isEmpty()) {
            loadMainBooks();
            return;
        }

        try {
            Book book = client.getBookByIsbn(query.trim());

            booksGrid.removeAll();

            if (book != null) {
                booksGrid.add(createBookCard(book));
            } else {
                booksGrid.add(new JLabel("No book found with ISBN: " + query, SwingConstants.CENTER));
            }

            booksGrid.revalidate();
            booksGrid.repaint();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Network error during search: " + e.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to display a Book Card, now includes list addition option
    private JPanel createBookCard(Book book) {
        JPanel bookCard = new JPanel(new BorderLayout());
        bookCard.setBackground(Color.WHITE);
        bookCard.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        bookCard.setPreferredSize(new Dimension(180, 100));

        JLabel title = new JLabel(book.getTitle(), SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel details = new JLabel("<html><center>"
                + book.getIsbn() + "<br>"
                + String.join(", ", book.getAuthors()) + "</center></html>", SwingConstants.CENTER);
        details.setFont(new Font("SansSerif", Font.PLAIN, 12));
        details.setForeground(Color.GRAY);
        details.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Add button to add to a list
        JButton addButton = new JButton("+ Add to List");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        addButton.setBackground(btnAdd);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        addButton.addActionListener(e -> showAddToListDialog(book));

        bookCard.add(title, BorderLayout.NORTH);
        bookCard.add(details, BorderLayout.CENTER);
        bookCard.add(addButton, BorderLayout.SOUTH);

        return bookCard;
    }


    // ===== CATEGORY PAGE: MODIFIED TO DISPLAY DYNAMIC LISTS AND ADD BUTTON =====
    private JPanel createCategoryPage(String name) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgMain);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel(name);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(textPrimary);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Top section for title and potential buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(bgMain);
        topPanel.add(title, BorderLayout.WEST);

        // Grid for books in this category list
        JPanel categoryGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        categoryGrid.setBackground(bgMain);
        categoryGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // The panel will be populated when the category is opened via loadCategoryBooks()

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(categoryGrid), BorderLayout.CENTER);

        // Store the grid in a map or use findComponent to update it later
        // For simplicity now, we'll rely on the loadCategoryBooks helper when activated.

        return panel;
    }

    // ===== LIST MANAGEMENT LOGIC =====

    private void showAddToListDialog(Book book) {
        String[] options = {"Completed", "Planned", "Favorites", "Reading", "On Hold"};
        String listName = (String) JOptionPane.showInputDialog(
                this,
                "Add '" + book.getTitle() + "' to which list?",
                "Add to List",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                "Planned"
        );

        if (listName != null && !listName.trim().isEmpty()) {
            addBookToList(book, listName);
        }
    }

    private void addBookToList(Book book, String listName) {
        if (userLists.get(listName).stream().anyMatch(b -> b.getIsbn().equals(book.getIsbn()))) {
            JOptionPane.showMessageDialog(this, "'" + book.getTitle() + "' is already in " + listName + "!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        userLists.get(listName).add(book);
        JOptionPane.showMessageDialog(this, "'" + book.getTitle() + "' added to " + listName + ".", "Success", JOptionPane.INFORMATION_MESSAGE);

        // If the user is currently viewing this list, refresh the view
        if (cardLayout.toString().contains(listName)) {
            // A more complex check is needed, but this is the goal:
            // loadCategoryBooks(listName);
        }
    }

    private void loadCategoryBooks(String listName) {
        JPanel categoryPanel = (JPanel) findComponentByName(mainContent, listName);
        if (categoryPanel == null) return;

        // Find the JScrollPane and then the JPanel inside it (categoryGrid)
        JScrollPane scrollPane = (JScrollPane) categoryPanel.getComponent(1);
        JPanel categoryGrid = (JPanel) scrollPane.getViewport().getView();

        categoryGrid.removeAll();
        ArrayList<Book> books = userLists.get(listName);

        if (books.isEmpty()) {
            categoryGrid.add(new JLabel("No books in " + listName + " list.", SwingConstants.CENTER));
        } else {
            for (Book book : books) {
                // Use a slightly different card for the list view, potentially with a 'remove' button
                categoryGrid.add(createListBookCard(book, listName));
            }
        }
        categoryGrid.revalidate();
        categoryGrid.repaint();
    }

    private JPanel createListBookCard(Book book, String listName) {
        // Simple card for books in the managed lists
        JPanel card = createBookCard(book);
        // Find and replace the "+ Add to List" button with a "Remove" button
        JButton currentButton = (JButton) card.getComponent(2);

        JButton removeButton = new JButton("Remove");
        removeButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        removeButton.setBackground(new Color(200, 70, 70)); // Red for delete
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        removeButton.addActionListener(e -> {
            userLists.get(listName).removeIf(b -> b.getIsbn().equals(book.getIsbn()));
            JOptionPane.showMessageDialog(this, "'" + book.getTitle() + "' removed from " + listName + ".", "Removed", JOptionPane.INFORMATION_MESSAGE);
            loadCategoryBooks(listName); // Refresh the list view
        });

        card.remove(currentButton);
        card.add(removeButton, BorderLayout.SOUTH);
        return card;
    }

    // Simple recursive component search helper (necessary because of the CardLayout structure)
    private Component findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            }
            if (component instanceof Container) {
                Component found = findComponentByName((Container) component, name);
                if (found != null) return found;
            }
        }
        return null;
    }

    // ===== Actions =====
    private void showMainPage() {
        backButton.setVisible(false);
        cardLayout.show(mainContent, "MAIN");
        loadMainBooks(); // Reload books when returning to main page
    }

    private void openProfilePage() {
        backButton.setVisible(true);
        cardLayout.show(mainContent, "PROFILE");
    }

    private void openCategoryPage(String name) {
        backButton.setVisible(true);
        // Find the panel we want to show, and set its name to the category name for the helper function to find
        Component categoryPanel = mainContent.getComponent(findCategoryIndex(name));
        categoryPanel.setName(name);

        cardLayout.show(mainContent, name);
        loadCategoryBooks(name); // Load dynamic data when the page is opened
    }

    private int findCategoryIndex(String name) {
        // This is a crude way to find the index; a better way is to iterate over component names.
        // For simplicity based on your current setup:
        // 0: MAIN, 1: PROFILE, 2: Completed, 3: Planned, 4: Favorites, 5: Reading, 6: On Hold
        String[] allCardNames = {"MAIN", "PROFILE", "Completed", "Planned", "Favorites", "Reading", "On Hold"};
        for (int i = 0; i < allCardNames.length; i++) {
            if (allCardNames[i].equals(name)) {
                // Note: The actual component index may vary if you change the order of adding cards.
                return i;
            }
        }
        return -1;
    }

    // ... (styleMenuButton and main methods are the same) ...
    private void styleMenuButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBackground(sidebarColor);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setForeground(textPrimary);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(btnHover);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(sidebarColor);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShikiStyleUI().setVisible(true));
    }
}