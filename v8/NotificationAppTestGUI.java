package v8;
/*
 * v8 has finished the implementation of a user class. Aliases are handled in the filtering process.
 * There is now the ability to add, delete or edit filter addresses and keywords, and buckets. A 
 * RecentBucket class has been added which updates automatically to ensure it only contains messages
 * from the last 24 hours. Keywords have all been changed to arraylist formats. Faulty regex checking 
 * has been fixed to prevent stack overflows. 
 * 
 * On first run this version requests your email address and password. These are stored in subsequent runs.
 * All methods have been tested and work.
 * 
 * DRAWBACKS:
 * The GUI has not been updated, so testing is currently being done in a driver class.
 * Comments are VERY out of date and must be corrected in next 1-2 versions.
 * 
 * REMINDER:
 * For testing a first run, update testFilter in Filter class before running. Otherwise, to test
 * the data again the lastCheck must be reset. Reseting the lastCheck on successive runs will create
 * duplicate messages in buckets. This won't happen with normal use and unless testing requires it,
 * a solution for that is unneccessary. 
 * 
 */

import java.io.IOException;
import java.io.*;
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
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class NotificationAppTestGUI extends Application{

	private Stage stage = new Stage();
	
	private HBox homePageBox = new HBox();
	private Scene homePage;
	private Button showEmailButton = new Button("Show Email");
	private Button homeToSettings = new Button("Settings");
	
	private HBox settingsPageBox = new HBox();
	private Scene settingsPage;
	private Button settingsToHome = new Button("Home Page");
	
	
	private WebView emailDisplay = new WebView();
	private WebEngine emailEngine = emailDisplay.getEngine();
	private ArrayList<MailObject> messages;
	
	
	
	private Label sentFrom = new Label("sentFrom");
	private Label subject = new Label("subject");
	private Label sentDate = new Label("sentDate");
	private Label currentBucket = new Label("current bucket");
	
	
	private ComboBox<String> buckets = new ComboBox<String>();
	
	public ArrayList<EmailBucket> myBuckets = new ArrayList<EmailBucket>();
	
	/*
	 * 
	 */
	private EmailReceiver e = null;
	private int messageNum = 0;

	private Logger logger = Logger.getLogger(NotificationAppTestGUI.class.getName());



	
	public String fileName = "test2.ser";
	public User user = User.deserialize(fileName);


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
		stage = base;
				
		
		basePaneStyle(settingsPageBox);
		setupSettings(settingsPageBox);
		settingsPage = new Scene(settingsPageBox, 700, 700);
		
		
		basePaneStyle(homePageBox);
		homeControls(homePageBox);
		homePage = new Scene(homePageBox, 700, 700);
		
		changeToHome();
		
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

	private void setupSettings(Pane root)
	{
		settingsToHome.setOnAction(e -> {
			changeToHome();
		});
		
		HBox buttonBox = new HBox(5, settingsToHome);
		VBox settingsContainer = new VBox(5, buttonBox);
		root.getChildren().add(settingsContainer);
	}
	/**
	 * loads the controls for the GUI.
	 * @param root is the main pane.
	 */
	private void homeControls(Pane root) 
	{		
		emailDisplay.setPrefHeight(500);
		emailDisplay.setPrefWidth(700);

		/*
		 * setting the items in the buckets menu
		 */
		for(EmailBucket e: user.getBuckets())
		{
			buckets.getItems().add(e.getBucketName());
		}
		
		/*
		 * This sets the EmailBucket to display emails from the drop down menu.
		 * It also changes the displayed value indicating the bucket being viewed.
		 */
		buckets.setOnAction(e -> {
			for (EmailBucket b: user.getBuckets()) 
			{
				if(b.getBucketName().equals(buckets.getValue()))
				{
					messages = b.getMessages();
					messageNum = 0;
					displayMessage(messages);
					currentBucket.setText(buckets.getValue());
					break;
				}
			}
		});
		
		
		/*
		 * This Displays the messages in the current bucket.
		 */
		showEmailButton.setOnAction(e -> {
			displayMessage(messages);
		});
		/*
		 * This sets the action of the settings button to change to the
		 * settings page when clicked.
		 */
		
		homeToSettings.setOnAction(e -> {
			changeToSettings();
		});
		
		/*
		 * These set the values for the vertical and horizontal displays in
		 * the GUI.
		 */
		HBox bucketBox = new HBox(5, buckets);
		VBox labelBox = new VBox(5, sentFrom, subject, sentDate);
		HBox buttonBox = new HBox(5, showEmailButton, homeToSettings);
		VBox mailBox = new VBox(5, buttonBox, labelBox, bucketBox, this.emailDisplay);

		/*
		 * This adds the components to the GUI.
		 */
		root.getChildren().add(mailBox);
	}

	
	/**
	 * setStage sets the main stage for the GUI.
	 * @param mainStage
	 * @param scene 
	 */
	private void changeToHome() 
	{
		stage.setTitle("Priority Messages");
		stage.setScene(homePage);
		stage.show();			
	}
	
	/**
	 * changeToSettings() sets up the Edit buckets page.
	 * @param mainStage Root window.
	 * @param scene Edit Buckets Page.
	 */
	private void changeToSettings() 
	{
		stage.setTitle("Settings");
		stage.setScene(settingsPage);
		stage.show();
	}
	
	/**
	 * displayMessage() displays the next email message in the current
	 * @param mail is {types} {tags}
	 */
	private void displayMessage(ArrayList<MailObject> mail)
	{
		try {
		if(mail.size() > 0) 
		{
			MailObject m = mail.get(messageNum++);
			emailEngine.loadContent(m.getContentText());
			sentFrom.setText("Sent from: " + m.getFrom());
			subject.setText("Subject: " + m.getSubject());
			sentDate.setText("Sent at: " + m.getSentDate().toString());
			messageNum = (messageNum++) % (messages.size());
		}
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, "No messages in mail Box");
		}
}}
