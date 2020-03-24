package v8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMultipart;

public class EmailReceiver
{
	public final Logger logger = Logger.getLogger(EmailReceiver.class.getName());

	public final String PROTOCOL = "imap";
	public final String HOST = "imap.gmail.com";
	public final String PORT = "993";

	public String messageContent, contentType, toList, ccList, subject, sentDate, from;

	public final static int TAG_BUFFER = 50;
	public final static String SENDER_PATTERN = "<.*>";
	public final static String STRIP_HTML = "<[^>]*>";
	public final static String EMAILREGEX = "^([<a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z>]{2,6})$";

	public Filter filter;
	public ArrayList<String> recentMail = new ArrayList<String>();

	public EmailReceiver() {

	}

	public EmailReceiver(Filter filter)
	{
		this.filter = filter;
	}

	/**
	 * setProperties() sets the host, port and protocol for the email server connection.
	 * @return The properties for connection.
	 */
	private Properties setProperties()
	{
		Properties p = new Properties();
		p.put("mail." + PROTOCOL + ".host", HOST);
		p.put("mail." + PROTOCOL + ".port", PORT);

		p.setProperty("mail." + PROTOCOL + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		p.setProperty("mail." + PROTOCOL + ".soceketFactory.fallback", "false");
		p.setProperty("mail." + PROTOCOL + ".socketFactory.port", PORT);

		return p;
	}

	/**
	 * checkEmail() Accesses user email account with password and returns the messages.
	 * @param user username for email account.
	 * @param password password for email account.
	 * @return an array of message objects.
	 * @throws IOException 
	 */
	public ArrayList<MailObject> checkEmail (String user, String password) throws IOException//added Filter
	{
		Properties p = setProperties();
		Session session = Session.getDefaultInstance(p);
		Message[] messages = null;
		ArrayList<MailObject> newMail = new ArrayList<MailObject>();
		try
		{
			Store s = session.getStore(PROTOCOL);
			System.out.println("user: " + user + "\npass: " + password);
			s.connect(user, password);
			Folder inbox = s.getFolder("INBOX");
			inbox.open(Folder.READ_ONLY);

			messages = inbox.getMessages();

			newMail = extractComponents(messages, filter);// with Filter object
			// disconnect
			inbox.close(false);
			s.close();
		} catch (NoSuchProviderException ex) {
			System.out.println("No provider for protocol: " + PROTOCOL);
			ex.printStackTrace();
		} catch (MessagingException ex) {
			System.out.println("Could not connect to the message store");
			ex.printStackTrace();
		}
		return newMail;
	}


	/**
	 * extractComponents() assigns string representations of the different pieces of 
	 * an email message to class variables
	 * @param messages The array of messages from the inbox being checked.
	 * @throws MessagingException 
	 * @throws IOException 
	 */
	//added Filter parameter
	public ArrayList<MailObject> extractComponents(Message[] messages, Filter filter) throws MessagingException, IOException 
	{
		ArrayList<MailObject> newMail = new ArrayList<MailObject>();
		
		for(Message m: messages)
		{
			
			if(filter.getLastCheck().compareTo(m.getSentDate()) < 0)//check date before continuing.
			{
				messageContent = EmailReceiver.parseContent(m);
				contentType = m.getContentType();
				Address[] fromAddress = m.getFrom();
				from = fromAddress[0].toString();
				subject = m.getSubject();
				
				// print out details of each message
				if(isValidSender(from, subject, textFromHtml(messageContent))) 
				{
					
					
					toList = String.join(", ", parseAddresses(m.getRecipients(RecipientType.TO)));
					ccList = String.join(", ", parseAddresses(m.getRecipients(RecipientType.CC)));
					sentDate = m.getSentDate().toString();

					
					newMail.add(new MailObject(m));
					
					System.out.println("\t From: " + from);
					System.out.println("\t To: " + toList);
					System.out.println("\t CC: " + ccList);
					System.out.println("\t Subject: " + subject);
					System.out.println("\t Sent Date: " + sentDate);
					System.out.println("\t Message: " + textFromHtml(messageContent));
				}
			}
		}
		return newMail;
	}

	/**
	 * isValidSender() checks if the sender is on the user's list of pre-approved senders.
	 * @param sender: sender of the message.
	 * @return true if on sender list, false otherwise.
	 */
	public boolean isValidSender(String sender, String subject, String message)//added Filter
	{
		
		for(String s: filter.getEmailFilter().keySet())//using Filter 
		{
			
			if(sender.contains(s))
			{
				for(String keyword: filter.getEmailFilter().get(s))
				{
					Pattern p = Pattern.compile(keyword);
					if(p.matcher(message).find() || p.matcher(subject).find())
					{
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	/**
	 * parseAddresses() turns an array of Address objects into a string, seperated by commas.
	 * @param address The address component of a message object.
	 * @return String representation of email addresses.
	 */
	public static String[] parseAddresses(Address[] address) 
	{
		if(address == null) 
		{
			return new String[] {""};
		}
		String[] s = new String[address.length];
		for(int i = 0; i < s.length; i++)
		{
			s[i] = address[i].toString();
		}
		return s;
	}


	/*
	 * @param Takes in a Type Message and determins the content type, if Text/plain return the result, if
	 * a multipart boy call getTextFromMultipart
	 * @returns a the email body of type string 
	 */
	public static String getTextFromMessage(Message message) throws MessagingException, IOException 
	{
		String result = "";
		if (message.isMimeType("text/plain")) {
			result = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		}
		return result;
	}

	/*
	 * @param takes in a type Message
	 * @returns a multipart email body converted to text and returns a type string.
	 */
	public static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException
	{
		String result = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				result = result + bodyPart.getContent();
				break; // without break same text appears two times
			} else if (bodyPart.isMimeType("text/html")) {
				String html = (String) bodyPart.getContent();
				result = result  + org.jsoup.Jsoup.parse(html).text();
			} else if (bodyPart.getContent() instanceof MimeMultipart){
				result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
			}
		}
		return result;
	}

	/**
	 * textFromHtml() strips away all tags from an html script.
	 * @param s The string to be parsed.
	 * @return A string with only the text between the html tags.
	 */
	public static String textFromHtml(String s)
	{
		String s1 = s.replaceAll(STRIP_HTML, " ");
		String s2 = s1.replaceAll("&nbsp;", "\n");
		s1 = s2.replaceAll("\\s+"," ");
		s2 = s1.toLowerCase();
		return s;
	}


	/**
	 * extractSender() extracts the email address from 
	 * @param s
	 * @return is {types} {tags}
	 */
//	public static String extractSender(String s)
//	{
//		Pattern p = Pattern.compile(SENDER_PATTERN);
//		Matcher m = p.matcher(s);
//		int start = 0, end = 0;
//		if(m.find()) 
//		{
//			start = m.start();
//			end = m.end();
//			return s.substring(start + 1, end - 1);
//		}
//		else
//			return "NO MATCH";
//	}
	
	public static ArrayList<String> extractSender(String s)
	{
		Pattern p = Pattern.compile(EMAILREGEX);
		Matcher m = p.matcher(s);
		ArrayList<String> senders = new ArrayList<String>();
		while(m.find())
		{
			senders.add(s.substring(m.start(), m.end()));
		}
		return senders;
	}
	
	/**
	 * parseContent() returns a string version of the body of the message
	 * @param m Message to be parsed.
	 * @return String version of the message body.
	 * @throws MessagingException 
	 */
	public static String parseContent(Message m) throws MessagingException {
		if(m.getContentType().toUpperCase().contains("TEXT/PLAIN")
				|| m.getContentType().toUpperCase().contains("TEXT/HTML"))
		{
			try {return m.getContent().toString();}
			catch (IOException e) {return "Error loading message";}
		}
		else
		{
			try {return getTextFromMessage(m);}
			catch(IOException e) {return "Error loading message";}
		}
	}

} 