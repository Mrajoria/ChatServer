package server_cli;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable {
	
	private int port;
	private DatagramSocket socket;
	private Thread runServer,manage,recieve,send;
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private List<Integer> clientResponse = new ArrayList<Integer>();
	private boolean running = false;
	private final int MAX_ATTEMPTS =5;
	private boolean raw = false;
	
	public Server(int port) {
		this.port = port;
		try {
		socket = new DatagramSocket(port);
		}
		catch(IOException e) {
			e.printStackTrace();
			return;
		}
		runServer = new Thread(this,"server");
		runServer.start();
	}

	@Override
	public void run() {
	running = true;
	System.out.println("Server listening on port: "+port);
	manageClients();
	recieve();
	Scanner scanner = new Scanner(System.in);
	while(running) {
		String text = scanner.nextLine();
	if(!text.startsWith("/")) {
		sendToAll("/m/Server: "+text+"/e/");
		continue;
	}
	text = text.substring(1);
	if(text.equals("raw")) {
		raw = !raw;
	}
	else if(text.contains("clients")) {
		System.out.println("clietns: ");
		System.out.println("------------------");
		for(int x =0;x< clients.size();x++) {
			ServerClient c = clients.get(x);
			System.out.println(c.name+" (" +c.getID()+" ):"+c.address.toString()+":"+c.port);
		}
		System.out.println("-------------------");
	}
	else if(text.startsWith("kick")) {
		String name =  text.split(" ")[1];
		int id = -1;
		boolean number = true;
		try {
			id = Integer.parseInt(name);
		   }
		catch(NumberFormatException e) {
			number = false;
		}
		if(number) {
			boolean exists = false;
			ServerClient c = null;
			for(int i=0; i<clients.size();i++) {
				 c = clients.get(i);
				if(c.getID() == id) {
					exists = true;
					break;
				}
				
			}
			if(exists)  {
				closeClientRemotely(c.address, c.port);
				disconnect(id,true);
			}
			else System.out.println("Cliend "+id+ " doesnt exist! Check ID numebr");
			
		}
		else {
			for(int i=0;i<clients.size();i++) {
				ServerClient c = clients.get(i);
				if(c.name.equals(name)) {
					closeClientRemotely(c.address, c.port);
					disconnect(c.getID(), true);
					break;
				}
			}
		}
	}
	
	}
	}
	
	private void manageClients() {
		manage = new Thread("manage") {
			public void run() {
				while(running) {
					sendToAll("/i/server");

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.out.println("exception");
					}
				for(int i=0;i<clients.size();i++) {
					ServerClient c = clients.get(i);
					if(!clientResponse.contains(c.getID())){
						if(c.attempt >=MAX_ATTEMPTS) {
							disconnect(c.getID(), false);
						}
						else {
							c.attempt++;
						}
					}
					else {
						Object o = c.getID();
						clientResponse.remove(o);
						c.attempt = 0;
					}
				}
			}
			}
			
	};
	manage.start();
	}
	
	
	private void recieve() {
		recieve = new Thread("recieve") {
			public void run() {
				while(running) {
					
					// Recieve data;
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
						}
					catch(IOException e) {
						e.printStackTrace();
					}
					String string = new String(packet.getData());
					process(packet);
			     //	clients.add( new ServerClient("localman", packet.getAddress(),packet.getPort(), 50));
			    // 	System.out.println(clients.get(0).address+" "+clients.get(0).port);
			   //  	System.out.println(string);
				}
			}
		}; recieve.start();
		
	}
	
	private void sendToAll(String message) {
		if(message.startsWith("/m/")) {
			String text = message.substring(3);
			text = text.split("/e/")[0];
			System.out.println(message);
		}
		for(int i=0;i< clients.size();i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.address, client.port);
		//	System.out.println("sending to client "+client.name);
		}
	}
	
	private void send(String message, InetAddress address, int port) {
		 message = message+"/e/";
		 send(message.getBytes(), address, port);
	}
	
	private void send(byte[] data, InetAddress adress,  int port) {
		send = new Thread("send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length,adress, port);
				try {
				socket.send(packet);
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}; send.start();
		
	}
		
	
	private void process(DatagramPacket packet) {
		String string  = new  String(packet.getData());
		if(raw) System.out.println(string);
		if(string.startsWith("/c/")) {
			int id = UniqueIdentifier.getIdentifier();
	    // 	System.out.println("identifier "+id);
			String name = string.split("/c/|/e/")[1];
        	clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id));
			
	     	String SENDID = "/c/"+id;
	     
	     	send(SENDID, packet.getAddress(), packet.getPort());

		}
		else if(string.startsWith("/m/")) {
			sendToAll(string);
			System.out.println(string);
		}
		
		else if(string.startsWith("/d/")) {
			String ID = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(ID),true);
		}
		
		else if(string.startsWith("/i/")) {
			clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
		}
		
		else {
			
		}
		
	}
	//------------------------------------------ //
	// I will be implementing a sort             //
	// of experimental function here to remotely // 
	// close the connection. Well we will just   //
	// send a /closeRemote/ packet               //
	// and client's receive thread will process  //
	// the packet and call socket.close          //
	//-------------------------------------------//
	
//-----------------------------------------------------------begins here-------------//	
	private void closeClientRemotely(InetAddress a, int port) {
		
	String closePacket = "/x/";
	send(closePacket,a,port);
		
	}

//------------------------------------------------------------ends here-------------//
	
	private void disconnect(int id, boolean status) {
		ServerClient client = null;
		boolean existed = false;
		for (int x =0; x<clients.size();x++) {
			if(clients.get(x).getID() == id)
			{
			client = clients.get(x);
			clients.remove(x);
			existed = true;
			break;
			}
		}
		if(existed == false) return;
		String message = "";
		if(status) {
			message = "client "+ "( "+client.getID()+" ) @ "+client.address.toString()+ ":" +client.port+" disconnected";
		}
		else {
			message = "client "+ "( "+client.getID()+" ) @ "+client.address.toString()+ ":" +client.port+" timeout";
		}
		
		System.out.println(message);
		System.out.println("TOTAL CONNECTED CLIENTS "+clients.size());

	}
	

}
