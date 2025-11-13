package com.commander4j.network;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.commander4j.gui.JImageIconLoader;
import com.commander4j.gui.SocketClientGUI;
import com.commander4j.gui.SocketServerGUI;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class SocketTest extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;
	public static SocketClientGUI client;
	public static SocketServerGUI server;
	public static String version = "5.76";
	public static final JImageIconLoader imageIconloader = new JImageIconLoader();
	public ImageIcon logo = imageIconloader.getImageIcon("logo.gif");
	public ImageIcon ball = imageIconloader.getImageIcon("ball.gif");
	private JButton btnNewButton;

	/** Creates a new instance of SocketTest */
	public SocketTest()
	{
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE ); 
		
		setResizable(false);

		addWindowListener(new WindowListener());

		Container cp = getContentPane();
		getContentPane().setLayout(null);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1062, 690);
		client = new SocketClientGUI(this);
		server = new SocketServerGUI(this);

		tabbedPane.addTab("Client", ball, (Component) client, "Test any server");
		tabbedPane.addTab("Server", ball, server, "Test any client");

		tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		cp.add(tabbedPane);
		
		btnNewButton = new JButton("Close");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (confirmExit())
				{
					System.exit(0);
				}
			}
		});
		btnNewButton.setIcon(Util.exitIcon);
		btnNewButton.setBounds(447, 680, 117, 32);
		getContentPane().add(btnNewButton);
	}

	public boolean confirmExit()
	{
		boolean result = false;
		
		int question = JOptionPane.showConfirmDialog(SocketTest.this, "Exit application ?", "Confirm", JOptionPane.YES_NO_OPTION, 0, Util.exitIcon);
		
		if (question == 0)
		{
			result = true;
		}
		return result;
	}

	class WindowListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			if (confirmExit())
			{
				System.exit(0);
			}
		}

	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args)
	{

		Util util = new Util();

		util.getIPAddresses();

		System.setProperty("apple.laf.useScreenMenuBar", "true");
		util.setLookAndFeel("Nimbus");

		SocketTest st = new SocketTest();
		st.setTitle("SocketTest v" + version);
		st.setSize(1030, 750);

		GraphicsDevice gd = util.getGraphicsDevice();

		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		Rectangle screenBounds = gc.getBounds();

		st.setBounds(screenBounds.x + ((screenBounds.width - st.getWidth()) / 2), screenBounds.y + ((screenBounds.height - st.getHeight()) / 2), st.getWidth(), st.getHeight());

		st.setIconImage(st.logo.getImage());

		st.setVisible(true);
	}
}
