package gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;
import processing.FileInfo;
import processing.SyncSrcDes;

public class FilesSync {
	JMenuBar menuBar;
	MigLayout migLayout;
	public JFrame frame;
	static TrayIcon trayIcon = null;
	static SystemTray tray = null;
	public JLabel srcLocLabel;
	public JTextField srcLocTextField;
	public JButton srcLocButton;

	public JLabel desLocLabel;
	public JTextField desLocTextField;
	public JButton desLocButton;

	public JProgressBar progressBar;
	public JButton confirmButton;
	public ArrayList<FileInfo> desFileList;
	public ArrayList<FileInfo> srcFileList;
	public String versionString = "1.0.2";

	public FilesSync fs;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FilesSync window = new FilesSync();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FilesSync() {
		initialize();
	}

	private void initialize() {
		this.fs = this;
		this.frame = new JFrame();
		this.frame.setTitle("File Sync");
		this.frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"files/trans128.png"));

		this.frame.setBounds(0, 0, 500, 300);
		this.frame.setLocationRelativeTo(null);
		this.frame.setResizable(false);

		menuBar = new JMenuBar();
		JMenu menu = new JMenu("Help...");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription("");
		menuBar.add(menu);
		JMenuItem aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
		aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		aboutMenuItem.getAccessibleContext().setAccessibleDescription(
				"about...");
		menu.add(aboutMenuItem);
		aboutMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JOptionPane.showMessageDialog(frame,
						"This program is lisenced under GPL v2. \n"
								+ "Version " + versionString + ""
								+ "           Sheng, Li " + " 2012.05",
						"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		frame.setJMenuBar(menuBar);

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(null);
		rootPanel.setSize(500, 300);
		rootPanel.setBackground(new Color(255, 255, 255));
		srcLocLabel = new JLabel("src folder:");
		srcLocLabel.setBounds(20, 20, 100, 20);
		srcLocTextField = new JTextField();
		srcLocTextField.setBounds(20, 50, 350, 20);
		srcLocButton = new JButton("choose");
		srcLocButton.setBounds(375, 50, 100, 20);
		srcLocButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// JFileChooser srcChooser = new JFileChooser(new File("e:\\"));
				JFileChooser srcChooser = new JFileChooser();
				srcChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (srcChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
					srcLocTextField.setText(srcChooser.getSelectedFile()
							.getPath());
			}
		});

		desLocLabel = new JLabel("des folder:");
		desLocLabel.setBounds(20, 90, 100, 20);
		desLocTextField = new JTextField();
		desLocTextField.setBounds(20, 120, 350, 20);
		desLocButton = new JButton("choose");
		desLocButton.setBounds(375, 120, 100, 20);
		desLocButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// JFileChooser desChooser = new JFileChooser(new File("e:\\"));
				JFileChooser desChooser = new JFileChooser();
				desChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (desChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
					desLocTextField.setText(desChooser.getSelectedFile()
							.getPath());
			}
		});

		desLocTextField.setEditable(false);
		srcLocTextField.setEditable(false);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setBounds(20, 160, 450, 25);
		progressBar.setStringPainted(true);
		confirmButton = new JButton("confirm");
		confirmButton.setBounds(350, 200, 80, 20);
		confirmButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SyncSrcDes syncSrcDes = new SyncSrcDes(fs);
				new Thread(syncSrcDes).start();
			}
		});

		rootPanel.add(srcLocLabel);
		rootPanel.add(srcLocTextField);
		rootPanel.add(srcLocButton);
		rootPanel.add(desLocLabel);
		rootPanel.add(desLocTextField);
		rootPanel.add(desLocButton);
		rootPanel.add(progressBar);
		rootPanel.add(confirmButton);

		this.migLayout = new MigLayout("", "5px[grow, fill]5px",
				"5px[grow,fill]5px");
		this.frame.getContentPane().setLayout(this.migLayout);
		this.frame.getContentPane().add(rootPanel, "cell 0 0");
		if (SystemTray.isSupported()) {
			tray();
		}

		this.frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

			public void windowIconified(WindowEvent e) {
				try {
					FilesSync.tray.add(FilesSync.trayIcon);

					FilesSync.this.frame.dispose();
				} catch (AWTException ex) {
					ex.printStackTrace();
				}
			}
		});

	}

	private void tray() {
		tray = SystemTray.getSystemTray();

		PopupMenu pop = new PopupMenu();
		MenuItem show = new MenuItem("open");
		MenuItem exit = new MenuItem("exit");
		pop.add(show);
		pop.add(exit);
		trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(
				"files/trans16.png"), "File Sync", pop);

		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2)
					return;
				FilesSync.tray.remove(FilesSync.trayIcon);
				FilesSync.this.frame.setExtendedState(0);
				FilesSync.this.frame.setVisible(true);
				FilesSync.this.frame.toFront();
			}
		});
		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FilesSync.tray.remove(FilesSync.trayIcon);
				FilesSync.this.frame.setExtendedState(0);
				FilesSync.this.frame.setVisible(true);
				FilesSync.this.frame.toFront();
			}
		});
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

}
