package view;

import core.Customer;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * A view all the stores GUI
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 5
 *
 * @author Tingyu Yin
 * @version April 18, 2023
 */
public class ViewStoresWindow {

    private static Map<String, Store> storeMap = new HashMap<>();
    private CustomerMenu customerMenu;
    private Runnable onGoBack;
    private Customer customer;


    public ViewStoresWindow(Runnable onGoBack, Customer customer, CustomerMenu customerMenu) {
        this.onGoBack = onGoBack;
        this.customer = customer;
        this.customerMenu = customerMenu;
    }


    public void openCustomerSMGWindow(CustomerMenu customerMenu, Store store, Customer customer) {
        CustomerSMGWindow sendWindow = new CustomerSMGWindow(customerMenu, store, customer);
        try {
            sendWindow.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        CSVReader reader = new CSVReader();
        storeMap = reader.readStores();

        JFrame frame = new JFrame("All the stores");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(new Dimension(800, 600));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        Map<String, Store> visibleStoreMap = new HashMap<>();
        for (Store store : storeMap.values()) {

            // have to tell whether the seller has been invisible to the customer
            List<String> sellerInvisList = reader.getInvisList(store.getSeller());
            boolean invisible = sellerInvisList.contains(customer.getUsername());
            if (!invisible) {
                visibleStoreMap.put(store.getStoreName(), store);
            }

        }

        for (Store store : visibleStoreMap.values()) {
            String storeName = store.getStoreName();
            String storeSeller = store.getSeller().getUsername();

            JPanel storePanel = new JPanel(new BorderLayout());

            JPanel labelPanel = new JPanel();
            labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            labelPanel.add(new JLabel("Store Name: " + storeName));
            labelPanel.add(new JLabel(" | "));
            labelPanel.add(new JLabel("Seller: " + storeSeller));

            JButton sendMessageButton = new JButton("Send Message");
            labelPanel.add(sendMessageButton);
            sendMessageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    Seller storeSeller = store.getSeller();
                    List<String> sellerBlockList = new ArrayList<>();
                    try {
                        sellerBlockList = reader.getBlockList(storeSeller);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (sellerBlockList.contains(customer.getUsername())) {
                        JOptionPane.showMessageDialog(null, "You have been blocked!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        openCustomerSMGWindow(customerMenu, store, customer);
                    }
                }
            });

            storePanel.add(labelPanel, BorderLayout.NORTH);

            int productSize = store.getProduct().size();

            String[] columns = {"Product", "Amount Available", "Price($)"};
            Object[][] productData = updateProductData(store);

            // set the value of dateset then initialize model. order is important !
            DefaultTableModel model = new DefaultTableModel(productData, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            JTable productTable = new JTable(model);
            productTable.setRowHeight(25);
            JScrollPane scrollPane = new JScrollPane(productTable);
            storePanel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

            // Add a little vertical space at the beginning of the buttonPanel
            buttonPanel.add(Box.createVerticalStrut(12));
            for (int i = 0; i < productSize; i++) {
                JButton buyButton = new JButton("Buy");

                buyButton.setPreferredSize(new Dimension(80, 30));

                // Align the button to the right
                buyButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

                buttonPanel.add(buyButton);
                String product = store.getProduct().get(i);
                String format = String.format("You have bought: %s", product);
                int finalI = i; // need to be final so that can be used in ActionListener
                buyButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        List<Integer> currentAmountList = store.getAmountAvailable();
                        // get the amount of current store's ith product then minus one
                        int newAmount = currentAmountList.get(finalI) - 1;
                        if (newAmount < 0) {
                            JOptionPane.showMessageDialog(null, "Sorry! The product sold out!");
                        } else {
                            currentAmountList.set(finalI, newAmount);
                            store.setAmountAvailable(currentAmountList);
                            Object[][] newProductData = updateProductData(store);
                            // Update the existing table model
                            model.setDataVector(newProductData, columns);
                            model.fireTableDataChanged();
                            JOptionPane.showMessageDialog(null, format);
                        }

                    }
                });
            }

            storePanel.add(buttonPanel, BorderLayout.EAST);

            mainPanel.add(storePanel);
        }

        JScrollPane mainScroll = new JScrollPane(mainPanel);

        JButton goBackButton = new JButton("Go Back");
        frame.add(goBackButton, BorderLayout.SOUTH);
        goBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                onGoBack.run(); // Call the run method of Runnable
            }
        });

        frame.add(mainScroll);
        frame.setVisible(true);

    }

    public Object[][] updateProductData(Store store) {

        int productSize = store.getProduct().size();
        Object[][] productData = new Object[productSize][3];

        for (int i = 0; i < productSize; i++) {
            productData[i][0] = store.getProduct().get(i);
            productData[i][1] = store.getAmountAvailable().get(i);
            productData[i][2] = store.getPrice().get(i);
        }
        return productData;
    }

}
