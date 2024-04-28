package com.commander4j.network;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class Util
{

	public  static ImageIcon logo = SocketTest.imageIconloader.getImageIcon("logo.gif");
	public  static ImageIcon sendIcon = SocketTest.imageIconloader.getImageIcon("send.png");
	public  static ImageIcon clearIcon = SocketTest.imageIconloader.getImageIcon("clear.png");
	public  static ImageIcon loadIcon = SocketTest.imageIconloader.getImageIcon("load.gif");
	public  static ImageIcon saveIcon = SocketTest.imageIconloader.getImageIcon("save.gif");
	public  static ImageIcon exitIcon = SocketTest.imageIconloader.getImageIcon("exit.gif");
	public  static ImageIcon connectIcon = SocketTest.imageIconloader.getImageIcon("connect.gif");
	public  static ImageIcon disconnectIcon = SocketTest.imageIconloader.getImageIcon("disconnect.gif");
	
	public static String typeServer = "server";
	public static String typeClient = "client";
	public static String typeStatus = "status";
	public static String typeLog = "log";

	public static Color client_Color_FG = Color.BLUE;
	public static Color server_Color_FG = Color.MAGENTA;
	public static Color status_Color_FG = Color.RED;
	public static Color time_Color_FG = Color.WHITE;

	public static Color client_Color_BG = Color.WHITE;
	public static Color server_Color_BG = Color.WHITE;
	public static Color status_Color_BG = Color.LIGHT_GRAY;
	public static Color time_Color_BG = Color.BLACK;

	public static Color log_Color_BG = Color.LIGHT_GRAY;

	private ControlCodes controlCodes = new ControlCodes();

	private StyleContext sc_client_data = StyleContext.getDefaultStyleContext();
	private StyleContext sc_server_data = StyleContext.getDefaultStyleContext();
	private StyleContext sc_timestamp = StyleContext.getDefaultStyleContext();
	private StyleContext sc_message = StyleContext.getDefaultStyleContext();

	private AttributeSet aset_client_data;
	private AttributeSet aset_server_data;
	private AttributeSet aset_status;
	private AttributeSet aset_timestamp;

	private String timestamp = "";

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private Calendar cal = Calendar.getInstance();

	public String NEW_LINE = "\r\n";

	public Util()
	{
		aset_timestamp = sc_timestamp.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset_timestamp = sc_timestamp.addAttribute(aset_timestamp, StyleConstants.Foreground, Util.time_Color_FG);
		aset_timestamp = sc_timestamp.addAttribute(aset_timestamp, StyleConstants.Background, Util.time_Color_BG);

		aset_status = sc_message.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset_status = sc_message.addAttribute(aset_status, StyleConstants.Foreground, Util.status_Color_FG);
		aset_status = sc_message.addAttribute(aset_status, StyleConstants.Background, Util.status_Color_BG);

		aset_client_data = sc_client_data.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset_client_data = sc_client_data.addAttribute(aset_client_data, StyleConstants.Foreground, Util.client_Color_FG);
		aset_client_data = sc_client_data.addAttribute(aset_client_data, StyleConstants.Background, Util.client_Color_BG);

		aset_server_data = sc_server_data.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset_server_data = sc_server_data.addAttribute(aset_server_data, StyleConstants.Foreground, Util.server_Color_FG);
		aset_server_data = sc_server_data.addAttribute(aset_server_data, StyleConstants.Background, Util.server_Color_BG);

	}

	public String upperCaseTokens(String input)
	{
		String result = input;

		for (int x = 0; x < controlCodes.getControlCodes().size(); x++)
		{
			result = StringUtils.replaceIgnoreCase(result, controlCodes.getControlCodes().get(x).getToken().toLowerCase(), controlCodes.getControlCodes().get(x).getToken().toUpperCase());
		}

		return result;
	}

	public String decodeControlChars(String input)
	{
		String result = input;

		for (int x = 0; x < controlCodes.getControlCodes().size(); x++)
		{
			result = result.replaceAll(Pattern.quote(controlCodes.getControlCodes().get(x).getUnicode()), controlCodes.getControlCodes().get(x).getToken());
		}

		return result;
	}

	public String encodeControlChars(String input)
	{
		String result = input;

		for (int x = 0; x < controlCodes.getControlCodes().size(); x++)
		{
			result = result.replaceAll(Pattern.quote(controlCodes.getControlCodes().get(x).getToken()), controlCodes.getControlCodes().get(x).getUnicode());
		}

		return result;
	}

	public void centerWindow(Window win)
	{
		Dimension dim = win.getToolkit().getScreenSize();
		win.setLocation(dim.width / 2 - win.getWidth() / 2, dim.height / 2 - win.getHeight() / 2);
	}

	public boolean checkHost(String host)
	{
		try
		{
			InetAddress.getByName(host);
			return (true);
		}
		catch (UnknownHostException uhe)
		{
			return (false);
		}
	}

	public void writeFile(File fileName, String text)
	{
		try
		{
			FileUtils.writeStringToFile(fileName, text,"UTF-8");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String readFile(String fileName)
	{
		String result = "";
		
		try
		{
			result = FileUtils.readFileToString(new File(fileName),"UTF-8");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	public void log(String msg, JTextPane textPane, boolean printtimestamp, String type)
	{
		msg = msg.replace("<CR><LF>", "<*CR*><*LF*>");
		msg = msg.replace("<LF>", "<LF>" + encodeControlChars("<LF>"));
		msg = msg.replace("<CR>", "<CR>" + encodeControlChars("<LF>"));
		msg = msg.replace("<*CR*><*LF*>", "<CR><LF>");
		msg = msg.replace("<CR><LF>", "<CR><LF>" + encodeControlChars("<LF>"));

		if (printtimestamp)
		{
			cal = Calendar.getInstance();

			timestamp = "[" + sdf.format(cal.getTimeInMillis()) + "]";

			textPane.setCharacterAttributes(aset_timestamp, false);

			conditionalNewLine(msg, textPane, type);
			
			textPane.setCharacterAttributes(aset_timestamp, false);

			write_to_pane(textPane, timestamp);
		}

		if (type.equals(typeServer))
		{
			textPane.setCharacterAttributes(aset_server_data, false);
		}
		if (type.equals(typeClient))
		{
			textPane.setCharacterAttributes(aset_client_data, false);
		}
		if (type.equals(typeStatus))
		{
			conditionalNewLine(msg, textPane, type);
			textPane.setCharacterAttributes(aset_status, false);
		}

		write_to_pane(textPane, msg);
	}

	private void write_to_pane(JTextPane textPane, String msg)
	{
		int len = textPane.getDocument().getLength();

		textPane.setCaretPosition(len);
		textPane.repaint();

		textPane.replaceSelection(msg);

		len = textPane.getDocument().getLength();

		textPane.setCaretPosition(len);
		textPane.repaint();
	}

	private void conditionalNewLine(String msg, JTextPane textPane, String type)
	{

		try
		{
			int len = textPane.getDocument().getLength();
			if (len > 0)
			{
				String txt = textPane.getDocument().getText(0, len);

				if (txt.endsWith("\n") == false)
				{
					if (type.equals(typeServer))
					{
						textPane.setCharacterAttributes(aset_server_data, false);
					}
					if (type.equals(typeClient))
					{
						textPane.setCharacterAttributes(aset_client_data, false);
					}
					if (type.equals(typeStatus))
					{
						textPane.setCharacterAttributes(aset_status, false);
					}

					write_to_pane(textPane, NEW_LINE);
				}
			}
		}
		catch (BadLocationException e)
		{

		}

	}

}
