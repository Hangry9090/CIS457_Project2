
public class UserElement {
  private String userName;
  private String hostName;
  private String speed;

  public UserElement(String userName, String speed, String hostName) {
    this.userName = userName;
    this.hostName = hostName;
    this.speed = speed;
  }

  public String getUserName() {
    return this.userName;
  }

  public String getHostName() {
    return this.hostName;
  }

  public String getSpeed() {
    return this.speed;
  }
}