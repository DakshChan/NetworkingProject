//imports for network communication
import java.io.*;
import java.net.*;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

class MultiStart {
	public static void main(String[] args) {
		Encryption.generateKeys();
		
		ArrayList<String> channels = new ArrayList<String>();
		//import channels
		
		File dir = new File(".");
		
		File [] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".channel");
			}
		});
		
		for (File x : files) {
			channels.add(x.getName().substring(0,x.getName().lastIndexOf('.')));
		}
		
		
		//MultiThreadedServer.FileHandler fileHandler = new MultiThreadedServer.FileHandler(files);
		
		MultiThreadedServer server = new MultiThreadedServer(channels);
		server.start();
		
		//UI Code here
		
	}
}

class MultiThreadedServer extends Thread {
	final int PORT = 5000;

	ServerSocket serverSocket;//server socket for connection
	int clientCounter = 0;
	
	ArrayList<Handler> handlers;
	
	ArrayList<String> channels;
	
	MultiThreadedServer(ArrayList<String> channels) {
		this.channels = channels;
	}

	public void start() {
		handlers = new ArrayList<Handler>();
		
		//create a socket with the local IP address (try-catch required) and wait for connection request
		
		try {
			serverSocket = new ServerSocket(PORT);          //create and bind a socket
			while(true) {
				System.out.println("Waiting for a connection request from a client ...");
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
		
		private void getOld() {
		
		}
	}
	
	class FileHandler extends Thread {
		ArrayList<File> files;
		ArrayList<BufferedWriter> writers;
		
		FileHandler(ArrayList<File> files) {
			this.files = files;
		}
		
		@Override
		public void start() {
			for (File f: files) {
				try {
					writers.add(new BufferedWriter(new FileWriter(f.getAbsolutePath(), true)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
}