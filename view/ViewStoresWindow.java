package view;

import core.Store;
import utils.CSVReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewStoresWindow {

    private static Map<String, Store> storeMap = new HashMap<>();
    private CustomerMenu customerMenu;
    private Runnable onGoBack;

    public ViewStoresWindow(Runnable onGoBack) {
        this.onGoBack = onGoBack;
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

        for (Store store : storeMap.values()) {
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

            storePanel.add(labelPanel, BorderLayout.NORTH);

            int productSize = store.getProduct().size();

            String[] columns = {"Product", "Amount Available", "Price"};
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
                onGoBack.run(); // Call the Runnable's run method
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
