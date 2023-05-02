package view;

import core.Customer;
import core.Seller;
import core.Store;
import utils.CSVReader;
import utils.CSVWriter;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * A seller menu
 *
 * <p>Purdue University -- CS18000 -- Spring 2023 -- project 5
 *
 * @author Tingyu Yin
 * @version April 19, 2023
 */
public class SellerMenu extends Menu {
    private static final String MANAGE_STORE = "Manage my store";
    private static final String BLOCKED = "You are blocked!";

    //Codes
    private static final String CREATE_NEW_ACCOUNT_OPTION_CODE = "AA01";
    private static final String LOG_IN_OPTION_CODE = "AA02";
    private static final String EXIT_FIRST_MENU_OPTION_CODE = "AA03";

    private static final String CONTACT_USER_CODE = "BB01";
    private static final String BLOCK_USER_CODE = "BB02";
    private static final String SET_INVISIBLE_CODE = "BB03";
    private static final String VIEW_DASHBOARD_CODE = "BB04";
    private static final String EXPORT_FILE_CODE = "BB05";
    private static final String IMPORT_FILE_CODE = "BB06";
    private static final String CREATE_STORE_CODE = "BB067";

    private static final String CONFIRMATION_CODE = "OKAY";
    private static final String REJECTION_CODE = "NOT OKAY";
    private static final String FULL_EXIT_CODE = "FULL EXIT";
    private static final String REFRESH_CODE = "REFRESH";
    private static final String CUSTOMER_TYPE = "Customer";
    private static final String SELLER_TYPE = "Seller";

    private static final String QUERY_USER_BLOCKED = "CC01";

    public String message;
    private String filename = "sellers.csv";
    private SellerMenu sellerMenu;
    private CSVReader csvReader;

    public Seller seller;
    public List<String> blockList;
    public List<String> invisList;
    public List<String> sellerList = new ArrayList<>();
    public List<Store> sellerStore;
    private JPanel invisiblePanel = new JPanel();
    private JPanel blockPanel = new JPanel();
    private Socket socket;
    private BufferedReader bfr;
    private PrintWriter pw;

    public void showSellerMenu(Seller seller) throws IOException { // this is used when login and when go back
        run(this.seller);
    }

    public SellerMenu(Seller seller, Socket inputSocket) { // used when login gui instance the CustomerMenu
        this.seller = seller;
        this.csvReader = new CSVReader();
        try {
            sellerStore = csvReader.getSellerStores(seller);        
            this.socket = inputSocket;
            System.out.println(inputSocket);
            bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openSellerSMGWindow(SellerMenu sellerMenu, Customer customer, Seller seller) {

        SellerSMGWindow sendWindow = new SellerSMGWindow(sellerMenu, customer, seller);
        try {
            sendWindow.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openManageStoresWindow(Seller seller) throws IOException { // to open view all the stores GUI
        ManageStoresWindow storesWindow = new ManageStoresWindow(() -> { // use lambda expressions to create anonymous methods
            try {
                showSellerMenu(seller);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, this.seller, this); // the lambda expression is passed as the first argument,
        // and the Customer object is passed as the second argument, sellerMenu as the third parameter
        storesWindow.run();
    }

    public void run(Seller seller) throws IOException {
        CSVWriter writer = new CSVWriter(seller);

        this.seller = seller;
        sellerStore = seller.getStores();
        Map<String, Customer> customerMap = csvReader.readCustomers();

        for (String customer : customerMap.keySet()) {
            sellerList.add(customer);
        }

        blockList = csvReader.getBlockList(seller); // load all the blocked users
        invisList = csvReader.getInvisList(seller); // load all the invisible users

        JFrame frame = new JFrame("Seller Menu");
        frame.setSize(new Dimension(500, 600));
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(25);
        searchField.setEditable(false);
        searchPanel.add(new JLabel("Search for"));
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
                Customer searched = customerMap.get(searchUsername);
                if (searched == null) { // if searched user is null then no results
                    JOptionPane.showMessageDialog(null, NO_RESULT + searchUsername,
                            null, JOptionPane.ERROR_MESSAGE);
                } else { // if found
                    try {
                        searUserInvisList = csvReader.getInvisList(searched);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (searUserInvisList.contains(seller.getUsername())) { // check if the seller in the searched user's invisible list
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
                        writer.writeBlockList(seller, blockList);
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
                        writer.writeInvisList(seller, invisList);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            JButton sendMSGButton = new JButton("Send Message");
            friendButtonPanel.add(sendMSGButton);
            sendMSGButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    pw.println(QUERY_USER_BLOCKED + "$" + seller.getUsername() + "$" + friend);
                    pw.flush();
                    try {
                        if(bfr.readLine().equals(REJECTION_CODE)) {
                            JOptionPane.showMessageDialog(null, "You have been blocked!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        } else if (blockList.contains(seller.getUsername())) {
                            JOptionPane.showMessageDialog(null, "User is blocked!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            openSellerSMGWindow(sellerMenu, customerMap.get(friend), seller);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            singleFriendPanel.add(friendButtonPanel);

            friendPanel.add(singleFriendPanel);
        }

        JScrollPane scrollPane = new JScrollPane(friendPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // bottom panel to hold bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewBlockList = new JButton(BLOCK_LIST);
        JButton viewInvisibleList = new JButton(INVISIBLE_LIST);
        JButton manageStores = new JButton(MANAGE_STORE); // manage stores
        JButton logOutButton = new JButton(LOG_OUT);
        bottomPanel.add(manageStores);
        bottomPanel.add(viewBlockList);
        bottomPanel.add(viewInvisibleList);
        bottomPanel.add(logOutButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        manageStores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    frame.dispose();
                    openManageStoresWindow(seller);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        viewBlockList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showBlockDialog(seller);
            }
        });

        viewInvisibleList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showInvisibleDialog(seller);
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // get timestamp when log out
                Timestamp logOutTimestamp = new Timestamp(System.currentTimeMillis());

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String logOutTspStr = dateFormat.format(logOutTimestamp);
                frame.dispose();
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);


    }

    // have to separately refresh the block panel
    private void updateBlockPanel(List<String> blockList) {

        CSVWriter writer = new CSVWriter(seller);
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
                            writer.writeBlockList(seller, blockList);
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
    private void showBlockDialog(Seller seller) {

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

        CSVWriter writer = new CSVWriter(seller);
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
                            writer.writeInvisList(seller, invisList);
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
    private void showInvisibleDialog(Seller seller) {

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
