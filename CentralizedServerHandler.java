import java.util.StringTokenizer;
import java.util.Vector;
import java.lang.Runnable;
public class CentralizedServerHandler implements Runnable{
  //Use Vectors to have a synchronized list. This way, multiple threads won't be able to change the list at the same time.
  protected static Vector userList;
  protected static Vector fileList;

  private Socket connectionSocket;
  private Socket dataSocket;

  private DataOutputStream outToClient;
  private BufferedReader inFromClient;

  //private DataOutputStream dataOutToClient;
  private DataInputStream dataInFromClient;

  boolean welcomeMessage;


  public CentralizedServerHandler(){
    /*
      TODO: fills the user table
      TODO: send ack for received data
      TODO: accept the file
      TODO: parse the file
     */
    this.connectionSocket = connection;
    this.outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());
    this.inFromClient = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
    this.welcomeMessage = true;
    
  }

  public void run(){
    if(welcomeMessage){
      getInitialRequest();
    }
    /*
      TODO: wait for a search request.
    */
  }

  private void getInitialRequest(){

    /*
      TODO: accept the user and the xml file
      TODO: create a UserElement with info from the user.
    */
    String userInfo = this.inFromClient.readLine();
    StringTokenizer parseUserInfo = StringTokenizer(userInfo);
    String userName = parseUserInfo.nextToken();
    String speed = parseUserInfo.nextToken();
    String hostName = parseUserInfo.nextToken();
    UserElement user = new UserElement(userName, speed, hostName);
    addUser(user);
    
    File file = getFile();
    ArrayList<FileElement> files = parseData(file);   
    addContent(files);
    this.welcomeMessage = false;
  }

  private File getFile(){
    System.out.println("A client is sending a xml file...");
    FileOutputStream fos = new FileOutputStream("temp.xml");
    byte[] fileData = new byte[1024];
    int bytes;
    while ((bytes = this.dataInFromClient.read(fileData)) != -1) {
      //System.out.println("Bytes sent: " + bytes);
      fos.write(fileData, 0, bytes);
    }
    System.out.println("File received!");
    fos.close();
    File file = new File("temp.xml");
    return file;
  }

  private ArrayList<FileElement> parseData(File file){
    ArrayList<FileElement> dataList = new ArrayList();
    FileElement temp = null;
    //FIXME: https://stackoverflow.com/questions/428073/what-is-the-best-simplest-way-to-read-in-an-xml-file-in-java-application
    /*
      TODO: parse the file, make a fileElement for each paragraph in the file.
      TODO: in the end, put all the elements made in a list and return the list.
    */

    file.delete();
    return dataList;
  }

  private void addUser(UserElement newUser){
    synchronized(userList){
      userList.addElement(newUser);
    }
  }

  private void addContent(ArrayList<FileElement> newData){
    synchronized(fileList){
      fileList.addAll(newData);
    }
  }
}