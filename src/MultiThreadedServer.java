//imports for network communication
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

class MultiThreadedServer {
	public static void main(String[] args) {
		MultiThreadedServer server = new MultiThreadedServer();
		//GUI HERE
	}
	
	final int PORT = 5000;

	ServerSocket serverSocket;
	
	ArrayList<ConnectionManager> connectionManagers;
	
	ArrayList<String> channels;
	
	MultiThreadedServer.FileHandler fileHandler;
	
	ArrayList<User> accounts;
	
	
	
	MultiThreadedServer() {
		Encryption.generateKeys();
		
		this.channels = new ArrayList<>();
		//import channels
		
		File dir = new File(".");
		
		File [] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".channel");
			}
		});
		
		//HashMap<String, String> accounts = new HashMap<>();
		this.accounts = new ArrayList<>();
		
		try {
			BufferedReader a = new BufferedReader(new FileReader("acc.data"));
			String aline;
			while ((aline = a.readLine()) != null) {
				String[] user = aline.split(":");
				String username = user[0];
				String password = user[1];
				accounts.add(new User(username, password));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (File x : files) {
			channels.add(x.getName().substring(0,x.getName().lastIndexOf('.')));
		}
		
		ArrayList<File> fileArrayList = new ArrayList<File>(Arrays.asList(files));
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
			String username = "";
			
			boolean auth = false;
			try {
				while (!auth) {
					msg = connectionManager.receive();

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (String s : channels) {
				try {
					connectionManager.send("channelInfo", s);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			getOld(); //send the client all the old messages
			
			try{
				while (connectionManager.alive()) {
					msg = connectionManager.receive();

					System.out.println(msg[0]);

					if (msg[0].equals("addFriend")) {
						int index = accounts.indexOf(new User(msg[1]));
						System.out.println("index: " + index);
						if (index != -1) {
							System.out.println("Found");
							User friend = accounts.get(index);
							Chat chat = new Chat(friend.getName());
							connectionManager.send("addFriend", chat.id + "\0" + chat.name);
						} else {
							System.out.println("Not found");
							connectionManager.send("addFriend", "friendNotFound");
						}

					} else if (msg[0].equals("sendMessage")) {
						//System.out.println(msg[1]);
						String[] data = msg[1].split("\0");
						String id = data[0];
						String message = data[1];
						Chat chat = Chat.chats.get(Chat.chats.indexOf(new Chat(id)));
						for (User users : chat.users) {

							synchronized (connectionManagers) {
								for (ConnectionManager h: connectionManagers) {
									h.send(msg[0], msg[1]);
								}
							}

						}
					} else if (msg[0].equals("getKeys")) {
						connectionManager.send("getKeys", Encryption.n + "\0" + Encryption.g);
						connectionManager.send("createKey", Encryption.partialKey.toString());
					} else if (msg[0].equals("createKey")) {
						Encryption.createSharedKey(Integer.parseInt(msg[1]));
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void getOld() {
			for(String c : channels) {
				ArrayList<String> lines = fileHandler.readAll(c);
				for(String line : lines) {
					try {
						connectionManager.send(c, line);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}