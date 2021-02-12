package server_cli;

import java.io.IOException;
import java.net.DatagramSocket;
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
	
	private void process(DatagramPacket packet) {
		String string  = new  String(packet.getData());
		if(string.startsWith("/c/")) {
			int id = UniqueIdentifier.getIdentifier();
	     	System.out.println("identifier "+id);
        	clients.add(new ServerClient(string.substring(3,string.length()), packet.getAddress(), packet.getPort(), id));
			System.out.println(string.substring(3, string.length()));
	     	System.out.println(clients.get(0).address+" "+clients.get(0).port);

		}
		else
			System.out.println(string);
		
		
	}
	
	
	private void send() {
		
	}
		
}
