
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public class clientThread extends Thread {

	private BufferedReader is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;
	private Socket nextServer = null;
	private final ArrayList<clientThread> threads;
	private String name;
	private HashSet<String> names;

	public clientThread(Socket clientSocket, Socket nextServer, ArrayList<clientThread> threads, String name, HashSet<String> names) 
	{
		this.clientSocket = clientSocket;
		this.threads = threads;
		this.nextServer = nextServer;
		this.name = name;
		this.names = names;
	}

	public void run() 
	{
		ArrayList<clientThread> threads = this.threads;

		try 
		{
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			os = new PrintStream(clientSocket.getOutputStream());

			String line;
			while (true)
			{
				line=is.readLine();
				
				if (line.equals("QUIT") || line.equals("BYE") ) {
					is.close();
					os.close();
					clientSocket.close();
					break;
				}
				else if(line.equals("getList"))
				{
					getMemberList();	
				}
				else if(line.equals("local_list"))
				{
					os.println("Local Clients(including me):");
					for(int i=0;i<threads.size();i++)
						os.println(threads.get(i).name);
				}
				else if(line.isEmpty())
				{
					os.println("Invalid Input!");
				}
				else
				{
					StringTokenizer st=new StringTokenizer(line, ":");
					String receiver=st.nextToken();
					String message="From "+this.name+": ";
					if(st.hasMoreTokens())
					{
						message+=st.nextToken();
						chat(this.name, receiver , 5 , message);
					}
					else
						os.println("Invalid Input!");
				}
			}

			for (int i = 0; i < threads.size(); i++) 
			{
				if (threads.get(i) == this)
				{
					PrintStream os= new PrintStream(nextServer.getOutputStream());
					os.println("removeName"+","+this.name+","+5);

					threads.remove(i);
				}
			}

		}
		catch (IOException e) 
		{
			System.err.println("IOException:  " + e);
		}

	}

	public void getMemberList()
	{
		memberListResponse();
	}

	public void memberListResponse()
	{
		if (names.size() == 1)
			os.println("No other users available to chat with!");
		else
		{
			os.println("Other available users:");
			for (String name: names) 
			{
				if(name!=this.name)
					os.println(name);
			}
		}
	}

	public void chat(String source, String destination, int TTL , String message) throws IOException 
	{
		TTL--;
		
		boolean found=false;
		for (int i = 0; i < threads.size(); i++) 
		{
			if (threads.get(i).name.equals(destination)) 
			{
				threads.get(i).os.println( message);
				found=true;
			}
		}
		if(!found)
		{
			PrintStream os= new PrintStream(nextServer.getOutputStream());
			os.println("chat"+","+source+","+destination+","+TTL+","+message);
		}

	}

	public String Name() 
	{
		return name;
	}

	public PrintStream getOs() 
	{
		return os;
	}

}
