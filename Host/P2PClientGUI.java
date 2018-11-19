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

import com.sun.security.ntlm.Client;

import java.awt.Color;

import java.awt.Component;
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
  private FTPServer server;


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

  private boolean connect(String serverInfo, String userInfo) throws Exception {

    StringTokenizer tokens = new StringTokenizer(serverInfo);

    String serverName = tokens.nextToken();
    int controlPort = Integer.parseInt(tokens.nextToken());

    controlSocket = new Socket(serverName, controlPort);
    outToServer = new DataOutputStream(controlSocket.getOutputStream());
    inFromServer = new DataInputStream(new BufferedInputStream(controlSocket.getInputStream()));

    outToServer.writeUTF(userInfo);

    FileInputStream file = new FileInputStream("filelist.xml");
    byte[] buffer = new byte[1024];
    int bytes = 0;
    while ((bytes = file.read(buffer)) != -1) {
      outToServer.write(buffer, 0, bytes);
    }
    file.close();
    return inFromServer.readUTF().equals("OK");

  }

  private void fileSearch(String keyword) {
    this.outToServer.writeUTF(keyword);
    String word = this.inFromServer.readUTF();
    StringTokenizer tokens = new StringTokenizer(word);
    tModel = new DefaultTableModel(columnNames, 0);
    while(tokens.hasMoreTokens()){
      String speed = tokens.nextToken();
      String hostName = tokens.nextToken();
      String fileName = tokens.nextToken();
      if(tokens.nextToken().equals("\n")){ //Throw away corrupted lines that do not end with \n
        updateTable(speed, hostName, fileName);
      }
    }
    tModel.fireTableDataChanged();
  }

  private void updateTable(String speed, String hostName, String fileName){
    String[] data = {speed, hostName, fileName};
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

    // Search Button for keyword search
    JButton fileSearch = new JButton("Search");
    fileSearch.setEnabled(false);
    fileSearch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {

      }
    });

    JButton btnConnnect = new JButton("Connect");
    btnConnnect.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent p) {
        String serverInfo = hostnameInput.getText() + " " + portInput.getText();
        String userInfo = usernameInput.getText() + " " + speedInput.getSelectedIndex() + " " + serverInput.getText();
        server = new FTPServer();
        try {
          fileSearch.setEnabled(connect(serverInfo, userInfo));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

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
    gbc_SearchPanel.gridwidth = 2;
    gbc_SearchPanel.fill = GridBagConstraints.BOTH;
    gbc_SearchPanel.gridx = 0;
    gbc_SearchPanel.gridy = 2;

    // Label for keyword search
    JLabel lblKeyword = new JLabel("Keyword:");

    // Text input for keyword search
    keywordInput = new JTextField();
    keywordInput.setColumns(25);

    //String[][] testData = { { "124mb", "127.0.0.1", "test file" } };
    
    tModel = new DefaultTableModel(columnNames, 0);
    table = new JTable(tModel);
    table.setPreferredScrollableViewportSize(new Dimension(300, 100));
    // table.setFillsViewportHeight(true);

    JScrollPane test = new JScrollPane(table);

    contentPane.add(SearchPanel, gbc_SearchPanel);
    SearchPanel.add(lblKeyword);
    SearchPanel.add(keywordInput);
    SearchPanel.add(fileSearch);
    SearchPanel.add(test);

    /**
     * FTP Panel
     */
    JPanel FTPPanel = new JPanel();
    FTPPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
    GridBagConstraints gbc_FTPPanel = new GridBagConstraints();
    gbc_FTPPanel.insets = new Insets(0, 0, 5, 0);
    gbc_FTPPanel.gridwidth = 2;
    gbc_FTPPanel.fill = GridBagConstraints.BOTH;
    gbc_FTPPanel.gridx = 0;
    gbc_FTPPanel.gridy = 3;

    JLabel CommandLbl = new JLabel("Enter Command: ");

    commandInput = new JTextField();
    commandInput.setColumns(20);

    JTextArea commandArea = new JTextArea(14, 58);
    commandArea.setEditable(false);
    JScrollPane scroll = new JScrollPane(commandArea);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    // Search Button for keyword search
    JButton commandBtn = new JButton("Go");
    commandBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        String command = commandInput.getText();
        StringTokenizer tokens = new StringTokenizer(command);
        commandArea.setText(">>" + command);
        if (tokens.nextToken().equals("connect")) {
          String serverName = tokens.nextToken();
          int port = Integer.parseInt(tokens.nextToken());

          client = new ClientInstance(serverName, port);

          commandArea.setText("Connected to " + serverName + ":" + port);
        } else {
          commandArea.setText(client.executeCommand(command));
        }

      }
    });

    contentPane.add(FTPPanel, gbc_FTPPanel);
    FTPPanel.add(CommandLbl);
    FTPPanel.add(commandInput);
    FTPPanel.add(commandBtn);
    FTPPanel.add(scroll);

  }

}
