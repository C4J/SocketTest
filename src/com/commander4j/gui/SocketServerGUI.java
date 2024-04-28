package com.commander4j.gui;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import com.commander4j.network.SocketServer;
import com.commander4j.network.SocketTest;
import com.commander4j.network.Util;

public class SocketServerGUI extends JPanel
/* JFrame */ {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImageIcon logo = SocketTest.imageIconloader.getImageIcon("logo.gif");

	private JPanel topPanel;
	private JPanel toPanel;

	private JPanel centerPanel;

	private JLabel ipLabel = new JLabel("IP Address");
	private JLabel portLabel = new JLabel("Port");
	private JTextField ipField = new JTextField("127.0.0.1", 20);
	private JTextField portField = new JTextField("8000", 10);
	private JLabel logoLabel = new JLabel(logo, JLabel.CENTER);
	private JButton connectButton = new JButton("Start Listening");
	private Border connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected Client : < NONE >");
	public static JTextArea messagesField = new JTextArea();

	private JButton sendButton = new JButton("Send");
	private JButton clearButton = new JButton("Clear");

	private Socket socket;
	private ServerSocket server;
	private SocketServer socketServer;
	private PrintWriter out;

	protected final JFrame parent;
	private final JButton disconnectButton = new JButton("Disconnect");
	private final JButton btnClose = new JButton("Close");

	public JTextPane textPane = new JTextPane();
	public JCheckBox chckbxTimestamp = new JCheckBox("Timestamp");
	public JCheckBox chckbxProxy = new JCheckBox("Proxy");

	private Util util = new Util();
	private JComboBox<String> comboBoxEndofLine = new JComboBox<String>();
	private JComboBox<String> comboBoxSendSuffix = new JComboBox<String>();
	private JComboBox<String> comboBoxSendPrefix = new JComboBox<String>();

	public SocketServerGUI(final JFrame parent)
	{
		// Container cp = getContentPane();
		this.parent = parent;
		Container cp = this;

		topPanel = new JPanel();
		topPanel.setBounds(6, 6, 861, 85);
		toPanel = new JPanel();
		toPanel.setBounds(0, 0, 456, 85);
		toPanel.setLayout(null);
		ipLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		ipLabel.setSize(84, 20);
		ipLabel.setLocation(29, 20);

		toPanel.add(ipLabel);

		ActionListener ipListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				portField.requestFocus();
			}
		};
		ipField.setSize(160, 20);
		ipField.setLocation(119, 20);
		ipField.addActionListener(ipListener);
		toPanel.add(ipField);
		portLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		portLabel.setSize(46, 20);
		portLabel.setLocation(281, 20);

		toPanel.add(portLabel);

		ActionListener connectListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				connect();
			}
		};
		portField.setSize(75, 20);
		portField.setLocation(339, 20);
		portField.addActionListener(connectListener);
		toPanel.add(portField);
		connectButton.setSize(115, 29);
		connectButton.setLocation(171, 47);

		connectButton.setMnemonic('S');
		connectButton.setToolTipText("Start Listening");
		connectButton.addActionListener(connectListener);
		topPanel.setLayout(null);
		toPanel.add(connectButton);

		toPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Listen On"));
		topPanel.add(toPanel);
		disconnectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				disconnect();
			}
		});
		disconnectButton.setEnabled(false);
		disconnectButton.setBounds(286, 47, 115, 29);

		toPanel.add(disconnectButton);
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		ActionListener sendListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String msg = messagesField.getText();
				msg = util.upperCaseTokens(msg);

				// remove soft line feeds included in textarea
				msg = msg.replace(util.encodeControlChars("<LF>"), comboBoxEndofLine.getItemAt(comboBoxEndofLine.getSelectedIndex()).toString());
				
				msg = comboBoxSendPrefix.getItemAt(comboBoxSendPrefix.getSelectedIndex()).toString() + msg + comboBoxSendSuffix.getItemAt(comboBoxSendSuffix.getSelectedIndex()).toString();

				transmit(msg);
				util.log(msg, textPane, chckbxTimestamp.isSelected(), Util.typeServer);
			}
		};

		ActionListener clearListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				messagesField.setText("");
			}
		};

		centerPanel = new JPanel();
		centerPanel.setBounds(0, 90, 980, 587);
		centerPanel.setLayout(null);

		CompoundBorder cb = new CompoundBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10), connectedBorder);
		centerPanel.setBorder(cb);
		setLayout(null);
		cp.add(topPanel);
		cp.add(centerPanel);
		messagesField.setLocation(26, 0);
		messagesField.setFont(new Font("Lucida Console", Font.PLAIN, 14));
		JScrollPane jsp = new JScrollPane(messagesField);
		jsp.setBounds(26, 30, 450, 500);
		centerPanel.add(jsp);
		SpinnerNumberModel jSpinnerIntModel = new SpinnerNumberModel();
		jSpinnerIntModel.setMinimum(1);
		jSpinnerIntModel.setMaximum(60);
		jSpinnerIntModel.setStepSize(1);

		sendButton.setBounds(73, 532, 115, 29);
		centerPanel.add(sendButton);

		sendButton.setEnabled(false);
		sendButton.setToolTipText("Send text to client");
		clearButton.setBounds(190, 532, 115, 29);
		centerPanel.add(clearButton);

		clearButton.setToolTipText("Clear conversation with client");
		clearButton.setMnemonic('C');

		btnClose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				disconnect();
				System.exit(0);
			}
		});
		btnClose.setToolTipText("Send text to client");
		btnClose.setBounds(307, 532, 115, 29);

		centerPanel.add(btnClose);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(500, 30, 450, 500);
		centerPanel.add(scrollPane);
		textPane.setBackground(Util.log_Color_BG);

		scrollPane.setViewportView(textPane);
		chckbxTimestamp.setBounds(496, 538, 128, 23);

		centerPanel.add(chckbxTimestamp);

		logoLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		logoLabel.setBounds(867, 5, 104, 85);
		add(logoLabel);
		clearButton.addActionListener(clearListener);
		sendButton.addActionListener(sendListener);
		comboBoxEndofLine.setEditable(true);

		comboBoxEndofLine.setModel(new DefaultComboBoxModel<String>(new String[]
		{ "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxEndofLine.setSelectedIndex(0);
		comboBoxEndofLine.setBounds(569, 33, 164, 27);
		topPanel.add(comboBoxEndofLine);
		comboBoxSendSuffix.setEditable(true);
		
		comboBoxSendSuffix.setModel(new DefaultComboBoxModel<String>(new String[] { "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxSendSuffix.setBounds(569, 58, 164, 27);
		comboBoxSendSuffix.setSelectedIndex(0);
		topPanel.add(comboBoxSendSuffix);

		JLabel lblAppendToMessage = new JLabel("Send Suffix");
		lblAppendToMessage.setHorizontalAlignment(SwingConstants.TRAILING);
		lblAppendToMessage.setBounds(480, 63, 79, 16);
		topPanel.add(lblAppendToMessage);
		comboBoxSendPrefix.setEditable(true);

		comboBoxSendPrefix.setModel(new DefaultComboBoxModel<String>(new String[] { "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxSendPrefix.setBounds(569, 8, 164, 27);
		topPanel.add(comboBoxSendPrefix);

		JLabel lblSendPrefix = new JLabel("Send Prefix");
		lblSendPrefix.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSendPrefix.setBounds(480, 13, 79, 16);
		topPanel.add(lblSendPrefix);

		JLabel lblEndOfLine = new JLabel("End of Line");
		lblEndOfLine.setHorizontalAlignment(SwingConstants.TRAILING);
		lblEndOfLine.setBounds(478, 38, 79, 16);
		topPanel.add(lblEndOfLine);
		chckbxProxy.setBounds(753, 37, 67, 23);
		topPanel.add(chckbxProxy);
	}

	// ///////////////////
	// action & helper methods
	// ///////////////////
	private void connect()
	{
		if (server != null)
		{
			stop();
			return;
		}
		String ip = ipField.getText();
		String port = portField.getText();
		if (ip == null || ip.equals(""))
		{
			JOptionPane.showMessageDialog(SocketServerGUI.this, "No IP Address. Please enter IP Address", "Error connecting", JOptionPane.ERROR_MESSAGE);
			ipField.requestFocus();
			ipField.selectAll();
			return;
		}
		if (port == null || port.equals(""))
		{
			JOptionPane.showMessageDialog(SocketServerGUI.this, "No Port number. Please enter Port number", "Error connecting", JOptionPane.ERROR_MESSAGE);
			portField.requestFocus();
			portField.selectAll();
			return;
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (!util.checkHost(ip))
		{
			JOptionPane.showMessageDialog(SocketServerGUI.this, "Bad IP Address", "Error connecting", JOptionPane.ERROR_MESSAGE);
			ipField.requestFocus();
			ipField.selectAll();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		int portNo = 0;
		try
		{
			portNo = Integer.parseInt(port);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(SocketServerGUI.this, "Bad Port number. Please enter Port number", "Error connecting", JOptionPane.ERROR_MESSAGE);
			portField.requestFocus();
			portField.selectAll();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		try
		{
			InetAddress bindAddr = null;
			if (!ip.equals("0.0.0.0"))
				bindAddr = InetAddress.getByName(ip);
			else
				bindAddr = null;
			server = new ServerSocket(portNo, 1, bindAddr);

			ipField.setEditable(false);
			portField.setEditable(false);

			connectButton.setText("Stop Listening");
			connectButton.setMnemonic('S');
			connectButton.setToolTipText("Stop Listening");

		}
		catch (Exception e)
		{
			error(e.getMessage(), "Starting Server at " + portNo);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		socketServer = SocketServer.handle(this, server);

	}

	// disconnect a client
	public synchronized void disconnect()
	{
		try
		{
			socketServer.setDisconnected(true);
		}
		catch (Exception e)
		{
		}
	}

	public synchronized void stop()
	{
		try
		{
			disconnect(); // close any client
			socketServer.setStop(true);
		}
		catch (Exception e)
		{
		}
		server = null;
		ipField.setEditable(true);
		portField.setEditable(true);
		connectButton.setText("Start Listening");
		connectButton.setMnemonic('S');
		connectButton.setToolTipText("Start Listening");

		util.log("Server stopped", textPane, chckbxTimestamp.isSelected(), Util.typeStatus);
		util.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", textPane, chckbxTimestamp.isSelected(), Util.typeStatus);

	}

	public synchronized void setClientSocket(Socket s)
	{

		if (s == null)
		{
			out = null;
			socket = null;
			changeBorder(null);
			sendButton.setEnabled(false);
			// messagesField.setEditable(false);
			disconnectButton.setEnabled(false);
			sendButton.setEnabled(false);
		}
		else
		{
			socket = s;
			changeBorder(" " + socket.getInetAddress().getHostName() + " [" + socket.getInetAddress().getHostAddress() + "] ");
			sendButton.setEnabled(true);
			// messagesField.setEditable(true);
			disconnectButton.setEnabled(true);
			sendButton.setEnabled(true);

		}
	}

	public void error(String error)
	{
		if (error == null || error.equals(""))
			return;
		JOptionPane.showMessageDialog(SocketServerGUI.this, error, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void error(String error, String heading)
	{
		if (error == null || error.equals(""))
			return;
		JOptionPane.showMessageDialog(SocketServerGUI.this, error, heading, JOptionPane.ERROR_MESSAGE);
	}

	public void transmit(String sendData)
	{
		sendData = util.encodeControlChars(sendData);

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try
		{
			if (out == null)
			{
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			}

			out.print(sendData);
			out.flush();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		catch (Exception e)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(SocketServerGUI.this, e.getMessage(), "Error Sending Message", JOptionPane.ERROR_MESSAGE);
			disconnect();
		}
	}

	private void changeBorder(String ip)
	{
		if (ip == null || ip.equals(""))
			connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected Client : < NONE >");
		else
			connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected Client : < " + ip + " >");
		CompoundBorder cb = new CompoundBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10), connectedBorder);
		centerPanel.setBorder(cb);
		invalidate();
		repaint();
	}

}
