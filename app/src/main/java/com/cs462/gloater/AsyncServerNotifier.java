package com.cs462.gloater;

import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

/**
 * Note that this asynchronous task is not tested yet! After the server is put up we need to make sure
 * that the POST request is sent properly.
 */
public class AsyncServerNotifier extends AsyncTask<Void, Void, Void> {

    private final String POST_GLOAT_I = "http://" + Constants.AWS_PUBLIC_IP_ADDRESS + "/gloater?username=";
    private final String POST_GLOAT_II = "&deviceid=";
    private final String POST_GLOAT_III = "&killcount=";

    private String username;
    private String killCount;
    private String deviceID;

    public AsyncServerNotifier(String username, String killCount, String deviceID) {
        this.username = username.trim();
        this.killCount = killCount;
        this.deviceID = deviceID;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            InputStream is = null;
            String result = "";

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(POST_GLOAT_I + username.replace(" ", "%20") + POST_GLOAT_II + deviceID + POST_GLOAT_III + killCount);
            httpClient.execute(httpPost);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error sending POST request to server.");
        }

        return null;
    }
}
