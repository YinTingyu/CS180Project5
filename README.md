# CS180Project5

## Instruction

### Welcome to our message sending application! Our app allows users to create an account and choose a role as either a customer or a seller.

### To run our OOP project written in Java, you will need to use an IDE that supports Java. Once you have opened the project in your IDE, you can begin by creating a user account by instantiating a User object.

### After successfully logging in, you will be presented with a main menu with options to view, search, and send messages. From there, you can navigate through the application and use its features to communicate with other users.

## Parts of the assignment on Brightspace and Vocareum
Tingyu Yin - Submitted Report on Brightspace. Srinath Dantu - Submitted Vocareum workspace.

## A detailed description of each class

1. **Core (Entity)**

  - **User**: *User* is a super class for both *Sellers* and *Customers* in a marketplace system. Each user is identified by an email and a password, and can have either a seller or customer role. The class contains methods to authenticate a user's password, edit their account information, delete their account. The class HashMap to ensure user's uniqueness.

  - **Customer**: *Customer* extends the *User* class and represents a customer in a messaging and marketplace system.

  - **Seller**: *Seller* extends the *User* class and represents a seller in a messaging and marketplace system. The class includes fields for the seller's stores.

  - **Store**: The class includes fields for the store's name, product, price, and amount available, as well as a reference to the seller who owns the store. The class provides methods for getting and setting the store's name, product, price, and amount available, as well as getting and setting the seller who owns the store. Furthermore, it provides methods for managing stores which include adding, deleting, and editing stores.

  - **Message**: The class includes fields for the message's sender, recipient, content, and timestamp. The constructor for this class takes a sender, recipient, and content, and automatically generates a timestamp for the message based on the current date and time. The class provides methods for getting and setting the sender, recipient, content, and timestamp fields.

2. **View (GUI)**

  - **LoginGUI**: This class implements the ActionListener interface. It includes final fields for several messages, two panels (loginPanel and signUpPanel), labels and text fields for username and password, and buttons for login and registration. It also includes methods for opening the customer and seller menus, and a run() method that creates a JFrame and sets up the card layout and components for the login panel. The actionPerformed() method checks the user's credentials, creates a new account window, or shows an error message depending on the user's input. The createAccountWindow() method sets up a JFrame and components for the registration panel and writes the new user's information to a CSV file.
  
  - **Menu**: This class only contain final fields for messages that are used both in *SellerMenu* and *CustomerMenu*.
  
  - **SellerMenu**: This class is a subclass of *Menu* and represents the menu for a seller user in a marketplace system. It contains several methods and instance variables for managing a seller's stores, messaging customers, and handling blocked and invisible users. It uses instances of the *Seller*, *Store*, *Customer*, *CSVReader*, and *CSVWriter* classes to manage stores and customer interactions, read and write data to CSV files, and retrieve data about customers and their stores. It also uses the *SellerSMGWindow* and *ManageStoresWindow* classes to display windows for sending messages and managing stores.
    + *showSellerMenu()* and *run()* methods: These methods display the main menu for the seller and handle the user interaction with the interface.
    + *openManageStoresWindow()* method: This method opens a new window to display all the stores associated with the seller and allows the seller to manage their stores.
    + *openSellerSMGWindow()* method: This method opens a new window for the seller to send messages to a specific customer.
    + *showBlockDialog()* and *showInvisibleDialog()* methods: These methods display dialogs for the seller to view and manage their blocked and invisible users.
    + *updateBlockPanel()* and *updateInvisiblePanel()* methods: These methods update the panels that display the blocked and invisible users, respectively.
  
  - **CustomerMenu**: This class is a subclass of Menu and contains methods for displaying a customer menu, showing the list of sellers, blocking and becoming invisible to a seller, and displaying the block and invisible lists. It takes an instance of *Customer* as a parameter in its constructor and uses it throughout its methods. Also, it uses *CSVReader* and *CSVWriter* to read and write to CSV files for retrieving and updating the blocked and invisible user lists. Finally, it will instance *ViewStoresWindow* class when the customer click related button.
    + *showCustomerMenu(Customer customer)*: a method for displaying the customer menu, which takes a Customer object as input and calls the run() method
    + *CustomerMenu(Customer customer)*: a constructor that initializes a CustomerMenu object with a Customer object
openViewStoresWindow(Customer customer): a method for opening the view stores window, which takes a Customer object as input and creates a new ViewStoresWindow object to display the view stores GUI
    + *run(Customer customer)*: a method for running the customer menu, which takes a Customer object as input and creates a JFrame object to display the customer menu GUI
    + *updateBlockPanel(List<String> blockList)*: a method for updating the block panel, which takes a list of strings as input and updates the block panel to display the list of blocked sellers
    + *showBlockDialog(Customer customer)*: a method for showing the block list, which takes a Customer object as input and creates a JDialog object to display the block list GUI
    + *updateInvisiblePanel(List<String> invisList)*: a method for updating the invisible panel, which takes a list of strings as input and updates the invisible panel to display the list of invisible sellers
    + *showInvisibleDialog(Customer customer)*: a method for showing the invisible list, which takes a Customer object as input and creates a JDialog object to display the invisible list GUI

  
  - **ViewStoresWindow**: This class displays all the stores available in the system for a customer. It reads the data of all stores from a CSV file using a *CSVReader* object and displays it in a JTable. The JTable displays the name of the store, the name of the seller, the product name, the amount of the product available, and the price of the product. It also includes invisible function that show the name of the seller associated with the store depend on whether the customer is in the invisible list of the seller associated with the store. For each store displayed in the JTable, there is a button labeled "Send Message" that allows the customer to send a message to the seller of that store. Before allowing the customer to send messages and call *openCustomerSMGWindow* method, it will checks if the customer has been blocked by the seller. For each product displayed in the JTable, there is a button labeled "Buy" that allows the customer to purchase that product. When the customer clicks on the "Buy" button, the amount of the product available is decremented, and the JTable is updated to reflect the new amount of the product available. The *onGoBack* object is used to navigate back to the previous *CustomerMenu*.
  
  - **ManageStoresWindow**: This class for displaying and managing all the stores and products of a seller. It reads all the stores of a seller from a CSV file using a *CSVReader* object and displays it in a JTable. The JTable displays the name of the store, the name of the product, the amount of the product, the price of the product. Seller can add or delete stores by clicking "Add Store" or "Delete Store" buttons. When seller clicks "Add Store" button, it will display a small window to prompt the input of store information, this includes handle the invalid input of product information. Also, each store has four buttons for seller to add, delete or edit the information of their products. Seller can add or delete product by clicking "Add Product" or "Delete Product" buttons. When seller clicked "Edit" button, a "Save" button becomes visible and "Edit" button becomes invisible. The cells of JTable will be editable for seller to edit the product. When seller edits product, invalid input of product will get errors. Once the seller finish editting and clicked "Save", "Edit" button will becomes vsible and "Save" button will becomes invisible. The *onGoBack* object is used to navigate back to the previous *CustomerMenu*.

  - **CustomerSMGWindow**: This class is responsible for displaying and managing the conversations between a customer and a seller. It contains methods for updating the conversation panel, writing and reading messages to and from CSV files, and sending messages to the seller. It takes a *CustomerMenu*, *Store*, and *Customer* objects as parameters in its constructor, which it uses to display the store name and seller username in the conversation panel. It also uses the *CSVReader* and *CSVWriter* classes to read and write messages to CSV files. Additionally, customer can navigate to *ViewStoresWindow* by clicking the "onGoBack" button.
  
  - **SellerSMGWindow**: This class is responsible for displaying and managing the conversations between a customer and a seller. It contains methods for updating the conversation panel, writing and reading messages to and from CSV files, and sending messages to the seller. It takes a *SellerMenu*, *Store*, and *Seller* objects as parameters in its constructor, which it uses to display the store name and seller username in the conversation panel. It also uses the CSVReader and *CSVWriter* classes to read and write messages to CSV files. Additonally, customer can navigate to *ViewStoresWindow* by clicking the "onGoBack" button.

3. **Utils**
  
  - **CSVReader**: The *CSVReader* class is part of the utils package and is responsible for reading CSV files that contain information about customers, stores, sellers, and messages. It provides methods for reading customer, store, and seller information and storing them in corresponding maps. It also provides methods for retrieving a specific user's block list, invisible list, and a seller's stores. In addition, it has a method for reading messages from a CSV file and parsing them into a formatted string. The class has a relationship with other classes in the core package.
  
  - **CSVWriter**: The *CSVWriter* class is part of the utils package and is responsible for writing CSV files that store information about customers, stores, sellers, and messages. It provides methods for writing a new user, new store, new store, and new message in CSV files. It also provides methods for updating block list, invisible list for both sellers and customers. It also contains methods for updating product for sellers. The information of product are written in a formatted string. It use ";" to split multiple products, stores or users in columns of block list, invesible list, conversation filenames and products. The class has a relationship with other classes in the core package.
  



