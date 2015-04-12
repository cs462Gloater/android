package com.cs462.gloater;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class Gloater extends Application {

    private static Gloater singleton;

    public Gloater() {};

    public Application getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        boolean secretsReplaced = !"SECRET1".equalsIgnoreCase(Constants.PRIVATE_APPLICATION_ID) &&
                !"SECRET2".equalsIgnoreCase(Constants.PRIVATE_CLIENT_KEY); /*&&
                !"SECRET3".equalsIgnoreCase(Constants.PRIVATE_RIOT_DEVELOPER_API_KEY);*/

        if (!secretsReplaced) {
            System.err.println("You must replace key values in the Constants class where you see \"SECRETn\"");
            return;
        } else {
            Parse.initialize(this, Constants.PRIVATE_APPLICATION_ID, Constants.PRIVATE_CLIENT_KEY);
            ParsePush.subscribeInBackground(Constants.PARSE_CHANNEL, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                    } else {
                        Log.e("com.parse.push", "failed to subscribe for push", e);
                    }
                }
            });
        }
    }

}
