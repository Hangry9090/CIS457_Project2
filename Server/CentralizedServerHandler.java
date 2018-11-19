import java.util.StringTokenizer;
import java.util.Vector;
import java.lang.Runnable;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class CentralizedServerHandler implements Runnable {
  // Use Vectors to have a synchronized list. This way, multiple threads won't be
  // able to change the list at the same time.
  protected static Vector userList;
  protected static Vector fileList;

  private Socket connectionSocket;

  private DataOutputStream outToClient;
  private BufferedReader inFromClient;

  // private DataOutputStream dataOutToClient;
  private DataInputStream dataInFromClient;

  private boolean running;

  boolean welcomeMessage;

  public CentralizedServerHandler() {
    this.connectionSocket = connection;
    this.outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());
    this.inFromClient = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
    this.welcomeMessage = true;
    this.running = true;

  }

  public void run() {
    if (welcomeMessage) {
      getInitialRequest();
    } else {
      waitForRequest();
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
    String command = tokens.nextToken();

    if (command.equals("search:")) {
      searchCommand(tokens.nextToken());
    } else if (command.equals("quit:")) {
      endConnection();
      connectionShutdown();
    }

  }

  private void searchCommand(String keyword) {
    synchronized (fileList) {
      synchronized (userList) {
        String output = "";
        for (int i = 0; i < CentralizedServerHandler.fileList.size(); i++) {
          FileElement fileEntry = CentralizedServerHandler.fileList.get(i);
          String description = fileEntry.getDescription();
          if (description.contains(keyword)) {
            UserElement user = fileEntry.getUser();
            output += user.getSpeed() + " " + user.getHostName() + " " + fileEntry.getFileName() + " \n";
          }
        }
        this.outToClient.writeUTF(output);

      }
    }
  }

  private void getInitialRequest() {
    String userInfo = this.inFromClient.readLine();
    StringTokenizer parseUserInfo = StringTokenizer(userInfo);
    String userName = parseUserInfo.nextToken();
    String speed = parseUserInfo.nextToken();
    String hostName = parseUserInfo.nextToken();
    UserElement user = new UserElement(userName, speed, hostName);
    addUser(user);

    File file = getFile();
    ArrayList<FileElement> files = parseData(file, user);
    addContent(files);
    this.welcomeMessage = false;
    this.outToClient.writeUTF("OK");
  }

  private File getFile() {
    System.out.println("A client is sending a xml file...");
    FileOutputStream fos = new FileOutputStream("temp.xml");
    byte[] fileData = new byte[1024];
    int bytes;
    while ((bytes = this.dataInFromClient.read(fileData)) != -1) {
      // System.out.println("Bytes sent: " + bytes);
      fos.write(fileData, 0, bytes);
    }
    System.out.println("File received!");
    fos.close();
    File file = new File("temp.xml");
    return file;
  }

  /**
   * Method to parse a XML file using a DOM parser.
   * Resource used: https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
   * @param file
   * @param user
   * @return
   */
  private ArrayList<FileElement> parseData(File file, UserElement user) {
    ArrayList<FileElement> dataList = new ArrayList();
    DocumentBuilderFactory factory =
    DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(file);
    doc.getDocumentElement().normalize();// Make sure there are no funky things going on in the parser
    NodeList nList = doc.getElementsByTagName("file");
    for(int i = 0; i < nList.getLength(); i++){
      FileElement temp = null;
      Node node = nList.item(i);
      if(node.getNodeType() == Node.ELEMENT_NODE){
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
      userList.addElement(newUser);
    }
  }

  private void addContent(ArrayList<FileElement> newData) {
    synchronized (fileList) {
      fileList.addAll(newData);
    }
  }
}