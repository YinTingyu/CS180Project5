package view;

import core.Seller;
import core.Store;
import utils.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageStoresWindow {
    private static final String INVALID_AMOUNT = "Invalid amount input. Please enter a valid integer.";
    private static final String INVALID_PRICE = "Invalid price input. Please enter a valid decimal number.";
    private static final String DELETE_PRODUCT = "Are you sure to delete the selected product?";
    private static final String DELETE_STORE = "Are you sure to delete the selected store?";
    private static final String INVALID_INPUT = "Invalid amount or price input. Please enter a valid number.";
    private Runnable onGoBack;
    private Seller seller;

    private SellerMenu sellerMenu;
    private int selectedRow;
    private List<Store> sellerStores;


    public ManageStoresWindow(Runnable onGoBack, Seller seller, SellerMenu sellerMenu) {
        this.onGoBack = onGoBack;
        this.seller = seller;
        this.sellerMenu = sellerMenu;
    }


    public void run() throws IOException {

        CSVReader reader = new CSVReader();
        sellerStores = reader.getSellerStores(seller);

        JFrame frame = new JFrame("Manege My Stores");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(new Dimension(800, 600));

        // mainPanel to hold store panel
        final JPanel[] mainPanel = {new JPanel()};
        mainPanel[0].setLayout(new BoxLayout(mainPanel[0], BoxLayout.Y_AXIS));

        mainPanel[0] = createStorePanel(sellerStores);

        JPanel storeButtonPanel = new JPanel();
        JButton addStoreButton = new JButton("Add Store");
        JButton deleteStoreButton = new JButton("Delete Store");
        JButton goBackButton = new JButton("Go Back");

        storeButtonPanel.add(addStoreButton);
        storeButtonPanel.add(deleteStoreButton);
        storeButtonPanel.add(goBackButton);

        addStoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                JTextField storeNameField = new JTextField();
                JTextField nameField = new JTextField();
                JTextField amountField = new JTextField();
                JTextField priceField = new JTextField();
                Object[] message = {
                        "New Store: ", storeNameField,
                        "New Product:", nameField,
                        "Amount Available:", amountField,
                        "Price:", priceField
                };
                int option = JOptionPane.showConfirmDialog(null, message,
                        "Create a New Store", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String storeName = storeNameField.getText();
                    List<String> product = new ArrayList<>();
                    List<Integer> amount = new ArrayList<>();
                    List<Double> price = new ArrayList<>();
                    String newProduct = nameField.getText();
                    Integer newAmount = null;
                    Double newPrice = null;
                    try {
                        newAmount = Integer.parseInt(amountField.getText());
                        newPrice = Double.parseDouble(priceField.getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, INVALID_INPUT);
                        return;
                    }
                    product.add(newProduct);
                    amount.add(newAmount);
                    price.add(newPrice);
                    Store newStore = new Store(storeName, product, amount, price, seller);
                    sellerStores.add(newStore);
                    mainPanel[0] = createStorePanel(sellerStores);

                    frame.getContentPane().removeAll();

                    // Add the updated mainPanel and storeButtonPanel back to the frame
                    frame.add(storeButtonPanel, BorderLayout.SOUTH);
                    frame.add(mainPanel[0]);

                    // Refresh the frame
                    frame.revalidate();
                    frame.repaint();
                }

            }
        });

        deleteStoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String[] storeNames = new String[sellerStores.size()];
                for (int i = 0; i < sellerStores.size(); i++) {
                    storeNames[i] = sellerStores.get(i).getStoreName();
                }
                int option = JOptionPane.showOptionDialog(null, "Select a store to delete",
                        "Delete a Store", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, storeNames, storeNames[0]);
                if (option != -1) { // if seller do not close the window

                    int reConfirm = JOptionPane.showConfirmDialog(null, DELETE_STORE,
                            "Delete a Store", JOptionPane.YES_NO_OPTION);
                    if (reConfirm == JOptionPane.OK_OPTION) {
                        sellerStores.remove(option);
                        mainPanel[0] = createStorePanel(sellerStores);

                        frame.getContentPane().removeAll();

                        // Add the updated mainPanel and storeButtonPanel back to the frame
                        frame.add(storeButtonPanel, BorderLayout.SOUTH);
                        frame.add(mainPanel[0]);

                        // Refresh the frame
                        frame.revalidate();
                        frame.repaint();
                    }
                }
            }
        });

        goBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                onGoBack.run(); // Call the run method of Runnable
            }
        });

        frame.add(storeButtonPanel, BorderLayout.SOUTH);
        frame.add(mainPanel[0]);
        frame.setVisible(true);
    }



    public Object[][] updateProduct(Store store) {
        int productSize = store.getProduct().size();
        Object[][] productData = new Object[productSize][3];

        for (int i = 0; i < productSize; i++) {
            productData[i][0] = store.getProduct().get(i);
            productData[i][1] = store.getAmountAvailable().get(i);
            productData[i][2] = store.getPrice().get(i);
        }
        return productData;
    }

    public JPanel createStorePanel(List<Store> sellerStores) {

        // mainPanel to hold store panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.removeAll();

        if (sellerStores.isEmpty()) {
            JLabel emptyLabel = new JLabel("You have not owned stores");
            mainPanel.add(emptyLabel);
        } else {

            for (Store store : sellerStores) {
                // to hold store label and product table
                JPanel storePanel = new JPanel(new BorderLayout());

                JPanel storeLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                String storeName = store.getStoreName();
                storeLabelPanel.add(new JLabel(storeName));
                storePanel.add(storeLabelPanel, BorderLayout.NORTH); // Label at north

                List<String> products = store.getProduct();
                List<Integer> amounts = store.getAmountAvailable();
                List<Double> prices = store.getPrice();

                String[] columns = {"Product", "Amount Available", "Price($)"};
                Object[][] productData = updateProduct(store);


                final boolean[] isEditable = {false};
                DefaultTableModel tableModel = new DefaultTableModel(productData, columns) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return isEditable[0];
                    }
                };

                JTable table = new JTable(tableModel);
                table.setRowHeight(25);
                JScrollPane scrollPane = new JScrollPane(table);
                storePanel.add(scrollPane, BorderLayout.CENTER);


                JPanel productButtonPanel = new JPanel();
                productButtonPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

                JButton editButton = new JButton("Edit");

                JButton saveButton = new JButton("Save");

                JButton addProductButton = new JButton("Add Product");

                JButton deleteProductButton = new JButton("Delete Product");

                editButton.setVisible(true);
                saveButton.setVisible(false);
                productButtonPanel.add(editButton);
                productButtonPanel.add(saveButton);
                productButtonPanel.add(addProductButton);
                productButtonPanel.add(deleteProductButton);

                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        isEditable[0] = true;
                        editButton.setVisible(false);
                        saveButton.setVisible(true);
                    }
                });

                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        isEditable[0] = false;
                        saveButton.setVisible(false);
                        editButton.setVisible(true);
                        selectedRow = table.getSelectedRow();
                        int selectedCol = table.getSelectedColumn();

                        if (selectedRow != -1) {
                            Object getNewValue = tableModel.getValueAt(selectedRow, selectedCol);
                            if (selectedCol == 0) {
                                products.set(selectedRow, (String) getNewValue);
                            } else if (selectedCol == 1) {

                                try {
                                    amounts.set(selectedRow, (Integer) getNewValue);
                                } catch (NumberFormatException e) {
                                    // Handle invalid integer input
                                    JOptionPane.showMessageDialog(null, INVALID_AMOUNT);
                                }

                            } else if (selectedCol == 2) {

                                try {
                                    prices.set(selectedRow, (Double) getNewValue);
                                } catch (NumberFormatException e) {
                                    // Handle invalid double input
                                    JOptionPane.showMessageDialog(null, INVALID_PRICE);
                                }

                            }
                        }

                        // write csv file to be implemented

                    }
                });

                addProductButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        JTextField nameField = new JTextField();
                        JTextField amountField = new JTextField();
                        JTextField priceField = new JTextField();
                        Object[] message = {
                                "New Product:", nameField,
                                "Amount Available:", amountField,
                                "Price:", priceField
                        };
                        int option = JOptionPane.showConfirmDialog(null, message, "Add a new product", JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.OK_OPTION) {
                            String newNameStr = nameField.getText();
                            Integer newAmount = null;
                            Double newPrice = null;
                            try {
                                newAmount = Integer.parseInt(amountField.getText());
                                newPrice = Double.parseDouble(priceField.getText());
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null, INVALID_INPUT);
                                return;
                            }
                            store.addProduct(newNameStr, newAmount, newPrice);
                            Object[] newProductData = {newNameStr, newAmount, newPrice};
                            tableModel.addRow(newProductData);
                            tableModel.fireTableDataChanged();
                        }

                    }
                });

                deleteProductButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        int ensure = JOptionPane.showConfirmDialog(null, DELETE_PRODUCT,
                                "Delete Product", JOptionPane.YES_NO_OPTION);
                        if (ensure == JOptionPane.YES_OPTION) {
                            selectedRow = table.getSelectedRow();
                            if (selectedRow != -1) {
                                tableModel.removeRow(selectedRow);
                                store.deleteProduct(selectedRow);
                            } else {
                                JOptionPane.showMessageDialog(null, "No product is selected!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }

                    }
                });
                storePanel.add(productButtonPanel, BorderLayout.EAST);

                mainPanel.add(storePanel);
            }
        }

        mainPanel.revalidate();
        mainPanel.repaint();

        return mainPanel;
    }
}
