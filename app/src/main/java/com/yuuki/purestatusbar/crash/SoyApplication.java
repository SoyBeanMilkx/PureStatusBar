package com.yuuki.purestatusbar.crash;

import android.app.Application;

public class SoyApplication extends Application {

    @Override
    public void onCreate()
    {
        // TODO: Implement this method
        super.onCreate();
        ExceptionHandler.getInstance(this).init();
    }

}
