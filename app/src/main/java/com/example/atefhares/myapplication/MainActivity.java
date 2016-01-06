package com.example.atefhares.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements Callback{
    public boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //showing the app icon on ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Checking if the device is connected to the Internet
        if(! CheckInternetConnection() )
            showDialog(this);

        //setting the layout
        setContentView(R.layout.activity_main);

        //Tablet or Phone
        if(findViewById(R.id.detailfragment) != null)
        {
            mTwoPane=true;
            MainActivityFragment.mTwopane=true;
            DetailActivityFragment.mTwopane=true;
        }
        else
        {
            mTwoPane=false;
            MainActivityFragment.mTwopane=false;
            DetailActivityFragment.mTwopane=false;
        }

    }

    public boolean CheckInternetConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void showDialog(final Activity activity) {
        String title = "No Internet Connection Found";
        CharSequence message = "You can not proceed without an Internet connection, Please check your connection and then press \"Retry\"";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        recreate();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {

            if(id!=null) {
            Bundle args = new Bundle();
            args.putString("ID",id);
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
             getFragmentManager().beginTransaction()
                        .replace(R.id.detailfragment, fragment/*, DETAILFRAGMENT_TAG*/)
                        .commit();
            }
        }
        else {
            //on Phone mode show launch detail activity using Intent
            Intent intent = new Intent(this, DetailActivity.class).putExtra(Intent.EXTRA_TEXT,id);
            startActivity(intent);
        }
    }
}

