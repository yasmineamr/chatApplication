import java.io.*;
import java.net.*;

public class MultiClient implements Runnable {

  // The client socket
  private static Socket clientSocket;
  // The output stream
  private static PrintStream os;
  // The input stream
  private static BufferedReader is;

  private static BufferedReader inputLine;
  private static boolean closed = false;
  
  public static void main(String[] args) {

    // The default port.
    int portNo = 6000;
    // The default ip.
    String ip = "localhost";

    if (args.length >= 1) 
      ip = args[0];

    /*
     * Open a socket on a given ip and port. Open input and output streams.
     */
    try {
      clientSocket = new Socket(ip, portNo);
      inputLine = new BufferedReader(new InputStreamReader(System.in));
      os = new PrintStream(clientSocket.getOutputStream());
      is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch (UnknownHostException e) {
      System.err.println("Don't know about ip " + ip);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to the ip "
          + ip);
    }

    /*
     * If everything has been initialized then we want to write some data to the
     * socket we have opened a connection to on the port portNo.
     */
    if (clientSocket != null && os != null && is != null) {
      try {

        /* Create a thread to read from the server. */
        new Thread(new MultiClient()).start();
        while (!closed) {
          os.println(inputLine.readLine().trim());
        }
        /*
         * Close the output stream, close the input stream, close the socket.
         */
        os.close();
        is.close();
        clientSocket.close();
      } catch (IOException e) {
        System.err.println("IOException:  " + e);
      }
    }
  }

  public void run() {
    String responseLine;
    try {
      while ((responseLine = is.readLine()) != null) {
    	  if (responseLine.equals("QUIT"))
              break;
    	  System.out.println(responseLine);
        
      }
      closed = true;
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }
  
  
  
  
}