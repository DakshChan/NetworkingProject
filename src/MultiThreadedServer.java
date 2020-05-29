//imports for network communication
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class MultiStart {

	public static void main(String[] args) {
		//Encryption.generateKeys();
		
		ArrayList<String> channels = new ArrayList<>();
		//import channels
		
		File dir = new File(".");
		
		File [] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".channel");
			}
		});
		
		HashMap<String, String> accounts = new HashMap<>();
		
		try {
			BufferedReader a = new BufferedReader(new FileReader("acc.data"));
			String aline;
			while ((aline = a.readLine()) != null) {
				String p = a.readLine();
				accounts.put(aline, p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (File x : files) {
			channels.add(x.getName().substring(0,x.getName().lastIndexOf('.')));
		}
		
		ArrayList<File> fileArrayList = new ArrayList<File>(Arrays.asList(files));
		
		MultiThreadedServer server = new MultiThreadedServer(channels, fileArrayList, accounts);
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
	
	MultiThreadedServer.FileHandler fileHandler;
	
	HashMap<String, String> accounts;
	
	MultiThreadedServer(ArrayList<String> channels, ArrayList<File> files, HashMap<String, String> accounts) {
		this.channels = channels;
		this.fileHandler = new MultiThreadedServer.FileHandler(channels, files);
		this.accounts = accounts;
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
					System.out.println(msg[0]);
					System.out.println(msg[1]);
					if (msg[0].equals("createAccount")) {
						int userNameLength = Integer.parseInt(msg[1].substring(0,1));
						msg[1] = msg[1].substring(1);
						username = msg[1].substring(0,userNameLength);
						String password =  msg[1].substring(userNameLength);
						
						System.out.println(username);
						System.out.println(password);
						//create the account
						accounts.put(username, password);
						
						BufferedWriter w = new BufferedWriter(new FileWriter("acc.data", true));
						w.write(username);
						w.newLine();
						w.write(password);
						w.newLine();
						w.flush();
						w.close();
						
						connectionManager.send("Account", "loggedInTrue");
						auth = true;
					} else if (msg[0].equals("loginAccount")) {
						int userNameLength = Integer.parseInt(msg[1].substring(0,1));
						msg[1] = msg[1].substring(1);
						username = msg[1].substring(0,userNameLength);
						String password =  msg[1].substring(userNameLength);
						
						System.out.println(username);
						System.out.println(password);
						//validate login
						
						try {
							if (accounts.get(username).equals(password)) {
								connectionManager.send("Account", "loggedInTrue");
								auth = true;
							} else {
								connectionManager.send("Account", "wrongPassword");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
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
	
	class FileHandler extends Thread {
		ArrayList<File> files;
		ArrayList<BufferedWriter> writers;
		ArrayList<String> channels;
		
		FileHandler(ArrayList<String> channels, ArrayList<File> files) {
			this.channels = channels;
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
		
		public void append(String channelName, String data) {
			int i = channels.indexOf(channelName);
			if (i != -1) {
				BufferedWriter w = writers.get(i);
				synchronized (w) {
					try {
						w.write(data);
						w.write("\n");
						w.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		public synchronized ArrayList<String> readAll(String channelName) {
			int i = channels.indexOf(channelName);
			if (i != -1) {
				try {
					ArrayList<String> history = new ArrayList<String>();
					BufferedReader r = new BufferedReader(new FileReader(files.get(i).getAbsolutePath()));
					String line;
					while ((line = r.readLine()) != null) {
						history.add(line);
					}
					return history;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
}