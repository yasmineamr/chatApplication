import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class ClientGUI extends JFrame {

	private JPanel contentPane;
	private JTextField name;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					ClientGUI frame = new ClientGUI();
					frame.setVisible(true);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	JLabel lblentername;
	JButton btnJoin;
	JLabel error1;
	JButton btnSend;
	JTextArea message;
	JTextArea form;
	JTextArea area;
	// The client socket
	private Socket clientSocket;
	// The output stream
	private DataOutputStream os;
	// The input stream
	private BufferedReader is;

	boolean first = true;
	private JButton btnQuit;
	private JScrollPane scrollPaneArea;
	private JScrollPane scrollPaneMsg;
	private JButton btnGetList;
	private JButton btnLocalList;
	
	class Read implements Runnable {
		  public void run() {
			    String responseLine;
			    try {
			    	while(true) 
			    	{
			    	  responseLine = is.readLine();
			    	  if(responseLine==null) break;
			    	  if(first)
			    	  {
			    		  if(responseLine.equals("This username is already used!"))
							{
								error1.setVisible(true);
								name.setText("");
							}
							else
							{
								lblentername.setVisible(false);
								name.setVisible(false);
								btnJoin.setVisible(false);
								error1.setVisible(false);
								
								btnQuit.setVisible(true);
								btnSend.setVisible(true);
								message.setVisible(true);
								area.setVisible(true);
								form.setVisible(true);
								scrollPaneMsg.setVisible(true);
								scrollPaneArea.setVisible(true);
								btnGetList.setVisible(true);
								btnLocalList.setVisible(true);
								first = false;
							}
			    	  }
			    	  else
			    	  {
			    		  area.append(responseLine+"\n");
			    		  area.repaint();
			    		  area.revalidate();
			    	  }
			      }
			    }
			    catch (IOException e) 
			    {
			      System.err.println("IOException:  " + e);
			    }
			  }
	}
	
	
	public ClientGUI() 
	{
		try 
		{
			clientSocket = new Socket("localhost", 6000);
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  //receive data from server
			os = new DataOutputStream(clientSocket.getOutputStream()); //send data to the server

			Thread r = new Thread(new Read());
			r.start();
		}
		catch (UnknownHostException ex) 
		{
			System.err.println("Don't know about ip");
		}
		catch (IOException ex){}
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 624, 468);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblentername = new JLabel("Please Enter Your Name:");
		lblentername.setBounds(35, 79, 171, 16);
		contentPane.add(lblentername);

		name = new JTextField();
		name.setBounds(35, 136, 218, 26);
		contentPane.add(name);
		name.setColumns(10);

		btnJoin = new JButton("JOIN");
		btnJoin.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					os.writeBytes(name.getText()+"\n");
				} 
				catch (IOException e2) 
				{
					e2.printStackTrace();
				}
				
			}
		});
		btnJoin.setBounds(190, 209, 117, 29);
		contentPane.add(btnJoin);

		error1 = new JLabel("Choose Another Name");
		error1.setForeground(Color.RED);
		error1.setBounds(45, 174, 150, 16);
		contentPane.add(error1);
		error1.setVisible(false);
		
		btnSend = new JButton("SEND");
		btnSend.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if(message.getText().length()>0)
				{
					area.append(message.getText()+"\n");
					area.repaint();
					area.revalidate();
					try 
					{
						os.writeBytes(message.getText()+"\n");
					} 
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
					message.setText("");
				}
			}
		});
		btnSend.setBounds(492, 391, 105, 29);
		contentPane.add(btnSend);
		
		btnQuit = new JButton("QUIT");
		btnQuit.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					os.writeBytes("QUIT\n");
					dispose();
		            System.exit(0);
				}
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		btnQuit.setBounds(492, 14, 105, 29);
		contentPane.add(btnQuit);
		btnQuit.setVisible(false);
		
		btnSend.setVisible(false);
		
		form = new JTextArea();
		form.setBackground(UIManager.getColor("Button.background"));
		form.setForeground(Color.BLACK);
		form.setText("Enter your \nmessage in the \nfollowing format:\nUsername: Message");
		form.setEditable(false);
		form.setBounds(483, 79, 124, 103);
		contentPane.add(form);
		form.setColumns(10);
		form.setVisible(false);		
		
		area = new JTextArea();
		area.setEditable(false);
		area.setColumns(10);
		area.setVisible(false);
		
		scrollPaneArea = new JScrollPane();
		scrollPaneArea.setBounds(10, 19, 452, 353);
		scrollPaneArea.setViewportView(area);
		contentPane.add(scrollPaneArea);
		scrollPaneArea.setVisible(false);
		
		message = new JTextArea();
		message.setColumns(10);
		message.setVisible(false);
		
		scrollPaneMsg = new JScrollPane();
		scrollPaneMsg.setBounds(6, 384, 456, 36);
		scrollPaneMsg.setViewportView(message);
		contentPane.add(scrollPaneMsg);
		scrollPaneMsg.setVisible(false);
		
		btnGetList = new JButton("Get List");
		btnGetList.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					os.writeBytes("getList\n");
				}
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		btnGetList.setBounds(492, 350, 105, 29);
		btnGetList.setVisible(false);
		contentPane.add(btnGetList);
		
		btnLocalList = new JButton("Local List");
		btnLocalList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					os.writeBytes("local_list\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnLocalList.setBounds(492, 309, 105, 29);
		btnLocalList.setVisible(false);
		contentPane.add(btnLocalList);
	}
}





