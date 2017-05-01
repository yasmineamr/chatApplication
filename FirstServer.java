
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

public class FirstServer implements Runnable{

	// The server socket for connection with clients
	private static ServerSocket serverSocket1 = null;
	
	// The server socket for connection with the previous server
	private static ServerSocket serverSocket2 = null;
	
	// The client socket.
	private static Socket clientSocket = null;

	// Socket for connection with the next server
	private static Socket nextServer = null;
	
	// Socket for connection with the previous server
	private static Socket previousServer = null;

	// Output Stream for the next server
	private static PrintStream nextServerOutput = null;

	// Input Stream for the previous server
	private static BufferedReader prevServerInput = null;
	
	// The usernames of available members
	private static HashSet<String> names = new HashSet<>();
	
	// Threads for clients available
	private static final ArrayList<clientThread> threads = new ArrayList<clientThread>();
	
	public static void main(String args[]) throws UnknownHostException {

		System.out.println("The IP address of this server: "+InetAddress.getLocalHost().getHostAddress());

		try 
		{
			
			serverSocket2= new ServerSocket(6001);
			while(true)
			{
				previousServer=serverSocket2.accept();
				
				if(previousServer.isConnected())
				{
					prevServerInput = new BufferedReader(new InputStreamReader(previousServer.getInputStream()));
					//create thread to read input from the previous server
					new Thread(new FirstServer()).start();
					System.out.println("connected to previous server successfully !");
					break;
				}
			}
		}
		catch (IOException e) 
		{
			System.out.println(e);
		}

		try 
		{
			System.out.println("enter the IP address of the next server:");
			String ip=null;
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{
				String line=br.readLine();
				if(line!=null)
				{
					ip=line;
					break;
				}
			}
			while(true)
			{
				nextServer = new Socket(ip, 6001);
				if(nextServer.isConnected())
				{
					nextServerOutput = new PrintStream(nextServer.getOutputStream());
					System.out.println("connected to next server successfully !");
					break;
				}
			}
			
		} 
		catch (IOException e) 
		{
			System.out.println(e);
		}
		
		try 
		{
			serverSocket1 = new ServerSocket(6000);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (true) 
		{
			try 
			{
				clientSocket = serverSocket1.accept();

				while(true)
				{
					BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
					String name = is.readLine();
					if(!names.contains(name))
					{
						join(name);
						joinResponse(true, os);
						break;
					}
					else
					{
						joinResponse(false, os);
					}
				}
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
	}
	
	public static void removeName(String name,int counter)
	{
		counter--;
		if(counter!=0)
		{
			names.remove(name);
			nextServerOutput.println("removeName"+","+name+","+counter);
		}
	}
	
	public static void addName(String name,int counter)
	{
		counter--;
		if(counter!=0)
		{
			names.add(name);
			nextServerOutput.println("addName"+","+name+","+counter);
		}
	}
	
	public static void join(String name)
	{
		threads.add(new clientThread(clientSocket, nextServer, threads, name, names));
		threads.get(threads.size()-1).start();
		
		addName(name,5);
	}

	public static void joinResponse(boolean status, DataOutputStream os) throws IOException
	{
		if(status)
		{
			os.writeBytes("Joined successfully!\n");
		}
		else
		{
			os.writeBytes("This username is already used!\n");
		}
	}
	
	public static void Route(String source,String destination,int TTL,String message)
	{
		TTL--;
		if(TTL==0)
		{
			for(int i = 0; i < threads.size(); ++i)
			{
				if(threads.get(i).Name().equals(source))
					threads.get(i).getOs().println(	"Error: There is no member with username="+destination+"!");

			}
		}
		else
		{
			boolean found=false;
			for(int i = 0; i < threads.size(); ++i)
			{
				if(threads.get(i).Name().equals(destination))
				{
					threads.get(i).getOs().println(message);
					found=true;
				}
			}
			if(!found)
			{
				nextServerOutput.println("chat"+","+source+","+destination+","+TTL+","+message);
			}
		}
		
	}



	// Thread to read input from the previous server
	public void run() {

		String responseLine;
		try 
		{
			while ((responseLine = prevServerInput.readLine()) != null)
			{
				StringTokenizer st=new StringTokenizer(responseLine,",");
				String command=st.nextToken();
				if(command.equals("chat"))
				{
					String source=st.nextToken();
					String destination=st.nextToken();
					int TTL=Integer.parseInt(st.nextToken());
					String message=st.nextToken();
					
					Route(source,destination,TTL,message);
				}
				else if(command.equals("addName"))
				{
					String name=st.nextToken();
					int counter=Integer.parseInt(st.nextToken());
					
					addName(name, counter);;
				}
				else if(command.equals("removeName"))
				{
					String name=st.nextToken();
					int counter=Integer.parseInt(st.nextToken());
					
					removeName(name,counter);
				}


			}
		} catch (IOException e) 
		{
			System.err.println("IOException:  " + e);
		}

	}
}