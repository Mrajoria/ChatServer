package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
	
	
private DatagramSocket socket;
private InetAddress ip;
private Thread sendt;
private int ID = -1;


private String name;
private String address;
private int port;
	
    Client(String name ,String address ,int port){
	this.name = name;
	this.address = address;
	this.port = port;
}

public boolean OpenConnection(String address) {
	
	try {
		socket = new DatagramSocket();
		ip = InetAddress.getByName(address);
	} catch (SocketException e) {
		e.printStackTrace();
		return false;
	} catch (UnknownHostException e) {
		e.printStackTrace();
		return false;
	}
	return true;
}
	

public String receive() {
	byte[] data = new byte[1024];
	DatagramPacket packet = new DatagramPacket(data, data.length);
	
	try {
		socket.receive(packet);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	String message = new String(packet.getData());
	return message;
}

public void send(byte[] data) {
	sendt = new Thread("send") {
		public void run() {
			DatagramPacket packet = new DatagramPacket(data, data.length,ip, port);
			try {
			socket.send(packet);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	};
	sendt.start();
}

public void close() {
	new Thread() {
		public void  run() {
	synchronized(socket){
		socket.close();
	}
	}
	}.start();
}

public String getName() {
	return this.name;
}

public String getAddress() {
	return this.address;
}

public int getPort() {
	return this.port;
}

public void setID(int ID) {
	this.ID = ID;
}

public int getID() {
	return this.ID;
}

	
}


