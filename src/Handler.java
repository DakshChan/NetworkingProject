import java.io.*;
import java.net.Socket;

class Handler{
	private BufferedWriter out;
	private BufferedReader in;

	Handler(Socket socket) {
		try {
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("created");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//overload this method for different data types
	public void sendOBJ (String triggerName, String dataType, String data) throws IOException {
		//dunno
		
		//send the string
		out.write(data); //data just for testing
		out.newLine();
		out.flush();
	}
	
	//overload this method for different data types
	public void send (String triggerName, String data) throws IOException {
		//take data and convert to huffman
		
		//triggerName
		//huffman rep
		//padding bits
		//data
		
		//take huffman and encrypt into one line string
		
		//send the string
		out.write(data); //data just for testing
		out.newLine();
		out.flush();
	}
	
	//OBJ arr where [0] is String triggerName, [1] is String dataType, [2] is OBJ data
	public Object[] recieveOBJ (String data) throws IOException {
		//dunno
		return new Object[]{null,null,data};
	}
	
	//OBJ arr where [0] is String triggerName, [1] is String data
	public String[] recieve (String data) throws IOException {
		//dunno
		return new String[]{null,data};
	}
}
