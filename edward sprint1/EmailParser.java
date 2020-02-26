package v2;

import java.util.regex.*;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMultipart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailParser extends EmailReceiver
{
	/*
	 * Fields
	 */
	//
	public String message;
	public String email;
	public ArrayList<String> recentMail;	
	public boolean conditional;						
	public int leftOffset, rightOffset;
	public final static int TAG_BUFFER = 50;	
	

	private static Logger logger = Logger.getLogger(EmailParser.class.getName());
	/*
	 * Parsing Patterns:
	 */
	public final static String SENDER_PATTERN = "<.*>";
	public final static String STRIP_HTML = "<(.|\\s)*?>";

	public final static String SKIP_HTML_PATTERN = "<(p|a).*?>";
	public final static String GRAB_PARAGRAPH = "(.*<)?";
	/*
	 * Constructors
	 */

	public EmailParser() {
		//todo 
	}

	public EmailParser(Filter filter) {//changed to Filter object
		super(filter);
	}
	/*
	 * Getters and Setters
	 */

	/*
	 * Methods
	 */

	/*
	 * Helper Methods
	 */
	/**
	 * textFromHtml() strips away all tags from an html script.
	 * @param s The string to be parsed.
	 * @return A string with only the text between the html tags.
	 */
	public static String textFromHtml(String s)
	{
		return s.replaceAll(STRIP_HTML, " ").replaceAll("&nbsp;", "\n").replaceAll("\\s+"," ").toLowerCase();
	}


	public static String extractSender(String s)
	{
		Pattern p = Pattern.compile(SENDER_PATTERN);
		Matcher m = p.matcher(s);
		int start = 0, end = 0;
		if(m.find()) 
		{
			start = m.start();
			end = m.end();
			return s.substring(start + 1, end - 1);
		}
		else
			return "NO MATCH";
	}
	/**
	 * createExcerpt() checks to see if the text contains a string tag. If found, 
	 * it then combines the text surrounding the tag to create an excerpt. 
	 * @param text: the body of text to be searched.
	 * @param tags: The strings being searched for in a body of text.
	 * @return When a tag is found, A string containing that tag and up to 20 characters 
	 * before and after the location of the tag.
	 */
	public String createExcerpt(String text, ArrayList<String> tags) 
	{
		Matcher m = null;
		for(String s: tags) 
		{
			m = Pattern.compile(s).matcher(text);
			if(m.find())
			{
				leftOffset = Math.min(m.start(), TAG_BUFFER);
				rightOffset = Math.min(text.length()-m.end(), TAG_BUFFER);
				break;
			}
		}
		return text.substring(m.start() - leftOffset, m.end() + rightOffset);
	}

	
	

	
	

//	public ArrayList<String> filterMessages(Message[] mail, HashMap<String, String> filter, Date lastCheck) throws MessagingException, IOException
//	{
//		ArrayList<String> newMail = new ArrayList<String>();
//
//		for(Message m: mail)
//		{
//			String messageContent = "";
//			String contentType = null;
//			contentType = m.getContentType().toString();
//			String sender = null;
//			sender = m.getFrom()[0].toString();
//			contentType = m.getContentType();
//			if (contentType.toUpperCase().contains("TEXT/PLAIN")
//					|| contentType.toUpperCase().contains("TEXT/HTML")) 
//			{
//					messageContent = m.getContent().toString();
//			}
//			else
//			{
//					messageContent = getTextFromMessage(m);
//			}
//				if(lastCheck.compareTo(m.getSentDate()) < 0 && isValidSender(sender, messageContent))
//				{
//					newMail.add(messageContent);
//				}
//		}
//		return newMail;
//	}
}
