import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.lang.Runnable;
import java.net.Socket;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class CentralizedServerHandler implements Runnable {
  // Use Vectors to have a synchronized list. This way, multiple threads won't be
  // able to change the list at the same time.
  protected static Vector<UserElement> userList = new Vector<UserElement>();
  protected static Vector<FileElement> fileList = new Vector<FileElement>();

  private Socket connectionSocket;
  private Socket dataSocket;

  private DataOutputStream outToClient;
  private BufferedReader inFromClient;

  // private DataOutputStream dataOutToClient;
  private DataInputStream dataInFromClient;

  private boolean running;

  boolean welcomeMessage;

  public CentralizedServerHandler(Socket connection) throws Exception {
    this.connectionSocket = connection;
    this.outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());
    this.inFromClient = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
    
    this.welcomeMessage = true;
    this.running = true;
    System.out.println("Connection created");
  }

  public void run() {
    try {
      if (welcomeMessage) {
        getInitialRequest();
      } else {
        waitForRequest();
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void waitForRequest() throws Exception {
    while (this.running) {
      String fromClient = null;
      fromClient = this.inFromClient.readLine();
      processRequest(fromClient);
    }
  }

  private void processRequest(String sentence) throws Exception {
    StringTokenizer tokens = new StringTokenizer(sentence);
    this.dataSocket = new Socket(this.connectionSocket.getInetAddress(), Integer.parseInt(tokens.nextToken()));
    this.dataInFromClient = new DataInputStream(new BufferedInputStream(this.dataSocket.getInputStream()));
    //String command = tokens.nextToken();

    //if (command.equals("search")) {
    	System.out.println("A client requested a keyword search");
      searchCommand(tokens.nextToken());
    //}
  }

  private void searchCommand(String keyword) throws Exception {
    synchronized (fileList) {
      synchronized (userList) {
        String output = "";
        for (int i = 0; i < CentralizedServerHandler.fileList.size(); i++) {
          FileElement fileEntry = (FileElement) CentralizedServerHandler.fileList.get(i);
          String description = fileEntry.getDescription();
          if (description.contains(keyword)) {
            UserElement user = fileEntry.getUser();
            output += user.getSpeed() + " " + user.getHostName() + " " + fileEntry.getFileName() + " \n";
          }
        }
        
        this.outToClient.writeUTF(output);
        this.dataInFromClient.close();
        this.dataSocket.close();

      }
    }
  }

  private void getInitialRequest() throws Exception {
	  System.out.println("Waiting for user's data");
    String userInfo = this.inFromClient.readLine();
    System.out.println("User's data received");
    StringTokenizer parseUserInfo = new StringTokenizer(userInfo);
    this.dataSocket = new Socket(this.connectionSocket.getInetAddress(), Integer.parseInt(parseUserInfo.nextToken()));
    System.out.println("First Token");
    String userName = parseUserInfo.nextToken();
    System.out.println("Second Token");
    String speed = parseUserInfo.nextToken();
    System.out.println("Third Token");
    String hostName = parseUserInfo.nextToken();
    System.out.println("done with Token");
    UserElement user = new UserElement(userName, speed, hostName);
    System.out.println("Adding the user");
    addUser(user);
    
    System.out.println("User added");
    
    File file = getFile();
    ArrayList<FileElement> files = parseData(file, user);
    addContent(files);
    this.welcomeMessage = false;
    System.out.println("Initial connection completed");
  }

  private File getFile() throws Exception {
    System.out.println("A client is sending a xml file...");
    FileOutputStream fos = new FileOutputStream("temp.xml");
    System.out.println("File Stream created");
    byte[] fileData = new byte[1024];
    int bytes;
    while ((bytes = this.dataInFromClient.read(fileData)) != -1) {
      System.out.println("Bytes sent: " + bytes);
      fos.write(fileData, 0, bytes);
    }
    System.out.println("File received!");
    fos.close();
    File file = new File("temp.xml");
    return file;
  }

  /**
   * Method to parse a XML file using a DOM parser. Resource used:
   * https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
   * 
   * @param file
   * @param user
   * @return
   */
  private ArrayList<FileElement> parseData(File file, UserElement user) throws Exception {
    ArrayList<FileElement> dataList = new ArrayList();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(file);
    doc.getDocumentElement().normalize();// Make sure there are no funky things going on in the parser
    NodeList nList = doc.getElementsByTagName("file");
    for (int i = 0; i < nList.getLength(); i++) {
      FileElement temp = null;
      Node node = nList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element eTemp = (Element) node;
        temp = new FileElement(user, eTemp.getAttribute("name"), eTemp.getAttribute("description"));
        dataList.add(temp);
      }
    }
    file.delete();
    return dataList;
  }

  private void addUser(UserElement newUser) {
    synchronized (userList) {
    	System.out.println("Adding user to the list");
      userList.addElement(newUser);
      System.out.println("added user to the list");
    }
  }

  private void addContent(ArrayList<FileElement> newData) {
    synchronized (fileList) {
      fileList.addAll(newData);
    }
  }
}