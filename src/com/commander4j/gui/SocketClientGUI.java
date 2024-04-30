package com.commander4j.gui;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.StringUtils;

import com.commander4j.network.SocketClient;
import com.commander4j.network.Util;
import java.awt.Color;

public class SocketClientGUI extends JPanel
{

	private static final long serialVersionUID = 1L;

	protected JFrame parent;

	private JPanel topPanel;
	private JPanel connectPanel;
	private JPanel centerPanel;

	private JLabel ipLabel = new JLabel("IP Address");
	private JLabel portLabel = new JLabel("Port");
	private JLabel logoLabel = new JLabel(Util.logo, JLabel.CENTER);
	private JTextField portField = new JTextField("8000", 10);

	private JButton connectButton = new JButton(Util.connectIcon);
	private JButton loadInputButton = new JButton(Util.loadIcon);
	private JButton saveInputButton = new JButton(Util.saveIcon);
	private JButton clearLogButton = new JButton(Util.clearIcon);
	private JButton saveLogButton = new JButton(Util.saveIcon);
	private JButton sendInputButton = new JButton(Util.sendIcon);
	private JButton clearInputButton = new JButton(Util.clearIcon);

	private Border connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected To < NONE >");

	private JTextArea messagesField = new JTextArea();

	private Socket socket;
	private PrintWriter out;
	private SocketClient socketClient;

	public JTextPane textPane = new JTextPane();

	public JCheckBox chckbxTimestamp = new JCheckBox("Timestamp");

	private JComboBox<String> comboBoxStartofLine = new JComboBox<String>();
	private JComboBox<String> comboBoxEndofLine = new JComboBox<String>();
	private JComboBox<String> comboBoxSendSuffix = new JComboBox<String>();
	private JComboBox<String> comboBoxSendPrefix = new JComboBox<String>();
	private JComboBox<String> comboBoxIP = new JComboBox<String>();

	public Util util = new Util();
	private final JLabel lblInput = new JLabel("Input");
	private final JLabel lblLog = new JLabel("Log");

	public SocketClientGUI(final JFrame parent)
	{

		this.parent = parent;

		setLayout(null);

		Container cp = this;

		topPanel = new JPanel();
		topPanel.setBounds(6, 6, 818, 82);
		topPanel.setLayout(null);

		connectPanel = new JPanel();
		connectPanel.setBounds(0, 0, 355, 85);
		connectPanel.setLayout(null);

		ipLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		ipLabel.setBounds(3, 20, 84, 20);
		connectPanel.add(ipLabel);

		portLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		portLabel.setBounds(41, 50, 46, 20);
		connectPanel.add(portLabel);

		ActionListener connectListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				connect();
			}
		};

		portField.setBounds(90, 50, 75, 20);
		portField.addActionListener(connectListener);
		connectPanel.add(portField);

		connectButton.setBounds(228, 17, 115, 29);
		connectButton.setMnemonic(KeyEvent.VK_N);
		connectButton.setToolTipText("Start Connection");
		connectButton.addActionListener(connectListener);
		connectPanel.add(connectButton);

		Vector<String> ips = new Vector<String>();
		ips = util.getIPAddresses();
		ComboBoxModel<String> jComboBox2Model = new DefaultComboBoxModel<String>(ips);
		comboBoxIP.setEditable(true);

		comboBoxIP.setModel(jComboBox2Model);
		comboBoxIP.setBounds(90, 18, 141, 27);
		connectPanel.add(comboBoxIP);

		connectPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Connect To"));
		topPanel.add(connectPanel);
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

		comboBoxEndofLine.setEditable(true);
		comboBoxEndofLine.setModel(new DefaultComboBoxModel<String>(new String[]
		{ "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxEndofLine.setSelectedIndex(0);
		comboBoxEndofLine.setBounds(667, 33, 124, 27);
		topPanel.add(comboBoxEndofLine);

		JLabel lblEndOfLine = new JLabel("End of Line");
		lblEndOfLine.setHorizontalAlignment(SwingConstants.TRAILING);
		lblEndOfLine.setBounds(576, 40, 79, 16);
		topPanel.add(lblEndOfLine);
		
		JLabel lblStartOfLine = new JLabel("Start of Line");
		lblStartOfLine.setHorizontalAlignment(SwingConstants.TRAILING);
		lblStartOfLine.setBounds(357, 40, 79, 16);
		topPanel.add(lblStartOfLine);

		comboBoxSendSuffix.setEditable(true);
		comboBoxSendSuffix.setModel(new DefaultComboBoxModel<String>(new String[]
		{ "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxSendSuffix.setBounds(446, 61, 124, 27);
		comboBoxSendSuffix.setSelectedIndex(0);
		topPanel.add(comboBoxSendSuffix);

		JLabel lblAppendToMessage = new JLabel("Send Suffix");
		lblAppendToMessage.setHorizontalAlignment(SwingConstants.TRAILING);
		lblAppendToMessage.setBounds(357, 64, 79, 16);
		topPanel.add(lblAppendToMessage);
		comboBoxSendPrefix.setEditable(true);

		comboBoxSendPrefix.setModel(new DefaultComboBoxModel<String>(new String[]
		{ "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxSendPrefix.setBounds(446, 4, 124, 27);
		topPanel.add(comboBoxSendPrefix);

		JLabel lblSendPrefix = new JLabel("Send Prefix");
		lblSendPrefix.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSendPrefix.setBounds(357, 15, 79, 16);
		topPanel.add(lblSendPrefix);

		ActionListener sendListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				boolean timestampReqd = chckbxTimestamp.isSelected();
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
					
					util.log( logData, textPane, timestampReqd, Util.typeClient);
					
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

						util.log( logData, textPane, timestampReqd, Util.typeClient);
						
						if (logData.equals("")==false)
						{
							timestampReqd=false;
						}
						
					}
					
					//Suffix
					String suffixMessage =  comboBoxSendSuffix.getItemAt(comboBoxSendSuffix.getSelectedIndex()).toString();
					
					logData = suffixMessage;
					
					transmit(logData);
					
					util.log( logData, textPane, timestampReqd, Util.typeClient);
					
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
		centerPanel.setBounds(0, 90, 980, 587);
		centerPanel.setLayout(null);

		CompoundBorder cb = new CompoundBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10), connectedBorder);
		centerPanel.setBorder(cb);

		cp.add(topPanel);
		
		comboBoxStartofLine.setBounds(446, 33, 124, 27);
		topPanel.add(comboBoxStartofLine);
		
		comboBoxStartofLine.setEditable(true);
		comboBoxStartofLine.setModel(new DefaultComboBoxModel<String>(new String[]
		{ "", "<CR>", "<LF>", "<CR><LF>", "<STX>", "<ETX>", "<ESC>", "<ACK>", "<NAK>" }));
		comboBoxStartofLine.setSelectedIndex(0);
		
		cp.add(centerPanel);
		messagesField.setFont(Util.textFont);

		JScrollPane jsp = new JScrollPane(messagesField);
		jsp.setBounds(26, 42, 450, 483);
		centerPanel.add(jsp);


		sendInputButton.setBounds(17, 532, 95, 29);
		sendInputButton.setText("Send");
		sendInputButton.setMnemonic(KeyEvent.VK_E);
		centerPanel.add(sendInputButton);
		sendInputButton.setToolTipText("Send text to host");
		clearInputButton.setBounds(111, 532, 95, 29);
		centerPanel.add(clearInputButton);

		clearInputButton.setToolTipText("Clear conversation with host");
		clearInputButton.setText("Clear");
		clearInputButton.setMnemonic(KeyEvent.VK_C);

		JButton btnClose = new JButton(Util.exitIcon);
		btnClose.setText("Exit");
		btnClose.setBounds(389, 532, 95, 29);
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
		btnClose.setMnemonic(KeyEvent.VK_X);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(500, 42, 450, 500);
		centerPanel.add(scrollPane);
		textPane.setFont(Util.textFont);
		textPane.setBackground(Util.log_Color_BG);
		textPane.setEditorKit(new WrapEditorKit());

		scrollPane.setViewportView(textPane);

		chckbxTimestamp.setBounds(496, 534, 128, 23);
		centerPanel.add(chckbxTimestamp);
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
		loadInputButton.setToolTipText("Load from file");
		loadInputButton.setText("Load");
		loadInputButton.setMnemonic(KeyEvent.VK_L);
		loadInputButton.setBounds(203, 532, 95, 29);

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
		saveInputButton.setToolTipText("Save file");
		saveInputButton.setText("Save");
		saveInputButton.setMnemonic(KeyEvent.VK_S);
		saveInputButton.setBounds(294, 532, 95, 29);

		centerPanel.add(saveInputButton);
		clearLogButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textPane.setText("");
			}
		});
		clearLogButton.setToolTipText("Clear conversation with client");
		clearLogButton.setText("Clear");
		clearLogButton.setMnemonic(KeyEvent.VK_R);
		clearLogButton.setBounds(654, 532, 95, 29);

		centerPanel.add(clearLogButton);
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
		saveLogButton.setText("Save");
		saveLogButton.setMnemonic(KeyEvent.VK_V);
		saveLogButton.setBounds(743, 532, 95, 29);

		centerPanel.add(saveLogButton);
		lblInput.setHorizontalAlignment(SwingConstants.LEFT);
		lblInput.setForeground(Color.BLUE);
		lblInput.setBounds(26, 21, 441, 20);
		
		centerPanel.add(lblInput);
		lblLog.setHorizontalAlignment(SwingConstants.LEFT);
		lblLog.setForeground(Color.BLUE);
		lblLog.setBounds(500, 21, 441, 20);
		
		centerPanel.add(lblLog);
		logoLabel.setBounds(867, 5, 104, 85);
		add(logoLabel);
		logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
		logoLabel.setHorizontalTextPosition(JLabel.CENTER);
		clearInputButton.addActionListener(clearListener);
		sendInputButton.addActionListener(sendListener);

		disconnect();

	}

	private void connect()
	{
		if (socket != null)
		{
			disconnect();
			return;
		}
		
		String ip = comboBoxIP.getItemAt(comboBoxIP.getSelectedIndex());
		String port = portField.getText();
		if (ip == null || ip.equals(""))
		{
			JOptionPane.showMessageDialog(SocketClientGUI.this, "No IP Address. Please enter IP Address", "Error connecting", JOptionPane.ERROR_MESSAGE);
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

			portField.setEditable(false);
			connectButton.setIcon(Util.disconnectIcon);
			connectButton.setText("Disconnect");

			connectButton.setToolTipText("Stop Connection");
			sendInputButton.setEnabled(true);

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
		portField.setEditable(true);
		connectButton.setText("Connect");
		connectButton.setIcon(Util.connectIcon);
		connectButton.setToolTipText("Start Connection");
		sendInputButton.setEnabled(false);

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
