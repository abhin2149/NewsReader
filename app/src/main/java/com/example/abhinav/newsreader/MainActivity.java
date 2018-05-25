package com.example.abhinav.newsreader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    ArrayList<String> titles;



    static ArrayList<String> newsurl;
    ArrayAdapter arrayAdapter;
    ListView newsList;

    SwipeRefreshLayout refreshLayout;


    public class DownloadContent extends AsyncTask<String, Void, String> {

        URL url = null;
        String result = "";
        HttpURLConnection connection;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            refreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                url = new URL(strings[0]);

                connection = (HttpURLConnection) url.openConnection();

                InputStream is = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(is);

                int data = reader.read();

                while (data != -1) {

                    char b = (char) data;

                    result += b;


                    data = reader.read();

                }


                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);


                String articles = jsonObject.getString("articles");


                JSONArray allNews = new JSONArray(articles);

                titles.clear();
                newsurl.clear();


                for (int i = 0; i < allNews.length(); i++) {

                    JSONObject jsonpart = allNews.getJSONObject(i);

                    titles.add(jsonpart.getString("title"));

                    newsurl.add(jsonpart.getString("url"));




                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            arrayAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);

        }
    }




    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    public void setTitles() {

        String type;

        if(getIntent().hasExtra("category")) {

            type = getIntent().getStringExtra("category");

            type.toLowerCase();

        }

        else{

            type="general";
        }


        DownloadContent downloadContent = new DownloadContent();



        downloadContent.execute("https://newsapi.org/v2/top-headlines?country=in&category="+type+"&apiKey=955497a9d8964f428c8bff35684de645");




        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent in = new Intent(getApplicationContext(), Webview.class);

                in.putExtra("index", i);

                startActivity(in);

            }
        });


    }

    public void showAlert() {


        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("No Internet Connection")
                .setMessage("Please set up network connection")
                .setCancelable(false)
                .setPositiveButton("WIFI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                        Toast.makeText(MainActivity.this,"Swipe Down to Refresh",Toast.LENGTH_SHORT).show();


                    }
                })
                .setNegativeButton("3G/4G", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        startActivity(new Intent(Settings.ACTION_SETTINGS));

                        Toast.makeText(MainActivity.this,"Swipe Down to Refresh",Toast.LENGTH_SHORT).show();


                    }
                }).show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        newsList = (ListView) findViewById(R.id.newsList);





        titles = new ArrayList<String>();

        newsurl = new ArrayList<String>();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, titles);

        newsList.setAdapter(arrayAdapter);

        if (Build.VERSION.SDK_INT < 23) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 1);


            }

        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);

            }
        }


        if (haveNetworkConnection()) {

            setTitles();


            Log.i("hello0", "efe");


        } else {

            showAlert();
        }


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                Log.i("hello2", "efe");

                if (haveNetworkConnection()) {


                    setTitles();


                } else {

                    showAlert();

                    refreshLayout.setRefreshing(false);
                }


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.menu_refresh: {

                refreshLayout.setRefreshing(true);
                setTitles();



            }

                break;

            case R.id.category:{

                Intent in =new Intent(getApplicationContext(),Category_list.class);

                startActivity(in);

            }

           return false;






        }


        return super.onOptionsItemSelected(item);
    }

}


