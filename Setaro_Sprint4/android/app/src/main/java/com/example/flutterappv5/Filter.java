package com.example.flutterappv5;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Filter implements Serializable
{
    /*
     * Fields
     */
    /**
     *
     */
    private static final long serialVersionUID = -8992591591894000416L;
    public HashMap<String, String> emailFilter;
    public Date lastCheck;
    public static Logger logger = Logger.getLogger(Filter.class.getName());
    public ArrayList<String> keys = new ArrayList<String>();

    private HashMap<String, String> testfilter = new HashMap<String, String>()
    {{
        put("bonfield@rowan.edu", ".*");
        put("myersjac@rowan.edu", ".*");
    }};
    /*
     * constructors
     */
    //Default Constructor with empty contact list.
    public Filter()
    {
        emailFilter = testfilter;
        resetLastCheck();
    }
    //Constructor for creating a preset list of contacts.
    public Filter(HashMap<String, String> contacts)
    {
        emailFilter = contacts;
        resetLastCheck();
    }

    /*
     * methods
     */
    /**
     * resetLastCheck() sets the lastCheck value to Jan 1st, 2000.
     */
    public void resetLastCheck()
    {
        Calendar c = Calendar.getInstance();
        c.set(2000, 1, 1, 0, 0, 0);
        this.lastCheck = c.getTime();
    }
    /**
     * updateLastCheck() sets the lastCheck to current day and time.
     * pre: A Filter object has been instantiated.
     * post: lastCheck value set to current day and time.
     */
    public void updateLastCheck()
    {
        lastCheck = new Date();
    }
    /**
     * Removes an address of any type from the emailFilter.
     * pre: The emailFilter contains an address.
     * post: The Address is removed from the emailFilter.
     * @param address Address to be removed from emailFilter.
     */
    public void removeAddress(String address)
    {
        emailFilter.remove(address);
    }
    /**
     * resetFilter() clears the filter of all email addresses and tags.
     * pre: A Filter object has an emailFilter that is not null.
     * post: The emailFilter will be set to contain only the default values.
     */
    public void resetFilter()
    {
        emailFilter.clear();
        //add defaults here
    }
    /**
     * addAbsolute() adds a new email address to the email filter that does
     * not rely on keywords.
     * pre: A Filter object has been instantiated.
     * post: The email address is added to the filter for all messages.
     * @param emailAddress The address to be added.
     */
    public void addAbsolute(String emailAddress)
    {
        emailFilter.put(emailAddress, ".*");
    }
    /**
     * addConditional() adds a new email address to the email filter that
     * relies on keywords.
     * pre: A Filter object has been instantiated.
     * post: The email address is added to the filter for messages with the given keywords.
     * @param emailAddress
     * @param tags is {types} {tags}
     */
    @TargetApi(Build.VERSION_CODES.O)
    public void addConditional(String emailAddress, String[] tags)
    {
        emailFilter.put(emailAddress, String.join("|", tags));
    }

    /**
     * serialize() saves the state of a Filter object.
     * @param filter The Filter to be serialized.
     * @param fileName The name of the file where the Filter object is saved.
     */
    public static void serialize(Filter filter, String fileName) {
        try {
            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(filter);
            out.close();
            file.close();
            logger.log(Level.INFO,"Filter has been serialized.");
        }
        catch(Exception e) {
            logger.log(Level.INFO, "Filter has not been serialized.");
            e.printStackTrace();

        }
    }
    /**
     * deserialize() loads the saved field values for a Filter object.
     * @param fileName The name of the file where the Filter data is saved.
     * @return Filter with saved field values.
     */
    public static Filter deserialize(String fileName)
    {
        Filter filter = null;
        try {

            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);
            Object object = in.readObject();
            filter = (Filter) object;
            in.close();
            file.close();
            logger.log(Level.INFO, "Filter has been deserialized.");

        }
        catch(Exception e) {
            serialize(new Filter(), fileName);
            logger.log(Level.INFO, "Default Filter being used.");
            e.printStackTrace();

        }
        return filter;
    }
    /*
     * Getters and Setters
     */

    /**
     * @return the emailFilter
     */
    public HashMap<String, String> getEmailFilter() {
        return emailFilter;
    }

    /**
     * @param emailFilter the emailFilter to set
     */
    public void setEmailFilter(HashMap<String, String> emailFilter) {
        this.emailFilter = emailFilter;
    }

    /**
     * @return the lastCheck
     */
    public Date getLastCheck() {
        return lastCheck;
    }

    /**
     * @param lastCheck the lastCheck to set
     */
    public void setLastCheck(Date lastCheck) {
        this.lastCheck = lastCheck;
    }

    public Set<String> showAddresses()
    {
        return emailFilter.keySet();
    }

    public ArrayList<String> getEmailAddresses(){
        return keys = new ArrayList<String>(emailFilter.keySet());
    }
}
