package com.commander4j.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.commander4j.gui.SocketServerGUI;

public class SocketServer extends Thread
{

	private static SocketServer socketServer = null;
	private Socket socket = null;
	private ServerSocket server = null;
	private SocketServerGUI parent;
	private BufferedInputStream in;
	private boolean disconnected = false;
	private boolean stop = false;
	private Util util = new Util();

	// disconnect client
	public synchronized void setDisconnected(boolean cr)
	{
		if (socket != null && cr == true)
		{
			try
			{
				socket.close();
			}
			catch (Exception e)
			{
				util.log("Error closing client. " + e.getMessage() + "\n", parent.textPane, parent.chckbxTimestamp.isSelected(), Util.typeServer);
			}
		}
		disconnected = cr;
		// parent.setClientSocket(null);
	}

	// stop server
	public synchronized void setStop(boolean cr)
	{
		stop = cr;
		if (server != null && cr == true)
		{
			try
			{
				server.close();
			}
			catch (Exception e)
			{
				util.log("Error closing server. " + e.getMessage() + "\n", parent.textPane, parent.chckbxTimestamp.isSelected(), Util.typeStatus);
			}
		}
	}

	private SocketServer(SocketServerGUI parent, ServerSocket s)
	{
		super("SocketServer");
		this.parent = parent;
		server = s;
		setStop(false);
		setDisconnected(false);
		start();
	}

	public static synchronized SocketServer handle(SocketServerGUI parent, ServerSocket s)
	{
		if (socketServer == null)
			socketServer = new SocketServer(parent, s);
		else
		{
			if (socketServer.server != null)
			{
				try
				{
					socketServer.setDisconnected(true);
					socketServer.setStop(true);
					if (socketServer.socket != null)
						socketServer.socket.close();
					if (socketServer.server != null)
						socketServer.server.close();
				}
				catch (Exception e)
				{
					parent.error(e.getMessage());
				}
			}
			socketServer.server = null;
			socketServer.socket = null;
			socketServer = new SocketServer(parent, s);
		}
		return socketServer;
	}

	public void run()
	{
		while (!stop)
		{
			try
			{
				socket = server.accept();
			}
			catch (Exception e)
			{
				if (!stop)
				{
					util.log("Error accepting connection. " + e.getMessage() + "\n", parent.textPane, parent.chckbxTimestamp.isSelected(), Util.typeStatus);
					stop = true;
				}
				continue;
			}

			startServer();

			if (socket != null)
			{
				try
				{
					socket.close();
				}
				catch (Exception e)
				{
					util.log("Error closing client socket : " + e.getMessage() + "\n", parent.textPane, parent.chckbxTimestamp.isSelected(), Util.typeStatus);
				}
				socket = null;
				parent.setClientSocket(socket);
			}
		} // end of while
	}// end of run

	private void startServer()
	{
		parent.setClientSocket(socket);
		InputStream is = null;

		try
		{
			is = socket.getInputStream();
			in = new BufferedInputStream(is);

			util.log("Client connected " + "[" + socket.getInetAddress().getHostAddress() + "].\n", parent.textPane, parent.chckbxTimestamp.isSelected(), Util.typeStatus);

		}
		catch (IOException e)
		{
			util.log("Couldn't open input stream on Client" + e.getMessage() + "\n", parent.textPane, parent.chckbxTimestamp.isSelected(), Util.typeStatus);

			setDisconnected(true);

			return;
		}

		String rec = null;
		while (true)
		{
			rec = null;
			try
			{
				rec = readInputStream(in);// in.readLine();
			}
			catch (Exception e)
			{
				setDisconnected(true);
				if (!disconnected)
				{
					parent.error(e.getMessage(), "Lost Client connection");

					util.log("Server lost Client connection.\n", parent.textPane, parent.chckbxTimestamp.isSelected(), Util.typeStatus);
				}
				else
					util.log("Server closed Client connection.\n", parent.textPane, parent.chckbxTimestamp.isSelected(), Util.typeStatus);
				break;
			}

			if (rec != null)
			{

				util.log(util.decodeControlChars(rec), parent.textPane, parent.chckbxTimestamp.isSelected(), Util.typeClient);

				if (SocketTest.server.chckbxProxy.isSelected())
				{
                	if (disconnected == false)
                	{
                		SocketTest.client.transmit(rec);
                	}
				}

			}
			else
			{
				setDisconnected(true);
				util.log("Client closed connection.\n", parent.textPane, parent.chckbxTimestamp.isSelected(), Util.typeStatus);
				break;
			}
		} // end of while
	} // end of startServer

	private static String readInputStream(BufferedInputStream _in) throws IOException
	{
		String data = "";
		int s = _in.read();
		if (s == -1)
			return null;
		data += "" + (char) s;
		int len = _in.available();

		if (len > 0)
		{
			byte[] byteData = new byte[len];
			_in.read(byteData);
			data += new String(byteData);
		}
		return data;
	}
}
