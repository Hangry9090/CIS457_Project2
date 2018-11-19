import java.io.*;
import java.net.*;
import java.util.*;

public class FTPServer {

  public static void main(String[] args) throws Exception {
    System.out.println("Welcome to our server!");
    ServerSocket welcomeSocket = new ServerSocket(2841);
    while (true) {
      Socket connectionSocket = welcomeSocket.accept();
      System.out.println("New client connected");

      FTPRequest connection = new FTPRequest(connectionSocket);

      Thread thread = new Thread(connection);
      thread.start();
    }
  }
}