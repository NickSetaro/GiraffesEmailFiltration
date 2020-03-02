package v3;

import java.util.ArrayList;

public class User 
{
	/**
	 * fields
	 */
	Filter myFilter;
	ArrayList<EmailBucket> myBuckets;
	
	/*
	 * Constructors
	 */
	public User(Filter filter, ArrayList<EmailBucket> buckets)
	{
		myFilter = filter;
		myBuckets = buckets;
	}

	
	
	
	/*
	 * methods
	 */
	
	/*
	 * Getters and Setters
	 */
	/**
	 * @return the myFilter
	 */
	public Filter getMyFilter() {
		return myFilter;
	}

	/**
	 * @param myFilter the myFilter to set
	 */
	public void setMyFilter(Filter myFilter) {
		this.myFilter = myFilter;
	}

	/**
	 * @return the myBuckets
	 */
	public ArrayList<EmailBucket> getMyBuckets() {
		return myBuckets;
	}

	/**
	 * @param myBuckets the myBuckets to set
	 */
	public void setMyBuckets(ArrayList<EmailBucket> myBuckets) {
		this.myBuckets = myBuckets;
	}
}
