import java.io.*;
import java.net.*;

public class SampleServer{
	
	public static void main(String[] args) throws IOException{
		
		String clientMessage;
		String reply;
		
		ServerSocket welcomeSocket = new ServerSocket(6000);
		
		Socket connectionSocket = welcomeSocket.accept();
		
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); //to receive inputs from the client
		BufferedReader serverReply = new BufferedReader(new InputStreamReader(System.in));
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream()); //to send data to the client
		
		
		while(true)
		{	
			clientMessage = inFromClient.readLine();
			System.out.println("FROM CLIENT: " +	clientMessage);
			if(clientMessage.equals("BYE")||clientMessage.equals("QUIT"))
			{
				System.out.println("Client Went Offline!");
				break;
			}
				
			reply = serverReply.readLine() + '\n';
			outToClient.writeBytes(reply);
			
		}
		
		connectionSocket.close();
		welcomeSocket.close();
		
		
	}
	
} 