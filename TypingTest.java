   import javafx.application.Application;
   import javafx.application.Platform;
   import javafx.event.*;
   import javafx.scene.*;
   import javafx.scene.control.*;
   import javafx.scene.control.Alert.*;
   import javafx.scene.layout.*;
   import javafx.stage.*;
   import javafx.geometry.*;
   import java.util.*;
 
  import java.net.*;
  import java.io.*;
  /**
   * TypingTet - main class of program
   * @author  Group 3
   * @version 2211
  */
  
  //TEST 4
  //ADding a comment 
  public class TypingTest extends Application implements EventHandler<ActionEvent> {
     // GUI stuff
     private Stage stage;  
    private Scene scene;       
     private VBox root = new VBox(8);
    
     // attributes
     private WordProgressBar pbBar = new WordProgressBar();
    
     private TextArea taOutput = new TextArea();
    
     private TextField tfInput = new TextField();
     private TextField tfName = new TextField();
    private TextField tfIP = new TextField();
    
    private Button btnDone = new Button("Done");
     private Button btnEnter = new Button("Enter");
     private Button btnConnect = new Button("Connect");
    
     private Label lblName = new Label("Enter Your Name Here:");
    private Label lblProgress = new Label("Your Progress:");
     private Label lblInput = new Label("Type Sentence Here:");
   
     // boolean attribute
     private boolean keepGoing = true; 
     private WordProgressBar wpb = null;
    
     // Server attributes
     public static final int SERVER_PORT = 32001;
     private Socket socket = null;
   
     private String name = "UNKNOWN";
     
     
     private ObjectInputStream input = null;
         
     // Main just instantiates an instance of this GUI class
     public static void main(String[] args) {
        launch(args);
     }
    
     // Called automatically after launch sets up javaFX
     public void start(Stage _stage) throws Exception {
        stage = _stage;                        // save stage as an attribute
        stage.setTitle("Typing Test");            // set the text in the title bar
       
        wpb = new WordProgressBar();
       
        FlowPane fpTop = new FlowPane(8,8);
        fpTop.getChildren().addAll(lblName, tfName, btnEnter, tfIP, btnConnect);
        fpTop.setAlignment(Pos.CENTER);
       
        FlowPane fpMid = new FlowPane(8,8);
        fpMid.getChildren().addAll(lblProgress, pbBar);
        fpMid.setAlignment(Pos.CENTER);
       
        FlowPane fpBot = new FlowPane(8,8);
        fpBot.getChildren().add(taOutput);
        fpBot.setAlignment(Pos.CENTER);
       
        FlowPane fpBot2 = new FlowPane(8,8);
        fpBot2.getChildren().addAll(lblInput, tfInput, btnDone);
       fpBot2.setAlignment(Pos.CENTER);
       
       
        btnEnter.setOnAction(this);
        btnDone.setOnAction(this);
        btnConnect.setOnAction(this);
      
        root.getChildren().addAll(fpTop, fpMid, fpBot, fpBot2);
       
        scene = new Scene(root, 400, 315);   // create scene of specified size
                                               // with given layout
        stage.setScene(scene);                 // associate the scene with the stage
        stage.show();                          // display the stage (window)
     }
    
     public void handle(ActionEvent evt) {
        // Get the button that was clicked
        Button btn = (Button)evt.getSource();
       
        // Switch on its name
        switch(btn.getText()) {
            case "Connect":
               doConnect();
               test();
              break;
           case "Enter":
              doEnter();
             break;
          case "Done":
             doDone();
             break;
       }
    }
   
    public void doConnect() {
       try {
          socket = new Socket(tfIP.getText(), SERVER_PORT);
       }
       catch(IOException ioe) {
          System.out.println("IO Exception: " + ioe + "\n");
          return;
       }
    }
   
    public void doEnter() {
       name = tfName.getText();
      
       btnEnter.setDisable(true);
       tfName.setEditable(false);
      
       // System.out.println(name);
       
       Thread t0 = new Thread(wpb);
      t0.start();
    }
   
    public void doDone() {
   
    }
    
    public void test(){
      try{
         input = new ObjectInputStream(socket.getInputStream());
      
         ArrayList<String> text = (ArrayList<String>) input.readObject();
         
         for(int i = 0; i < text.size(); i++){
         
            taOutput.appendText(text.get(i) + "\n");
               
         }
         
         taOutput.appendText("this works");
      }
      catch(Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText(e.toString());
            alert.setTitle("Exception");
            alert.showAndWait();
            System.exit(1);
         }
    }
   
    class WordProgressBar extends FlowPane implements Runnable {
       // attributes
       // Labels
       private Label lblValue = new Label("0");
       private Label lblProgress;
      
       // Progress Bar
       private ProgressBar pbBar = new ProgressBar();
      
       //private int num;
      
       /* constructor */
       public WordProgressBar() {
         
          StackPane spBar = new StackPane();
         
          spBar.getChildren().addAll(pbBar, lblValue);
          this.getChildren().add(spBar);
          this.setAlignment(Pos.CENTER);

         
          pbBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS); 
       }
      
       /* run method for progress bar */
       public void run() {
          for(int i = 1; (i <= 100 && keepGoing == true); i+=4) {
            try {
               Thread.sleep(1000);
            }
            catch(InterruptedException ie) {
               // handles exception 
            }
            
            final int finalI = i;
         
            // updates Progress Bar
            Platform.runLater(new Runnable() {
               public void run() {
                  pbBar.setProgress(finalI / 100.0);
                  lblValue.setText("" + finalI);
               }
                  
            });
         }
         keepGoing = false;
         //btnStart.setDisable(false);

      }
   } 
 }

