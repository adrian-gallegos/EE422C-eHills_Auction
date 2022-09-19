/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* Adrian Gallegos
* ag76424
* 17360
* Spring 2022
*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class User {
	
	public String username;
	public Socket sock;
	public DataOutputStream send;
	public DataInputStream receive;
	
	public User(String u, Socket s) throws IOException {
		username = u;
		sock = s;
		send = new DataOutputStream(s.getOutputStream());
		receive = new DataInputStream(s.getInputStream());
	}
	
	public String toString() {
		return username;
	}
	
}
