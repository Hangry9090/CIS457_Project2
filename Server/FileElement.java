
public class FileElement{
  public String fileName;
  public String description;
  public UserElement user;

  public fileElement(UserElement user, String fileName, String description ){
    this.user    = user;
    this.description = description;
    this.fileName    = fileName;
  }

  public UserElement getUser(){
    return this.user;
  }

  public String getDescription(){
    return this.description;
  }

  public String getFileName(){
    return this.fileName;
  }
}