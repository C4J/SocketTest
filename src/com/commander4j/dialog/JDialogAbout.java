package com.commander4j.dialog;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.commander4j.network.Util;

public class JDialogAbout extends JDialog
{

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private static int widthadjustment = 0;
	private static int heightadjustment = 0;
	private JLabel jLabelWebPage;
	private Util util = new Util();

	public JDialogAbout()
	{
		setResizable(false);
		setTitle("About");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		util.setLookAndFeel("Nimbus");

		setBounds(100, 100, 250, 181);
		getContentPane().setLayout(null);
		contentPanel.setBackground(new Color(241, 241, 241));
		contentPanel.setBounds(0, 0, getWidth(), getHeight());
		contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);

		JLabel lbl_description = new JLabel("David Garratt");
		lbl_description.setFont(new Font("Arial", Font.ITALIC, 13));
		lbl_description.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_description.setBounds(6, 43, 224, 22);
		contentPanel.add(lbl_description);

		{
			JButton okButton = new JButton(Util.okIcon);
			okButton.setText("Ok");
			okButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					dispose();
				}
			});
			okButton.setBounds(67, 103, 103, 30);
			contentPanel.add(okButton);
			okButton.setActionCommand("OK");
			getRootPane().setDefaultButton(okButton);
		}

		{
			jLabelWebPage = new JLabel();
			jLabelWebPage.setFont(new Font("Arial", Font.PLAIN, 12));
			jLabelWebPage.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(jLabelWebPage);
			jLabelWebPage.setText("https://www.commander4j.com");
			jLabelWebPage.setBounds(2, 77, 229, 14);
			jLabelWebPage.setForeground(new Color(0, 0, 255));
			jLabelWebPage.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					try
					{
						if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
						{
							Desktop.getDesktop().browse(new URI("http://www.commander4j.com"));
						}
					}
					catch (Exception ex)
					{
						JOptionPane.showMessageDialog(JDialogAbout.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

				public void mouseExited(MouseEvent evt)
				{
					Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
					setCursor(normalCursor);
				}

				public void mouseEntered(MouseEvent evt)
				{
					Cursor hourglassCursor = new Cursor(Cursor.HAND_CURSOR);
					setCursor(hourglassCursor);
				}
			});
		}

		widthadjustment = util.getOSWidthAdjustment();
		heightadjustment = util.getOSHeightAdjustment();

		GraphicsDevice gd = util.getGraphicsDevice();

		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		Rectangle screenBounds = gc.getBounds();

		setBounds(screenBounds.x + ((screenBounds.width - JDialogAbout.this.getWidth()) / 2), screenBounds.y + ((screenBounds.height - JDialogAbout.this.getHeight()) / 2), JDialogAbout.this.getWidth() + widthadjustment, JDialogAbout.this.getHeight() + heightadjustment);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JLabel lbl_type = new JLabel("Written by");
				lbl_type.setFont(new Font("Arial", Font.ITALIC, 13));
				lbl_type.setBounds(6, 11, 224, 22);
				lbl_type.setHorizontalAlignment(SwingConstants.CENTER);
				contentPanel.add(lbl_type);
			}
		});
	}
}
