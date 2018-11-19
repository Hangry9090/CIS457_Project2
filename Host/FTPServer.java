import java.io.*;
import java.net.*;
import java.util.*;

public class FTPServer {

  public FTPServer() throws Exception {
    ServerSocket welcomeSocket = new ServerSocket(2841);
    while (true) {
      Socket connectionSocket = welcomeSocket.accept();

      FTPRequest connection = new FTPRequest(connectionSocket);

      Thread thread = new Thread(connection);
      thread.start();
    }
  }
}
