package view;

import core.Customer;
import core.Seller;
import utils.CSVReader;
import utils.CSVWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
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
public class CustomerMenu {
    private static final String PROMPT_ACTION = "What do you want to do?";
    private static final String BLOCK_LIST = "Block List";
    private static final String INVISIBLE_LIST = "Invisible List";
    private static final String VIEW_ALL_STORES = "View all the stores";
    private static final String SEARCH_USER = "Search a user";
    private static final String BLOCK_SUCCEED = "Successfully blocked this user!";
    private static final String INVISIBLE_SUCCEED = "Successfully become invisible to this user!";
    private static final String LOG_OUT = "Log out";

    public String message;
    private CSVReader csvReader;
    private Customer customer;
    public List<String> blockList;
    public List<String> invisList;
    public List<String> sellerList = new ArrayList<>();

    public void showCustomerMenu(Customer customer) throws IOException { // this is used when login and when go back
        run(customer);
    }

    public CustomerMenu() { // used when login gui instance the CustomerMenu
        this.csvReader = new CSVReader();
    }

    public void openViewStoresWindow(Customer customer) throws IOException { // to open view all the stores GUI
        ViewStoresWindow storesWindow = new ViewStoresWindow(() -> { // use lambda expressions to create anonymous methods
            try {
                showCustomerMenu(customer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, customer); // the lambda expression is passed as the first argument,
        // and the Customer object is passed as the second argument
        storesWindow.run();
    }

    public void run(Customer customer) throws IOException {

        CSVWriter CSVWriter = new CSVWriter();
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
        searchPanel.add(new JLabel("Search user"));
        searchPanel.add(searchField);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel friendPanel = new JPanel();
        friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));

        for (String friend : sellerList) {

            JPanel singleFriendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel friendLabel = new JLabel(friend);
            friendLabel.setPreferredSize(new Dimension(100, 20));
            singleFriendPanel.add(friendLabel);

            JPanel friendButtonPanel = new JPanel();
            JButton blockButton = new JButton("Block");
            blockButton.setBackground(Color.RED); // do not show?
            friendButtonPanel.add(blockButton);
            blockButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    // just add to block list, do not need to remove from sellerList(friendList)
                    blockList.add(friend);

                    // (write) update csv file to be implemented
                }
            });

            JButton ivsButton = new JButton("Invisible");
            ivsButton.setBackground(Color.GRAY); // do not show ?...
            friendButtonPanel.add(ivsButton);
            ivsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    invisList.add(friend);

                    // (write) update csv file to be implemented
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
        bottomPanel.add(viewAllStores);
        bottomPanel.add(viewBlockList);
        bottomPanel.add(viewInvisibleList);
        bottomPanel.add(logOutButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        viewAllStores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    openViewStoresWindow(customer);
                } catch (IOException e) {
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

    // method to show block list
    private void showBlockDialog(Customer customer) {
        JDialog blockDialog = new JDialog();
        blockDialog.setLayout(new BorderLayout());
        blockDialog.setSize(new Dimension(300, 400));
        blockDialog.setLocationRelativeTo(null);
        blockDialog.setTitle("Blocked User");

        JPanel blockPanel = new JPanel();
        for (String block : blockList) {
            blockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel blockLabel = new JLabel(block);
            blockLabel.setPreferredSize(new Dimension(100, 20));
            blockPanel.add(blockLabel);

            JButton removeButton = new JButton("Remove");
            blockPanel.add(removeButton);

            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    blockList.remove(block);

                    // (write) update csv file to be implemented
                }
            });
        }

        JScrollPane scrollPane = new JScrollPane(blockPanel);
        blockDialog.add(scrollPane, BorderLayout.CENTER);

        blockDialog.setVisible(true);
    }

    // method to show block list
    private void showInvisibleDialog(Customer customer) {
        JDialog invisibleDialog = new JDialog();
        invisibleDialog.setTitle("Become invisible to");
        invisibleDialog.setLayout(new BorderLayout());
        invisibleDialog.setSize(new Dimension(300, 400));
        invisibleDialog.setLocationRelativeTo(null);

        JPanel invisiblePanel = new JPanel();
        for (String invis : invisList) {
            invisiblePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            JLabel invisLabel = new JLabel(invis);
            invisLabel.setPreferredSize(new Dimension(100, 20));
            invisiblePanel.add(invisLabel);

            JButton removeButton = new JButton("Remove");
            invisiblePanel.add(removeButton);

            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    invisList.remove(invis);

                    // (write) update csv file to be implemented
                }
            });
        }

        JScrollPane scrollPane = new JScrollPane(invisiblePanel);
        invisibleDialog.add(scrollPane, BorderLayout.CENTER);

        invisibleDialog.setVisible(true);
    }
}
