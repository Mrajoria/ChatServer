package server_cli;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
	
	private int port;
	private DatagramSocket socket;
	private Thread runServer,manage,recieve,send;
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private boolean running = false;
	
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
	}
	
	private void manageClients() {
		manage = new Thread("manage") {
			public void run() {
				while(running) {
				// Manage;
			}
			}
			
	};
	manage.start();
	}
	
	
	private void recieve() {
		recieve = new Thread("recieve") {
			public void run() {
				while(running) {
					System.out.println("TOTAL CONNECTED CLIENTS "+clients.size());
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
		
		for(int i=0;i< clients.size();i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.address, client.port);
			System.out.println("sending to client "+client.name);
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
		if(string.startsWith("/c/")) {
			int id = UniqueIdentifier.getIdentifier();
	     	System.out.println("identifier "+id);
        	clients.add(new ServerClient(string.substring(3,string.length()), packet.getAddress(), packet.getPort(), id));
			System.out.println(string.substring(3, string.length()));
	     	System.out.println(clients.get(0).address+" "+clients.get(0).port);
	     	
	     	String SENDID = "/c/"+id;
	     	System.out.println("identifier  and its length "+id+" "+SENDID.length());

	     	send(SENDID, packet.getAddress(), packet.getPort());

		}
		else if(string.startsWith("/m/")) {
			sendToAll(string);
			System.out.println(string);
		}
		
		else if(string.startsWith("/d/")) {
			String ID = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(ID), true);
			
		}
		
	}
	
	private void disconnect(int id, boolean status) {
		ServerClient client = null;
		
		for (int x =0; x<clients.size();x++) {
			if(clients.get(x).getID() == id)
			{
			client = clients.get(x);
			clients.remove(x);
			break;
			}
		}
		
		String message = "";
		if(status) {
			message = "client "+ "( "+client.getID()+" ) @ "+client.address.toString()+ ":" +client.port+" disconnected";
		}
		else {
			message = "client "+ "( "+client.getID()+" ) @ "+client.address.toString()+ ":" +client.port+" timeout";
		}
		
		System.out.println(message);
		
	}
	

}
