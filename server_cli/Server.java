package server_cli;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
public class Server implements Runnable {
	
	private int port;
	private DatagramSocket socket;
	private Thread runServer,manage,recieve,send;
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
					System.out.println(string);
					
				}
			}
		}; recieve.start();
		
	}
	
	private void send() {
		
	}
		
}
