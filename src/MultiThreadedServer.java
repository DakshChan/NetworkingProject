//imports for network communication
import java.io.*;
import java.net.*;
import java.util.ArrayList;

class MultiStart {

	public static void main(String[] args) {
		Encryption.generateKeys();
		
		ArrayList<String> channels = new ArrayList<>();
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
	
	ArrayList<ConnectionManager> connectionManagers;
	
	ArrayList<String> channels;
	
	MultiThreadedServer(ArrayList<String> channels) {
		this.channels = channels;
	}

	public void start() {
		connectionManagers = new ArrayList<ConnectionManager>();
		
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
		public ConnectionManager connectionManager;

		public ConnectionHandler(Socket socket) {
			this.socket = socket;
			try {
				connectionManager = new ConnectionManager(socket);
				synchronized (connectionManagers) {
					connectionManagers.add(connectionManager);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String[] msg;
			
			boolean auth = false;
			while (!auth) {
				try {
					msg = connectionManager.receive();
					System.out.println(msg[0]);
					System.out.println(msg[1]);
					if (msg[0].equals("createAccount")) {
						int userNameLength = Integer.parseInt(msg[1].substring(0,1));
						msg[1] = msg[1].substring(1);
						String username = msg[1].substring(0,userNameLength);
						String password =  msg[1].substring(userNameLength);
						
						System.out.println(username);
						System.out.println(password);
						//create the account
						
						connectionManager.send("Account", "loggedInTrue");
					} else if (msg[0].equals("loginAccount")) {
						int userNameLength = Integer.parseInt(msg[1].substring(0,1));
						msg[1] = msg[1].substring(1);
						String username = msg[1].substring(0,userNameLength);
						String password =  msg[1].substring(userNameLength);
						
						System.out.println(username);
						System.out.println(password);
						//validate login
						
						connectionManager.send("Account", "loggedInTrue");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			getOld(); //send the client all the old messages
			
			try{
				while (connectionManager.alive()) {
					msg = connectionManager.receive();
					synchronized (connectionManagers) {
						for (ConnectionManager h: connectionManagers) {
							h.send(msg[0], msg[1]);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void getOld() {
			return;
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