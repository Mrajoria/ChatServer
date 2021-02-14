package server;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JFrame {
	
JLabel UserName;
JLabel ip;
JLabel port;
JPanel p;
JButton loginb;

JTextField name;
JTextField ipfield;
JTextField portfield;
JPanel cp1;
JPanel cp2;
JPanel cp3;

String u_name;
String ipaddrs;
int port_num;
	
Login(){

Dimension childSize = new Dimension(150,100);
UserName = new JLabel("        UserName        ");
name = new JTextField();
cp1 = new JPanel();
cp1.setLayout(new GridBagLayout());
GridBagConstraints gbc1 = new GridBagConstraints();
cp1.setPreferredSize(childSize);
gbc1.gridx =0;
gbc1.gridy =0;
cp1.add(UserName,gbc1);
gbc1.gridx =0;
gbc1.gridy =1;
gbc1.fill = GridBagConstraints.HORIZONTAL;
cp1.add(name,gbc1);


ip = new JLabel("    IP Address   ");
ipfield = new JTextField();
cp2 = new JPanel();
cp2.setPreferredSize(childSize);
cp2.setLayout(new GridBagLayout());
GridBagConstraints gbc2 = new GridBagConstraints();
gbc2.gridx =0;
gbc2.gridy =0;
cp2.add(ip,gbc2);
gbc2.gridx =0;
gbc2.gridy =1;
gbc2.fill = GridBagConstraints.HORIZONTAL;
cp2.add(ipfield,gbc2);



port = new JLabel("  Port  ");
portfield = new JTextField("");
cp3 = new JPanel();
cp3.setLayout(new GridBagLayout());
cp3.setPreferredSize(childSize);
GridBagConstraints gbc3 = new GridBagConstraints();
gbc3.gridx =0;
gbc3.gridy =0;
cp3.add(port,gbc3);
gbc3.gridx =0;
gbc3.gridy =1;
gbc3.fill = GridBagConstraints.HORIZONTAL;

cp3.add(portfield,gbc3);

p = new JPanel();
p.setLayout(new GridBagLayout());

GridBagConstraints gbc = new GridBagConstraints();

gbc.gridx = 0;
gbc.gridy =0;
gbc.weightx = 1;
gbc.weighty =0.3;
p.add(cp1,gbc);


gbc.gridx = 0;
gbc.gridy =1;
gbc.weightx = 1;
gbc.weighty =0.3;
p.add(cp2,gbc);

gbc.gridx = 0;
gbc.gridy =2;
gbc.weightx = 1;
gbc.weighty =0.3;
p.add(cp3,gbc);

loginb = new JButton("Login");
loginb.addMouseListener(new MouseAdapter() {
	public void mouseClicked(MouseEvent e) {
		u_name = name.getText();
		ipaddrs = ipfield.getText();
		port_num = Integer.parseInt(portfield.getText());
		
		login(u_name,ipaddrs,port_num);
		
	}
});

gbc.gridx = 0;
gbc.gridy =3;
gbc.weightx = 1;
gbc.weighty =1;;
p.add(loginb,gbc);



Dimension size = new Dimension(300,400);
p.setPreferredSize(size);

this.setTitle("Login");
this.add(p);
this.pack();
this.setVisible(true);
this.setLocationRelativeTo(null);
this.setResizable(false);

}

public void login(String name1, String ip1, int port1) {
	System.out.println(name1+" "+ip1+" "+port1);
	this.dispose();
	new chatWindow(name1, ip1,port1);	
	
}



public static void main(String args[])
{
	SwingUtilities.invokeLater(new Thread() {
		public void run() {
		new Login();
		}
	});
}

}
