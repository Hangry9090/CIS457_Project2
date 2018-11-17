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
  private DataOutputStream outToServer;

  private int num_of_operations;

  public ClientInstance(String serverName, int controlPort) throws Exception {
    this.controlPort = controlPort;
    this.controlSocket = new Socket(serverName, controlPort);
    this.outToServer = new DataOutputStream(controlSocket.getOutputStream());
  }

  /**
   * It can only retrieve a file from the server, or interrupt the connection.
   * @param sentence
   * @return
   * @throws Exception
   */
  public boolean executeCommand(String sentence) throws Exception{
    StringTokenizer token = new StringTokenizer(sentence);
    String command = token.nextToken();
    command = command.toLowerCase();

    setUpConnection(sentence);

    if (command.equals("retr:")) {
      retrieveFile(token.nextToken());
    } else if (command.equals("quit:")){
      closeConnection();
      return false;
    } 
    closeConnection();
    return true;
  }

  /**
   * Choose a port randomly based on how many operations this client made.
   * @return
   */
  private int getNewPort() {
    return this.controlPort + 2 + (2 * this.num_of_operations) % 8;
  }

  /**
   * Get a file from the server
   * @param fileName the name of the file
   * @throws Exception
   */
  private void retrieveFile(String fileName) throws Exception {
    FileOutputStream retrievedFile = new FileOutputStream(fileName);
    byte[] fileData = new byte[1024];
    int bytes = 0;
    while ((bytes = inData.read(fileData)) != -1) {
      retrievedFile.write(fileData, 0, bytes);
    }
    retrievedFile.close();
  }

  /**
   * Send the info for the connection to the server.
   * @param port
   * @param sentence contains the command for the server to execute
   * @throws Exception
   */
  private void welcomeMessage(int port, String sentence) throws Exception{
    outToServer.writeBytes(port + " " + sentence + " " + '\n');
  }

  /**
   * Open the data socket and the buffers.
   * @param sentence
   * @throws Exception
   */
  private void setUpConnection(String sentence) throws Exception{
    
    int connectionPort = getNewPort();

    this.welcomeData = new ServerSocket(connectionPort);

    welcomeMessage(connectionPort, sentence);

    this.dataSocket = this.welcomeData.accept();
    
    this.inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

    
  }

  /**
   * Close the data connection-related objects.
   * @throws Exception
   */
  private void closeConnection() throws Exception {
    this.inData.close();
    this.welcomeData.close();
    this.dataSocket.close();
    this.num_of_operations++;
  }
}