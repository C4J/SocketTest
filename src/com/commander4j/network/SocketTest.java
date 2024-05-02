package com.commander4j.network;

import java.awt.Component; 
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

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
	public static String version = "5.04";
	public static final JImageIconLoader imageIconloader = new JImageIconLoader();
	public ImageIcon logo = imageIconloader.getImageIcon("logo.gif");
	public ImageIcon ball = imageIconloader.getImageIcon("ball.gif");


	/** Creates a new instance of SocketTest */
	public SocketTest() {
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
		
		try {
			UIManager.setLookAndFeel("net.sourceforge.mlf.metouia.MetouiaLookAndFeel");
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ee) {
				System.out.println("Error setting native LAF: " + ee);
			}
		}

		SocketTest st = new SocketTest();
		st.setTitle("SocketTest v"+version);
		st.setSize(1010, 750);
		util.centerWindow(st);
		st.setDefaultCloseOperation(EXIT_ON_CLOSE);
		st.setIconImage(st.logo.getImage());

		st.setVisible(true);
	}

}
