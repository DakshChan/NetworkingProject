//imports for network communication
import java.io.*;
import java.net.*;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

class MultiThreadedServer {
	final int PORT = 5000;

	ServerSocket serverSocket;//server socket for connection
	int clientCounter = 0;
	
	ArrayList<Handler> handlers;

	public static void main(String[] args) {
		MultiThreadedServer server = new MultiThreadedServer();
		Encryption.generateKeys();
		
		server.go();
	}

	public void go() {
		handlers = new ArrayList<Handler>();
		
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
		public Handler handler;

		public ConnectionHandler(Socket socket) {
			this.socket = socket;
			handler = new Handler(socket);
			synchronized (handlers) {
				handlers.add(handler);
			}
		}

		public void run() {
			
			String[] msg;
			try{
				while ((msg = handler.recieve()) != null) {
					if (msg[0].equals("account")) {
						//do account stuff
					} else {
						synchronized (handlers) {
							for (Handler h: handlers) {
								h.send(msg[0], msg[1]);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}