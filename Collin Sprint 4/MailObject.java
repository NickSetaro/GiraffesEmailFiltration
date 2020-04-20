package com.example.flutterappv5;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

public class MailObject
{
    /*
     * fields
     */

    private String subject, from, toList, ccList, contentType, contentText;
    private Date sentDate;
    private Object message;
    private Logger logger = Logger.getLogger(this.getClass().getName());



    /*
     * constructors
     */

    @TargetApi(Build.VERSION_CODES.O)
    public MailObject(Message m) throws MessagingException, IOException
    {
        contentType = m.getContentType();
        contentText = EmailReceiver.parseContent(m);
        subject = m.getSubject();//0
        toList = String.join(", ", EmailReceiver.parseAddresses(m.getRecipients(RecipientType.TO)));
        ccList = String.join(", ", EmailReceiver.parseAddresses(m.getRecipients(RecipientType.CC)));
        sentDate = m.getSentDate();
        Address[] fromAddress = m.getFrom();
        from = fromAddress[0].toString();
        message = m.getContent();
    }

    /*
     * Getters and Setters
     */

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }


    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    /**
     * @return the contentText
     */
    public String getContentText() {
        return contentText;
    }


    /**
     * @param contentText the contentText to set
     */
    public void setContentText(String contentText) {
        this.contentText = contentText;
    }


    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }


    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }


    /**
     * @return the from
     */
    public String getFrom() {
        return EmailReceiver.extractSender(from);
    }


    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }


    /**
     * @return the toList
     */
    public String getToList() {
        return toList;
    }


    /**
     * @param toList the toList to set
     */
    public void setToList(String toList) {
        this.toList = toList;
    }


    /**
     * @return the ccList
     */
    public String getCcList() {
        return ccList;
    }


    /**
     * @param ccList the ccList to set
     */
    public void setCcList(String ccList) {
        this.ccList = ccList;
    }


    /**
     * @return the sentDate
     */
    public Date getSentDate() {
        return sentDate;
    }


    /**
     * @param sentDate the sentDate to set
     */
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }


    /**
     * @return the message
     */
    public Object getMessage() {
        return message;
    }


    /**
     * @param message the message to set
     */
    public void setMessage(Object message) {
        this.message = message;
    }

    public String stringDate(){
        return sentDate.toString();
    }

    public ArrayList<String> contentList(){
        ArrayList<String> list = new ArrayList<>();
        list.add(subject);
        list.add(contentText);
        return list;
    }

    public ArrayList<String> allContent(){
        ArrayList<String> list = new ArrayList<>();
        list.add("/");
        list.add(stringDate());
        list.add(subject);
        list.add(contentText);
        return list;
    }


}

