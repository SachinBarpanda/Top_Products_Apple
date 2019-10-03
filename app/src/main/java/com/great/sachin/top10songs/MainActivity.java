package com.great.sachin.top10songs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;
    private String feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit=10;
    private String feedCacheUrl = "INVALIDATE";
    public static final String  STATE_URL = "feedUrl";
    public static final String STATE_LIMIT= "feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = findViewById(R.id.xmlListView);

        if(savedInstanceState !=null){
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadUrl(String.format(feedUrl,feedLimit));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.mnufree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;

            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;

            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked()){
                    item.setChecked(true);
                    feedLimit = 35-feedLimit;
                }
                break;

            case R.id.mnuRefresh:
                feedCacheUrl="INVALIDATE";
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl,feedLimit));
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL,feedUrl);
        outState.putInt(STATE_LIMIT,feedLimit);

        super.onSaveInstanceState(outState);
    }

    private void downloadUrl(String feedUrl){
        if(!feedUrl.equalsIgnoreCase(feedCacheUrl)){

            Log.d(TAG, "onCreate: starting Asynctask");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedUrl);
            feedCacheUrl= feedUrl;
            Log.d(TAG, "onCreate: done");
        }
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is :"+s);

            ParseApplication parseApplication = new ParseApplication();
            parseApplication.parse(s);

//            ArrayAdapter<FeedEntry>arrayAdapter = new ArrayAdapter<>(
//                    MainActivity.this,R.layout.list_item,parseApplication.getApplications());
//
//            listApps.setAdapter(arrayAdapter);


            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this,R.layout.list_record,
                    parseApplication.getApplications());

            listApps.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with : "+strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed ==null){
                Log.e(TAG, "doInBackground: Error Downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath){
            StringBuilder xmlResult = new StringBuilder();
            try{
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response Code"+ response);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(reader);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                int charRead;
                char[] inputBuffer = new char[500];
                while(true){
                    charRead = reader.read(inputBuffer);
                    if(charRead<0){
                        break;
                    }
                    if(charRead>0){
                        xmlResult.append(String.copyValueOf(inputBuffer,0,charRead));
                    }
                }
                reader.close();
                return xmlResult.toString();
            }catch (MalformedURLException e){
                Log.e(TAG, "downloadXML: Invalid URL "+e.getMessage());
            }catch (IOException e){
                Log.e(TAG, "downloadXML: IOException reading data"+e.getMessage());
            }catch (SecurityException e){
                Log.e(TAG, "downloadXML: Security Exception"+e.getMessage());
            }

            return null;
        }
    }
}
