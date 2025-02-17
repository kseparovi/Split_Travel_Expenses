package View;

import Controller.LeftPanelController;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LeftPanel extends JPanel implements LeftPanelListener {
    private JTextField nameField;
    private JComboBox<String> categoryField;
    private JComboBox<String> paidByField;
    private JSpinner dateField;
    private JTextField amountField;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addUserBtn;
    private JButton sendDataBtn;
    private Border border;
    private Dimension dim = getPreferredSize();
    private List<String> users;
    private RightPanelListener rightPanelListener;
    private LeftPanelController controller;

    public LeftPanel(RightPanelListener rightPanelListener) {
        this.rightPanelListener = rightPanelListener;
        users = new ArrayList<>();
        dim.width = 350;
        dim.height = 450;
        setPreferredSize(dim);
        decoratePanel();
        initComps();
        layoutComps();
        activatePanel();
    }

    @Override
    public void leftPanelEventOccurred(LeftPanelEvent event) {
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add users before processing any calculations.", "No Users", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = event.getName();
        String category = event.getCategory();
        String paidBy = event.getPaidBy();
        Date date = event.getDate();
        double amount = event.getAmount();
        List<String> eventUsers = event.getUsers();

        rightPanelListener.rightPanelEventOccurred(new RightPanelEvent(this, paidBy, amount));
    }

    private void activatePanel() {
        setBackground(Color.LIGHT_GRAY);

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c)) {
                    e.consume();
                }
            }
        });

        addUserBtn.addActionListener(e -> {
            String userName = JOptionPane.showInputDialog("Enter user name:");
            if (userName != null && !userName.trim().isEmpty()) {
                if (users.contains(userName)) {
                    JOptionPane.showMessageDialog(this, "User already exists.", "Duplicate User", JOptionPane.WARNING_MESSAGE);
                } else {
                    tableModel.addRow(new Object[]{userName});
                    users.add(userName);
                    updatePaidByField();
                }
            }
        });
    }

    private void layoutComps() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weighty = 0.1;

        JLabel titleLabel = new JLabel("Title of expense ");
        add(titleLabel, gbc);

        gbc.gridy++;
        add(nameField, gbc);

        gbc.gridy++;
        JLabel categoryLabel = new JLabel("Category: ");
        add(categoryLabel, gbc);

        gbc.gridy++;
        add(categoryField, gbc);

        gbc.gridy++;
        JLabel paidByLabel = new JLabel("Paid by: ");
        add(paidByLabel, gbc);

        gbc.gridy++;
        add(paidByField, gbc);

        gbc.gridy++;
        JLabel dateLabel = new JLabel("Date ");
        add(dateLabel, gbc);

        gbc.gridy++;
        add(dateField, gbc);

        gbc.gridy++;
        JLabel amountLabel = new JLabel("Amount ");
        add(amountLabel, gbc);

        gbc.gridy++;
        add(amountField, gbc);

        gbc.gridy++;
        JLabel splitBetweenLabel = new JLabel("Split between user:  ");
        add(splitBetweenLabel, gbc);

        gbc.gridy++;
        add(new JScrollPane(userTable), gbc);

        gbc.gridy++;
        add(addUserBtn, gbc);

        gbc.weighty = 0.25;
        gbc.gridy++;
        add(sendDataBtn, gbc);
    }

    private void initComps() {
        nameField = new JTextField(20);
        categoryField = new JComboBox<>(new String[]{"Karte", "Smjestaj", "Hrana", "Pice", "Ostalo"});
        categoryField.setPreferredSize(new Dimension(200, 25));
        paidByField = new JComboBox<>();
        paidByField.setPreferredSize(new Dimension(200, 25));
        dateField = new JSpinner(new SpinnerDateModel());
        dateField.setEditor(new JSpinner.DateEditor(dateField, "dd/MM/yyyy"));
        dateField.setPreferredSize(new Dimension(200, 25));

        amountField = new JTextField(20);

        tableModel = new DefaultTableModel(new Object[]{"User"}, 0);
        userTable = new JTable(tableModel);
        userTable.setPreferredScrollableViewportSize(new Dimension(200, 100));

        addUserBtn = new JButton("Add User");
        sendDataBtn = new JButton("Send data");
    }

    private void decoratePanel() {
        border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(border);
        Font font = new Font("Verdana", Font.BOLD, 12);
        setFont(font);
    }

    private void updatePaidByField() {
        paidByField.removeAllItems();
        for (String user : users) {
            paidByField.addItem(user);
        }
    }

    public void setController(LeftPanelController controller) {
        this.controller = controller;
        for (ActionListener al : sendDataBtn.getActionListeners()) {
            sendDataBtn.removeActionListener(al);
        }
        sendDataBtn.addActionListener(e -> controller.processLeftPanelEvent());
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JComboBox<String> getCategoryField() {
        return categoryField;
    }

    public JComboBox<String> getPaidByField() {
        return paidByField;
    }

    public JSpinner getDateField() {
        return dateField;
    }

    public JTextField getAmountField() {
        return amountField;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    // Add methods to get debt users and debt amounts
    public List<String> getDebtUsers() {
        List<String> debtUsers = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            debtUsers.add((String) tableModel.getValueAt(i, 0));
        }
        return debtUsers;
    }

    public List<Double> getDebtAmounts() {
        List<Double> debtAmounts = new ArrayList<>();
        double amount = Double.parseDouble(amountField.getText());
        double splitAmount = amount / tableModel.getRowCount();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            debtAmounts.add(splitAmount);
        }
        return debtAmounts;
    }

    public void clearUsers() {
        users.clear();
        tableModel.setRowCount(0);
        paidByField.removeAllItems();
    }

    public void addUser(String user) {
        if (!users.contains(user)) {
            users.add(user);
            tableModel.addRow(new Object[]{user});
            paidByField.addItem(user);
        }
    }

}