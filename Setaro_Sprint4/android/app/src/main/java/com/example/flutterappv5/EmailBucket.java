package com.example.flutterappv5;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailBucket implements Serializable
{	
	/*
	 * fields
	 */
	private String bucketName;
	private ArrayList<MailObject> messages; 
	public static ArrayList<EmailBucket> emailBuckets;
	public HashMap<String, ArrayList<String>> returnMap;

	private static Logger logger = Logger.getLogger(EmailBucket.class.getName());
	private static final long serialVersionUID = 1L;
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
	public void addMessage(MailObject m)
	{
		
		messages.add(m);
	}

	public void deleteMessage(MailObject m)
	{
		messages.remove(m);
	}
	/**
	 * sortMail(ArrayList<MailObject>) sorts a ArrayList of MailObjects into the appropriate bucket. 
	 * If a bucket doesn't exist, one is created.
	 * @param 'm' is MailObject to be sorted into a folder.
	 */
	public static void sortMail(ArrayList<MailObject> mail)
	{

		for(MailObject m: mail)
		{
			boolean undelivered = true;
			for(EmailBucket b: emailBuckets)
			{
				logger.log(Level.INFO, "b.getBucketName(): " + b.getBucketName());
				logger.log(Level.INFO, "m.getFrom(): " + m.getFrom());
				if(m.getFrom().equals(b.getBucketName()))
				{
					logger.log(Level.INFO, "Match: " + m.getFrom().equals(b.getBucketName()));
					b.addMessage(m);
					undelivered = false;
					
				}
			}
			if(undelivered)
			{
				logger.log(Level.INFO, "In undelivered");
				EmailBucket eb = new EmailBucket(m.getFrom());
				eb.addMessage(m);
				logger.log(Level.INFO, "name of new bucket:" + eb.getBucketName());
				emailBuckets.add(eb);
			}
		}
		logger.log(Level.INFO, "" + EmailBucket.getEmailBuckets().size());
		
	}
	/**
	 * sortMail(MailObject) sorts a single MailObject into the appropriate bucket. If a bucket doesn't 
	 * exist, one is created.
	 * @param 'm' is MailObject to be sorted into a folder.
	 */
//	public static void sortMail(MailObject m) 
//	{
//		boolean undelivered = true;
////		for(EmailBucket b: emailBuckets)
////		{
////			if(m.getFrom().equals(b.getBucketName()))
////			{
////				b.addMessage(m);
////				undelivered = false;
////				break;
////			}
////		}
////		if(undelivered)
////		{
////			emailBuckets.add(new EmailBucket(m.getFrom()));
////		}
////		boolean undelivered = true;
//		for(EmailBucket b: emailBuckets)
//		{
//			if(m.getFrom().equals(b.getBucketName()))
//			{
//				b.addMessage(m);
//				undelivered = false;
//				break;
//			}
//		}
//		if(undelivered)
//		{
//			emailBuckets.add(new EmailBucket(m.getFrom()));
//		}
//	}
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
//good till here
		/*
		 * For each newBucket, see if there is an oldBucket with that name. If yes,
		 * the buckets will be merged. Otherwise, the new bucket is added to the
		 * oldBucket list.
		 */
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
	
	@TargetApi(Build.VERSION_CODES.N)
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
	 * @return the emailBuckets
	 */
	public static ArrayList<EmailBucket> getEmailBuckets() {
		return emailBuckets;
	}

	/**
	 * @param emailBuckets the emailBuckets to set
	 */
	public static void setEmailBuckets(ArrayList<EmailBucket> emailBuckets) {
		EmailBucket.emailBuckets = emailBuckets;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public HashMap<String,ArrayList<String>> messageMap(){
		HashMap<String, ArrayList<String>> returnMap = new HashMap<>();
		for(MailObject m: messages){
			returnMap.put(m.stringDate(), m.contentList());
		}
		return returnMap;
	}
	

}
