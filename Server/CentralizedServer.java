import java.net.ServerSocket;
import java.net.Socket;

public class CentralizedServer {
  public static void main(String[] args) throws Exception {
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
