package com.zkstudios.currencyconverterlite;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.zkstudios.currencyconverterlite.MainActivity.extractValue;
import static com.zkstudios.currencyconverterlite.MainActivity.keyVal3;
import static com.zkstudios.currencyconverterlite.MainActivity.keyVal4;
import static com.zkstudios.currencyconverterlite.MainActivity.keyVal5;

public class JobClass extends JobService {
    private boolean jobCancelled = false;
    static boolean fromJobClass = false;
    static String pastRate;
    static  Context context;
    SharedPreferences preferences;
    @Override
    public boolean onStartJob(JobParameters params) {
        preferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        doBackgroundWork(params);
        context=getBaseContext();
        fromJobClass=true;
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FetchData fetchData=new FetchData();
                String from=preferences.getString(keyVal4,"");
                String to=preferences.getString(keyVal5,"");
                pastRate=preferences.getString(keyVal3,"");
                fetchData.execute(extractValue(from)+"+to+"+extractValue(to));
                jobFinished(params, false);
                fromJobClass=false;
            }
        }).start();
    }
    public static void showNotification(String str)
    {
        try {
            if(!pastRate.equals(str))
            {
                if(notiValue(str)!=null)
                {
                    Intent intent=new Intent(context,MainActivity.class);
                    intent.setAction(Intent. ACTION_MAIN ) ;
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);
                    String valBefore=notiValue(pastRate);
                    String valAfter=notiValue(str);
                    NotificationManagerCompat managerCompat;
                    managerCompat = NotificationManagerCompat.from(context);
//                    Notification notify = new NotificationCompat.Builder(context, notification.CHANNEL_1_ID)
//                            .setSmallIcon(R.drawable.currency)
//                            .setContentTitle("Currency Checker")
//                            .setContentText("Rate Changed")
//                            .setAutoCancel(true)
//                            .setContentIntent(pendingIntent)
//                            .setStyle(new NotificationCompat.BigTextStyle()
//                                    .bigText("Before :"+valBefore+"\nAfter :"+valAfter))
//                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                            .setPriority(NotificationCompat.PRIORITY_LOW)
//                            .build();
//                    managerCompat.notify(1, notify);
                }
            }
        }
        catch (Exception e)
        {
        }

    }
    public static String notiValue(String str)
    {
        try {
            int int1=str.indexOf("=");
            int int2=str.indexOf(" ",int1);
            String value=str.substring(int1+1,int2);
            return  value;
        }
        catch (Exception e)
        {

        }
        return null;

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        fromJobClass=false;
        return true;
    }
}