package com.cs462.gloater;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class AsyncSummonerIdFetcher extends AsyncTask<Void, Void, String> {

    private final String getSummonerIDPartI = "https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/";
    private final String getSummonerIDPartII = "?api_key=";
    private final String developerApiKey = "TODO: enter your developer key here, but don't commit it!";
    private String username;
    private String userID;

    public AsyncSummonerIdFetcher(String username) {
        this.username = username.trim();
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            InputStream is = null;
            String result = "";

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getSummonerIDPartI + username.replace(" ", "%20") + getSummonerIDPartII + developerApiKey);
            is = httpClient.execute(httpGet).getEntity().getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            is.close();
            result = sb.toString();
            System.out.println("result: " + result);

            JSONObject mainObject = new JSONObject(result);
            String shortenedUsername = username.toLowerCase().replaceAll("\\s+", "");
            if (mainObject.has(shortenedUsername)) {
                JSONObject summoner = mainObject.getJSONObject(shortenedUsername);
                userID = summoner.getString("id");
                System.out.println("User ID: " + userID);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error sending request to riot");
        }

        return userID;
    }
}
