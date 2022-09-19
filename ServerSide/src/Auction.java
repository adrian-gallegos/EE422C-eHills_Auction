/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* Adrian Gallegos
* ag76424
* 17360
* Spring 2022
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Auction extends Observable {
	
	public static AnchorPane root = null; 
	
	// the users in this Auction. you can refer to all the users and their usernames in a Auction via this list.
	public ArrayList<User> users = new ArrayList<User>();
	

	public void add(User s) throws IOException {
		users.add(s);
		ClientObserver writer = new ClientObserver(s.sock.getOutputStream());
		this.addObserver(writer);
		Thread t = new Thread(new ClientHandler(s.sock));
		t.start();
		updateRoom();
	}
	
	public void updateRoom() {
		System.out.println(users.size());
		for(int i = 0; i < users.size(); i++) {
			System.out.println(users.get(i).username);
		}
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread t : threadSet) {
		    String name = t.getName();
		    Thread.State state = t.getState();
		    int priority = t.getPriority();
		    String type = t.isDaemon() ? "Daemon" : "Normal";
		    System.out.printf("%-20s \t %s \t %d \t %s\n", name, state, priority, type);
		}
	}
	
	public Auction() { // for the server to create a new Auction
		users = new ArrayList<User>();
	}

	class ClientHandler implements Runnable {
		private BufferedReader reader;
		private PrintWriter writer;
		
		Socket sock;
		
		DataOutputStream send;
		
		public ClientHandler(Socket clientSocket) {
			sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				writer = new PrintWriter(sock.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Reads in messages from clients and notifies all clients of what was sent
		// I can use this to keep a history log of who's bid/bought what
		@SuppressWarnings("deprecation")
		public void run() {
			try {
				send = new DataOutputStream(sock.getOutputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					if (message.contains("NewBid")) {
						String substring = message.substring(message.indexOf(":")+1, message.indexOf("]"));
						String[] temp = substring.split("-");
						String itemIndex = temp[0];
						int index = Integer.parseInt(itemIndex);
						String updatedBid = temp[1];
						double bid = Double.parseDouble(updatedBid);
						Server.items.get(index).setFinalPrice(bid);		// updates item ArrayList on server
						Server.finalPrice.set(index, bid);
					}
					if (message.contains("NewWinner")) {
						String substring = message.substring(message.indexOf(":")+1, message.indexOf("]"));
						String[] temp = substring.split("-");
						String itemIndex = temp[0];
						int index = Integer.parseInt(itemIndex);
						String updatedWinner = temp[1];
						Server.items.get(index).setWinner(updatedWinner);		// updates item ArrayList on server
						Server.winner.set(index, updatedWinner);
					}
					if (message.contains("NewStatus")) {
						String substring = message.substring(message.indexOf(":")+1, message.indexOf("]"));
						String[] temp = substring.split("-");
						String itemIndex = temp[0];
						int index = Integer.parseInt(itemIndex);
						String updatedStatus = temp[1];
						Server.items.get(index).setStatus(updatedStatus);		// updates item ArrayList on server
						Server.status.set(index, updatedStatus);
					}
					System.out.println("server read "+message);
					setChanged();
					notifyObservers(message);
				}
			} catch (IOException e) {
				User leftUser = null;
				for(int u = 0; u < users.size(); u++) {
					if(users.get(u).sock.equals(this.sock)) {
						leftUser = users.get(u);
						users.remove(u);
					}
				}
				setChanged();
				notifyObservers("[LeaveUser:"+leftUser.username+"]");
			}
		}
	}
	
}
