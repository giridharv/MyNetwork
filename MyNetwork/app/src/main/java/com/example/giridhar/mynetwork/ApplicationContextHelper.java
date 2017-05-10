package com.example.giridhar.mynetwork;

import android.app.Application;
import android.content.Context;

/**
 * Created by giridhar on 4/2/17.
 */

public class ApplicationContextHelper extends Application
{
    private static ApplicationContextHelper applicationHelper;
    public void onCreate()
    {
        super.onCreate();
        applicationHelper=this;
    }
    public static ApplicationContextHelper getInstance()
    {
        return  applicationHelper;

    }
    public static Context getAppContext()
    {
        return applicationHelper.getApplicationContext();
    }
}

