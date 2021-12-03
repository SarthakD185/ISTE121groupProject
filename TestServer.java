import java.io.*;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.geometry.*;
import java.io.BufferedReader;
import java.util.ArrayList;

import java.io.*;
import java.net.*;
import java.util.*;

//GITHUB TEST PUSH

public class TestServer extends Application implements EventHandler<ActionEvent> {
   // Window Attributes
   private Stage stage;
   private Scene scene;
   private VBox root = null;

   // GUI components
   private TextArea taText = new TextArea();
   private Button btnRecords = new Button("Show Records");
   private Button btnFile = new Button("Choose File");
   
   // Networking
   private static final int SERVER_PORT = 32001; 
   private long startTime = 0;
   private Vector<String> users = new Vector<String>();
   private Socket socket = null;
   private DataInputStream dis = null;
   private DataOutputStream dos = null;
   private Scanner scn = null; 
   
   private ObjectOutputStream out = null; 
   
   private ArrayList<String> text = new ArrayList<String>();
    
   /** Main program */
   public static void main(String[] args) {
      launch(args);
   }
   
   /** start the server */
   public void start(Stage _stage) {
      stage = _stage;
      stage.setTitle("Test Server Group3)");
      final int WIDTH = 450;
      final int HEIGHT = 400;
      final int X = 550;
      final int Y = 100;
   
      stage.setX(X);
      stage.setY(Y);
      stage.setOnCloseRequest(
         new EventHandler<WindowEvent>() {
            public void handle(WindowEvent evt) { System.exit(0); }
         });
      
      // Set up root
      root = new VBox();
      
      // Put clear button in North
      HBox hbNorth = new HBox();
      hbNorth.setAlignment(Pos.CENTER);
      hbNorth.getChildren().add(btnRecords);
      hbNorth.getChildren().add(btnFile);
      
      // Set up rootis
      root.getChildren().addAll(hbNorth, taText);
      for(Node n: root.getChildren()) {
         VBox.setMargin(n, new Insets(10));
      }
      
      // Set the scene and show the stage
      scene = new Scene(root, WIDTH, HEIGHT);
      stage.setScene(scene);
      stage.show();
      
      // handle button click
      btnRecords.setOnAction(this);
      btnFile.setOnAction(this);
      
      // Adjust size of TextArea
      taText.setPrefHeight(HEIGHT - hbNorth.getPrefHeight());
      
      // do Server Stuff
      Thread t = 
         new Thread() {
            public void run() {
               doServerStuff();
            }
         };
      t.start();
   }
   
   /** Server action */
   private void doServerStuff() {
      startTime = System.currentTimeMillis();
      try {
         ServerSocket sSocket = new ServerSocket(SERVER_PORT);
         while(true) {
            Socket cSocket = sSocket.accept();
            System.out.println("Client Connected");
            Thread t = new ClientThread(cSocket);
            t.start();
         }
      }
      catch(Exception e) {
         Alert alert = new Alert(AlertType.ERROR);
         alert.setContentText(e.toString());
         alert.setTitle("Exception");
         alert.showAndWait();
         System.exit(1);
      }
   }
   
   /** Button handler */
   public void handle(ActionEvent ae) {
   
      String label = ((Button)ae.getSource()).getText();
      switch(label) {
         case "Show Records":
            doShowRecords();
            break;
         case "Choose File":
            doChooseFile();
            break;
         
      
      }
   }
   
   private void doShowRecords(){
   
   }
   
   private void doChooseFile(){
   
      try{
         FileChooser fileChooser = new FileChooser();
         
         File selectedFile = fileChooser.showOpenDialog(stage);
         String fileName = selectedFile.getName();
         
         fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
         
         
         String line = "";
         try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            
         
            while((line = br.readLine()) != null){
               
               text.add(line + "\n");
            
               
            }
         
         }
         catch(FileNotFoundException e){
            e.printStackTrace();
            
         }
          
         
            
      
      }
      catch(Exception e) {
         Alert alert = new Alert(AlertType.ERROR);
         alert.setContentText(e.toString());
         alert.setTitle("Exception");
         alert.showAndWait();
         e.printStackTrace();
         System.exit(1);
      }
      
   }
   
   /** Class for a client thread */
   class ClientThread extends Thread {
      Socket cSocket = null;
      Scanner scn = null;
      PrintWriter pwt = null;
      
      /** constructor */
      public ClientThread(Socket _cSocket) {
         cSocket = _cSocket;
      }
      
      /** Thread main */
      public void run() {
         try {
         
            String line = "";
            try{
               BufferedReader br = new BufferedReader(new FileReader("wordBank.txt"));
            
            
               while((line = br.readLine()) != null){
               
                  text.add(line);
               
               
               }
            
            }
            catch(Exception e) {
         Alert alert = new Alert(AlertType.ERROR);
         alert.setContentText(e.toString());
         alert.setTitle("Exception");
         alert.showAndWait();
         e.printStackTrace();
         System.exit(1);
      }
            
            ObjectOutputStream out = new ObjectOutputStream(cSocket.getOutputStream());
            
            System.out.println(text.size());
            out.writeObject(text);
            out.flush();

            cSocket.close();
         }  
         catch(Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText(e.toString());
            alert.setTitle("Exception");
            alert.showAndWait();
            System.exit(1);
         }
      }  // run
      
      /** doRegister */
      private void doRegister() {
         if(scn.hasNextLine()) {
            String name = scn.nextLine();
            users.add(name);
            Platform.runLater(
               new Runnable() { 
                  public void run() { taText.appendText(name + "\n");  } } );
            pwt.println("REGISTERED\n" + name);
            pwt.flush();
         }  // if
         else {
            pwt.println("ERROR-EOF during Registration");
            pwt.flush();
         }  // else
      } // doRegister
      
      /** doList */
      private void doList() {
         synchronized(users) {
            pwt.println("TIME " + (System.currentTimeMillis() - startTime));
            pwt.println("USERS " + users.size());
            for(String u: users) {
               pwt.println(u);
            }
            pwt.flush();
         } // synchronized
      } // doList
   }
}