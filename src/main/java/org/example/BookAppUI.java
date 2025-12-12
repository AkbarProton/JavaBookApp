package org.example;

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


class BookAppUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContent;

    private Client client;

    private JPanel booksGrid;
    private JTextField searchField;
    private JComboBox<String> sortOptions;

    private Map<String, ArrayList<Book>> userLists;
    private String currentSortCriteria = "Title (A-Z)";

    Color bgMain = new Color(245, 247, 250);
    Color sidebarColor = new Color(250, 251, 253);
    Color navColor = new Color(255, 255, 255);
    Color borderColor = new Color(220, 223, 230);
    Color accent = new Color(70, 140, 200);
    Color textPrimary = new Color(40, 40, 50);
    Color btnHover = new Color(230, 234, 245);
    Color btnAdd = new Color(60, 180, 100);
    Color btnAddHover = new Color(75, 200, 115);

    JButton backButton;

    public BookAppUI() {
        setTitle("Library Explorer");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        userLists = new HashMap<>();
        String[] menuItems = {"Completed", "Planned", "Favorites", "Reading", "On Hold"};
        for (String item : menuItems) {
            userLists.put(item, new ArrayList<>());
        }


        try {
            this.client = new Client(ConnectionTestingConstants.serverIP, ConnectionTestingConstants.port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Client initialization failed: " + e.getMessage());
        }


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

        JPanel leftMenu = new JPanel();
        leftMenu.setLayout(new BorderLayout());
        leftMenu.setPreferredSize(new Dimension(250, 0));
        leftMenu.setBackground(sidebarColor);
        leftMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, borderColor));

        JPanel menuButtons = new JPanel();
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));
        menuButtons.setBackground(sidebarColor);
        menuButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton explorerBtn = new JButton("Book Explorer");
        styleMenuButton(explorerBtn);
        explorerBtn.addActionListener(e -> showMainPage());
        menuButtons.add(explorerBtn);
        menuButtons.add(Box.createRigidArea(new Dimension(0, 8)));


        for (String s : menuItems) {
            JButton btn = new JButton(s);
            styleMenuButton(btn);
            btn.addActionListener(e -> openCategoryPage(s));
            menuButtons.add(Box.createRigidArea(new Dimension(0, 8)));
            menuButtons.add(btn);
        }

        JButton publishBtn = new JButton("Publish Book");
        styleMenuButton(publishBtn);
        publishBtn.addActionListener(e -> openPublishPage());
        menuButtons.add(Box.createRigidArea(new Dimension(0, 8)));
        menuButtons.add(publishBtn);

        menuButtons.add(Box.createVerticalGlue());

        leftMenu.add(menuButtons, BorderLayout.CENTER);

        add(leftMenu, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);

        mainContent.add(createMainCenterPanel(), "MAIN");
        mainContent.add(createProfilePanel(), "PROFILE");
        mainContent.add(createPublishBookPanel(), "PUBLISH");

        for (String s : menuItems) {
            mainContent.add(createCategoryPage(s), s);
        }

        add(mainContent, BorderLayout.CENTER);

        showMainPage();
    }

    private JPanel createMainCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBackground(bgMain);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topControls = new JPanel(new BorderLayout(10, 0));
        topControls.setBackground(bgMain);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(bgMain);
        searchField = new JTextField();
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

        String[] sortOptionsArray = {"Title (A-Z)", "Author (A-Z)", "Most Favorites", "Most Reads"};
        sortOptions = new JComboBox<>(sortOptionsArray);
        sortOptions.setPreferredSize(new Dimension(150, 32));
        sortOptions.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sortOptions.addActionListener(e -> {
            currentSortCriteria = (String) sortOptions.getSelectedItem();
            loadMainBooks();
        });

        JPanel sortWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        sortWrapper.setBackground(bgMain);
        sortWrapper.add(new JLabel("Sort by: "));
        sortWrapper.add(sortOptions);

        topControls.add(searchPanel, BorderLayout.CENTER);
        topControls.add(sortWrapper, BorderLayout.EAST);

        centerPanel.add(topControls, BorderLayout.NORTH);

        booksGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        booksGrid.setBackground(bgMain);
        booksGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        centerPanel.add(new JScrollPane(booksGrid), BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createProfilePanel() {
        JPanel profile = new JPanel();
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
        profile.setBackground(bgMain);
        profile.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 90));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel username = new JLabel("Your Profile");
        username.setFont(new Font("SansSerif", Font.BOLD, 28));
        username.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel stats = new JLabel("Books read: 24   |   Favorites: 6   |   Planned: 12");
        stats.setFont(new Font("SansSerif", Font.PLAIN, 16));
        stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        stats.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JTextField nameField = createProfileField("Username:", "Default User");
        JTextField emailField = createProfileField("Email:", "user@example.com");

        JPanel nameWrapper = createProfileFieldWrapper("Username:", nameField);
        JPanel emailWrapper = createProfileFieldWrapper("Email:", emailField);

        JButton promoteButton = new JButton("Promote to Author");
        promoteButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        promoteButton.setBackground(accent);
        promoteButton.setForeground(Color.WHITE);
        promoteButton.setFocusPainted(false);
        promoteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        promoteButton.setMaximumSize(new Dimension(300, 40));

        promoteButton.addActionListener(e -> {
            String usernameToPromote = nameField.getText();

            if (client != null) {
                try {
                    client.promoteToAuthor(usernameToPromote);
                    JOptionPane.showMessageDialog(this, "User '" + usernameToPromote + "' submitted for author promotion. Status: Success", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to send promotion request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Client not connected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton saveButton = new JButton("Save Profile Changes");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        saveButton.setBackground(btnAdd);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.setMaximumSize(new Dimension(300, 40));

        saveButton.addActionListener(e -> {
            String newUsername = nameField.getText();
            String newEmail = emailField.getText();

            if (client != null) {
                try {
                    client.updateUser(newUsername, newEmail);
                    JOptionPane.showMessageDialog(this, "Profile changes saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to update profile: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Client not connected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        logoutButton.setBackground(Color.DARK_GRAY);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(300, 40));
        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Log Out", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                dispose();
            }
        });


        profile.add(avatar);
        profile.add(Box.createRigidArea(new Dimension(0, 20)));
        profile.add(username);
        profile.add(stats);

        profile.add(Box.createRigidArea(new Dimension(0, 20)));
        profile.add(nameWrapper);
        profile.add(emailWrapper);
        profile.add(Box.createRigidArea(new Dimension(0, 30)));

        profile.add(saveButton);
        profile.add(Box.createRigidArea(new Dimension(0, 10)));
        profile.add(promoteButton);
        profile.add(Box.createRigidArea(new Dimension(0, 30)));
        profile.add(logoutButton);

        profile.add(Box.createVerticalGlue());

        return profile;
    }

    private JTextField createProfileField(String labelText, String initialValue) {
        JTextField field = new JTextField(initialValue);
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        return field;
    }

    private JPanel createProfileFieldWrapper(String labelText, JTextField field) {
        JPanel wrapper = new JPanel(new BorderLayout(10, 0));
        wrapper.setBackground(bgMain);
        wrapper.setMaximumSize(new Dimension(400, 30));
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 15));
        label.setPreferredSize(new Dimension(100, 30));

        wrapper.add(label, BorderLayout.WEST);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }


    private JPanel createPublishBookPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgMain);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Publish Your Book");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(textPrimary);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));

        JTextField titleField = addPublishFieldToPanel(panel, "Title:", "The Great Java Adventure");
        JTextField isbnField = addPublishFieldToPanel(panel, "ISBN:", "978-1234567890");
        JTextField authorField = addPublishFieldToPanel(panel, "Author(s) (comma-separated):", "Jane Doe, John Smith");
        JTextField yearField = addPublishFieldToPanel(panel, "Year:", "2024");

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton publishButton = new JButton("Submit Book for Review");
        publishButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        publishButton.setBackground(accent);
        publishButton.setForeground(Color.WHITE);
        publishButton.setFocusPainted(false);
        publishButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        publishButton.setMaximumSize(new Dimension(400, 50));

        publishButton.addActionListener(e -> {
            String bookTitle = titleField.getText();
            String isbn = isbnField.getText();
            String[] authorArray = authorField.getText().split("\\s*,\\s*");
            ArrayList<String> authors = new ArrayList<>(Arrays.asList(authorArray));
            String year = yearField.getText();

            if (bookTitle.trim().isEmpty() || isbn.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and ISBN are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Book newBook = new Book(bookTitle, isbn, authors, year, 0);

            if (client != null) {
                try {
                    client.publishBook(newBook);
                    JOptionPane.showMessageDialog(this, "'" + bookTitle + "' has been published and saved to the server!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    titleField.setText("");
                    isbnField.setText("");
                    authorField.setText("");
                    yearField.setText("");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to publish book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Client not connected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(publishButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JTextField addPublishFieldToPanel(JPanel parentPanel, String labelText, String initialValue) {

        JTextField field = new JTextField(initialValue);
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createLineBorder(borderColor));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setPreferredSize(new Dimension(180, 30));

        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(bgMain);
        wrapper.setMaximumSize(new Dimension(600, 50));
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        wrapper.add(label, BorderLayout.WEST);
        wrapper.add(field, BorderLayout.CENTER);

        parentPanel.add(wrapper);
        parentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        return field;
    }

    private JPanel createCategoryPage(String name) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgMain);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel(name);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(textPrimary);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(bgMain);
        topPanel.add(title, BorderLayout.WEST);

        JPanel categoryGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        categoryGrid.setBackground(bgMain);
        categoryGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(categoryGrid), BorderLayout.CENTER);

        return panel;
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

        if (books != null) {
            books.sort((b1, b2) -> {
                switch (currentSortCriteria) {
                    case "Author (A-Z)":
                        String author1 = b1.getAuthors().isEmpty() ? "" : b1.getAuthors().get(0);
                        String author2 = b2.getAuthors().isEmpty() ? "" : b2.getAuthors().get(0);
                        return author1.compareTo(author2);
                    case "Most Favorites":
                        return Integer.compare(b2.getNumberInFavorites(), b1.getNumberInFavorites());
                    case "Most Reads":
                        return Integer.compare(b2.getNumberInFavorites(), b1.getNumberInFavorites());
                    case "Title (A-Z)":
                    default:
                        return b1.getTitle().compareTo(b2.getTitle());
                }
            });
        }


        booksGrid.removeAll();

        if (books == null || books.isEmpty()) {
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


    private void loadCategoryBooks(String listName) {
        JPanel categoryPanel = (JPanel) findComponentByName(mainContent, listName);
        if (categoryPanel == null) return;

        JScrollPane scrollPane = (JScrollPane) categoryPanel.getComponent(1);
        JPanel categoryGrid = (JPanel) scrollPane.getViewport().getView();

        categoryGrid.removeAll();
        ArrayList<Book> books = userLists.get(listName);

        if (books.isEmpty()) {
            categoryGrid.add(new JLabel("No books in " + listName + " list.", SwingConstants.CENTER));
        } else {
            for (Book book : books) {
                categoryGrid.add(createListBookCard(book, listName));
            }
        }
        categoryGrid.revalidate();
        categoryGrid.repaint();
    }

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

    private JPanel createListBookCard(Book book, String listName) {
        JPanel card = createBookCard(book);

        card.remove(2);

        JButton removeButton = new JButton("Remove");
        removeButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        removeButton.setBackground(new Color(200, 70, 70));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        removeButton.addActionListener(e -> {
            userLists.get(listName).removeIf(b -> b.getIsbn().equals(book.getIsbn()));
            JOptionPane.showMessageDialog(this, "'" + book.getTitle() + "' removed from " + listName + ".", "Removed", JOptionPane.INFORMATION_MESSAGE);
            loadCategoryBooks(listName);
        });

        card.add(removeButton, BorderLayout.SOUTH);
        return card;
    }


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

        if (backButton.isVisible() && getSelectedCardName().equals(listName)) {
            loadCategoryBooks(listName);
        }
    }

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

    private String getSelectedCardName() {
        for (Component comp : mainContent.getComponents()) {
            if (comp.isVisible()) {
                return comp.getName();
            }
        }
        return "";
    }

    private int findCategoryIndex(String name) {
        String[] allCardNames = {"MAIN", "PROFILE", "PUBLISH", "Completed", "Planned", "Favorites", "Reading", "On Hold"};
        for (int i = 0; i < allCardNames.length; i++) {
            if (allCardNames[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

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


    private void showMainPage() {
        backButton.setVisible(false);
        cardLayout.show(mainContent, "MAIN");
        loadMainBooks();
    }

    private void openProfilePage() {
        backButton.setVisible(true);
        cardLayout.show(mainContent, "PROFILE");
    }

    private void openPublishPage() {
        backButton.setVisible(true);
        cardLayout.show(mainContent, "PUBLISH");
    }

    private void openCategoryPage(String name) {
        backButton.setVisible(true);
        Component categoryPanel = mainContent.getComponent(findCategoryIndex(name));
        categoryPanel.setName(name);

        cardLayout.show(mainContent, name);
        loadCategoryBooks(name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookAppUI().setVisible(true));
    }
}