package com.commander4j.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.StringUtils;

import com.commander4j.network.SocketServer;
import com.commander4j.network.Util;

public class SocketServerGUI extends JPanel
{
	
	private static final long serialVersionUID = 1L;

	protected final JFrame parent;
	
	private JPanel topPanel;
	private JPanel connectPanel;
	private JPanel centerPanel;

	private JLabel ipLabel = new JLabel("IP Address");
	private JLabel portLabel = new JLabel("Port");
	private JLabel clientLabel = new JLabel("Client");
	private JLabel logoLabel = new JLabel(Util.logo, JLabel.CENTER);
	
	private JTextField portField = new JTextField("8000", 10);

	private JToggleButton connectButton = new JToggleButton(Util.connectIcon);
	private JButton sendInputButton = new JButton(Util.sendIcon);
	private JButton clearInputButton = new JButton(Util.clearIcon);
	private JButton loadInputButton = new JButton(Util.loadIcon);
	private JButton saveInputButton = new JButton(Util.saveIcon);
	public JToggleButton timestampButton = new JToggleButton(Util.clockIcon);
	public JToggleButton proxyButton = new JToggleButton(Util.proxyIcon);
	private JButton disconnectButton = new JButton(Util.breakIcon);
	private JButton btnClose = new JButton(Util.exitIcon);
	
	private Border connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected Client : < NONE >");
	
	public static JTextArea messagesField = new JTextArea();

	private Socket socket;
	private ServerSocket server;
	private SocketServer socketServer;
	private PrintWriter out;

	public JTextPane textPane = new JTextPane();
	
	private JComboBox<String> comboBoxStartofLine = new JComboBox<String>();
	private JComboBox<String> comboBoxEndofLine = new JComboBox<String>();
	private JComboBox<String> comboBoxSendSuffix = new JComboBox<String>();
	private JComboBox<String> comboBoxSendPrefix = new JComboBox<String>();
	private JComboBox<String> comboBoxIP = new JComboBox<String>();
	
	private Util util = new Util();
	private final JLabel lblInput = new JLabel("Input");
	private final JLabel lblOutput = new JLabel("Log");

	public SocketServerGUI(final JFrame parent)
	{

		this.parent = parent;

		setLayout(null);

		Container cp = this;

		topPanel = new JPanel();
		topPanel.setBounds(6, 6, 805, 103);
		topPanel.setLayout(null);

		connectPanel = new JPanel();
		connectPanel.setBounds(0, 0, 355, 90);
		connectPanel.setLayout(null);

		ipLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		ipLabel.setSize(84, 27);
		ipLabel.setLocation(3, 20);

		connectPanel.add(ipLabel);

		portLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		portLabel.setSize(46, 27);
		portLabel.setLocation(41, 55);

		connectPanel.add(portLabel);
		
		clientLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		clientLabel.setSize(55, 27);
		clientLabel.setLocation(180, 55);
		
		connectPanel.add(clientLabel);

		ActionListener connectListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				connectButton.setSelected(connect());
			}
		};

		portField.setSize(75, 27);
		portField.setLocation(90, 55);
		portField.addActionListener(connectListener);

		connectPanel.add(portField);

		connectButton.setSize(32, 32);
		connectButton.setLocation(240, 18);
		connectButton.setMnemonic(KeyEvent.VK_G);
		connectButton.setToolTipText("Open");
		connectButton.setIcon(Util.connectIcon);
		connectButton.addActionListener(connectListener);

		connectPanel.add(connectButton);
		
		connectPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Listen On"));
		topPanel.add(connectPanel);

		disconnectButton.setEnabled(false);
		disconnectButton.setBounds(240, 50, 32, 32);
		disconnectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				disconnect();
			}
		});
		connectPanel.add(disconnectButton);
		
		Vector<String> ips = new Vector<String>();
		ips = util.getIPAddresses();
		ComboBoxModel<String> jComboBox2Model = new DefaultComboBoxModel<String>(ips);

		comboBoxIP.setModel(jComboBox2Model);
		comboBoxIP.setBounds(90, 20, 141, 27);
		connectPanel.add(comboBoxIP);
		proxyButton.setBounds(274, 18, 32, 32);
		connectPanel.add(proxyButton);

		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

		ActionListener sendListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				boolean timestampReqd = timestampButton.isSelected();
				String logData = "";
				
				String msg = messagesField.getText();
				msg = util.upperCaseTokens(msg);

				String[] lines = StringUtils.split(msg, util.encodeControlChars("<LF>"));

				if (lines.length > 0)
				{
					
					//Prefix
					String prefixMessage =  comboBoxSendPrefix.getItemAt(comboBoxSendPrefix.getSelectedIndex()).toString();
					
					logData = prefixMessage;
					
					transmit(logData);
					
					util.log( logData, textPane, timestampReqd, Util.typeServer);
					
					if (logData.equals("")==false)
					{
						timestampReqd=false;
					}

					for (int x = 0; x < lines.length; x++)
					{

						String prefixLine=comboBoxStartofLine.getItemAt(comboBoxStartofLine.getSelectedIndex()).toString();
						String suffixLine=comboBoxEndofLine.getItemAt(comboBoxEndofLine.getSelectedIndex()).toString();
						
						logData = prefixLine+lines[x]+suffixLine;
						
						transmit(logData);

						util.log( logData, textPane, timestampReqd, Util.typeServer);
						
						if (logData.equals("")==false)
						{
							timestampReqd=false;
						}
						
					}
					
					//Suffix
					String suffixMessage =  comboBoxSendSuffix.getItemAt(comboBoxSendSuffix.getSelectedIndex()).toString();
					
					logData = suffixMessage;
					
					transmit(logData);
					
					util.log( logData, textPane, timestampReqd, Util.typeServer);
					
					if (logData.equals("")==false)
					{
						timestampReqd=false;
					}

				}
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
		centerPanel.setBounds(0, 102, 1020, 583);
		centerPanel.setLayout(null);

		CompoundBorder cb = new CompoundBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10), connectedBorder);
		centerPanel.setBorder(cb);

		cp.add(topPanel);
		cp.add(centerPanel);

		messagesField.setLocation(26, 0);
		messagesField.setFont(Util.textFont);

		JScrollPane jsp = new JScrollPane(messagesField);
		jsp.setBounds(26, 42, 450, 483);

		centerPanel.add(jsp);

		sendInputButton.setBounds(476, 40, 32, 32);
		sendInputButton.setEnabled(false);
		sendInputButton.setToolTipText("Send text to client");
		centerPanel.add(sendInputButton);

		clearInputButton.setBounds(476, 73, 32, 32);
		clearInputButton.setToolTipText("Clear conversation with client");
		clearInputButton.setMnemonic(KeyEvent.VK_C);
		clearInputButton.addActionListener(clearListener);
		centerPanel.add(clearInputButton);
		loadInputButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showOpenDialog(parent);
				if (result == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: " + selectedFile.getAbsolutePath());

					String input = util.readFile(selectedFile.getAbsolutePath());
					messagesField.setText(input);

				}
			}
		});

		loadInputButton.setBounds(476, 105, 32, 32);
		loadInputButton.setToolTipText("Load from file");
		loadInputButton.setMnemonic('L');

		centerPanel.add(loadInputButton);
		saveInputButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String text = messagesField.getText();
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showSaveDialog(parent);
				if (result == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: " + selectedFile.getAbsolutePath());
					util.writeFile(selectedFile, text);
				}
			}
		});

		saveInputButton.setBounds(476, 138, 32, 32);
		saveInputButton.setToolTipText("Save to file");
		saveInputButton.setMnemonic(KeyEvent.VK_S);

		centerPanel.add(saveInputButton);

		btnClose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				disconnect();
				System.exit(0);
			}
		});
		btnClose.setToolTipText("Close");
		btnClose.setText("Exit");
		btnClose.setMnemonic(KeyEvent.VK_X);
		btnClose.setBounds(450, 532, 95, 32);

		centerPanel.add(btnClose);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(520, 42, 450, 483);
		centerPanel.add(scrollPane);
		textPane.setBackground(Util.log_Color_BG);
		textPane.setFont(Util.textFont);
		textPane.setEditorKit(new WrapEditorKit());
		scrollPane.setViewportView(textPane);

		JButton clearLogButton = new JButton(Util.clearIcon);
		clearLogButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textPane.setText("");
			}
		});
		clearLogButton.setToolTipText("Clear conversation with client");
		clearLogButton.setMnemonic(KeyEvent.VK_R);
		clearLogButton.setBounds(971, 40, 32, 32);
		centerPanel.add(clearLogButton);

		JButton saveLogButton = new JButton(Util.saveIcon);
		saveLogButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String text = textPane.getText();
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showSaveDialog(parent);
				if (result == JFileChooser.APPROVE_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: " + selectedFile.getAbsolutePath());
					util.writeFile(selectedFile, text);
				}
			}
		});
		saveLogButton.setToolTipText("Save to file");
		saveLogButton.setMnemonic(KeyEvent.VK_V);
		saveLogButton.setBounds(971, 73, 32, 32);
		centerPanel.add(saveLogButton);
		
		timestampButton.setBounds(971, 105, 32, 32);
		timestampButton.setToolTipText("Timestamp On/Off");
		centerPanel.add(timestampButton);
		
		lblInput.setForeground(Color.BLUE);
		lblInput.setHorizontalAlignment(SwingConstants.LEFT);
		lblInput.setBounds(26, 21, 441, 20);
		
		centerPanel.add(lblInput);
		lblOutput.setHorizontalAlignment(SwingConstants.LEFT);
		lblOutput.setForeground(Color.BLUE);
		lblOutput.setBounds(520, 21, 441, 20);
		
		centerPanel.add(lblOutput);

		logoLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		logoLabel.setBounds(867, 15, 104, 85);
		add(logoLabel);

		sendInputButton.addActionListener(sendListener);
		
		
		comboBoxStartofLine.setBounds(446, 33, 124, 27);
		topPanel.add(comboBoxStartofLine);
		
		comboBoxStartofLine.setEditable(true);
		comboBoxStartofLine.setModel(new DefaultComboBoxModel<String>(new String[]
		{ "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxStartofLine.setSelectedIndex(0);
		
		comboBoxEndofLine.setEditable(true);

		comboBoxEndofLine.setModel(new DefaultComboBoxModel<String>(new String[]
		{ "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxEndofLine.setSelectedIndex(0);
		comboBoxEndofLine.setBounds(667, 33, 124, 27);

		topPanel.add(comboBoxEndofLine);

		comboBoxSendSuffix.setEditable(true);
		comboBoxSendSuffix.setModel(new DefaultComboBoxModel<String>(new String[]
		{ "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxSendSuffix.setBounds(446, 62, 124, 27);
		comboBoxSendSuffix.setSelectedIndex(0);

		topPanel.add(comboBoxSendSuffix);

		JLabel lblAppendToMessage = new JLabel("Send Suffix");
		lblAppendToMessage.setHorizontalAlignment(SwingConstants.TRAILING);
		lblAppendToMessage.setBounds(357, 61, 79, 27);
		topPanel.add(lblAppendToMessage);

		comboBoxSendPrefix.setEditable(true);
		comboBoxSendPrefix.setModel(new DefaultComboBoxModel<String>(new String[]
		{ "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxSendPrefix.setBounds(446, 4, 124, 27);
		topPanel.add(comboBoxSendPrefix);

		JLabel lblSendPrefix = new JLabel("Send Prefix");
		lblSendPrefix.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSendPrefix.setBounds(357, 4, 79, 27);
		topPanel.add(lblSendPrefix);

		JLabel lblEndOfLine = new JLabel("End of Line");
		lblEndOfLine.setHorizontalAlignment(SwingConstants.TRAILING);
		lblEndOfLine.setBounds(576, 33, 79, 27);
		topPanel.add(lblEndOfLine);
		
		JLabel lblStartOfLine = new JLabel("Start of Line");
		lblStartOfLine.setHorizontalAlignment(SwingConstants.TRAILING);
		lblStartOfLine.setBounds(357, 33, 79, 27);
		topPanel.add(lblStartOfLine);
	}

	private boolean connect()
	{
		boolean result = false;
		if (server != null)
		{
			stop();
			return result;
		}
		String ip = comboBoxIP.getSelectedItem().toString();
		String port = portField.getText();
		if (ip == null || ip.equals(""))
		{
			JOptionPane.showMessageDialog(SocketServerGUI.this, "No IP Address. Please enter IP Address", "Error connecting", JOptionPane.ERROR_MESSAGE);
			return result;
		}
		if (port == null || port.equals(""))
		{
			JOptionPane.showMessageDialog(SocketServerGUI.this, "No Port number. Please enter Port number", "Error connecting", JOptionPane.ERROR_MESSAGE);
			portField.requestFocus();
			portField.selectAll();
			return result;
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (!util.checkHost(ip))
		{
			JOptionPane.showMessageDialog(SocketServerGUI.this, "Bad IP Address", "Error connecting", JOptionPane.ERROR_MESSAGE);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return result;
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
			return result;
		}
		try
		{
			InetAddress bindAddr = null;
			if (!ip.equals("0.0.0.0"))
				bindAddr = InetAddress.getByName(ip);
			else
				bindAddr = null;
			server = new ServerSocket(portNo, 1, bindAddr);

			portField.setEditable(false);

			connectButton.setToolTipText("Close");
			connectButton.setIcon(Util.disconnectIcon);
			
			util.log("Server started", textPane, timestampButton.isSelected(), Util.typeStatus);

		}
		catch (Exception e)
		{
			error(e.getMessage(), "Starting Server at " + portNo);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return result;
		}

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		socketServer = SocketServer.handle(this, server);
		
		result = true;
		
		return result;

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

		portField.setEditable(true);
		connectButton.setToolTipText("Open");
		connectButton.setIcon(Util.connectIcon);

		util.log("Server stopped", textPane, timestampButton.isSelected(), Util.typeStatus);

	}

	public synchronized void setClientSocket(Socket s)
	{

		if (s == null)
		{
			out = null;
			socket = null;
			changeBorder(null);
			sendInputButton.setEnabled(false);
			// messagesField.setEditable(false);
			disconnectButton.setEnabled(false);
			sendInputButton.setEnabled(false);
		}
		else
		{
			socket = s;
			changeBorder(" " + socket.getInetAddress().getHostName() + " [" + socket.getInetAddress().getHostAddress() + "] ");
			sendInputButton.setEnabled(true);
			// messagesField.setEditable(true);
			disconnectButton.setEnabled(true);
			sendInputButton.setEnabled(true);

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
