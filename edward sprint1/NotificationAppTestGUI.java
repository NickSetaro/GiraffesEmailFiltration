package v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class NotificationAppTestGUI extends Application{

	private WebView emailDisplay = new WebView();
	private WebEngine emailEngine = emailDisplay.getEngine();
	private ArrayList<String> messages;
	
	
	
	private String saveFile = "myFilter.ser";//added
	
	private HashMap<String, String> filter = new HashMap<String, String>()
	{{
		put("edward.callihan@gmail.com", ".*");
		put("edward.callihan@yahoo.com", "pizza|cake");
		put("callih56@students.rowan.edu", "test|exam");
	}};
	private Filter efilter = new Filter(filter);//added
	private EmailParser e = new EmailParser(efilter);
	private int messageNum = 0;
//	private Date lastCheck;

	private Logger logger = Logger.getLogger(NotificationAppTestGUI.class.getName());


	/*
	 * Test Data////////////////////////
	 */
	private String user = "EdTheDev001@gmail.com";
	private String password = "password4sweng";
	


	/**
	 * launching GUI
	 * @param for main method.
	 */
	public static void main(String[] args) 
	{
		Application.launch(args);
	}

	/**
	 * initializing GUI components
	 * 
	 * lastCheck will be used to prevent repeated messages.
	 * --It is currently incomplete.
	 * @throws IOException 
	 * @throws MessagingException 
	 */
	public void start(final Stage base) throws MessagingException, IOException 
	{
		try
		{
			efilter = Filter.deserialize(saveFile);
		}
		catch(Exception e)
		{
			efilter = new Filter();
		}
//		Calendar c = Calendar.getInstance();
//		c.set(2000, 1, 1, 0, 0, 0);
//		lastCheck = c.getTime();
		messages = e.checkEmail(user, password, efilter);
//		lastCheck = new Date();

		HBox root = new HBox();
		basePaneStyle(root);
		setControls(root);
		Scene scene = new Scene(root, 700, 700);
		setStage(base, scene);
	}

	/**
	 * setting style for the GUI
	 * @param root is main pane of GUI
	 */
	private void basePaneStyle(Pane root) 
	{
		root.setStyle("-fx-padding: 10;" +
				"-fx-border-style: solid inside;" +
				"-fx-border-width: 2;" +
				"-fx-border-insets: 5;" +
				"-fx-border-radius: 5;" +
				"-fx-border-color: blue;" +
				"-fx-background-color: aliceblue");  		
	}

	/**
	 * loads the controls for the GUI.
	 * @param root is the main pane.
	 */
	private void setControls(Pane root) 
	{		
		emailDisplay.setPrefHeight(500);
		emailDisplay.setPrefWidth(500);

		Button showEmailButton = new Button("Show Email");
		

		showEmailButton.setOnAction(event -> 
		{			
			emailEngine.loadContent(messages.get(messageNum++));
			messageNum = (messageNum) % (messages.size());
			
		});
		
		HBox buttonBox = new HBox(5, showEmailButton);
		VBox mailBox = new VBox(5, buttonBox, this.emailDisplay);

		root.getChildren().add(mailBox);


	}

	/**
	 * setStage sets the main stage for the GUI.
	 * @param mainStage
	 * @param scene 
	 */
	private void setStage(Stage mainStage, Scene scene) {
		mainStage.setTitle("Priority Messages");
		mainStage.setScene(scene);
		mainStage.show();			
	}

	
	
}
