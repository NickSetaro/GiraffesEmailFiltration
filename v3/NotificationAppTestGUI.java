package v3;

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

	private WebView emailDisplay = new WebView();
	private WebEngine emailEngine = emailDisplay.getEngine();
	private ArrayList<MailObject> messages;
	private ArrayList<MailObject> messagesInView;
	
	public ArrayList<EmailBucket> myBuckets = new ArrayList<EmailBucket>();
	
	private Label sentFrom = new Label("sentFrom");
	private Label subject = new Label("subject");
	private Label sentDate = new Label("sentDate");
	private Label currentBucket = new Label("current bucket");
	
	private Button showEmailButton = new Button("Show Email");
	
	private ComboBox<String> buckets = new ComboBox<String>();
	
	
	private String saveFile = "myFilter.ser";
	private String bucketFile = "myBuckets.ser";
	
	
	private Filter filter = new Filter();//added
	private EmailReceiver e = null;
	private int messageNum = 0;

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
			filter = Filter.deserialize(saveFile);
			if(filter.showAddresses().isEmpty()) 
			{
				filter = new Filter();
			}
		}
		catch(Exception e)
		{
			filter = new Filter();
		}
		
		e = new EmailReceiver(filter);
		
		logger.log(Level.INFO, "addresses in filter: " + filter.showAddresses().size());
 
		
		for(int i = 0;i < filter.showAddresses().size();i++) {
			System.out.println(i + " " + filter.showAddresses().toArray()[i].toString());
		}
		
		//Enable the next line to test inbox for all messages regardless of time/date sent.
		//Disable to serialize.
		filter.resetLastCheck();
		
		
		messages = e.checkEmail(user, password, filter);
		
	
		
		
//		bucketsCol.addToBucket(messages);
		myBuckets = EmailBucket.sortMail(myBuckets, messages);
		
		
		logger.log(Level.INFO, "" + "buckets in app: "  + myBuckets.size());
//		for(EmailBucket b: myBuckets)
//		{
//			b.getMessages().forEach(e->{
//				logger.log(Level.INFO, "Message: " + e.getContentText());
//			});
//		}
		
//		String str = "";
//		EmailBucket.getEmailBuckets().forEach(e ->{
//			logger.log(Level.INFO, "" + e.getMessages().size());
//		});
		//logger.log(Level.INFO, str);
		
		
		filter.updateLastCheck();
		Filter.serialize(filter, saveFile);
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
		emailDisplay.setPrefWidth(700);

		/*
		 * setting the items in the buckets menu
		 */
		for(EmailBucket e: myBuckets)
		{
			buckets.getItems().add(e.getBucketName());
		}
		
		/*
		 * This sets the EmailBucket to display emails from the drop down menu.
		 * It also changes the displayed value indicating the bucket being viewed.
		 */
		buckets.setOnAction(e -> {
			for (EmailBucket b: myBuckets) 
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
		 * These set the values for the vertical and horizontal displays in
		 * the GUI.
		 */
		HBox bucketBox = new HBox(5, buckets);
		VBox labelBox = new VBox(5, sentFrom, subject, sentDate);
		HBox buttonBox = new HBox(5, showEmailButton);
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
	private void setStage(Stage mainStage, Scene scene) {
		mainStage.setTitle("Priority Messages");
		mainStage.setScene(scene);
		mainStage.show();			
	}

	/**
	 * displayMessage() displays the next email message in the current
	 * @param mail is {types} {tags}
	 */
	private void displayMessage(ArrayList<MailObject> mail)
	{
		if(mail.size() > 0) 
		{
			MailObject m = mail.get(messageNum++);
			emailEngine.loadContent(m.getContentText());
			sentFrom.setText("Sent from: " + m.getFrom());
			subject.setText("Subject: " + m.getSubject());
			sentDate.setText("Sent at: " + m.getSentDate().toString());
			messageNum = (messageNum++) % (messages.size());
		}
	
}}
