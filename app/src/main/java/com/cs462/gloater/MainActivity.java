package com.cs462.gloater;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {

    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        this.deviceID = tManager.getDeviceId();
        this.getApplication().onCreate();
        ((EditText) findViewById(R.id.username)).setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) { // this is the return key
                    hideKeyboard(v);
                    ((EditText) findViewById(R.id.username)).clearFocus();
                    ((Button) findViewById(R.id.gloatButton)).requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void gloat(View v) {

        boolean awsPublicIpDefined = null != Constants.AWS_PUBLIC_IP_ADDRESS &&
                !"".equalsIgnoreCase(Constants.AWS_PUBLIC_IP_ADDRESS.trim());

        if (!awsPublicIpDefined) {
            System.err.println("The AWS IP address is not defined!");
        }

        EditText editText = (EditText) findViewById(R.id.username);
        hideKeyboard(editText);

        String username = editText.getText().toString();
        AsyncSummonerIdFetcher idFetcher = new AsyncSummonerIdFetcher(username, deviceID);
        idFetcher.execute();
    }
}
