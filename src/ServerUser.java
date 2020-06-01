import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ServerUser extends Thread implements Comparable{
	static FileHandler fileHandler;
	
	ConnectionManager connectionManager;
	String userName;
	String hashedPassword;
	boolean auth;
	ArrayList<String> friends;
	ArrayList<String> hashedServerID;
	
	
	@Override
	public int compareTo(Object o) {
		return userName.compareTo(((ServerUser) o).userName);
	}
	
	ServerUser (ConnectionManager connectionManager, String userName){
		this.connectionManager = connectionManager;
		this.userName = userName;
		load();
		this.auth = false;
	}
	
	ServerUser (ConnectionManager connectionManager, String userName, String hashedPassword) {
		this.connectionManager = connectionManager;
		this.userName = userName;
		this.hashedPassword = hashedPassword;
		this.friends = new ArrayList<String>();
		this.auth = true;
	}
	
	public boolean auth(String hashedPassword) {
		this.auth = this.hashedPassword.equals(hashedPassword);
		return auth;
	}
	
	public void save(){
		//TODO: save the user info
	}
	
	public void load(){
		//TODO: load the user's friends and hashed password from file
	}
	
	@Override
	public void run() {
		boolean active = true;
		String[] data = new String[2];
		while (active) {
			while (!auth && active) {
				try {
					data = connectionManager.receive();
				} catch (IOException e) {
					active = false;
					e.printStackTrace();
				}
				if (data[0].equals("createAccount")) {
					
					int userNameLength = Integer.parseInt(data[1].substring(0,1));
					data[1] = data[1].substring(1);
					this.userName = data[1].substring(0,userNameLength);
					String password = data[1].substring(userNameLength);
					
					try {
						this.hashedPassword = Hash.hashPassword(this.userName, password);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					save();
					
					try {
						connectionManager.send("Account", "loggedInTrue");
					} catch (IOException e) {
						active = false;
						e.printStackTrace();
					}
					auth = true;
					
				} else if (msg[0].equals("loginAccount")) {
					
					int userNameLength = Integer.parseInt(msg[1].substring(0,1));
					msg[1] = msg[1].substring(1);
					username = msg[1].substring(0,userNameLength);
					String password =  msg[1].substring(userNameLength);
					
					//validate login
					
					try {
						if (accounts.contains(new User(username, Hash.hashPassword(username, password)))) {
							System.out.println("sent response");
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
			
			while (auth && active) {
			
			}
		}
	}
}
