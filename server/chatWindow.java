package server;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;                  // fairly unknown to me
import javax.swing.text.DefaultCaret;          // unknown, I am seeing it for first time
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.JFrame;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.DatagramPacket;


public class chatWindow extends JFrame {

JPanel p;	
private JTextArea area;	
private JTextField textmessage;
JButton send;
JPanel cp1;            //child panel 1;
JPanel cp2;            //child panel 2;
GridBagConstraints gbc;
private String name;
private String adrs;
private int port;
private DefaultCaret caret;
private DatagramSocket socket;
private InetAddress ip;
private Thread sendt;
private int count = 0;


chatWindow(String name, String adrs, int port){
this.name = name;	
this.adrs = adrs;	
this.port = port;

boolean connect = OpenConnection(adrs);
if(!connect) {
	System.err.println("Conection failed");
	console("connection failed");
}

Showtextwindow();
console("Attempting connection with "+adrs+" at port: "+port+", User: "+name);
count++;
String connection = "/c/"+name;
send(connection.getBytes());
}

public void Showtextwindow() {
	
try {
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
}
catch(Exception e ) {
	e.printStackTrace();
}

gbc = new GridBagConstraints ();
cp1 = new JPanel();
Dimension size = new Dimension(800,400);
cp1.setPreferredSize(size);

area = new JTextArea();
area.setEditable(false);
JScrollPane scroll = new JScrollPane(area);
cp1.setLayout(new GridBagLayout());

gbc.fill = GridBagConstraints.BOTH;
gbc.gridx = 0;
gbc.gridy = 0;
gbc.weightx =1;
gbc.weighty =1;
Insets inset = new Insets(3,3,3,3);
gbc.insets = inset;
cp1.add(scroll,gbc);


gbc= new GridBagConstraints();

cp2 = new JPanel();
Dimension othersize  = new Dimension(800,30);
cp2.setPreferredSize(othersize);
textmessage = new JTextField();
cp2.setLayout(new GridBagLayout());
gbc.anchor = GridBagConstraints.NORTHWEST;
gbc.gridx =0;
gbc.gridy = 0;
gbc.fill = GridBagConstraints.BOTH;
gbc.weightx = 0.95;
gbc.weighty =1;
gbc.insets = inset;
cp2.add(textmessage,gbc);
textmessage.addKeyListener(new KeyAdapter() {
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			send(textmessage.getText());
			textmessage.setText("");
		}
	}
});


send = new JButton("send");
send.addMouseListener(new MouseAdapter() {
	public void mouseClicked(MouseEvent e) {
		send(textmessage.getText());
		textmessage.setText("");
	}
});

gbc.anchor = GridBagConstraints.NORTHEAST;
gbc.gridx =1;
gbc.gridy = 0;
gbc.weightx = 0.05;
gbc.weighty = 1;
gbc.insets = inset;
cp2.add(send,gbc);



p = new JPanel();
p.setBackground(Color.red);
p.setLayout(new GridBagLayout());

gbc = new GridBagConstraints();
gbc.gridx =0;
gbc.gridy =0;
gbc.weightx = 1;
gbc.weighty =1;
gbc.fill = GridBagConstraints.BOTH;
p.add(cp1,gbc);

gbc.gridx = 0;
gbc.gridy = 1;
gbc.weighty =0;
gbc.fill = GridBagConstraints.HORIZONTAL;
p.add(cp2,gbc);

setContentPane(p);
this.setVisible(true);
this.pack();
this.validate();
this.setDefaultCloseOperation(EXIT_ON_CLOSE);
this.setLocationRelativeTo(null);
}

public void send(String message) {
	if(message.equals("")) return;
	
	message = this.name+": "+message;
	area.append(message+"\n\r");
	area.setCaretPosition(area.getDocument().getLength());
	send(message.getBytes());
	textmessage.setText("");
}

private boolean OpenConnection(String address) {
	
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

private String receive() {
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

private void send(byte[] data) {
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


public void console(String message) {
	area.append(message+"\n\r");
}



}
