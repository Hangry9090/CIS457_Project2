import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient {

  public  FTPClient(String sentence) throws Exception {
    boolean isOpen = true;
    boolean clientgo = true;

    StringTokenizer tokens = new StringTokenizer(sentence);
    //inFromUser.close();

    if (sentence.startsWith("connect")) {
      String serverName = tokens.nextToken(); // pass the connect command
      serverName = tokens.nextToken();
      int port1 = Integer.parseInt(tokens.nextToken());
      
      ClientInstance client = new ClientInstance(serverName, port1);

      System.out.println("You are connected to " + serverName);
      while (isOpen && clientgo) {
       clientgo = client.executeCommand();
      }
    }
  }
}
