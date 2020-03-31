package com.example.mailapp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * fields
	 */
	private Filter filter;
	private ArrayList<EmailBucket> buckets;
	private RecentBucket recents;
	private boolean verified = false;

	
	private String userName = "EdTheDev001@gmail.com";
	private String password = "password4sweng";
	private static final String fileName = "v8_11.ser";

	public final static int TAG_BUFFER = 10;
	public final static int NO_TAG_BUFFER = 50;
	/*
	 * Constructors
	 */
	public User()
	{
		this.filter = new Filter();
		this.buckets = new ArrayList<EmailBucket>();
		this.recents = new RecentBucket();
	}
	public User(String userName, String password)
	{
		this.filter = new Filter();
		this.buckets = new ArrayList<EmailBucket>();
		this.recents = new RecentBucket();
		this.userName = userName;
		this.password = password;
	}



	public boolean getMail() throws IOException
	{
		try {
		EmailReceiver receiver= new EmailReceiver(filter);
		receiver.checkEmail(this.userName, this.password);
//		sortMail(receiver.checkEmail(userName, password));
//		filter.updateLastCheck();
//		this.updateRecents();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		//reset the last check to have emails without sending new ones.
		//filter.resetLastCheck();
		//User.serialize(this, fileName);
	}

	/**
	 * @return the recents
	 */
	public RecentBucket getRecents() {
		return recents;
	}

	/**
	 * @param recents the recents to set
	 */
	public void setRecents(RecentBucket recents) {
		this.recents = recents;
	}

	/**
	 * sortMail(ArrayList<MailObject>) sorts a ArrayList of MailObjects into the appropriate bucket. 
	 * If a bucket doesn't exist, one is created. The filter is also updated with any aliased buckets.
	 * param m is MailObject to be sorted into a folder.
	 */
	public void sortMail(ArrayList<MailObject> mail)
	{
		Logger logger = Logger.getLogger(User.class.getName());

		for(MailObject m: mail)
		{
			boolean undelivered = true;
			for(EmailBucket b: buckets)
			{

				if(m.getFrom().equals(b.getBucketName()))
				{
					logger.log(Level.INFO, "Match: " + m.getFrom().equals(b.getBucketName()));
					b.addMessage(m);
					this.recents.addMessage(m);
					undelivered = false;

				}
			}
			if(undelivered)
			{
				logger.log(Level.INFO, "In undelivered");
				EmailBucket eb = new EmailBucket(m.getFrom());
				eb.addMessage(m);
				this.recents.addMessage(m);
				logger.log(Level.INFO, "name of new bucket:" + eb.getBucketName());
				buckets.add(eb);
				updateFilter(eb.getBucketName());
			}
		}
		logger.log(Level.INFO, "" + buckets.size());

	}


	/*
	 * methods
	 */


	public void updateRecents()
	{
		this.recents.updateRecents();
	}

	public void updateLastCheck()
	{
		this.filter.updateLastCheck();
	}
	public void resetLastCheck()
	{
		this.filter.resetLastCheck();
	}
	/**
	 * serialize() saves the state of a Filter object.
	 * param filter The Filter to be serialized.
	 * param fileName The name of the file where the Filter object is saved.
	 */
	public static void serialize(User user, String fileName) 
	{
		Logger logger = Logger.getLogger(User.class.getName());
		try {
			FileOutputStream file = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(user);
			out.close();
			file.close();
			logger.log(Level.INFO,"User has been serialized.");
		}
		catch(Exception e) {
			logger.log(Level.INFO, "User has not been serialized.");
			e.printStackTrace();

		}
	}
	/**
	 * deserialize() loads the saved field values for a Filter object.
	 * @param fileName The name of the file where the Filter data is saved.
	 * @return Filter with saved field values.
	 * @throws IOException 
	 */
	public static User deserialize(String fileName)
	{
		Logger logger = Logger.getLogger(User.class.getName());
		User user = null;
		try {

			FileInputStream file = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(file);
			Object object = in.readObject();
			user = (User) object;
			in.close();
			file.close();
			logger.log(Level.INFO, "User has been deserialized.");

		}
		catch(Exception e) {
			user = createNewUser();
			serialize(user, fileName);
			logger.log(Level.INFO, "new User has been created.");
			//e.printStackTrace();

		}
		try {
			System.out.println(user.userName + "   " + user.password);
			user.getMail();

		} catch (IOException e) {

			logger.log(Level.INFO, "System Initialized");
		}
		return user;
	}

	public static User createNewUser()
	{
		Logger logger = Logger.getLogger(User.class.getName());
		String email = "";
		String password = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter email address: ");
		try {
			email = reader.readLine();

		} catch (IOException e) {
			logger.log(Level.INFO, "Invalid email address entered.");
		}
		System.out.println("Enter password: ");
		try {
			password = reader.readLine();
		} catch (IOException e) {
			logger.log(Level.INFO, "Invalid password entered.");
		}

		return new User(email,password);
	}

	public void addBucket(String email)
	{
		EmailBucket b = new EmailBucket(email);
		boolean found = false;

		for(EmailBucket e: buckets)
		{
			if(e.getBucketName().equalsIgnoreCase(email))
			{
				found = true;
				break;
			}
		}
		if(!found) 
		{
			this.buckets.add(b);
		}
	}

	public int deleteBucket(String email)
	{
		int index = -1;
		boolean found = false;
		for(EmailBucket bucket: buckets)
		{
			if(bucket.getBucketName().equalsIgnoreCase(email))
			{
				index = buckets.indexOf(bucket);
				found = true;
				break;
			}
		}
		if(found) 
		{
			buckets.remove(index);
		}
		return index;
	}

	public void resetFilter()
	{
		this.filter.resetFilter();
	}

	public void addAddress(String emailAddress, ArrayList<String> tags)
	{
		this.filter.addAddress(emailAddress, tags);
	}

	public void addAddress(String emailAddress, String tag)
	{
		this.filter.addAddress(emailAddress, tag);
	}

	public void addAddress(String emailAddress)
	{
		this.filter.addAddress(emailAddress);
	}

	/**
	 * updateFilter
	 * param sender is {types} {tags}
	 */
	public void updateFilter(String sender)
	{
		ArrayList<String> keys = new ArrayList<String>();
		for(String s: this.filter.emailFilter.keySet())
		{
			if(sender.contains(s))
			{
				keys = this.filter.emailFilter.get(s);
				break;
			}
		}
		addAddress(sender, keys);
	}
	/**
	 * createExcerpt() checks to see if the text contains a string tag. If found, 
	 * it then combines the text surrounding the tag to create an excerpt. 
	 * param text: the body of text to be searched.
	 * param tags: The strings being searched for in a body of text.
	 * @return When a tag is found, A string containing that tag and up to 20 characters 
	 * before and after the location of the tag.
	 */
	public String createExcerpt(String text, ArrayList<String> keywords) 
	{

		
		Logger logger = Logger.getLogger(User.class.getName());
		
		
		Matcher m = null;

		int leftOffset = 0, rightOffset = 0;
		
		for(String keyword: keywords) 
		{
			if(keyword.equals(".*"))
			{
				return text.substring(0, Math.min(NO_TAG_BUFFER, text.length()-1));
			}
			logger.log(Level.INFO, "keyword being checked: " + keyword);

			
			m = Pattern.compile(keyword).matcher(text);
			if(m.find())
			{
				logger.log(Level.INFO, "Start: " + m.start() + " End: " + m.end());
				leftOffset = Math.min(m.start(), TAG_BUFFER);
				rightOffset = Math.min(text.length()-m.end(), TAG_BUFFER);
				return text.substring(m.start() - leftOffset, m.end() + rightOffset);
			}
		}
		return "No excerpt with current filter or keyword found in subject.";
	}

	public void addKeyword(String emailAddress, String keyword)
	{
		filter.addKeyword(emailAddress, keyword);
	}

	public void removeKeyword(String emailAddress, String keyword)
	{
		filter.removeKeyword(emailAddress, keyword);
	}
	/*
	 * Getters and Setters
	 */
	/**
	 * @return the myFilter
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * @param myFilter the myFilter to set
	 */
	public void setMyFilter(Filter myFilter) {
		this.filter = myFilter;
	}

	/**
	 * @return the myBuckets
	 */
	public ArrayList<EmailBucket> getBuckets() {
		return buckets;
	}

	/**
	 * @param myBuckets the myBuckets to set
	 */
	public void setMyBuckets(ArrayList<EmailBucket> myBuckets) {
		this.buckets = myBuckets;
	}

	public ArrayList<String> getTags(String bucketName)
	{
		return filter.getTags(bucketName);
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the tagBuffer
	 */
	public static int getTagBuffer() {
		return TAG_BUFFER;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	/**
	 * @param buckets the buckets to set
	 */
	public void setBuckets(ArrayList<EmailBucket> buckets) {
		this.buckets = buckets;
	}
	/**
	 * @return the filename
	 */
	public static String getFilename() {
		return fileName;
	}
	/**
	 * param new verified boolean value
	 */
	public void setVerified(boolean bool)
	{
		this.verified = bool;
	}
	/**
	 * return boolean verified
	 */
	public boolean isVerified() {
		return verified;
	}
}
