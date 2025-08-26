package com.commander4j.network;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.commander4j.gui.JImageIconLoader;
import com.commander4j.gui.SocketClientGUI;
import com.commander4j.gui.SocketServerGUI;


public class SocketTest extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;

	public static SocketClientGUI client;
	public static SocketServerGUI server;
	public static String version = "5.74";
	public static final JImageIconLoader imageIconloader = new JImageIconLoader();
	public ImageIcon logo = imageIconloader.getImageIcon("logo.gif");
	public ImageIcon ball = imageIconloader.getImageIcon("ball.gif");


	/** Creates a new instance of SocketTest */
	public SocketTest() {
		setResizable(false);
		Container cp = getContentPane();

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		client = new SocketClientGUI(this);
		server = new SocketServerGUI(this);

		tabbedPane.addTab("Client", ball, (Component) client, "Test any server");
		tabbedPane.addTab("Server", ball, server, "Test any client");

		tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		cp.add(tabbedPane);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		
		Util util = new Util();
		
		util.getIPAddresses();
		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		util.setLookAndFeel("Nimbus");

		SocketTest st = new SocketTest();
		st.setTitle("SocketTest v"+version);
		st.setSize(1030, 750);
		
		
		GraphicsDevice gd = Util.getGraphicsDevice();
		
		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		Rectangle screenBounds = gc.getBounds();

		st.setBounds(screenBounds.x + ((screenBounds.width - st.getWidth()) / 2), screenBounds.y + ((screenBounds.height - st.getHeight()) / 2), st.getWidth(), st.getHeight());

		
		st.setDefaultCloseOperation(EXIT_ON_CLOSE);
		st.setIconImage(st.logo.getImage());

		st.setVisible(true);
	}

}
