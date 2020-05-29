import java.io.*;
import java.net.Socket;
import java.sql.SQLOutput;

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
			alive = true;
		} catch (IOException e) {
			throw e;
		}
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
		System.out.println(triggerName + " " + data);
		//encrypt string data
		//TODO: figuring out encryption
		
		String encrypted = data;
		
		//compress
		String compressed = HuffmanEncoder.encode(triggerName, data);
		
		//send
		out.write(compressed);
		out.write("\n");
		out.flush();
		
		System.out.println(compressed);
	}
	
//	//OBJ arr where [0] is String triggerName, [1] is String dataType, [2] is OBJ data
//	public Object[] recieveOBJ () throws IOException {
//		//dunno
//		return new Object[]{null,null,null};
//	}
	
	//OBJ arr where [0] is String triggerName, [1] is String data
	public String[] receive() throws IOException {
		String data = "";
		for (int i = 0; i < 4; i++) {
			data += in.readLine() + "\n";
		}
		
		System.out.println(data);
		
		//uncompress
		String[] decompressed = HuffmanDecoder.decode(data);
		String triggerName = decompressed[0];
		String encrypted = decompressed[1];
		
		//decryption message:
		//TODO: decryption stuff
		
		String message = encrypted;
		
		System.out.println(triggerName + " " + message);
		
		//this returns the message
		return new String[]{triggerName, message};
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
