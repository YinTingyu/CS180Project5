package view;

import core.Customer;
import core.Seller;
import utils.CSVReader;
import utils.CSVWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * A customer menu
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 5
 *
 * @author Tingyu Yin
 * @version April 15, 2023
 */
public class CustomerMenu extends Menu {

    private static final String VIEW_ALL_STORES = "View all the stores";
    private static final String EXPORT_FILE = "Export File";

    public String message;
    private String filename = "./src/customers.csv";
    private CSVReader csvReader;
    public Customer customer;
    public List<String> blockList;
    public List<String> invisList;
    public List<String> sellerList = new ArrayList<>();
    private JPanel invisiblePanel = new JPanel();
    private JPanel blockPanel = new JPanel();



    public void showCustomerMenu(Customer customer) throws IOException { // this is used when login and when go back
        run(customer);
    }

    public CustomerMenu(Customer customer) { // used when login gui instance the CustomerMenu
        this.customer = customer;
        this.csvReader = new CSVReader();


    }

    public void openViewStoresWindow(Customer customer) throws IOException { // to open view all the stores GUI
        ViewStoresWindow storesWindow = new ViewStoresWindow(() -> { // use lambda expressions to create anonymous methods
            try {
                showCustomerMenu(customer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, this.customer, this); // the lambda expression is passed as the first argument,
        // and the Customer object is passed as the second argument
        storesWindow.run();
    }

    public void run(Customer customer) throws IOException {

        CSVWriter writer = new CSVWriter(customer);

        Map<String, Seller> sellerMap = csvReader.readSellers();

        for (String seller : sellerMap.keySet()) {
            sellerList.add(seller);
        }

        blockList = csvReader.getBlockList(customer); // load all the blocked users
        invisList = csvReader.getInvisList(customer); // load all the invisible users

        JFrame frame = new JFrame("Customer Menu");
        frame.setSize(new Dimension(500, 600));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(25);
        searchField.setEditable(false);
        searchPanel.add(new JLabel("Search user"));
        searchPanel.add(searchField);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        searchField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Make the searchField editable when clicked
                searchField.setEditable(true);
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String searchUsername = searchField.getText();
                List<String> searUserInvisList = new ArrayList<>();
                Seller searched = sellerMap.get(searchUsername);
                if (searched == null) { // if searched user is null then no results
                    JOptionPane.showMessageDialog(null, NO_RESULT + searchUsername,
                            null, JOptionPane.ERROR_MESSAGE);
                } else { // if found
                    try {
                        searUserInvisList = csvReader.getInvisList(searched);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (searUserInvisList.contains(customer.getUsername())) { // check if the customer in the user's invisible list
                        JOptionPane.showMessageDialog(null, INVISIBLE_WARNING,
                                null, JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, FOUND_USER + searched.getUsername(),
                                null,JOptionPane.PLAIN_MESSAGE);
                    }

                }
            }
        });

        JPanel friendPanel = new JPanel();
        friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));

        for (String friend : sellerList) {

            JPanel singleFriendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel friendLabel = new JLabel(friend);
            friendLabel.setPreferredSize(new Dimension(100, 20));
            singleFriendPanel.add(friendLabel);

            JPanel friendButtonPanel = new JPanel();
            JButton blockButton = new JButton("Block");

            friendButtonPanel.add(blockButton);
            blockButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    // just add to block list, do not need to remove from sellerList(friendList)
                    if (blockList.contains(friend)) {
                        JOptionPane.showMessageDialog(null, REPEAT_BLOCK,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, BLOCK_SUCCEED,
                                "", JOptionPane.INFORMATION_MESSAGE);
                        blockList.add(friend);
                    }

                    // (write) update csv file
                    try {
                        writer.writeBlockList(customer, blockList);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            JButton ivsButton = new JButton("Invisible");

            friendButtonPanel.add(ivsButton);
            ivsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (invisList.contains(friend)) {
                        JOptionPane.showMessageDialog(null, REPEAT_INVISIBLE,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, INVISIBLE_SUCCEED,
                                "", JOptionPane.INFORMATION_MESSAGE);
                        invisList.add(friend);
                    }

                    // (write) update csv file
                    try {
                        writer.writeInvisList(customer, invisList);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            singleFriendPanel.add(friendButtonPanel);

            friendPanel.add(singleFriendPanel);
        }

        JScrollPane scrollPane = new JScrollPane(friendPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewBlockList = new JButton(BLOCK_LIST);
        JButton viewInvisibleList = new JButton(INVISIBLE_LIST);
        JButton viewAllStores = new JButton(VIEW_ALL_STORES);
        JButton logOutButton = new JButton(LOG_OUT);
        JButton exportFileButton = new JButton(EXPORT_FILE);
        bottomPanel.add(viewAllStores);
        bottomPanel.add(viewBlockList);
        bottomPanel.add(viewInvisibleList);
        bottomPanel.add(exportFileButton);
        bottomPanel.add(logOutButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        viewAllStores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    frame.dispose();
                    openViewStoresWindow(customer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        exportFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try{

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        viewBlockList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showBlockDialog(customer);
            }
        });

        viewInvisibleList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showInvisibleDialog(customer);
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {


                frame.dispose();
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);


    }

    // have to separately refresh the block panel
    private void updateBlockPanel(List<String> blockList) {
        CSVWriter writer = new CSVWriter(customer);

        blockPanel.removeAll();

        if (blockList.isEmpty()) {

            JLabel emptyMessage = new JLabel("Block list is empty");
            emptyMessage.setHorizontalAlignment(SwingConstants.CENTER);
            blockPanel.add(emptyMessage);
        } else {

            for (String block : blockList) {
                JPanel singleBlockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel blockLabel = new JLabel(block);
                blockLabel.setPreferredSize(new Dimension(100, 20));
                singleBlockPanel.add(blockLabel);

                JButton removeButton = new JButton("Remove");
                singleBlockPanel.add(removeButton);

                removeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        blockList.remove(block);

                        updateBlockPanel(blockList);
                        blockPanel.revalidate();
                        blockPanel.repaint();

                        // (write) update csv file
                        try {
                            writer.writeBlockList(customer, blockList);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                blockPanel.add(singleBlockPanel);
            }
        }

    }


    // method to show block list
    private void showBlockDialog(Customer customer) {
        JDialog blockDialog = new JDialog();
        blockDialog.setLayout(new BorderLayout());
        blockDialog.setSize(new Dimension(300, 400));
        blockDialog.setLocationRelativeTo(null);
        blockDialog.setTitle("Blocked User");

        blockPanel.setLayout(new BoxLayout(blockPanel, BoxLayout.Y_AXIS));
        updateBlockPanel(blockList);



        JScrollPane scrollPane = new JScrollPane(blockPanel);
        blockDialog.add(scrollPane, BorderLayout.CENTER);
        blockDialog.setVisible(true);
    }

    public void updateInvisiblePanel(List<String> invisList) {
        CSVWriter writer = new CSVWriter(customer);

        invisiblePanel.removeAll();

        if (invisList.isEmpty()) {
            JLabel emptyLabel = new JLabel("Invisible list is empty");
            invisiblePanel.add(emptyLabel);

        } else {
            for (String invisible : invisList) {
                JPanel singleInvisiblePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel invisibleLabel = new JLabel(invisible);
                invisibleLabel.setPreferredSize(new Dimension(100, 20));
                singleInvisiblePanel.add(invisibleLabel);

                JButton removeButton = new JButton("Remove");
                singleInvisiblePanel.add(removeButton);

                removeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        invisList.remove(invisible);

                        updateInvisiblePanel(invisList);
                        invisiblePanel.revalidate();
                        invisiblePanel.repaint();

                        // (write) update csv file
                        try {
                            writer.writeInvisList(customer, invisList);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                invisiblePanel.add(singleInvisiblePanel);
            }
        }

    }

    // method to show block list
    private void showInvisibleDialog(Customer customer) {

        JDialog invisibleDialog = new JDialog();
        invisibleDialog.setTitle("Become invisible to");
        invisibleDialog.setLayout(new BorderLayout());
        invisibleDialog.setSize(new Dimension(300, 400));
        invisibleDialog.setLocationRelativeTo(null);

        invisiblePanel.setLayout(new BoxLayout(invisiblePanel, BoxLayout.Y_AXIS));
        updateInvisiblePanel(invisList);


        JScrollPane scrollPane = new JScrollPane(invisiblePanel);
        invisibleDialog.add(scrollPane, BorderLayout.CENTER);

        invisibleDialog.setVisible(true);
    }
}
