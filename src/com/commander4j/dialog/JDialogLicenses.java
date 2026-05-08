package com.commander4j.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.commander4j.network.Util;
import com.commander4j.sys.JLicenseInfo;

public class JDialogLicenses extends JDialog
{

	private static final long serialVersionUID = 1L;
	private static final Font FONT_LIST = new Font("Monospaced", 0, 11);

	private JPanel contentPanel = new JPanel();
	JList<JLicenseInfo> list = new JList<JLicenseInfo>();
	private Util util = new Util();
	private static int widthadjustment = 0;
	private static int heightadjustment = 0;

	public JDialogLicenses(JFrame frame)
	{
		super(frame);
		JDialogLicenses me = this;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		util.setLookAndFeel("Nimbus");
		setTitle("Libraries");
		setBounds(100, 100, 540, 350);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 24, 521, 246);
		contentPanel.add(scrollPane);

		populateList();

		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					if (list.isSelectionEmpty() == false)
					{
						JLicenseInfo iii = ((JLicenseInfo) list.getSelectedValue());

						JDialogDisplayLicense dialog = new JDialogDisplayLicense(me, iii);
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
					}
				}
			}
		});

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(list);

		JPanel buttonPane = new JPanel();
		buttonPane.setBounds(0, 271, 521, 39);
		contentPanel.add(buttonPane);
		buttonPane.setLayout(null);
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
			okButton.setBounds(200, 0, 128, 32);
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}

		JLabel lblNewLabel = new JLabel(padRight("Library", JLicenseInfo.width_description) + padRight("Version", JLicenseInfo.width_version) + "Licence");

		lblNewLabel.setBounds(3, 5, 727, 16);
		lblNewLabel.setFont(FONT_LIST);
		contentPanel.add(lblNewLabel);

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		widthadjustment = util.getOSWidthAdjustment();
		heightadjustment = util.getOSHeightAdjustment();

		GraphicsDevice gd = util.getGraphicsDevice();

		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		Rectangle screenBounds = gc.getBounds();

		setBounds(screenBounds.x + ((screenBounds.width - JDialogLicenses.this.getWidth()) / 2), screenBounds.y + ((screenBounds.height - JDialogLicenses.this.getHeight()) / 2), JDialogLicenses.this.getWidth() + widthadjustment,
				JDialogLicenses.this.getHeight() + heightadjustment);
	}

	private void populateList()
	{
		DefaultComboBoxModel<JLicenseInfo> DefComboBoxMod = new DefaultComboBoxModel<JLicenseInfo>();

		JLicenseInfo tempCust = new JLicenseInfo();

		LinkedList<JLicenseInfo> licenceList = tempCust.getLicences();

		for (int j = 0; j < licenceList.size(); j++)
		{
			JLicenseInfo t = (JLicenseInfo) licenceList.get(j);
			DefComboBoxMod.addElement(t);
		}

		ListModel<JLicenseInfo> jList1Model = DefComboBoxMod;
		list.setModel(jList1Model);
		list.setSelectedIndex(0);
		list.setFont(FONT_LIST);
		list.ensureIndexIsVisible(0);
	}

	private static String padRight(String input, int size)
	{
		if (input == null)
		{
			input = "";
		}
		if (input.length() >= size)
		{
			return input.substring(0, size);
		}
		StringBuilder sb = new StringBuilder(input);
		while (sb.length() < size)
		{
			sb.append(' ');
		}
		return sb.toString();
	}
}
