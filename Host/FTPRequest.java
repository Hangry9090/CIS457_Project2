import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

public class FTPRequest implements Runnable {
  private Socket connectionSocket;
  private Socket dataSocket;

  private DataOutputStream outToClient;
  private BufferedReader inFromClient;

  private DataOutputStream dataOutToClient;
  //private DataInputStream dataInFromClient;

  private boolean running;

  public FTPRequest(Socket connection) throws Exception {
    this.connectionSocket = connection;
    this.outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());
    this.inFromClient = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
  }

  public void run() {
    try {
      this.running = true;
      waitForRequest();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void waitForRequest() throws Exception {
    while (this.running) {
      String fromClient = null;
      fromClient = this.inFromClient.readLine();
      processRequest(fromClient);
    }
  }

  /** Get the command and call the correct method to process the request */
  private void processRequest(String sentence) throws Exception {
    StringTokenizer tokens = new StringTokenizer(sentence);
    int port = Integer.parseInt(tokens.nextToken());
    String command = tokens.nextToken();

    this.dataSocket = new Socket(this.connectionSocket.getInetAddress(), port);

    this.dataOutToClient = new DataOutputStream(this.dataSocket.getOutputStream());
    //this.dataInFromClient = new DataInputStream(new BufferedInputStream(this.dataSocket.getInputStream()));

    if (command.equals("retr:")) {
      retrCommand(tokens.nextToken());
    } else if (command.equals("quit:")) {
      endConnection();
      connectionShutdown();
    }

  }

  private void retrCommand(String fileName) throws Exception {
    FileInputStream fileToClient = new FileInputStream("./" + fileName);
    byte[] fileData = new byte[1024];
    int bytes = 0;
    while ((bytes = fileToClient.read(fileData, 0, fileData.length)) != -1) {
      this.dataOutToClient.write(fileData, 0, bytes);
    }
    fileToClient.close();
    endConnection();
  }

  private void endConnection() throws Exception {
    this.dataOutToClient.close();
    //this.dataInFromClient.close();
    this.dataSocket.close();
  }

  private void connectionShutdown() throws Exception {
    this.running = false;
  }
}