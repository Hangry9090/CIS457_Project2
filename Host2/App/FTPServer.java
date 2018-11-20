import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Thread;

public class FTPServer implements Runnable {
  private ServerSocket welcomeSocket;

  public FTPServer() throws Exception {
    System.out.println("Welcome to our server!");
    welcomeSocket = new ServerSocket(2842);
  }

  public void run() {
    while (true) {
      try{
      Socket connectionSocket = welcomeSocket.accept();
      System.out.println("New client connected");

      FTPRequest connection = new FTPRequest(connectionSocket);

      Thread thread = new Thread(connection);
      thread.start();
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }

}
