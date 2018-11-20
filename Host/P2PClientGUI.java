import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.awt.event.ActionEvent;
import javax.swing.JTable;

public class P2PClientGUI extends JFrame {

  private JPanel contentPane;
  private JTextField portInput;
  private JTextField usernameInput;
  private JTextField hostnameInput;
  private JTextField keywordInput;
  private JTextField commandInput;

  private JTable table;
  private DefaultTableModel tModel;
  private String[] columnNames = { "Speed", "Hostname", "Filename" };

  /** Centralized server */
  private Socket controlSocket;
  private DataOutputStream outToServer;
  private DataInputStream inFromServer;

  /** Host: P2P Client and Server */
  private ClientInstance client;
  private ServerSocket welcomeData;
  //private FTPServer server;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          P2PClientGUI frame = new P2PClientGUI();
          frame.pack();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void connect(String serverInfo, String userInfo) throws Exception {
	  
	  System.out.println("Connect 1");
	  welcomeData = new ServerSocket(7635);
	  
	  DataOutputStream outData;
	 
	  StringTokenizer tokens = new StringTokenizer(serverInfo);

    String serverName = tokens.nextToken();
    int controlPort = Integer.parseInt(tokens.nextToken());

    controlSocket = new Socket(serverName, controlPort);
    outToServer = new DataOutputStream(controlSocket.getOutputStream());
    inFromServer = new DataInputStream(new BufferedInputStream(controlSocket.getInputStream()));

    outToServer.writeUTF("7635 " + userInfo);
    Socket dataSocket = welcomeData.accept();
    
    System.out.println("Sent user info");
    
    FileInputStream file = new FileInputStream("filelist.xml");
    byte[] buffer = new byte[1024];
    int bytes = 0;
    while ((bytes = file.read(buffer)) != -1) {
      outToServer.write(buffer, 0, bytes);
    }
    file.close();
    System.out.println("File sent");

  }

  private void fileSearch(String keyword) throws Exception {
	  System.out.println("Search initiated");
    this.outToServer.writeUTF(keyword);
    System.out.println("Keyword sent");
    String word = this.inFromServer.readUTF();
    StringTokenizer tokens = new StringTokenizer(word);
    tModel = new DefaultTableModel(columnNames, 0);
    while (tokens.hasMoreTokens()) {
      String speed = tokens.nextToken();
      String hostName = tokens.nextToken();
      String fileName = tokens.nextToken();
      if (tokens.nextToken().equals("\n")) { // Throw away corrupted lines that do not end with \n
        updateTable(speed, hostName, fileName);
      }
    }
    tModel.fireTableDataChanged();
  }

  private void updateTable(String speed, String hostName, String fileName) {
    String[] data = { speed, hostName, fileName };
    tModel.addRow(data);
  }

  /**
   * Create the frame.
   */
  public P2PClientGUI() throws Exception {

    setTitle("P2P Project 2");
    setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    GridBagLayout gbl_contentPane = new GridBagLayout();
    gbl_contentPane.columnWidths = new int[] { 420, 0 };
    gbl_contentPane.rowHeights = new int[] { 34, 0, 0, 0 };
    gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
    gbl_contentPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE, 1.0, 1.0 };
    contentPane.setLayout(gbl_contentPane);

    /**
     * Connection Panel
     */
    JPanel ConnectionPanel = new JPanel();
    ConnectionPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
    GridBagConstraints gbc_ConnectionPanel = new GridBagConstraints();
    gbc_ConnectionPanel.insets = new Insets(0, 0, 5, 0);
    gbc_ConnectionPanel.gridheight = 2;
    gbc_ConnectionPanel.gridwidth = 0;
    gbc_ConnectionPanel.fill = GridBagConstraints.BOTH;
    gbc_ConnectionPanel.gridx = 0;
    gbc_ConnectionPanel.gridy = 0;
    contentPane.add(ConnectionPanel, gbc_ConnectionPanel);
    GridBagLayout gbl_ConnectionPanel = new GridBagLayout();
    gbl_ConnectionPanel.columnWidths = new int[] { 105, 105, 105, 105, 0 };
    gbl_ConnectionPanel.rowHeights = new int[] { 23, 23, 23, 0 };
    gbl_ConnectionPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gbl_ConnectionPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
    ConnectionPanel.setLayout(gbl_ConnectionPanel);

    JLabel lblHostname = new JLabel("Hostname:");
    lblHostname.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_lblHostname = new GridBagConstraints();
    gbc_lblHostname.fill = GridBagConstraints.BOTH;
    gbc_lblHostname.insets = new Insets(0, 0, 5, 5);
    gbc_lblHostname.gridx = 0;
    gbc_lblHostname.gridy = 0;
    ConnectionPanel.add(lblHostname, gbc_lblHostname);

    hostnameInput = new JTextField();
    hostnameInput.setColumns(10);
    GridBagConstraints gbc_hostnameInput = new GridBagConstraints();
    gbc_hostnameInput.fill = GridBagConstraints.BOTH;
    gbc_hostnameInput.insets = new Insets(0, 0, 5, 5);
    gbc_hostnameInput.gridx = 1;
    gbc_hostnameInput.gridy = 0;
    ConnectionPanel.add(hostnameInput, gbc_hostnameInput);

    JLabel lblPort = new JLabel("Port:");
    lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_lblPort = new GridBagConstraints();
    gbc_lblPort.fill = GridBagConstraints.BOTH;
    gbc_lblPort.insets = new Insets(0, 0, 5, 5);
    gbc_lblPort.gridx = 2;
    gbc_lblPort.gridy = 0;
    ConnectionPanel.add(lblPort, gbc_lblPort);

    portInput = new JTextField();
    portInput.setColumns(10);
    GridBagConstraints gbc_portInput = new GridBagConstraints();
    gbc_portInput.fill = GridBagConstraints.BOTH;
    gbc_portInput.insets = new Insets(0, 0, 5, 0);
    gbc_portInput.gridx = 3;
    gbc_portInput.gridy = 0;
    ConnectionPanel.add(portInput, gbc_portInput);

    JLabel lblUsername = new JLabel("Username: ");
    lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_lblUsername = new GridBagConstraints();
    gbc_lblUsername.fill = GridBagConstraints.BOTH;
    gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
    gbc_lblUsername.gridx = 0;
    gbc_lblUsername.gridy = 1;
    ConnectionPanel.add(lblUsername, gbc_lblUsername);

    usernameInput = new JTextField();
    usernameInput.setColumns(10);
    GridBagConstraints gbc_usernameInput = new GridBagConstraints();
    gbc_usernameInput.fill = GridBagConstraints.BOTH;
    gbc_usernameInput.insets = new Insets(0, 0, 5, 5);
    gbc_usernameInput.gridx = 1;
    gbc_usernameInput.gridy = 1;
    ConnectionPanel.add(usernameInput, gbc_usernameInput);

    JLabel lblServer = new JLabel("Server Hostname:");
    lblServer.setVerticalAlignment(SwingConstants.TOP);
    lblServer.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_lblServer = new GridBagConstraints();
    gbc_lblServer.fill = GridBagConstraints.BOTH;
    gbc_lblServer.insets = new Insets(0, 0, 5, 5);
    gbc_lblServer.gridx = 2;
    gbc_lblServer.gridy = 1;
    ConnectionPanel.add(lblServer, gbc_lblServer);

    JTextArea serverInput = new JTextArea();
    serverInput.setColumns(10);
    GridBagConstraints gbc_serverInput = new GridBagConstraints();
    gbc_serverInput.fill = GridBagConstraints.BOTH;
    gbc_serverInput.insets = new Insets(0, 0, 5, 0);
    gbc_serverInput.gridx = 3;
    gbc_serverInput.gridy = 1;
    ConnectionPanel.add(serverInput, gbc_serverInput);

    JLabel lblSpeed = new JLabel("Speed:");
    lblSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_lblSpeed = new GridBagConstraints();
    gbc_lblSpeed.fill = GridBagConstraints.BOTH;
    gbc_lblSpeed.insets = new Insets(0, 0, 0, 5);
    gbc_lblSpeed.gridx = 0;
    gbc_lblSpeed.gridy = 2;
    ConnectionPanel.add(lblSpeed, gbc_lblSpeed);

    JComboBox speedInput = new JComboBox();
    speedInput.setModel(new DefaultComboBoxModel(new String[] { "Ethernet", "T1", "T3" }));
    GridBagConstraints gbc_speedInput = new GridBagConstraints();
    gbc_speedInput.fill = GridBagConstraints.BOTH;
    gbc_speedInput.insets = new Insets(0, 0, 0, 5);
    gbc_speedInput.gridx = 1;
    gbc_speedInput.gridy = 2;
    ConnectionPanel.add(speedInput, gbc_speedInput);

    JButton btnConnnect = new JButton("Connect");
    

    GridBagConstraints gbc_btnConnnect = new GridBagConstraints();
    gbc_btnConnnect.fill = GridBagConstraints.BOTH;
    gbc_btnConnnect.gridx = 3;
    gbc_btnConnnect.gridy = 2;
    ConnectionPanel.add(btnConnnect, gbc_btnConnnect);
    


    /**
     * Search Panel
     */
    
    JPanel SearchPanel = new JPanel();
    SearchPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
    GridBagConstraints gbc_SearchPanel = new GridBagConstraints();
    gbc_SearchPanel.insets = new Insets(0, 0, 5, 0);
    gbc_SearchPanel.gridheight = 2;
    gbc_SearchPanel.gridwidth = 0;
    gbc_SearchPanel.fill = GridBagConstraints.BOTH;
    gbc_SearchPanel.gridx = 0;
    gbc_SearchPanel.gridy = 3;
    contentPane.add(SearchPanel, gbc_SearchPanel);
    GridBagLayout gbl_SearchPanel = new GridBagLayout();
    gbl_SearchPanel.columnWidths = new int[] { 105, 105, 105, 105, 0 };
    gbl_SearchPanel.rowHeights = new int[] { 23, 23, 23, 0 };
    gbl_SearchPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gbl_SearchPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
    SearchPanel.setLayout(gbl_SearchPanel);

    // Label for keyword search
    JLabel lblKeyword = new JLabel("Keyword:");
    GridBagConstraints gbc_lblKeyword = new GridBagConstraints();
    gbc_lblKeyword.fill = GridBagConstraints.BOTH;
    gbc_lblKeyword.insets = new Insets(0, 0, 5, 0);
    gbc_lblKeyword.gridx = 0;
    gbc_lblKeyword.gridy = 1;

    // Text input for keyword search
    keywordInput = new JTextField();
    keywordInput.setColumns(25);
    GridBagConstraints gbc_keywordInput = new GridBagConstraints();
    gbc_keywordInput.fill = GridBagConstraints.BOTH;
    gbc_keywordInput.insets = new Insets(0, 0, 5, 0);
    gbc_keywordInput.gridx = 1;
    gbc_keywordInput.gridy = 1;



    tModel = new DefaultTableModel(columnNames, 0);
    table = new JTable(tModel);
    table.setPreferredScrollableViewportSize(new Dimension(300, 100));

    // Search Button for keyword search
    JButton fileSearch = new JButton("Search");
    GridBagConstraints gbc_fileSearch = new GridBagConstraints();
    gbc_fileSearch.fill = GridBagConstraints.BOTH;
    gbc_fileSearch.insets = new Insets(0, 0, 5, 0);
    gbc_fileSearch.gridx = 2;
    gbc_fileSearch.gridy = 1;
    
    fileSearch.setEnabled(false);
    fileSearch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
    	  try {
    	  fileSearch(keywordInput.getText());
    	  }
    	  catch(Exception e) {
    		  e.printStackTrace();
    	  }
      }
    });
    
    btnConnnect.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent p) {
      	System.out.println("Connect button pressed");
      	if(!hostnameInput.getText().isEmpty() && !portInput.getText().isEmpty() && !usernameInput.getText().isEmpty() && !serverInput.getText().isEmpty()) {
          String serverInfo = hostnameInput.getText() + " " + portInput.getText();
          String userInfo = usernameInput.getText() + " " + speedInput.getSelectedIndex() + " " + serverInput.getText();
          try {
          	System.out.println("Before server");
            //server = new FTPServer();
            System.out.println("After server");
            connect(serverInfo, userInfo);
            fileSearch.setEnabled(true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        }
      });
    
    JScrollPane test = new JScrollPane(table);
    GridBagConstraints gbc_test = new GridBagConstraints();
    gbc_test.fill = GridBagConstraints.BOTH;
    gbc_test.insets = new Insets(0, 0, 5, 0);
    gbc_test.gridx = 1;
    gbc_test.gridy = 2;

    contentPane.add(SearchPanel, gbc_SearchPanel);
    SearchPanel.add(lblKeyword,gbc_lblKeyword);
    SearchPanel.add(keywordInput, gbc_keywordInput);
    SearchPanel.add(fileSearch, gbc_fileSearch);
    SearchPanel.add(test, gbc_test);
    /**
     * FTP Panel
     */
    
    JPanel FTPPanel = new JPanel();
    ConnectionPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
    GridBagConstraints gbc_FTPPanel = new GridBagConstraints();
    gbc_FTPPanel.insets = new Insets(0, 0, 5, 0);
    gbc_FTPPanel.gridheight = 2;
    gbc_FTPPanel.gridwidth = 0;
    gbc_FTPPanel.fill = GridBagConstraints.BOTH;
    gbc_FTPPanel.gridx = 0;
    gbc_FTPPanel.gridy = 5;
    contentPane.add(FTPPanel, gbc_FTPPanel);
    GridBagLayout gbl_FTPPanel = new GridBagLayout();
    gbl_FTPPanel.columnWidths = new int[] { 105, 105, 105, 105, 0 };
    gbl_FTPPanel.rowHeights = new int[] { 23, 23, 23, 0 };
    gbl_FTPPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    gbl_FTPPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
    FTPPanel.setLayout(gbl_FTPPanel);


    JTextArea commandArea = new JTextArea(14, 58);
    commandArea.setEditable(false);
    JScrollPane scroll = new JScrollPane(commandArea);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    GridBagConstraints gbc_commandArea = new GridBagConstraints();
    gbc_commandArea.fill = GridBagConstraints.BOTH;
    gbc_commandArea.insets = new Insets(0, 0, 5, 5);
    gbc_commandArea.gridx = 1;
    gbc_commandArea.gridy = 3;
    

    // Search Button for keyword search
    JButton commandBtn = new JButton("Go");
    GridBagConstraints gbc_commandBtn = new GridBagConstraints();
    gbc_commandBtn.fill = GridBagConstraints.BOTH;
    gbc_commandBtn.insets = new Insets(0, 0, 5, 0);
    gbc_commandBtn.gridx = 2;
    gbc_commandBtn.gridy = 1;
    
    
  
    commandBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        String command = commandInput.getText();
        StringTokenizer tokens = new StringTokenizer(command);
        commandArea.append(">>" + command + "\n");
        try {
          if (tokens.nextToken().equals("connect")) {
            String serverName = tokens.nextToken();
            int port = Integer.parseInt(tokens.nextToken());

            client = new ClientInstance(serverName, port);


            commandArea.append("Connected to " + serverName + ":" + port + "\n");
          } else {
            commandArea.append(client.executeCommand(command) + "\n");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

      }
    });
    
    JButton btnDisconnect = new JButton("Disconnect");
    GridBagConstraints gbc_btnDisconnect = new GridBagConstraints();
    gbc_btnDisconnect.fill = GridBagConstraints.BOTH;
    gbc_btnDisconnect.insets = new Insets(0, 0, 5, 0);
    gbc_btnDisconnect.gridx = 1;
    gbc_btnDisconnect.gridy = 4;
    
    
    btnDisconnect.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
         
        }
    });


    JLabel CommandLbl = new JLabel("Enter Command: ");
    CommandLbl.setVerticalAlignment(SwingConstants.TOP);
    CommandLbl.setHorizontalAlignment(SwingConstants.RIGHT);
    GridBagConstraints gbc_CommandLbl = new GridBagConstraints();
    gbc_CommandLbl.fill = GridBagConstraints.BOTH;
    gbc_CommandLbl.insets = new Insets(0, 0, 5, 5);
    gbc_CommandLbl.gridx = 0;
    gbc_CommandLbl.gridy = 1;
    
    
    commandInput = new JTextField();
    commandInput.setColumns(15);
    GridBagConstraints gbc_commandInput = new GridBagConstraints();
    gbc_commandInput.fill = GridBagConstraints.BOTH;
    gbc_commandInput.insets = new Insets(0, 0, 5, 0);
    gbc_commandInput.gridx = 1;
    gbc_commandInput.gridy = 1;

  
    
    contentPane.add(FTPPanel, gbc_FTPPanel);
    FTPPanel.add(CommandLbl, gbc_CommandLbl);
    FTPPanel.add(commandInput, gbc_commandInput);
    FTPPanel.add(commandBtn, gbc_commandBtn);
    FTPPanel.add(scroll, gbc_commandArea);
    FTPPanel.add(btnDisconnect, gbc_btnDisconnect);


  }

}
