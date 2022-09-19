/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* Adrian Gallegos
* ag76424
* 17360
* Spring 2022
*/

import java.io.*;
import java.net.*;
import java.net.InetAddress;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Client extends Application {

	AnchorPane root;
	public static Stage primaryStage;
	@FXML
	PasswordField passWordField;
	@FXML
	TextField ipAddressField, portNumber, usernameField, usersToAddField, newChatName;
	@FXML
	Button myIP, connect, createAccount, loginBtn, addUsersBtn, createNewChatroomBtn;

	@FXML
	GridPane userList;
	@FXML
	TextArea outgoingMessage;
	@FXML
	ScrollPane messages, usersPane;

	static String ipAddress;
	public static int port;
	static Socket sock = null;
	public static String username, password;
	public static ArrayList<String> users = new ArrayList<String>();
	
	public static ArrayList<String> itemNames = new ArrayList<String>();
	public static ArrayList<Item> items = new ArrayList();
	
	public static ArrayList<Label> chats = new ArrayList<Label>();
	public static ArrayList<String> newUsersToAdd = new ArrayList<String>();
	static BufferedReader reader;
	static PrintWriter writer;

	public DataOutputStream send;
	public DataInputStream receive;

	public String EVEN_USER = "-fx-background-color: #ecf0f1; -fx-font-family: Futura; -fx-font-size: 15;";
	public String ODD_USER = "-fx-background-color: #bdc3c7; -fx-font-family: Futura; -fx-font-size: 15;";

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Client.primaryStage = primaryStage;
		primaryStage.setOnCloseRequest(e ->{
			System.exit(0);
		});
		initView();
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}).start();
	}

	public void autofillIP(ActionEvent event) {
		System.out.println("hi");
		String ipAddress = null;
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress().trim();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ipAddressField.setText(ipAddress);
		this.ipAddress = ipAddressField.getText();
	}

	public void connectToServer() {
		Client.port = Integer.parseInt(portNumber.getText());
		System.out.println(port);
		ipAddress = ipAddressField.getText();
		System.out.println(ipAddress);
		try {
			showLogin();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createAccountBtnClicked() {
		try {
			root = (AnchorPane) FXMLLoader.load(Client.class.getResource("CreateUser.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene createUserScene = new Scene(root, 800, 450);
		primaryStage.setScene(createUserScene);
	}

	public void login() throws IOException {
		// TODO: Firebase authentication, maybe oath
		Client.username = usernameField.getText();
		Client.password = passWordField.getText();
		// some authentication
		// create chat window

		enterAuction();
	}

	public void enterAuction() throws IOException {
		
		try {
			sock = new Socket(ipAddress, port);
			root = (AnchorPane) FXMLLoader.load(Client.class.getResource("Auction.fxml"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR: NO SERVER STARTED");
			alert.setHeaderText("Error: No server");
			alert.setContentText("There is no server running. "
					+ "Please exit this application and start a server before relaunching");

			alert.showAndWait();
			System.exit(1);
		}

		send = new DataOutputStream(sock.getOutputStream());
		receive = new DataInputStream(sock.getInputStream());

		send.writeUTF("Item:" + username);

		Scene AuctionMain = new Scene(root, 800, 600);
		primaryStage.setScene(AuctionMain);

		System.out.println(root.getChildrenUnmodifiable().toString());
		userList = (GridPane) root.getChildren().get(0);
		System.out.println(userList.toString());
		
		String raw = receive.readUTF(); // this line receives global.users.toString() aka line 47
		String arr = raw.substring(1, raw.indexOf("]"));
		users = new ArrayList<String>(Arrays.asList(arr.split(",")));
		users.add(0, username);
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).trim().length() == 0) {
				users.remove(i);
				i--;
			}
			users.set(i, users.get(i).trim());
		}
		
		for (int i = 0; i < 5; i++) {
			//for (int j = 0; j < 3; j++) {
				String raw1 = receive.readUTF();
				
				String newItemName = "";
				String newDescription = "";
				String currentWinner = "";
				double finalPrice = 0;
				String currentStatus = "";
				if (raw1.contains("NewItem:")) {
					String thisItem = raw1.substring(raw1.indexOf(":") + 1, raw1.indexOf("]"));
					raw1 = raw1.substring(raw1.indexOf("]")+1);
					if (thisItem.trim().length() > 0) {
						itemNames.add(thisItem);
						newItemName = thisItem;
					}
				}
				raw1 = receive.readUTF();
				if (raw1.contains("NewDescription:")) {
					String thisItem = raw1.substring(raw1.indexOf(":") + 1, raw1.indexOf("]"));
					raw1 = raw1.substring(raw1.indexOf("]")+1);
					if (thisItem.trim().length() > 0) {
						newDescription = thisItem;
					}
				}
				raw1 = receive.readUTF();
				if (raw1.contains("NewWinner:")) {
					String thisItem = raw1.substring(raw1.indexOf(":") + 1, raw1.indexOf("]"));
					raw1 = raw1.substring(raw1.indexOf("]")+1);
					if (thisItem.trim().length() > 0) {
						currentWinner = thisItem;
					}
				}
				raw1 = receive.readUTF();
				if (raw1.contains("NewFinalPrice:")) {
					String thisItem = raw1.substring(raw1.indexOf(":") + 1, raw1.indexOf("]"));
					raw1 = raw1.substring(raw1.indexOf("]")+1);
					if (thisItem.trim().length() > 0) {
						finalPrice = Double.parseDouble(thisItem);
					}
				}
				raw1 = receive.readUTF();
				if (raw1.contains("NewStatus:")) {
					String thisItem = raw1.substring(raw1.indexOf(":") + 1, raw1.indexOf("]"));
					raw1 = raw1.substring(raw1.indexOf("]")+1);
					if (thisItem.trim().length() > 0) {
						currentStatus = thisItem;
					}
				}
				Item thisItem = new Item(newItemName, newDescription, currentWinner, finalPrice, currentStatus);
				items.add(i, thisItem);
			//}
		}
		

		for (int i = 0; i < itemNames.size(); i++) {

			Button currButton = new Button();

			currButton.setText(itemNames.get(i));
			currButton.setMinWidth(200);
			currButton.setOnMouseClicked(e->{
				createNewChatPopUp();
			});
			VBox currUserSize = new VBox(currButton);
			VBox.setVgrow(currButton, Priority.ALWAYS);
		
			if (i % 2 == 0) {
				currButton.setStyle(EVEN_USER);
			} else {
				currButton.setStyle(ODD_USER);
			}
			userList.add(currButton, 0, i);
			GridPane.setValignment(currUserSize, VPos.TOP);
		}
		
		///////
		System.out.println(userList.getChildren().toString());

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
				}
			}

		});

		t.start();
		try {
			setUpNetworking(root);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	public void createNewChatPopUp() {
		// TODO Auto-generated method stub
		Stage newStage = new Stage();
		try {
			root = (AnchorPane) FXMLLoader.load(Client.class.getResource("CreateChatRoom.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scene popUpScene = new Scene(root, 400, 300);
		newStage.setScene(popUpScene);
		newStage.show();
		
	}
	
	public void createNewChatRoom() throws IOException{
		Stage newStage = new Stage();
		try {
			sock = new Socket(ipAddress, port);
			root = (AnchorPane) FXMLLoader.load(Client.class.getResource("ChatRoom.fxml"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR: NO SERVER STARTED");
			alert.setHeaderText("Error: No server");
			alert.setContentText("There is no server running. "
					+ "Please exit this application and start a server before relaunching");

			alert.showAndWait();
			System.exit(1);
		}

		send = new DataOutputStream(sock.getOutputStream());
		receive = new DataInputStream(sock.getInputStream());

		send.writeUTF("Name:" + username);

		Scene chatroomMain = new Scene(root, 800, 600);
		newStage.setScene(chatroomMain);
		newStage.show();

		System.out.println(root.getChildrenUnmodifiable().toString());
		userList = (GridPane) root.getChildren().get(0);
		System.out.println(userList.toString());

		String raw = receive.readUTF();
		String arr = raw.substring(1, raw.indexOf("]"));

		this.users = newUsersToAdd;
		

		for (int i = 0; i < users.size(); i++) {

			Button currButton = new Button();

			if (users.get(i).equals(username)) {
				currButton.setText(users.get(i) + " (Me)");
			} else {
				currButton.setText(users.get(i));
			}
			currButton.setMinWidth(200);
			currButton.setOnMouseClicked(e->{
				createNewChatPopUp();
			});
			VBox currUserSize = new VBox(currButton);
			VBox.setVgrow(currButton, Priority.ALWAYS);
		
			if (i % 2 == 0) {
				currButton.setStyle(EVEN_USER);
			} else {
				currButton.setStyle(ODD_USER);
			}
			userList.add(currButton, 0, i);
			GridPane.setValignment(currUserSize, VPos.TOP);
			
			
		}
		System.out.println(userList.getChildren().toString());

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
				}
			}

		});

		t.start();
		try {
			setUpNetworking(root);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void validateUser() {
		newUsersToAdd = new ArrayList<String>();
		System.out.println("CHECK");
		String user = usersToAddField.getText();
		for(String u : users) {
			System.out.println(u);
		}
		if(users.indexOf(user) != -1) {
			System.out.println("user exits");
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Successfully added.");
			alert.setHeaderText("Added " + user + " successfully");
			alert.setContentText("The user " + user + " has been successfully addded");
			alert.showAndWait();
			newUsersToAdd.add(user);
			
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR: User Not Found");
			alert.setHeaderText("User " + user + " not found");
			alert.setContentText("The user " + user + " was unable to be added."
					+ " This user was not found within the currently active users");
			alert.showAndWait();
		}
	}
	
	public void periodicUpdate() throws InterruptedException {
		System.out.println(userList);
		userList.getChildren().clear();
		for (int i = 0; i < itemNames.size(); i++) {

			Button currButton = new Button();

			currButton.setText(itemNames.get(i));
			currButton.setMinWidth(200);
			currButton.setOnMouseClicked(e->{
				createNewChatPopUp();
			});
			VBox currUserSize = new VBox(currButton);
			VBox.setVgrow(currButton, Priority.ALWAYS);
		
			if (i % 2 == 0) {
				currButton.setStyle(EVEN_USER);
			} else {
				currButton.setStyle(ODD_USER);
			}
			userList.add(currButton, 0, i);
			GridPane.setValignment(currUserSize, VPos.TOP);
		}
		
	}

	private void initView() {
		try {
			root = (AnchorPane) FXMLLoader.load(Client.class.getResource("EnterIP.fxml"));
			Scene ipScene = new Scene(root, 800, 450);
			primaryStage.setResizable(false);
			primaryStage.setScene(ipScene);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void showLogin() {
		// TODO: UNCOMMENT SOCKET
		
		
		try {
//			this.sock = ;
//			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			root = (AnchorPane) FXMLLoader.load(Client.class.getResource("Login.fxml"));
			System.out.println("yo");
			Scene loginScene = new Scene(root, 800, 450);
			primaryStage.setScene(loginScene);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public void sendMessage1() {
		System.out.println(outgoingMessage.toString());
		String message = outgoingMessage.textProperty().getValueSafe();
		System.out.println(message);

		writer.println(username+": "+message);
		writer.flush();
		outgoingMessage.setText("");
		outgoingMessage.requestFocus();
	}

	public void sendMessage() throws IOException {
		// flag = 0 => no item with the entered name exists
		// flag = 1 => item exists
		//send = new DataOutputStream(sock.getOutputStream());
		int flag = 0;
		String clientMessage = outgoingMessage.textProperty().getValueSafe();
		if (clientMessage.contains(":")) {	// avoids indexoutofbounds exception for temp[1] when input doesn't have ":"
			String[] temp = clientMessage.split(":");
			String itemName = temp[0];
			String itemBid = temp[1];
			for (int i = 0; i < items.size(); i++) {
				// find item name in item list
				if (itemName.equals(items.get(i).getName())) {	// then we'd need to check if bid is high enough (skip for now)
					// check if item is still being auctioned
					if (items.get(i).getStatus().equals("open")) {
						double currentBid = Double.parseDouble(itemBid);
						if (currentBid > items.get(i).getFinalPrice()) {	// item bid is greater than current highest bid
							items.get(i).setFinalPrice(currentBid); 	// update current highest bid
							String s = Integer.toString(i);
							//send.writeUTF(s+"-"+itemBid);	// update the current highest bid on the server
							writer.println("[NewBid:"+s+"-"+itemBid+"]");
							writer.flush();
							writer.println("[NewWinner:"+s+"-"+username+"]");
							writer.flush();
							if (currentBid >= 200) {
								writer.println("[NewStatus:"+s+"-"+"closed"+"]");
								writer.flush();
							}
						}
						else {
							System.out.println("You're bid is too low");
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("ERROR: You're bid is too low");
							alert.setHeaderText("Bid was unsuccessful");
							alert.setContentText("The "+itemName+ " has a current bid of "+items.get(i).getFinalPrice()
									+". If you'd like to bid on the "+itemName+", bid higher than "+items.get(i).getFinalPrice()+".");
							alert.showAndWait();
							return;
						}
						flag = 1;
						System.out.println(outgoingMessage.toString());
						String message = outgoingMessage.textProperty().getValueSafe();
						System.out.println(message);
						
						writer.println(username+": "+message);
						writer.flush();
						outgoingMessage.setText("");
						outgoingMessage.requestFocus();
						return;
					}
					else {
						System.out.println("This item is no longer being auctioned");
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("ERROR: Auction for this item is closed");
						alert.setHeaderText("Bid was unsuccessful");
						alert.setContentText("The "+itemName+ " has been won by "+items.get(i).getWinner()+" with a bid of " +
								items.get(i).getFinalPrice()+". "+ "You're welome to continue browsing the auction, sorry about that.");
						alert.showAndWait();
						return;
					}
				}
			}
			// if we get here, item doesn't exist
			if (flag == 0) {
				System.out.println("Item not found");
			}
		}
		System.out.println("This item is incorrectly formatted");
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("ERROR: Item entered is formatted incorrectly");
		alert.setHeaderText("Please adhere to the input format: Item Name:Bid Value");
		alert.setContentText("The item: "+clientMessage+ " is not in the auction"+". "
				+ "Please enter a valid item name and bid. If you'd wish to bid on an item, use the input format: Item Name:Bid Value");
		alert.showAndWait();
	}

	private void setUpNetworking(AnchorPane root) throws Exception {
		@SuppressWarnings("resource")
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader(root));
		readerThread.start();
	}

	class IncomingReader implements Runnable {
		
		ScrollPane messagesPane = null;
		GridPane displayedMessages = null;
		
		public IncomingReader(AnchorPane root) {
			System.out.println(root.toString());
			messagesPane = (ScrollPane)root.getChildren().get(2);
			displayedMessages = (GridPane) messagesPane.getContent();
		}
		// Periodically updates the users within the auction
		public void run() {
			String raw;
			try {
				while ((raw = reader.readLine().trim()) != null && raw.indexOf(":") != -1) {
					System.out.println(raw);
					
					
					while (raw.contains("NewUser:")) {
						String thisUsername = raw.substring(raw.indexOf(":") + 1, raw.indexOf("]"));
						raw = raw.substring(raw.indexOf("]")+1);
						if (thisUsername.trim().length() > 0) {
							users.add(thisUsername);
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								try {
									periodicUpdate();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						});
					}
					
					while (raw.contains("NewBid:")) {
						String thisUsername = raw.substring(raw.indexOf(":") + 1, raw.indexOf("]"));
						String[] temp = thisUsername.split("-");
						String itemIndex = temp[0];
						String updatedBid = temp[1];
						raw = raw.substring(raw.indexOf("]")+1);
						int index = Integer.parseInt(itemIndex);
						double bid = Double.parseDouble(updatedBid);
						items.get(index).setFinalPrice(bid);
						//break;
					}
					
					while (raw.contains("NewWinner:")) {
						String thisUsername = raw.substring(raw.indexOf(":") + 1, raw.indexOf("]"));
						String[] temp = thisUsername.split("-");
						String itemIndex = temp[0];
						String updatedWinner = temp[1];
						raw = raw.substring(raw.indexOf("]")+1);
						int index = Integer.parseInt(itemIndex);
						items.get(index).setWinner(updatedWinner);
						//break;
					}
					
					while (raw.contains("NewStatus:")) {
						String thisUsername = raw.substring(raw.indexOf(":") + 1, raw.indexOf("]"));
						String[] temp = thisUsername.split("-");
						String itemIndex = temp[0];
						String updatedStatus = temp[1];
						raw = raw.substring(raw.indexOf("]")+1);
						int index = Integer.parseInt(itemIndex);
						items.get(index).setStatus(updatedStatus);
						//raw = items.get(index).getWinner() + " has won the " + items.get(index).getName() +
						//		" with a bid of " + items.get(index).getFinalPrice() + ".";
						//break;
					}
					
					// attempting to read in new Items from server
					while (raw.contains("NewItem:")) {
						String thisItem = raw.substring(raw.indexOf(":") + 1, raw.indexOf("]"));
						raw = raw.substring(raw.indexOf("]")+1);
						if (thisItem.trim().length() > 0) {
							itemNames.add(thisItem);
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								try {
									periodicUpdate();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						});
					}

					while (raw.contains("LeaveUser:")) {
						String thisUsername = raw.substring(raw.indexOf(":") + 1, raw.indexOf("]"));
						System.out.println(thisUsername);
						for (int x = 0; x < users.size(); x++) {
							System.out.println(users.get(x) + " == " + thisUsername);
							if (users.get(x).equals(thisUsername)) {
								users.remove(x);
								x--;
							}
						}
						raw = raw.substring(raw.indexOf("]")+1);

						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								try {
									periodicUpdate();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						});
					} 
					/*if (raw.contains("-")) {
						String[] temp = raw.split("-");
						String itemIndex = temp[0];
						int index = Integer.parseInt(itemIndex);
						String updatedBid = temp[1];
						double bid = Double.parseDouble(updatedBid);
						items.get(index).setFinalPrice(bid);
						break;
					}*/
					
					{
						if (!(raw.equals(""))) {
							chats.add(new Label(raw + "\n"));
					//}
					
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							String test = displayedMessages.toString();
							if (!(test.contains(chats.get(chats.size()-1).toString()))){
								displayedMessages.add(chats.get(chats.size()-1), 0, chats.size() );
								GridPane.setHgrow(chats.get(chats.size()-1), Priority.ALWAYS);
							}
						}
						
					});
						}
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			new Client();
			Application.launch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
