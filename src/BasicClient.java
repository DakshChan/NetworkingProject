//imports for network communication
import java.io.*;
import java.net.*;
import java.util.Scanner;

class BasicClient {
    final String LOCAL_HOST = "127.0.0.1";
    final int PORT = 5000;
    
    Socket clientSocket;      //client socket for connection
    BufferedReader input;     //reader for the input stream
    PrintWriter output;       //writer for the output stream
    boolean running = true;   //program status
    
    public static void main (String[] args) { 
        BasicClient client = new BasicClient();
        client.go();
    }

    public void go() { 
        //create a socket (try-catch required) and attempt a connection to the local IP address
        System.out.println("Attempting to establish a connection ...");
        try {
            clientSocket = new Socket(LOCAL_HOST, PORT);    //create and bind a socket, and request connection
            InputStreamReader stream = new InputStreamReader(clientSocket.getInputStream());
            input = new BufferedReader(stream);
            output = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Connection to Server Failed");
            e.printStackTrace();
        }
        System.out.println("Connection to server established!");

        //wait for response from the server
        while(running){
            try {

                Scanner in = new Scanner(System.in);

                if (input.ready()) {
                    String msg = input.readLine();
                    System.out.println(msg);

                    String info = in.nextLine();

                    if (msg.equals("Enter your password"))
                    {
                        try {
                            info = Hash.hashPassword(info);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    output.println(info);
                    output.flush();
                }

            } catch (IOException e) {
                System.out.println("Failed to receive message from the server.");
                e.printStackTrace();
            }
        }
        
        //after completing the communication close all streams and sockets
        try {  
            input.close();
            output.close();
            clientSocket.close();
        }catch (Exception e) { 
            System.out.println("Failed to close stream or/and socket.");
        }
    }
}
