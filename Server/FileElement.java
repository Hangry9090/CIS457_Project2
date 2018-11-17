
public class FileElement{
  public String fileName;
  public String description;
  public String userName;

  public fileElement(String userName, String fileName, String description ){
    this.userName    = userName;
    this.description = description;
    this.fileName    = fileName;
  }

  public String getUserName(){
    return this.userName;
  }

  public String getDescription(){
    return this.description;
  }

  public String getFileName(){
    return this.fileName;
  }
}