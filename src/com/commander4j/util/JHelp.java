package com.commander4j.util;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

import javax.swing.JButton;

public class JHelp
{
	private boolean HelpAvailable = false;
	private Desktop desktop;
	private String helpURL = "";

	public JHelp()
	{
		try
		{
			if (Desktop.isDesktopSupported())
			{
				desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE))
				{
					HelpAvailable = true;
				}
			}
		}
		catch (Exception ex)
		{
			HelpAvailable = false;
		}
	}

	public void setHelpURL(String url)
	{
		helpURL = url == null ? "" : url;
	}

	public void enableHelpOnButton(JButton button, String helpsetID)
	{
		if (HelpAvailable)
		{
			try
			{
				setHelpURL(helpsetID);
				button.addActionListener(new ButtonHandler());
			}
			catch (Exception ex)
			{
				HelpAvailable = false;
			}
		}
	}

	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (HelpAvailable == false)
			{
				return;
			}
			try
			{
				URI uri;
				if (helpURL.contains("http"))
				{
					uri = new URI(helpURL);
				}
				else
				{
					File file = new File(helpURL);
					uri = file.toURI().normalize();
				}
				desktop.browse(uri);
			}
			catch (Exception ex)
			{
				// silent — same behaviour as the xml_viewer JHelp
			}
		}
	}
}
