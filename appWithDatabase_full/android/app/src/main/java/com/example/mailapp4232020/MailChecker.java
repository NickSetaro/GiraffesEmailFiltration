package com.example.mailapp4232020;


import android.annotation.TargetApi;
import android.os.Build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

public class MailChecker
{
    private final String PROTOCOL = "imap";

    private String messageContent;
    private String ccList;
    private String sentDate;
    private String from;
    private String fromEmail;

    private final static int TAG_BUFFER = 50;
    private final static String STRIP_HTML = "<(.|\\s)*?>";
    private final static String EMAILREGEX = "^([<a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z>]{2,6})$";

    //key: email address
    //val: arrayList of keywords
    private Map<String, List<String>> filter;
    private Date lastCheck;

    private static final String mailColMessageText = "message_text";
    private static final String mailColMessageHtml = "message_html";
    private static final String mailColSender = "sender";
    private static final String mailColCCList = "cc_list";
    private static final String mailColDate = "message_date";
    private static final String mailColId = "idmail";
    private static final String mailColSubject = "message_subject";

    MailChecker(String date, ArrayList<Map<String, List<String>>> filter) {
        long dateAsLong = Long.parseLong(date);
        this.lastCheck = new Date(dateAsLong);
        this.filter = toMap(filter);
    }
    MailChecker(){
        this.lastCheck = new Date(1);
        this.filter = new HashMap<String, List<String>>();
    }

    /**
     * setProperties() sets the host, port and protocol for the email server connection.
     * @return The properties for connection.
     */
    private Properties setProperties()
    {
        Properties p = new Properties();
        String PROTOCOL = "imap";
        String HOST = "imap.gmail.com";
        String PORT = "993";
        p.put("mail." + PROTOCOL + ".host", HOST);
        p.put("mail." + PROTOCOL + ".port", PORT);

        p.setProperty("mail." + PROTOCOL + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.setProperty("mail." + PROTOCOL + ".socketFactory.fallback", "false");
        p.setProperty("mail." + PROTOCOL + ".socketFactory.port", PORT);

        return p;
    }


    /**
     * stringCheck() checks to see if the user address and password are valid.
     * @param address user address.
     * @param pass user password.
     * @return string to pass to frontend.
     */
    public String stringCheck(String address, String pass) {
        String result = "true";
        Properties p = setProperties();
        Session session = Session.getDefaultInstance(p);
        try{
            Store s = session.getStore(PROTOCOL);
            s.connect(address, pass);
            s.close();
        }catch (NoSuchProviderException ex) {
            result = "false";
        }catch (MessagingException ex) {
            result = "false";
        }

        return result;
    }

    /**
     * checkEmail() Accesses user email account with password and returns the messages.
     * @param user username for email account.
     * @param password password for email account.
     * @return an array of message objects.
     */
    public ArrayList<String> checkEmail (String user, String password) //added Filter
    {
        Properties p = setProperties();
        Session session = Session.getDefaultInstance(p);
        Message[] messages = null;
        ArrayList<String> newMail = new ArrayList<String>();
        try
        {
            Store s = session.getStore(PROTOCOL);
            s.connect(user, password);
            Folder inbox = s.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            messages = inbox.getMessages(Math.max(1,inbox.getMessageCount()-80), inbox.getMessageCount());

            newMail = extractComponents(messages);// with Filter object
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
     * @throws MessagingException on MessagingException
     */
    //added Filter parameter
    @TargetApi(Build.VERSION_CODES.O)
    private ArrayList<String> extractComponents(Message[] messages) throws MessagingException {
        ArrayList<String> mailList = new ArrayList<String>();
        for(Message message: messages)
        {
            //if(lastCheck.before(message.getSentDate())){
                messageContent = MailChecker.parseContent(message);
                Address[] fromAddress = message.getFrom();
                from = fromAddress[0].toString();
                String subject = message.getSubject();

                if(isValidSender(from, subject, textFromHtml(messageContent)))
                {
                    String myUrl = message.getFileName();
                    ccList = String.join(", ", parseAddresses(message.getRecipients(RecipientType.CC)));
                    sentDate = String.valueOf(message.getSentDate().getTime());

                    mailList.add(messageContent);
                    mailList.add(textFromHtml(messageContent));
                    mailList.add(ccList);
                    mailList.add(sentDate);
                    mailList.add(from);
                    mailList.add(subject);
                    mailList.add(fromEmail);


                    System.out.println("\t From: " + from);
                    System.out.println("\t Subject: " + subject);
                    System.out.println("\t Sent Date: " + sentDate);
                    System.out.println("\t Message: " + textFromHtml(messageContent));
                }
            //}// print out details of each message
        }
        return mailList;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Map<String, List<String>> toMap(ArrayList<Map<String,List<String>>> list){
        Map<String, List<String>> myMap = new HashMap<String, List<String>>();
        for(Map<String, List<String>> address: list){
            address.forEach(myMap::put);
        }
        return myMap;
    }
    /**
     * isValidSender() checks if the sender is on the user's list of pre-approved senders.
     * @param sender: sender of the message.
     * @return true if on sender list, false otherwise.
     */
    @TargetApi(Build.VERSION_CODES.N)
    private boolean isValidSender(String sender, String subject, String message)//added Filter
    {

        for(String address: filter.keySet())//using Filter
        {
            if(sender.contains(address))
            {
                for(String keyword: Objects.requireNonNull(filter.get(address)))
                {
                    Pattern p = Pattern.compile(keyword);
                    if(p.matcher(message).find() || p.matcher(subject).find())
                    {
                        fromEmail = address;
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
    private static String[] parseAddresses(Address[] address)
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
    private static String getTextFromMessage(Message message) throws MessagingException, IOException
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
    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException
    {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
                break; // without break same text appears two times
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append(org.jsoup.Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    /**
     * textFromHtml() strips away all tags from an html script.
     * @param s The string to be parsed.
     * @return A string with only the text between the html tags.
     */
    private static String textFromHtml(String s)
    {
        String s1 = s.replaceAll(STRIP_HTML, " ");
        String s2 = s1.replaceAll("&nbsp;", "\n");
        s1 = s2.replaceAll("\\s+"," ");
        return s1;
    }


    /**
     * extractSender() extracts the email address from
     * @param s string
     * @return is {types} {tags}
     */
    public static ArrayList<String> extractSender(String s)
    {
        Pattern p = Pattern.compile(EMAILREGEX);
        Matcher m = p.matcher(s);
        ArrayList<String> senders = new ArrayList<>();
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
     * @throws MessagingException on MessagingException
     */
    private static String parseContent(Message m) throws MessagingException {
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
