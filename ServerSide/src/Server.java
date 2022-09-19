/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* Adrian Gallegos
* ag76424
* 17360
* Spring 2022
*/

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;

import javafx.application.Application;

public class Server extends Observable  {
	
	public static int port = 5000;	// current server port being used
	public Auction global;
	
	public static ArrayList<Item> items = new ArrayList(); 
	public static ArrayList<String> itemsName = new ArrayList(); 
	public static ArrayList<String> description = new ArrayList();
	public static ArrayList<String> winner = new ArrayList();
	public static ArrayList<Double> finalPrice = new ArrayList();
	public static ArrayList<String> status = new ArrayList();
	
	public static void main(String[] args) {
		try {
			Application.launch(viewController.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	

	void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(port);
		global = new Auction();
		
		DataOutputStream send;
		
		while (true) {
			
			Socket clientSocket = serverSock.accept();
			
			// Create a new user u with an arbritrary username and socket, add it to the global Auction
			User u = new User(""+clientSocket.hashCode(), clientSocket);
			String raw = u.receive.readUTF();
			u.send.writeUTF(global.users.toString());
			String thisUsername = raw.substring(raw.indexOf(":")+1);
			u.username  = thisUsername;
			for(User z : global.users) {
				z.send.writeUTF("[NewUser:"+thisUsername+"]");
			}
			global.add(u);
			
			System.out.println("got a connection from " + thisUsername);
			
			//send = new DataOutputStream(clientSocket.getOutputStream());	// use this to send clients item List
			// Checks if there was a previous user in the server
			// If there was, then we should use the updated item list on the server
			if (global.users.size() > 1) {
				int nameIndex = 0;
				int descriptionIndex = 0;
				int winnerIndex = 0;
				int finalPriceIndex = 0;
				int statusIndex = 0;
				//String winner = "";
				
				for (int i = 0; i < items.size(); i++) {
					u.send.writeUTF("[NewItem:"+itemsName.get(nameIndex)+"]");
					u.send.writeUTF("[NewDescription:"+description.get(descriptionIndex)+"]");
					u.send.writeUTF("[NewWinner:"+winner.get(winnerIndex)+"]");
					u.send.writeUTF("[NewFinalPrice:"+String.valueOf(finalPrice.get(finalPriceIndex))+"]");
					u.send.writeUTF("[NewStatus:"+status.get(statusIndex)+"]");
					nameIndex++;
					descriptionIndex++;
					winnerIndex++;
					finalPriceIndex++;
					statusIndex++;
				}
			}
			// Read in the item list from the text file and keep a record on the server
			else {
				File file = new File("ItemList.txt");
				try {
					Scanner scanner = new Scanner(file);

					//now read the file line by line...
					int lineNum = 0;
					int nameIndex = 0;
					int descriptionIndex = 0;
					int winnerIndex = 0;
					int finalPriceIndex = 0;
					int statusIndex = 0;
					String winner = "";
					
					while (scanner.hasNextLine()) {
						String line = scanner.nextLine();
						String[] temp = line.split(":");
						String isName = temp[0];
						String itemName = temp[1];
						
						lineNum++;
						if(isName.equals("name")) { 
							u.send.writeUTF("[NewItem:"+itemName+"]");
							itemsName.add(itemName);
						}
						if(isName.equals("description")) { 
							u.send.writeUTF("[NewDescription:"+itemName+"]");
							description.add(itemName);
						}
						if(isName.equals("winner")) { 
							u.send.writeUTF("[NewWinner:"+itemName+"]");
							this.winner.add(itemName);
						}
						if(isName.equals("finalPrice")) { 
							u.send.writeUTF("[NewFinalPrice:"+itemName+"]");
							finalPrice.add(Double.parseDouble(itemName));
						}
						if(isName.equals("status")) { 
							u.send.writeUTF("[NewStatus:"+itemName+"]");
							status.add(itemName);
						}
					}
					for (int i = 0; i < 5; i++) {
						Item item = new Item(itemsName.get(nameIndex), description.get(descriptionIndex), this.winner.get(winnerIndex), 
								finalPrice.get(finalPriceIndex), status.get(statusIndex));
						items.add(item);
						nameIndex++;
						descriptionIndex++;
						winnerIndex++;
						finalPriceIndex++;
						statusIndex++;
					}
			    
				} catch(FileNotFoundException e) { 
					e.printStackTrace();
				}
			}
		}
	}
	
	public static int getPort() {
		return port;
	}

}
