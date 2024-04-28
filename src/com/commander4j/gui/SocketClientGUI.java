package com.commander4j.gui;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import com.commander4j.network.SocketClient;
import com.commander4j.network.SocketTest;
import com.commander4j.network.Util;

public class SocketClientGUI extends JPanel
{

	private static final long serialVersionUID = 1L;
	public ImageIcon logo = SocketTest.imageIconloader.getImageIcon("logo.gif");
	private JPanel topPanel;
	private JPanel toPanel;
	private JPanel centerPanel;
	private JLabel ipLabel = new JLabel("IP Address");
	private JLabel portLabel = new JLabel("Port");
	private JLabel logoLabel = new JLabel(logo, JLabel.CENTER);
	private JTextField ipField = new JTextField("127.0.0.1", 20);
	private JTextField portField = new JTextField("8000", 10);
	private JButton connectButton = new JButton("Connect");
	private Border connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected To < NONE >");
	private JTextArea messagesField = new JTextArea();
	private JButton sendButton = new JButton("Send");
	private JButton clearButton = new JButton("Clear");
	private Socket socket;
	private PrintWriter out;
	private SocketClient socketClient;
	protected JFrame parent;
	public JTextPane textPane = new JTextPane();
	public JCheckBox chckbxTimestamp = new JCheckBox("Timestamp");
	private JComboBox<String> comboBoxEndofLine = new JComboBox<String>();
	private JComboBox<String> comboBoxSendSuffix = new JComboBox<String>();
	private JComboBox<String> comboBoxSendPrefix = new JComboBox<String>();

	public Util util = new Util();

	public SocketClientGUI(final JFrame parent)
	{

		this.parent = parent;
		Container cp = this;

		topPanel = new JPanel();
		topPanel.setBounds(6, 6, 750, 82);
		toPanel = new JPanel();
		toPanel.setBounds(0, 0, 456, 85);
		toPanel.setLayout(null);
		ipLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		ipLabel.setBounds(29, 20, 84, 20);
		toPanel.add(ipLabel);

		ActionListener ipListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				portField.requestFocus();
			}
		};
		ipField.setBounds(119, 20, 160, 20);
		ipField.addActionListener(ipListener);
		toPanel.add(ipField);
		portLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		portLabel.setBounds(281, 20, 46, 20);

		toPanel.add(portLabel);

		ActionListener connectListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				connect();
			}
		};
		portField.setBounds(339, 20, 75, 20);
		portField.addActionListener(connectListener);
		toPanel.add(portField);
		connectButton.setBounds(164, 47, 115, 29);

		connectButton.setMnemonic('C');
		connectButton.setToolTipText("Start Connection");
		connectButton.addActionListener(connectListener);
		topPanel.setLayout(null);
		toPanel.add(connectButton);

		toPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Connect To"));
		topPanel.add(toPanel);
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		comboBoxEndofLine.setEditable(true);

		comboBoxEndofLine.setModel(new DefaultComboBoxModel<String>(new String[]
		{ "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxEndofLine.setSelectedIndex(0);
		comboBoxEndofLine.setBounds(569, 33, 164, 27);
		topPanel.add(comboBoxEndofLine);

		JLabel lblEndOfLine = new JLabel("End of Line");
		lblEndOfLine.setHorizontalAlignment(SwingConstants.TRAILING);
		lblEndOfLine.setBounds(478, 38, 79, 16);
		topPanel.add(lblEndOfLine);
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

				util.log(msg, textPane, chckbxTimestamp.isSelected(), Util.typeClient);

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
		messagesField.setFont(new Font("Lucida Console", Font.PLAIN, 14));
		// messagesField.setWrapStyleWord(true);

		JScrollPane jsp = new JScrollPane(messagesField);
		jsp.setBounds(26, 30, 450, 500);
		centerPanel.add(jsp);
		sendButton.setBounds(73, 532, 115, 29);
		centerPanel.add(sendButton);
		sendButton.setToolTipText("Send text to host");
		clearButton.setBounds(190, 532, 115, 29);
		centerPanel.add(clearButton);

		clearButton.setToolTipText("Clear conversation with host");
		clearButton.setMnemonic('C');

		JButton btnClose = new JButton("Close");
		btnClose.setBounds(307, 532, 115, 29);
		centerPanel.add(btnClose);
		btnClose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				disconnect();
				System.exit(0);

			}
		});
		btnClose.setToolTipText("Clear conversation with host");
		btnClose.setMnemonic('C');

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(500, 30, 450, 500);
		centerPanel.add(scrollPane);
		textPane.setBackground(Util.log_Color_BG);

		scrollPane.setViewportView(textPane);

		chckbxTimestamp.setBounds(496, 538, 128, 23);
		centerPanel.add(chckbxTimestamp);
		logoLabel.setBounds(867, 5, 104, 85);
		add(logoLabel);
		logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
		logoLabel.setHorizontalTextPosition(JLabel.CENTER);
		clearButton.addActionListener(clearListener);
		sendButton.addActionListener(sendListener);

		disconnect();

	}

	private void connect()
	{
		if (socket != null)
		{
			disconnect();
			return;
		}
		String ip = ipField.getText();
		String port = portField.getText();
		if (ip == null || ip.equals(""))
		{
			JOptionPane.showMessageDialog(SocketClientGUI.this, "No IP Address. Please enter IP Address", "Error connecting", JOptionPane.ERROR_MESSAGE);
			ipField.requestFocus();
			ipField.selectAll();
			return;
		}
		if (port == null || port.equals(""))
		{
			JOptionPane.showMessageDialog(SocketClientGUI.this, "No Port number. Please enter Port number", "Error connecting", JOptionPane.ERROR_MESSAGE);
			portField.requestFocus();
			portField.selectAll();
			return;
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (!util.checkHost(ip))
		{
			JOptionPane.showMessageDialog(SocketClientGUI.this, "Bad IP Address", "Error connecting", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(SocketClientGUI.this, "Bad Port number. Please enter Port number", "Error connecting", JOptionPane.ERROR_MESSAGE);
			portField.requestFocus();
			portField.selectAll();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		try
		{

			socket = new Socket(ip, portNo);

			ipField.setEditable(false);
			portField.setEditable(false);
			connectButton.setText("Disconnect");
			connectButton.setMnemonic('D');
			connectButton.setToolTipText("Stop Connection");
			sendButton.setEnabled(true);

			messagesField.setEditable(true);
		}
		catch (Exception e)
		{
			error(e.getMessage(), "Opening connection");
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		changeBorder(" " + socket.getInetAddress().getHostName() + " [" + socket.getInetAddress().getHostAddress() + "] ");
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		// messagesField.setText("");
		socketClient = SocketClient.handle(this, socket);
		messagesField.requestFocus();
	}

	public synchronized void disconnect()
	{
		try
		{
			socketClient.setDisconnected(true);
			socket.close();
		}
		catch (Exception e)
		{

		}
		socket = null;
		out = null;
		changeBorder(null);
		ipField.setEditable(true);
		portField.setEditable(true);
		connectButton.setText("Connect");
		connectButton.setMnemonic('C');
		connectButton.setToolTipText("Start Connection");
		sendButton.setEnabled(false);

	}

	public void error(String error)
	{
		if (error == null || error.equals(""))
			return;
		JOptionPane.showMessageDialog(SocketClientGUI.this, error, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void error(String error, String heading)
	{
		if (error == null || error.equals(""))
			return;
		JOptionPane.showMessageDialog(SocketClientGUI.this, error, heading, JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(SocketClientGUI.this, e.getMessage(), "Error Sending Message", JOptionPane.ERROR_MESSAGE);
			disconnect();
		}
	}

	private void changeBorder(String ip)
	{
		if (ip == null || ip.equals(""))
			connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected To < NONE >");
		else
			connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected To < " + ip + " >");
		
		CompoundBorder cb = new CompoundBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10), connectedBorder);
		
		centerPanel.setBorder(cb);
		invalidate();
		repaint();
	}

}
