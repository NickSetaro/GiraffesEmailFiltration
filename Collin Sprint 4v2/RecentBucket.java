package com.example.flutterappv5;

import android.annotation.TargetApi;
import android.os.Build;

import com.example.flutterappv5.EmailBucket;
import com.example.flutterappv5.MailObject;

import java.util.ArrayList;
import java.util.Date;

public class RecentBucket extends EmailBucket
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final int HOURS = 24;
    private final int MINUTES = 60;
    private final int SECONDS= 60;
    private final long MILSEC = 1000L;

    public RecentBucket()
    {
        super("Recent");
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void updateRecents()
    {
        Date timeStamp = new Date();
        Date cutOff = new Date(timeStamp.getTime() - HOURS * MINUTES * SECONDS * MILSEC);

        ArrayList<MailObject> newMessages = new ArrayList<MailObject>();
        this.messages.forEach(e -> {
            if(e.getSentDate().compareTo(cutOff) > 0)
            {
                newMessages.add(e);
            }
        });
        messages = newMessages;
    }
}
