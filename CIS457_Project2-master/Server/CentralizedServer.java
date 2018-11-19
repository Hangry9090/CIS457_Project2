public class CentralizedServer{
  public static void main(String[] args){
    System.out.println("Welcome to our server!");
    ServerSocket welcomeSocket = new ServerSocket(2840);
    while (true) {
      Socket connectionSocket = welcomeSocket.accept();
      System.out.println("New client connected");

      CentralizedServerHandler connection = new CentralizedServerHandler(connectionSocket);

      Thread thread = new Thread(connection);
      thread.start();
    }
  }
  }
}