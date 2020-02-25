package v1;

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
	private HashMap<String, String> filter = new HashMap<String, String>()
	{{
		put("edward.callihan@gmail.com", ".*");
		put("edward.callihan@yahoo.com", "pizza|cake");
		put("callih56@students.rowan.edu", "test|exam");
	}};
	private EmailReceiver e = new EmailReceiver(filter);
	private int messageNum = 0;

	private Logger logger = Logger.getLogger(NotificationAppTestGUI.class.getName());


	/*
	 * Test Data////////////////////////
	 */
	private String user = "EdTheDev001@gmail.com";
	private String password = "password4sweng";
	


	public static void main(String[] args) 
	{
		Application.launch(args);
	}

	public void start(final Stage base) 
	{
		Calendar c = Calendar.getInstance();
		c.set(2000, 1, 1, 0, 0, 0);
		Date lastCheck = c.getTime();


		loadNewMessages(lastCheck);
		//lastCheck = new Date();

		HBox root = new HBox();
		basePaneStyle(root);
		setControls(root);
		Scene scene = new Scene(root, 700, 700);
		setStage(base, scene);
	}

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

	private void setControls(Pane root) 
	{		
		emailDisplay.setPrefHeight(500);
		emailDisplay.setPrefWidth(500);

		Button showEmailButton = new Button("Show Email");

		showEmailButton.setOnAction(event -> 
		{			
			emailEngine.loadContent(messages.get(messageNum++));
			messageNum = (messageNum++) % (messages.size());
		});

		HBox buttonBox = new HBox(5, showEmailButton);
		VBox mailBox = new VBox(5, buttonBox, this.emailDisplay);

		root.getChildren().add(mailBox);


	}

	private void setStage(Stage mainStage, Scene scene) {
		mainStage.setTitle("Priority Messages");
		mainStage.setScene(scene);
		mainStage.show();			
	}

	private void loadNewMessages(Date lastCheck) 
	{

		messages = e.checkEmail("EdTheDev001@gmail.com", "password4sweng", lastCheck);
	}
	
}
