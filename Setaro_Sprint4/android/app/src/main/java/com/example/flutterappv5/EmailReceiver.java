package com.example.flutterappv5;

import java.io.IOException;
import java.util.ArrayList;
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



   /* public java.lang.Boolean loginCheck(String address, String pass) throws IOException{
        java.lang.Boolean result = true;
        Properties p = setProperties();
        Session session = Session.getDefaultInstance(p);
        try{
            Store s = session.getStore(PROTOCOL);
            s.connect(address, pass);
        }catch (NoSuchProviderException ex) {
            result = false;
        }catch (MessagingException ex) {
            result = false;
        }
        return result;
    }*/

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
    public ArrayList<MailObject> checkEmail (String user, String password, Filter filter) throws IOException//added Filter
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

            messages = inbox.getMessages(inbox.getMessageCount()-300, inbox.getMessageCount());

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
    }


    /**
     * extractComponents() assigns string representations of the different pieces of
     * an email message to class variables
     * @param messages The array of messages from the inbox being checked.
     * @throws MessagingException
     * @throws IOException
     */
    //added Filter parameter
    public ArrayList<MailObject> extractComponents(Message[] messages, Filter filter) throws MessagingException, IOException
    {
        ArrayList<MailObject> newMail = new ArrayList<MailObject>();
        ArrayList<String> recentMail = new ArrayList<String>();
        for(Message m: messages)
        {
            if(filter.getLastCheck().compareTo(m.getSentDate()) < 0)//added: check date before continuing.
            {
                messageContent = EmailReceiver.parseContent(m);
                contentType = m.getContentType();
                Address[] fromAddress = m.getFrom();
                from = fromAddress[0].toString();


                // print out details of each message
                if(isValidSender(from, textFromHtml(messageContent), filter))
                {
                    subject = m.getSubject();//0
                    toList = String.join(", ", parseAddresses(m.getRecipients(RecipientType.TO)));
                    ccList = String.join(", ", parseAddresses(m.getRecipients(RecipientType.CC)));
                    sentDate = m.getSentDate().toString();


                    newMail.add(new MailObject(m));

                    recentMail.add(messageContent);
                    System.out.println("\t From: " + from);
                    System.out.println("\t To: " + toList);
                    System.out.println("\t CC: " + ccList);
                    System.out.println("\t Subject: " + subject);
                    System.out.println("\t Sent Date: " + sentDate);
                    System.out.println("\t Message: " + textFromHtml(messageContent));
                }
            }
        }
        //logger.log(Level.INFO, recentMail.toString());
        return newMail;
    }

    /**
     * isValidSender() checks if the sender is on the user's list of pre-approved senders.
     * @param sender: sender of the message.
     * @return true if on sender list, false otherwise.
     */
    public boolean isValidSender(String sender, String message, Filter filter)//added Filter
    {
        String extracted = extractSender(sender);
        for(String e: AliasEmails)
        {
            if(extracted.equals(e))
            {
                for(String s: filter.getEmailFilter().keySet())
                {
                    if(sender.contains(s))
                        return true;
                    else {
                        return false;
                    }
                }
            }
        }
        for(String s: filter.getEmailFilter().keySet())//using Filter
        {
            if(extracted.contains(s))
            {
                if(Pattern.compile(filter.getEmailFilter().get(s)).matcher(message).find())// using Filter
                {
                    return true;
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
    public String textFromHtml(String s)
    {
        return s.replaceAll(STRIP_HTML, " ").replaceAll("&nbsp;", "\n").replaceAll("\\s+"," ").toLowerCase();
    }


    /**
     * extractSender() extracts the email address from
     * @param s
     * @return is {types} {tags}
     */
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
    /**
     * parseContent() returns a string version of the body of the message
     * @param m Message to be parsed.
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

}
