
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public class MultiServer implements Runnable{

	// The server socket.
	private static ServerSocket serverSocket = null;
	private static ServerSocket serverSocket2 = null;
	// The client socket.
	private static Socket clientSocket = null;
	// The users currently online
	private static Socket nextServer = null;
	private static Socket previousServer = null;
	
	private static PrintStream os2 = null;
	
	private static BufferedReader is2 = null;
	
//	private static HashSet<String> names = new HashSet<>();
//	// Threads for clients available
//	private static final ArrayList<clientThread> threads = new ArrayList<clientThread>();
//
//	public static void join(String name)
//	{
//		threads.add(new clientThread(clientSocket, nextServer, threads, name, names));
//		threads.get(threads.size()-1).start();
//		names.add(name);
//	}
//	
	public static void joinResponse(boolean status, DataOutputStream os) throws IOException
	{
		if(status){
			os.writeBytes("Joined successfully!\n");
		}
		else{
			os.writeBytes("Choose another name!\n");
		}
	}
	
	public static void main(String args[]) throws UnknownHostException {

		System.out.println(InetAddress.getLocalHost().getHostAddress());
		
		try {
			serverSocket = new ServerSocket(6000);
			serverSocket2= new ServerSocket(6001);
			while(true){
				previousServer=serverSocket2.accept();
//				System.out.println("yes");
				if(previousServer.isConnected())
                {
                    BufferedReader br=new BufferedReader(new InputStreamReader(previousServer.getInputStream()));
                    String line=null;
                    while((line=br.readLine())!=null)
                        System.out.println(line);
                    break;
                }
			}
		} catch (IOException e) {
			System.out.println(e);
		}

		try {
			nextServer = new Socket("192.168.1.103", 6001);
			os2 = new PrintStream(nextServer.getOutputStream());
			is2 = new BufferedReader(new InputStreamReader(previousServer.getInputStream()));
			new Thread(new MultiServer()).start();
			os2.println("Hello!");
		} catch (IOException e) {
			System.out.println(e);
		}

		while (true) {
			try {
				clientSocket = serverSocket.accept();

				String intro = "Enter your name:\n";
				while(true){
					BufferedReader ic = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
					os.writeBytes(intro);
					String name = ic.readLine();
//					if(!names.contains(name))
//					{
//						join(name);
//						joinResponse(true, os);
//						break;
//					}
//					else{
//						joinResponse(false, os);
//					}
				}
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
	}

	@Override
	public void run() {
		
//		 String responseLine;
//		    try {
//		      while ((responseLine = is2.readLine()) != null) {
//		    	  StringTokenizer st=new StringTokenizer(responseLine,",");
//		    	  String destination=st.nextToken();
//		    	  String message=st.nextToken();
//		    	  if(names.contains(destination))
//		    	  {
//		    		  for(int i = 0; i < threads.size(); ++i)
//		    		  {
//		    			  if(threads.get(i).Name().equals(destination))
//		    				  threads.get(i).getOs().println(message);
//		    				  
//		    		  }
//		    	  }
//		    		  
//		      }
//		    } catch (IOException e) {
//		    }
		
	}
}