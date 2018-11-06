import java.util.Vector;
public class CentralizedServer{
  //Use Vectors to have a synchronized list. This way, multiple threads won't be able to change the list at the same time.
  private Vector userList;
  private Vector fileList;


  private void getRequest(){

    /*
      TODO: accept the user and the xml file
      TODO: create a UserElement with info from the user.
    */

    ArrayList<FileElement> files = parseData(file);
    addUser(user);
    addContent(files);
  }

  private ArrayList<FileElement> parseData(File file){
    /*
      TODO: parse the file, make a fileElement for each paragraph in the file.
      TODO: in the end, put all the elements made in a list and return the list.
    */
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