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
  private DataInputStream dataInFromClient;

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

    System.out.println("Setting up connection at address: " + this.connectionSocket.getInetAddress() + " : "+ port);

    this.dataSocket = new Socket(this.connectionSocket.getInetAddress(), port);

    System.out.println("Socket Ready");

    this.dataOutToClient = new DataOutputStream(this.dataSocket.getOutputStream());
    this.dataInFromClient = new DataInputStream(new BufferedInputStream(this.dataSocket.getInputStream()));

    System.out.println("Connection Ready");

    if (command.equals("list:")) {
      listCommand();
    } else if (command.equals("stor:")) {
      storCommand(tokens.nextToken());
    } else if (command.equals("retr:")) {
      retrCommand(tokens.nextToken());
    } else if (command.equals("quit:")) {
      endConnection();
      connectionShutdown();
    }

  }

  private void listCommand() throws Exception {
    System.out.println("A client requested the files");
    File directory = new File(".");
    if (directory.isDirectory()) {
      File[] files = directory.listFiles();
      for (int i = 0; i < files.length; i++) {
        this.dataOutToClient.writeUTF(files[i].getName());
      }
    } else {
      System.out.println("THE SELECTED PATH: " + directory.getPath() + " IS NOT A DIRECTORY!");
    }
    endConnection();
  }

  private void storCommand(String fileName) throws Exception {
    System.out.println("A client is sending a file...");
    FileOutputStream fos = new FileOutputStream(fileName);
    byte[] fileData = new byte[1024];
    int bytes;
    while ((bytes = this.dataInFromClient.read(fileData)) != -1) {
      //System.out.println("Bytes sent: " + bytes);
      fos.write(fileData, 0, bytes);
    }
    System.out.println("File received!");
    fos.close();
  }

  private void retrCommand(String fileName) throws Exception {
    System.out.println("A client requested the file: " + fileName);
    FileInputStream fileToClient = new FileInputStream("./" + fileName);
    byte[] fileData = new byte[1024];
    int bytes = 0;
    while ((bytes = fileToClient.read(fileData, 0, fileData.length)) != -1) {
      this.dataOutToClient.write(fileData, 0, bytes);
    }
    fileToClient.close();
    System.out.println("The requested file has been sent successfully");
    endConnection();
  }

  private void endConnection() throws Exception {
    this.dataOutToClient.close();
    this.dataInFromClient.close();
    this.dataSocket.close();
  }

  private void connectionShutdown() throws Exception {
    this.outToClient.close();
    this.inFromClient.close();
    this.connectionSocket.close();
    this.running = false;
  }
}