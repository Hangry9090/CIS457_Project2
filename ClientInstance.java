import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

public class ClientInstance {
  private int controlPort;

  private ServerSocket welcomeData;
  private Socket dataSocket;
  private Socket controlSocket;

  private DataInputStream inData;
  private DataInputStream inFromServer;

  private DataOutputStream outData;
  private DataOutputStream outToServer;

  private int num_of_operations = 0;

  public ClientInstance(String serverName, int controlPort) throws Exception {
    this.controlPort = controlPort;
    this.controlSocket = new Socket(serverName, controlPort);
    this.inFromServer = new DataInputStream(new BufferedInputStream (controlSocket.getInputStream()));
    this.outToServer = new DataOutputStream(controlSocket.getOutputStream());
  }

  public boolean executeCommand() throws Exception{
    listCommands();
    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    String sentence;
    sentence = inFromUser.readLine();
    //inFromUser.close();
    StringTokenizer token = new StringTokenizer(sentence);
    String command = token.nextToken();
    command = command.toLowerCase();

    setUpConnection(sentence);

    System.out.println("Request received: " + sentence);

    if (command.equals("list:")) {
      listFiles();
    } else if (command.equals("retr:")) {
      retrieveFile(token.nextToken());
    } else if (command.equals("stor:")) {
      sendFile(token.nextToken());
    } else if (command.equals("quit:")){
      closeConnection();
      shutDown();
      return false;
    } 
    else {
      invalidCommand();
    }
    closeConnection();
    return true;
  }

  private int getNewPort() {
    return this.controlPort + 2 + (2 * this.num_of_operations) % 8;
  }

  private void listFiles() throws Exception{
    System.out.println("Listing Files: ");
    boolean notEnd = true;
    String fileList = "";
    while (notEnd) {
      try{
      fileList = inData.readUTF();
      System.out.println(fileList);
      }catch (EOFException e){
        notEnd = false;
      }
    }
  }

  private void retrieveFile(String fileName) throws Exception {
    FileOutputStream retrievedFile = new FileOutputStream(fileName);
    byte[] fileData = new byte[1024];
    int bytes = 0;
    while ((bytes = inData.read(fileData)) != -1) {
      retrievedFile.write(fileData, 0, bytes);
    }
    retrievedFile.close();
  }

  private void invalidCommand() {
    System.out.println("Command not valid, closing connection...");
  }

  public void listCommands() {
    System.out.println("\nWhat would you like to do next: \nlist: || retr: file.txt || stor: file.txt  || quit:\n");
  }

  private void welcomeMessage(int port, String sentence) throws Exception{
    outToServer.writeBytes(port + " " + sentence + " " + '\n');
  }

  private void setUpConnection(String sentence) throws Exception{
    System.out.println("Setting up the data connection...");
    int connectionPort = getNewPort();

    //System.out.println("Sending message...");

   

    //System.out.println("Opening welcome Socket...");

    this.welcomeData = new ServerSocket(connectionPort);

    welcomeMessage(connectionPort, sentence);

    //System.out.println("Opening Data socket...");
    this.dataSocket = this.welcomeData.accept();

    //System.out.println("Opening Streams...");
    
    this.inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
    this.outData = new DataOutputStream(dataSocket.getOutputStream());
    
    System.out.println("Data connection active!");

    
  }

  private void closeConnection() throws Exception {
    this.inData.close();
    this.outData.close();
    this.welcomeData.close();
    this.dataSocket.close();
    this.num_of_operations++;
  }

  private void shutDown() throws Exception{
    this.controlSocket.close();
    System.out.println("--- CONNECTION CLOSED ---");
  }
}
