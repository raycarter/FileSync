package gui;


import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import net.miginfocom.swing.MigLayout;

public class FilesSync {
	JMenuBar menuBar;
	MigLayout migLayout;
	private JFrame frame;
	static TrayIcon trayIcon = null;
	static SystemTray tray = null;
	JLabel srcLocLabel;
	JTextField srcLocTextField;
	JButton srcLocButton;
	
	JLabel desLocLabel;
	JTextField desLocTextField;
	JButton desLocButton;
	
	ArrayList<FileInfo> desFileList = new ArrayList<FileInfo>();
	ArrayList<FileInfo> srcFileList = new ArrayList<FileInfo>();
	
	private class FileInfo {
		String path;
		String name;
		long lastModified;
		boolean isFolder; 
		public FileInfo(String path, String name, long lm, boolean isFolder) {
			this.path = path;
			this.lastModified = lm;
			this.name = name;
			this.isFolder = isFolder;
		}
		
		@Override
		public String toString(){
			return getPathName() + "\t\t" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(lastModified) + "\n");
		}
		
		public String getPathName(){
			
			if (isFolder) {
				return path;
			}
			else {
				return path + File.separator + name;
			}
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FilesSync window = new FilesSync();
					window.frame.setVisible(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FilesSync() {
		initialize();
	}

	private void initialize() {
		this.frame = new JFrame();
		this.frame.setTitle("File Sync");
		this.frame.setIconImage(Toolkit.getDefaultToolkit().getImage("files/trans128.png"));

		this.frame.setBounds(0, 0, 500, 300);
		this.frame.setLocationRelativeTo(null);
		this.frame.setResizable(false);
		
		menuBar = new JMenuBar();
		JMenu menu = new JMenu("Help...");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription(
		        "");
		menuBar.add(menu);
		JMenuItem aboutMenuItem = new JMenuItem("About",
                KeyEvent.VK_A);
		aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_1, ActionEvent.ALT_MASK));
		aboutMenuItem.getAccessibleContext().setAccessibleDescription(
		"about...");
		menu.add(aboutMenuItem);
		aboutMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				JOptionPane.showMessageDialog(frame, "This program is lisenced under GPL v2. \n"+
													 "                   Sheng, Li "+
													 " 2014.07", 
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
				//JFileChooser srcChooser = new JFileChooser(new File("e:\\"));
				JFileChooser srcChooser = new JFileChooser();
				srcChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				srcChooser.showOpenDialog(null);
				srcLocTextField.setText(srcChooser.getSelectedFile().getPath());
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
				//JFileChooser desChooser = new JFileChooser(new File("e:\\"));
				JFileChooser desChooser = new JFileChooser();
				desChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				desChooser.showOpenDialog(null);
				desLocTextField.setText(desChooser.getSelectedFile().getPath());
			}
		});
		
		JButton confirmButton = new JButton("confirm");
		confirmButton.setBounds(350, 200, 80, 20);
		confirmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				desLocTextField.setEditable(false);
				srcLocTextField.setEditable(false);
				//read file list in src and des
				File srcFolderPath = new File(srcLocTextField.getText());
				File desFolderPath = new File(desLocTextField.getText());
				listFilesInFolder(desFolderPath, desFileList, desLocTextField.getText());
				listFilesInFolder(srcFolderPath, srcFileList, srcLocTextField.getText());
				
				showFileLists();
				
				//compare

				compareFolder(desLocTextField.getText());
				try {
					compareFiles(srcLocTextField.getText(), desLocTextField.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				

				desLocTextField.setEditable(true);
				srcLocTextField.setEditable(true);
			}
		});
		
		rootPanel.add(srcLocLabel);
		rootPanel.add(srcLocTextField);
		rootPanel.add(srcLocButton);
		rootPanel.add(desLocLabel);
		rootPanel.add(desLocTextField);
		rootPanel.add(desLocButton);
		rootPanel.add(confirmButton);
		

		this.migLayout = new MigLayout("", "5px[grow, fill]5px", "5px[grow,fill]5px");
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
				}
				catch (AWTException ex) {
					ex.printStackTrace();
				}
			}
		});

	}

	protected void compareFiles(String srcPre, String desPre) throws IOException {
		for (int i = 0; i < srcFileList.size(); i++) {
			if (srcFileList.get(i).isFolder) {
				continue;
			}
			boolean ident = false;
			String srcPath = srcFileList.get(i).path;
			String srcName = srcFileList.get(i).name;
			for (int j = 0; j < desFileList.size(); j++) {
				if (desFileList.get(j).isFolder) {
					continue;
				}
				String desPath = desFileList.get(j).path;
				String desName = desFileList.get(j).name;
				if (srcPath.equals(desPath)) {
					if (srcName.compareTo(desName) == 0 && srcFileList.get(i).lastModified == desFileList.get(j).lastModified) {
						ident = true;
						break;
					}
					else if (srcName.compareTo(desName) > 0) {
						continue;
					}
					else if (srcName.compareTo(desName) < 0){
						continue;
					}
				}
				else {
					continue;
				}
				
			}
			if (ident) {
				continue;
			}
			else {
				File srcFile = new File(srcPre + srcFileList.get(i).getPathName());
				File desFile = new File(desPre + srcFileList.get(i).getPathName());
				if (desFile.exists()) {
					Files.delete(desFile.toPath());
				}
				Files.copy(srcFile.toPath(), 
						desFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
				System.out.println("copy: "+ srcFileList.get(i).getPathName());
			}
		}
		
	}

	protected void compareFolder( String pre) {
		for (int i = 0; i < srcFileList.size(); i++) {
			boolean ident = false;
			if (srcFileList.get(i).isFolder == false) {
				continue;
			}
			String srcPathName = srcFileList.get(i).getPathName();
			for (int j = 0; j < desFileList.size(); j++) {
				if (desFileList.get(j).isFolder == false) {
					continue;
				}
				String desPathName = desFileList.get(j).getPathName();
				if (srcPathName.compareTo(desPathName) == 0) {
					ident = true;
					break;
				}
				else if (srcPathName.compareTo(desPathName) > 0) {
					continue;
				}
				else if (srcPathName.compareTo(desPathName) < 0){
					continue;
				}
			}
			if (ident) {
				continue;
			}
			else {
				File desFolder = new File(pre + srcPathName);
				desFolder.mkdir();
				System.out.println("copy: "+ pre + srcPathName);
			}
		}
		
	}

	protected void listFilesInFolder(File folder, ArrayList<FileInfo> fileInfoList, String pre) {
		for (File fileEntry : folder.listFiles()){
			if(fileEntry.isDirectory()){
				fileInfoList.add(new FileInfo(fileEntry.getPath().replace(pre, ""), fileEntry.getName(), fileEntry.lastModified(), true));
				listFilesInFolder(fileEntry, fileInfoList, pre);
			}
			else {

				fileInfoList.add(new FileInfo(fileEntry.getParent().replace(pre, ""), fileEntry.getName(), fileEntry.lastModified(), false));
			}
		}
		/*
		for (FileInfo fileInfo : fileInfoList) {
			System.out.println(fileInfo.toString());
		}*/
		
	}

	void showFileLists(){
		System.out.println("src list: "+srcFileList.size());
		for (FileInfo fileInfo : srcFileList) {
			System.out.print(fileInfo.toString());
		}
		System.out.println("des list: "+desFileList.size());
		for (FileInfo fileInfo : desFileList) {
			System.out.print(fileInfo.toString());
		}
		
	}
	private void tray() {
		tray = SystemTray.getSystemTray();

		PopupMenu pop = new PopupMenu();
		MenuItem show = new MenuItem("open");
		MenuItem exit = new MenuItem("exit");
		pop.add(show);
		pop.add(exit);
		trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("files/trans16.png"), "File Sync", pop);

		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2) return;
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
