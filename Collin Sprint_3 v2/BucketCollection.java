package com.example.flutterappv5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BucketCollection 
{
	/*
	 * fields
	 */
	private HashMap<String, EmailBucket> bucketCollection = new HashMap<String, EmailBucket>();

	private Logger logger = Logger.getLogger(this.getClass().getName());
	/*
	 * constructors
	 */
	public BucketCollection()
	{

	}

	/*
	 * methods
	 */
	public EmailBucket selectBucket(String s)
	{
		for(Map.Entry<String, EmailBucket> entry: bucketCollection.entrySet())
		{
			if(entry.getKey() == s)
				return entry.getValue();
		}
		return null;
	}

	/**
	 * addBucket() adds a bucket to the bucketCollection.
	 * @param b bucket to be added to the collection.
	 */
	public void addBucket(EmailBucket b)
	{
		bucketCollection.put(b.getBucketName(), b);
	}
	/**
	 * deleteBucket() deletes a bucket from the bucketCollection.
	 * @param b Bucket to be deleted.
	 */
	public void deleteBucket(EmailBucket b)
	{
		bucketCollection.remove(b);
	}
	/**
	 * addToBucket(MailObject) places a mailObject into the correct bucket or creates a new
	 * bucket if an appropriate one does not exist.
	 * @param m is MailObject to be placed into a bucket.
	 */
	public void addToBucket(MailObject m)
	{
		boolean undelivered = true;
		for(EmailBucket entry: bucketCollection.values())
		{

			if(EmailReceiver.extractSender(m.getFrom()).equals(entry.getBucketName()) )
			{
				//logger.log(Level.INFO, "Delivered");
				//logger.log(Level.INFO, entry.getBucketName());
				EmailBucket eb = entry;
				//logger.log(Level.INFO, "eb bucket is null = " + (eb == null));
				//logger.log(Level.INFO, "entrykey: " + bucketCollection.get(entry.getKey()).getBucketName());
				eb.addMessage(m);
				addBucket(eb);
				//logger.log(Level.INFO, eb.getBucketName());
				undelivered = false;
				break;
			}
		}
		if(undelivered)
		{
			//logger.log(Level.INFO, "Not Delivered");
			EmailBucket e = new EmailBucket(EmailReceiver.extractSender(m.getFrom()));
			e.addMessage(m);
			//logger.log(Level.INFO, "message from: " + m.getFrom());
			addBucket(e);
			//logger.log(Level.INFO, "new bucket is null = " + (e == null));
		}


	}
	/**
	 * addToBucket(ArrayList) adds an arraylist of mail objects to their correct bucket.
	 * @param mail is arrayList of mail objects going into buckets.
	 */
	public void addToBucket(ArrayList<MailObject> mail)
	{

		for(MailObject m: mail)
		{
			addToBucket(m);
		}
	}
	/*
	 * Getters and Setters
	 */
	/**
	 * @return the bucketCollection
	 */
	public HashMap<String, EmailBucket> getBucketCollection() {
		return bucketCollection;
	}

	/**
	 * @param bucketCollection the bucketCollection to set
	 */
	public void setBucketCollection(HashMap<String, EmailBucket> bucketCollection) {
		this.bucketCollection = bucketCollection;
	}

	public String getBucketNames()
	{
		String s = "";
		for(Map.Entry<String, EmailBucket> b: bucketCollection.entrySet())
		{
			logger.log(Level.INFO, "bucket name: " + b.getKey());
			s += b.getKey() + " ";
		}
		return s;
	}

	//	/**
	//	 * serialize() saves the state of a Filter object.
	//	 * @param filter The Filter to be serialized.
	//	 * @param fileName The name of the file where the Filter object is saved.
	//	 */
	//	public static void serialize(Filter filter, String fileName) {
	//		try {
	//			FileOutputStream file = new FileOutputStream(fileName);
	//			ObjectOutputStream out = new ObjectOutputStream(file);
	//			out.writeObject(filter);
	//			out.close();
	//			file.close();
	//			logger.log(Level.INFO,"Filter has been serialized.");
	//		}
	//		catch(Exception e) {
	//			logger.log(Level.INFO, "Filter has not been serialized.");
	//			e.printStackTrace();
	//			
	//		}
	//	}
	//	/**
	//	 * deserialize() loads the saved field values for a Filter object.
	//	 * @param fileName The name of the file where the Filter data is saved.
	//	 * @return Filter with saved field values.
	//	 */
	//	public static Filter deserialize(String fileName) 
	//	{
	//		Filter filter = null;
	//		try {
	//
	//			FileInputStream file = new FileInputStream(fileName);
	//			ObjectInputStream in = new ObjectInputStream(file);
	//			Object object = in.readObject();
	//			filter = (Filter) object;
	//			in.close();
	//			file.close();
	//			logger.log(Level.INFO, "Filter has been deserialized.");
	//
	//		}
	//		catch(Exception e) {
	//			serialize(new Filter(), fileName);
	//			logger.log(Level.INFO, "Default Filter being used.");
	//			e.printStackTrace();
	//			
	//		}
	//		return filter;
	//	}
}
