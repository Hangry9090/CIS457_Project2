#! /bin/bash
javac ClientInstance.java FTPRequest.java FTPServer.java P2PClientGUI.java
x-terminal-emulator -e java FTPServer
java P2PClientGUI