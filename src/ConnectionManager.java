import java.io.*;
import java.net.Socket;

class ConnectionManager {
	private BufferedWriter out;
	private BufferedReader in;
	private Socket socket;
	private boolean alive;

	ConnectionManager(Socket socket) throws IOException {
		this.socket = socket;
		try {
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("created");
		} catch (IOException e) {
			throw e;
		}
		alive = true;
	}
	
//	//overload this method for different data types
//	public void sendOBJ (String triggerName, String dataType, String data) throws IOException {
//		//dunno
//
//		//send the string
//		out.write(data); //data just for testing
//		out.newLine();
//		out.flush();
//	}
	
	public void send (String triggerName, String data) throws IOException {
		//take data and convert to huffman
		
		//triggerName
		//huffman rep
		//padding bits
		//data
		
		//take huffman and encrypt into one line string
		
		//send the string
		
		//this sends only the message as a line to test sending
		out.write(data);
		out.newLine();
		out.flush();
	}
	
//	//OBJ arr where [0] is String triggerName, [1] is String dataType, [2] is OBJ data
//	public Object[] recieveOBJ () throws IOException {
//		//dunno
//		return new Object[]{null,null,null};
//	}
	
	//OBJ arr where [0] is String triggerName, [1] is String data
	public String[] receive() throws IOException {
		String msg = in.readLine();
		
		//unencrypt, unhuffman
		
		//this returns the message with no channel, just to test receiving
		return new String[]{"testChannel",msg};
	}
	
	public void close() throws IOException {
		out.close();
		in.close();
		socket.close();
		alive = false;
	}
	
	public boolean alive() {
		return this.alive;
	}
}
