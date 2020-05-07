package com.example.flutterappv5;

import android.annotation.TargetApi;
import android.os.Build;

import com.sun.mail.imap.IMAPFolder;

import java.io.IOException;
import java.util.*;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
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
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
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
    public final static String STRIP_HTML = "<(.|\\s)*?>";
    public final static String EMAILREGEX = "^([<a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z>]{2,6})$";

    public Filter filter;
    public ArrayList<String> recentMail = new ArrayList<String>();
    public ArrayList<String> AliasEmails = new ArrayList<String>();

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



    public String stringCheck(String address, String pass) throws IOException{
        String result = "true";
        Properties p = setProperties();
        Session session = Session.getDefaultInstance(p);
        try{
            Store s = session.getStore(PROTOCOL);
            s.connect(address, pass);
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
     * @throws IOException
     */
   /* public ArrayList<MailObject> checkEmail (String user, String password) throws IOException//added Filter
    {
        Properties p = setProperties();
        Session session = Session.getDefaultInstance(p);
        Message[] messages = null;
        ArrayList<MailObject> newMail = new ArrayList<MailObject>();
        try
        {
            Store s = session.getStore(PROTOCOL);
            s.connect(user, password);
            Folder inbox = s.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            messages = inbox.getMessages(inbox.getMessageCount()-100, inbox.getMessageCount());

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
    }*/



    public ArrayList<MailObject> returnNewMessagesWithDate(String user, String password) throws IOException{

        Properties p = setProperties();
        Session session = Session.getDefaultInstance(p);
        Message[] messages;
        ArrayList<MailObject> newMail = new ArrayList<MailObject>();
        int end = 0;
        try {
            Store s = session.getStore(PROTOCOL);
            s.connect(user, password);
            Folder inbox = s.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            if(filter.lastCheck == null) {
                messages = inbox.getMessages(inbox.getMessageCount()-50, inbox.getMessageCount());
                newMail = extractComponents(messages, filter);
                filter.lastIndex = inbox.getMessageCount();

            }
            else if(inbox.getMessage(inbox.getMessageCount()).getSentDate().after(filter.lastCheck)) {
                    end = inbox.getMessageCount();
                    messages = inbox.getMessages(filter.lastIndex, end);
                    newMail = extractComponents(messages, filter);
                    filter.lastIndex = inbox.getMessageCount();
            }
            else{
                System.out.println("No new Emails");
                inbox.close(false);
                s.close();
                return null;
            }


            inbox.close(false);
            s.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + PROTOCOL);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        }
        Collections.reverse(newMail);
        return newMail;
    }





    /*public void messageListener (Folder inbox) throws IOException//added Filter
    {

        Properties p = setProperties();
        Session session = Session.getDefaultInstance(p);
        ArrayList<MailObject> newMail = new ArrayList<MailObject>();
        int start;
        int end;
        try {
            start = inbox.getMessageCount() - 50;
            end = inbox.getMessageCount();

            while (start <= end) {
                Message[] messages = inbox.getMessages(start, end);
               extractComponents(messages, filter);
                // new messages that have arrived
                start = end + 1;
                end = inbox.getMessageCount();
            }

            inbox.addMessageCountListener(new MessageCountAdapter() {
                public void messagesAdded(MessageCountEvent ev) {
                    Message[] message = ev.getMessages();
                    try {
                        extractComponents(message, filter);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            // wait for new messages
            for (; ; )
                ((IMAPFolder) inbox).idle();

        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + PROTOCOL);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        }
    }*/




    /**
     * extractComponents() assigns string representations of the different pieces of
     * an email message to class variables
     * @param messages The array of messages from the inbox being checked.
     * @throws MessagingException
     * @throws IOException
     */
    //added Filter parameter
    @TargetApi(Build.VERSION_CODES.O)
    public ArrayList<MailObject> extractComponents(Message[] messages, Filter filter) throws MessagingException, IOException
    {
        ArrayList<MailObject> newMail = new ArrayList<MailObject>();
        UUID uuid;

        for(Message m: messages)
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

                    if(filter.getKeywords(from) == null) {
                        newMail.add(new MailObject(m));
                    }
                    else{
                        newMail.add(new MailObject(m, filter.getKeywords(from)));
                    }

                    System.out.println("\t From: " + from);
                    System.out.println("\t To: " + toList);
                    System.out.println("\t CC: " + ccList);
                    System.out.println("\t Subject: " + subject);
                    System.out.println("\t Sent Date: " + sentDate);
                    System.out.println("\t Message: " + textFromHtml(messageContent));
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
        int leftOffset = 0, rightOffset = 0;
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



    public static String filterbody(String content, ArrayList<String> keywords) {
        String result = "";
        String sub = "";
        int index;
        int indexright;
        int indexleft;
        int size = 100;

        content = content.replace("\n", "").replace("\r", "");

        if (keywords == null) {
            if (content.length() <= size)
                return content;
            index = content.lastIndexOf(" ", size - 3);
            if (index < 0)
                return content.substring(0, size);
            return content.substring(0, index) + "...";
        }
            for (int i = 0; i < keywords.size(); i++) {
                if (content.contains(keywords.get(i))) {
                    index = content.indexOf(keywords.get(i));

                    try {
                        sub = content.substring(index - 50);
                        indexleft = index - 50;
                    } catch (IndexOutOfBoundsException e) {
                        indexleft = 0;
                    }

                    try {
                        sub = content.substring(index + 50);
                        indexright = index + 50;
                    } catch (IndexOutOfBoundsException e) {
                        indexright = content.length();
                    }

                    result = "With Keyword: " + content.substring(indexleft, indexright);
                    return result + "...";
                }
                try {
                    result = content.substring(0, 100);
                } catch (IndexOutOfBoundsException e) {
                    result = content.substring(0, content.length());

                }
            }
            return result + "...";
        }

    /**
     * parseContent() returns a string version of the body of the message
     * @param 'm' Message to be parsed.
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

    public void addAlias(String address) {
        AliasEmails.add(address);
    }

    public void removeAlias(String address) {
        for(int i = 0; i < AliasEmails.size(); i++) {
            if(AliasEmails.get(i).contentEquals(address)) {
                AliasEmails.remove(i);
            }
        }
    }

    public void setFilter(Filter newFilter){
        filter = newFilter;
    }

}
