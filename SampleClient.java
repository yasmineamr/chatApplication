import java.io.*;
import java.net.*;

public class SampleClient{
	
	public static void main(String[] args) throws IOException{
		
		String message;
		String reply;
		
		InetAddress addr = InetAddress.getByName("localhost"); //Insert IP Adress of Server machine. 
		Socket clientSocket = new Socket(addr, 6000);
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());  // to send the data to the server socket
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //receive responses from the server
		
		
		while(true)
		{	
			message = inFromUser.readLine();
			outToServer.writeBytes(message + '\n');
			if(message.equals("BYE")||message.equals("QUIT")) break;
			reply = inFromServer.readLine();
			System.out.println("FROM SERVER: " + reply);
			
		}
		
		clientSocket.close();
	
	}
	
}