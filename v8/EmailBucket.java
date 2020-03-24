package v8;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailBucket implements Serializable
{	
	/*
	 * fields
	 */
	protected String bucketName;
	protected ArrayList<MailObject> messages; 

	protected static Logger logger = Logger.getLogger(EmailBucket.class.getName());
	protected static final long serialVersionUID = 1L;
	/*
	 * constructors
	 */
	public EmailBucket(String bucketName)
	{
		this.bucketName = bucketName;
		this.messages = new ArrayList<MailObject>();
	}

	
	public EmailBucket(MailObject m)
	{
		this.bucketName = m.getFrom();
		this.messages = new ArrayList<MailObject>() {
			{
				add(m);
			}
		};
	}
	public void removeDuplicates() 
	{
		ArrayList<MailObject> newList = new ArrayList<MailObject>();
		ArrayList<MailObject> a = new ArrayList<MailObject>();
		
		for(MailObject m: messages)
		{
			boolean duplicate = false;
			for(MailObject o: a)
			{
				if(m.equals(o)) 
				{
					duplicate = true;
					break;
				}
			}
			if(!duplicate)
			{
				newList.add(m);
			}
		}
		messages = newList;
	}
	
	public void addMessage(MailObject m)
	{
		
		messages.add(m);
	}

	public void deleteMessage(MailObject m)
	{
		messages.remove(m);
	}
	
	public static ArrayList<EmailBucket> sortMail(ArrayList<EmailBucket> oldBuckets, ArrayList<MailObject> mail)
	{
		ArrayList<EmailBucket> bucks = new ArrayList<EmailBucket>();
		ArrayList<EmailBucket> newBuckets = EmailBucket.mailToBuckets(mail);
		logger.log(Level.INFO, "buckets in sortMail: " + newBuckets.size());
		
		
		for(EmailBucket b: oldBuckets)
		{
			logger.log(Level.INFO, "buckets from 'buckets': " + b.getBucketName());
		}
		for(EmailBucket b: newBuckets)
		{
			for(MailObject m: mail)
			{
				if (m.getFrom().equals(b.getBucketName())) 
				{
					b.addMessage(m);
				}
			}
			
		}
		for(EmailBucket n: newBuckets)
		{
			boolean noSuchBucket = true;
			for(EmailBucket b: oldBuckets)
			{
				if(n.getBucketName().equals(b.getBucketName()))
				{
					bucks.add(EmailBucket.mergeBuckets(b, n));
					logger.log(Level.INFO, "bucks size: " + bucks.size());
					noSuchBucket = false;
					break;
				}
			} 
			if(noSuchBucket)
			{
				bucks.add(n);
			}
		}
		return bucks;
	}
	/**
	 * mailToBuckets() creates buckets for an ArrayList of MailObjects
	 * @param mail
	 * @return is {types} {tags}
	 */
	private static ArrayList<EmailBucket> mailToBuckets(ArrayList<MailObject> mail)
	{
		HashSet<String> names = new HashSet<String>();
		ArrayList<EmailBucket> buckets = new ArrayList<EmailBucket>();
		
		for(MailObject m: mail)
		{
			names.add(m.getFrom());
		}
		for(String s: names)
		{
			buckets.add(new EmailBucket(s));
		}
		return buckets;
		
	}
	
	private static EmailBucket mergeBuckets(EmailBucket one, EmailBucket two)
	{
		
		one.getMessages().forEach(e->{
			two.addMessage(e);
		});
		return two;
	}
	
	/*
	 * Getters and Setters
	 */
	/**
	 * @return the bucketName
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * @param bucketName the bucketName to set
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * @return the messages
	 */
	public ArrayList<MailObject> getMessages() {
		return messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(ArrayList<MailObject> messages) {
		this.messages = messages;
	}


	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
