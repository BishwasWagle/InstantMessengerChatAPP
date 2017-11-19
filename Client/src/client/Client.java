
package client;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
    
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    //constructor
    public Client(String host){
        super("Client!");
        serverIP = host;
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
        add(new JScrollPane(chatWindow),BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true); 
    }
    //connectthe server
    public void startrunning(){
        try{
            //connect and have conversation
            connectToServer();
            setupStreams();
            whileChatting();
            }catch(EOFException eofe){
                showMessage("\n Client ended the conversation!");    
        }catch(IOException ioe){
            ioe.printStackTrace();     
        }finally{
            closeEverything();
        }
    }
    
    //connect to server
    private void connectToServer() throws IOException{
        showMessage("Attempting connection... \n");
        connection = new Socket(InetAddress.getByName(serverIP),1234);
        showMessage("Connected to:" + connection.getInetAddress().getHostName());
    }
    // setup streams to send and receive data
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are now setup! \n");    
}
    //while chatting with server 
    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n " + message); 
            }catch(ClassNotFoundException cnfe){
                showMessage("\n Object Type not recognized\n");
            }          
        }while(!message.equals("SERVER - END"));
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
      //send a message to server
     private void sendMessage(String message){
         try{
             output.writeObject("CLIENT -" + message);
             output.flush();
             showMessage("\n CLIENT -" + message);
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



