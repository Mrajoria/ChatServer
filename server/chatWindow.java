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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.DatagramPacket;


public class chatWindow extends JFrame implements Runnable {

JPanel p;	
private JTextArea area;	
private JTextField textmessage;
JButton send;
JPanel cp1;            //child panel 1;
JPanel cp2;            //child panel 2;
GridBagConstraints gbc;
private DefaultCaret caret;

private volatile boolean running = false;
Thread run;
Thread listen ;

Client client;

chatWindow(String name, String adrs, int port){

client = new Client(name,adrs,port);

boolean connect = client.OpenConnection(adrs);
if(!connect) {
	System.err.println("Conection failed");
	console("connection failed");
}
 running = true;
 
 run = new Thread(this, "Running");
 run.start();
 Showtextwindow();
console("Attempting connection with "+adrs+" at port: "+port+", User: "+name);

String connection = "/c/"+name;
client.send(connection.getBytes());
}

public void Showtextwindow() {
	
try {
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
}
catch(Exception e ) {
	e.printStackTrace();
}
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
			send(textmessage.getText(), true);
			textmessage.setText("");
		}
	}
});


send = new JButton("send");
send.addMouseListener(new MouseAdapter() {
	public void mouseClicked(MouseEvent e) {
		send(textmessage.getText(), true);
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

addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
		String disconnect = "/d/" +client.getID()+"/e/";
		send(disconnect, false);	
		running = false;
	    client.close();
	    
		}
});


setContentPane(p);
this.setVisible(true);
this.setTitle("Chat Window");
this.pack();
this.validate();
this.setLocationRelativeTo(null);
}

public void run() {
	listen();
}

public void send(String message, boolean text) {
	if(text == true) {
	if(message.equals("")) return;
	
	message = client.getName()+": "+message;
	message = "/m/"+message+"/e/";

	}
	client.send(message.getBytes());
	area.setCaretPosition(area.getDocument().getLength());

}

public void listen() {
	listen = new Thread("listen") {
		public void run() {
			while(running) {
				
				String message = client.receive();
				
				if(message.startsWith("/c/")) {
					System.out.println(message.length());
					client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));      // learn regex and string handling;   
					console("Succesfully connected to server, ID: "+client.getID());
				}
				else if(message.startsWith("/m/")) {
					String text = message.substring(3);
					text = text.split("/e/")[0];
					console(text);
					System.out.println(text);
				}
				else if (message.startsWith("/i/")) {
					String text = "/i/"+client.getID()+ "/e/";
					send(text,false);
				}
			}
		}
	}; listen.start(); 
}


public void console(String message) {
	area.append(message+"\n\r");
	area.setCaretPosition(area.getDocument().getLength());

}



}
