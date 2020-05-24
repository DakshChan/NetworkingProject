//imports for network communication
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

class BasicClient {
    final String LOCAL_HOST = "127.0.0.1";
    final int PORT = 5000;
    
    Socket clientSocket;      //client socket for connection
    ObjectInputStream input;     //reader for the input stream
    ObjectOutputStream output;       //writer for the output stream
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
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Connection to Server Failed");
            e.printStackTrace();
        }
        System.out.println("Connection to server established!");

        //wait for response from the server
        while(running) {
            try {

                if (input.available() > 0) {
                    Handler handler = (Handler) input.readObject();
                    System.out.println(handler.getName());
                    running = false;
                }


            } catch (IOException e) {
                System.out.println("Failed to receive message from the server.");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
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
