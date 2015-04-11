package com.cs462.gloater;

import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AsyncKillCountFetcher extends AsyncTask<Void, Void, String> {

    private final String GET_KILL_COUNT_I = "https://na.api.pvp.net/api/lol/na/v1.3/stats/by-summoner/";
    private final String GET_KILL_COUNT_II = "/summary?season=SEASON2015&api_key=" + Constants.PRIVATE_RIOT_DEVELOPER_API_KEY;

    private String username;
    private String userID;
    private String deviceID;

    public AsyncKillCountFetcher(String username, String userID, String deviceID) {
        this.username = username.trim();
        this.userID = userID;
        this.deviceID = deviceID;
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            InputStream is = null;
            String result = "";

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(GET_KILL_COUNT_I + userID + GET_KILL_COUNT_II);
            is = httpClient.execute(httpGet).getEntity().getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            is.close();
            result = sb.toString();

            JSONObject mainObject = new JSONObject(result).getJSONArray("playerStatSummaries").getJSONObject(Constants.PLAYER_STAT_SUMMARY_TYPE_UNRANKED).getJSONObject("aggregatedStats");
            String killCount = mainObject.getString("totalChampionKills");

            // Now that we have the username and kill count, send off the information to the server in another asynchronous task.
            AsyncServerNotifier serverNotifier = new AsyncServerNotifier(username, killCount, deviceID);
            // TODO uncomment this next line when ready to test with the server:
            // serverNotifier.execute();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error sending request to riot to get the kill count.");
        }

        return userID;
    }
}
