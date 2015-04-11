package com.cs462.gloater;

import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AsyncSummonerIdFetcher extends AsyncTask<Void, Void, String> {

    private final String GET_SUMMONER_ID_I = "https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/";
    private final String GET_SUMMONER_ID_II = "?api_key=" + Constants.PRIVATE_RIOT_DEVELOPER_API_KEY;

    private String username;
    private String userID;
    private String deviceID;

    public AsyncSummonerIdFetcher(String username, String deviceID) {
        this.username = username.trim();
        this.deviceID = deviceID;
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            InputStream is = null;
            String result = "";

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(GET_SUMMONER_ID_I + username.replace(" ", "%20") + GET_SUMMONER_ID_II);
            is = httpClient.execute(httpGet).getEntity().getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            is.close();
            result = sb.toString();

            JSONObject mainObject = new JSONObject(result);
            String shortenedUsername = username.toLowerCase().replaceAll("\\s+", "");
            if (mainObject.has(shortenedUsername)) {
                JSONObject summoner = mainObject.getJSONObject(shortenedUsername);
                userID = summoner.getString("id");

                // Now that we have the user ID, grab the stats we want to gloat about in another asynchronous task
                AsyncKillCountFetcher killCountFetcher = new AsyncKillCountFetcher(username, userID, deviceID);
                killCountFetcher.execute();

                // Insecure way of sending push notification to the channel "Everyone" from the mobile app
                // Note that a setting would have to be changed in the parse push dashboard to allow this
                // to work. By default it won't. Really, we want to send the push from the server using the
                // REST api for parse push.
                /*
                ParsePush push = new ParsePush();
                push.setChannel("Everyone");
                push.setMessage("The summoner whose user ID is " + userID + " is dominating someone!");
                push.sendInBackground();
                */
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error sending request to riot to get the summoner id.");
        }

        return userID;
    }
}
