package v1;

import java.util.regex.*;

import javax.mail.Message;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailParser extends EmailReceiver
{
	/*
	 * Fields
	 */
	//
	public Message message;
	//public String message;
	public String email;
	public ArrayList<String> tags, emailAddresses;	
	public boolean conditional;						
	public int leftOffset, rightOffset;
	public final static int TAG_BUFFER = 20;			

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

	public EmailParser(Message message) {
		super();
		this.message = message;
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
	 * hasTag() returns true if one of the tags is found in the text. Also sets the
	 * leftOffset and rightOffset to the first and last index of the first appearance 
	 * of the first tag found.
	 * @param text: The body of text to be searched.
	 * @param tags: strings that are being searched for in the body of text
	 * @return is true if the text contains one of the tags, false otherwise.
	 */
	public boolean hasTag(String text, ArrayList<String> tags) 
	{
		for(String s: tags) 
		{
			if(text.contains(s)) {
				Pattern p = Pattern.compile(s);
				Matcher m = p.matcher(text);
				m.find();

				leftOffset = Math.min(m.start(), TAG_BUFFER);
				rightOffset = Math.min(text.length()-m.end(), TAG_BUFFER);
				return true;
			}
		}
		return false;
	}
	/**
	 * createExcerpt() checks to see if the text contains a string tag. If found, 
	 * it then combines the text surrounding the tag to create an excerpt. 
	 * @param text: the body of text to be searched.
	 * @param tags: The strings being searched for in a body of text.
	 * @return If a tag is found, A string containing the tag and up to 20 characters 
	 * before and after the location of the tag. Otherwise NULL.
	 */
	public String createExcerpt(String text) 
	{

		return text.substring(leftOffset, rightOffset);


	}

}
