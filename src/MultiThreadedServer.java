//imports for network communication
import java.io.*;
import java.net.*;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

class MultiThreadedServer {
	final String LOCAL_HOST = "127.0.0.1";
	final int PORT = 5000;

	ServerSocket serverSocket;//server socket for connection
	int clientCounter = 0;

	public static void main(String[] args) {
		MultiThreadedServer server = new MultiThreadedServer();
		server.go();
	}

	public void go() {
		//create a socket with the local IP address (try-catch required) and wait for connection request
		System.out.println("Waiting for a connection request from a client ...");
		try {
			serverSocket = new ServerSocket(PORT);          //create and bind a socket
			while(true) {
				Socket socket = serverSocket.accept();      //wait for connection request
				clientCounter = clientCounter + 1;
				System.out.println("Client " + clientCounter + " connected");
				Thread connectionThread = new Thread(new ConnectionHandler(socket));
				connectionThread.start();                   //start a new thread to handle the connection
			}
		} catch(Exception e) {
			System.out.println("Error accepting connection");
			e.printStackTrace();
		}
	}

	//------------------------------------------------------------------------------
	class ConnectionHandler extends Thread {
		Socket socket;            //socket to handle
		ObjectOutputStream output;
		ObjectInputStream input;

		public ConnectionHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				//InputStreamReader stream = new InputStreamReader(socket.getInputStream());
				output = new ObjectOutputStream(socket.getOutputStream());
				input = new ObjectInputStream(socket.getInputStream());
			} catch(IOException e) {
				e.printStackTrace();
			}

			try {
				Handler handler = new Handler("username", "Enter your username: ");
				output.writeObject(handler);
				output.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

			//after completing the communication close all streams but do not close the socket
			try {
				input.close();
				output.close();
			}catch (Exception e) {
				System.out.println("Failed to close a stream.");
			}
		}
	}
}