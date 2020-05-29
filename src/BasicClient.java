//imports for network communication
import java.io.*;
import java.net.*;

public class BasicClient {

	final String LOCAL_HOST = "127.0.0.1";
	final int PORT = 5000;

	Socket clientSocket;      //client socket for connection
	ConnectionManager connectionManager;
	boolean running = true;   //program status

	public static void main (String[] args) {
		BasicClient client = new BasicClient();
		client.go();
	}

	public void go() {

		GUI gui = new GUI(this);
		Thread thread = new Thread(gui);
		thread.start();

		//create a socket (try-catch required) and attempt a connection to the local IP address
		System.out.println("Attempting to establish a connection ...");
		try {
			clientSocket = new Socket(LOCAL_HOST, PORT);    //create and bind a socket, and request connection
			connectionManager = new ConnectionManager(clientSocket);
			System.out.println("Connection to server established!");
		} catch (IOException e) {
			System.out.println("Connection to Server Failed");
			e.printStackTrace();
		}
		
		String[] msg;
		//wait for response from the server
		while(running) {
			try {
				msg = connectionManager.receive();

				if (msg[0].equals("Account") && msg[1].equals("loggedInTrue")) {
					System.out.println("Logged In Successfully");
					gui.logIn();
				}

			} catch (IOException e) {
				System.out.println("Failed to receive message from the server.");
				e.printStackTrace();
				running = false;
			}
		}

		//after completing the communication close all streams and sockets
		try {
			connectionManager.close();
		}catch (Exception e) {
			System.out.println("Failed to close stream or/and socket.");
		}
	}
	
	//login data:
	//[0]usernamepassword
	
	//[0] is length of username

	public void createAccount(String username, String password) {
		System.out.println(username);
		System.out.println(password);
		// send a request to server
		// once the server gets the request, build a user and add it to the database
		
		try {
			connectionManager.send("createAccount", username.length() + username + password);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void logIn(String username, String password) {
		try {
			connectionManager.send("loginAccount", username.length() + username + password);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
