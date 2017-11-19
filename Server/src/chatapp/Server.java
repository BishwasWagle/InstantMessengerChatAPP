
package chatapp;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
    
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    
    public Server(){
        super("Awesome Instant Messenger");
        
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
            new ActionListener(){
            public void actionPerformed(ActionEvent event){
                sendMessage(event.getActionCommand());
                userText.setText("");
                    
                }
            }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300,150);
        setVisible(true);       
    }
    
    //setup and run the server
    public void startrunning(){
        try{
            server = new ServerSocket(1234,100);
            while(true){
                try{
                    //connect and have conversation
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                    
                }catch(EOFException eofe){
                    showMessage("\n Server ended the conversation!");
                }finally{
                    closeEverything();
                }
            }
        }catch(IOException ioe){
            ioe.printStackTrace();     
        }
    }
    
    //wait for connection, and display connection information
    private void waitForConnection() throws IOException{
        showMessage("Waiting for someone to connect...\n");
        connection = server.accept();
        showMessage("Connected to" +connection.getInetAddress().getHostName());   
    }
    
    // get stream to send and receive data
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are now setup! \n");
    }
    
    //while chat conversation
    private void whileChatting() throws IOException{
        String message = "You are now connected!";
        sendMessage(message);
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n " + message); 
                
            }catch(ClassNotFoundException cnfe){
                showMessage("\n Cannot understand the message \n");
            }
            
        }while(!message.equals("CLIENT - END"));
    }
    
    //close streams and sockets after chating
     private void closeEverything(){
         showMessage("\n Closing connections... \n");
         ableToType(false);
         try{
             output.close();
             input.close();
             connection.close();
         }catch(IOException ioe){
             ioe.printStackTrace();
         }  
     }
     
     //send a message to client
     private void sendMessage(String message){
         try{
             output.writeObject("SERVER -" + message);
             output.flush();
             showMessage("\n SERVER -" + message);
         }catch(IOException ioe){
             chatWindow.append("\n ERROR: I CANT SEND THE MESSAGE ");
         }
     }
     
     //update chatwindow(show message)
     private void showMessage(final String text){
         SwingUtilities.invokeLater(
                 new Runnable(){
                     public void run(){
                         chatWindow.append(text);
                         
                     }
                 }
         
         );
         
     }
     //allowing users to type while chatting
     private void ableToType(final boolean tof){
         SwingUtilities.invokeLater(
                 new Runnable(){
                     public void run(){
                        userText.setEditable(tof);
                         
                     }
                 }
         
         );
}
}

