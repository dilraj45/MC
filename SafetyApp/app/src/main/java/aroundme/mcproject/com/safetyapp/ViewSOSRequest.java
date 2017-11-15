package aroundme.mcproject.com.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;
import java.util.Locale;

public class ViewSOSRequest extends AppCompatActivity implements Constants {

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private final String TAG = "ViewSOS";
    private static int count = 0;
    private ArrayList<SOSMessage> sosMessages;
    private MyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sosrequest);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
        pref.edit().putInt("Count", 0).apply();

        mainListView = (ListView) findViewById( R.id.mainListView );
        sosMessages = new ArrayList<SOSMessage>();
        sosMessages.add(new SOSMessage("Shubhkarman", "Help me!!", "1.00000", "0.00000"));

        this.mAdapter = new MyListAdapter(this, sosMessages);
        mainListView.setAdapter(mAdapter);
        // creating a list view adapter
        sosMessages.clear();
        mAdapter.notifyDataSetChanged();
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                //Log.i("m", "-"+pos);
                postCallForAccept(sosMessages.get(pos).username);
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f", Double.valueOf(sosMessages.get(pos).latitude),Double.valueOf(sosMessages.get(pos).longitude), Double.valueOf(sosMessages.get(pos).latitude),Double.valueOf(sosMessages.get(pos).longitude));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    pollToServer();
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {}
                }
            }
        }).start();
        // Find the ListView resource.
    }

    private void postCallForAccept(String username) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_BUILD_SCHEME).encodedAuthority(HOST);
        builder.appendPath(ACCEPT_SOS_REQUEST_URI);
        String urlString = builder.build().toString();

        String token = getAuthenticationToken();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put(AUTH_TOKEN, token);
            requestBody.put(USERNAME, username);
        } catch (JSONException exception) {
            exception.printStackTrace();
            return;
        }

        PostRequestHandler handler = new PostRequestHandler(getApplicationContext());
        Log.v(TAG, String.format("URL: %s", urlString));
        Log.v(TAG, String.format("Request Body: %s", requestBody.toString()));
        handler.execute(urlString, requestBody.toString());

    }

    private String getAuthenticationToken() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
        return pref.getString(AUTH_TOKEN, null);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ourmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_repo)
        {
            String x = (String) item.getTitle();
            item.setTitle("Reputation: " + String.valueOf(Integer.valueOf(x.charAt(x.length()-1))-48));
            return true;
        }
        else if(item.getItemId()==R.id.action_settings)
        {
            Intent i = new Intent(this,Setting.class);
            startActivity(i);
        }
        return false;
    }


    private void pollToServer() {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_BUILD_SCHEME).encodedAuthority(HOST);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
        builder.appendEncodedPath(OPEN_SOS_REQUEST_URI).appendQueryParameter(
                AUTH_TOKEN, pref.getString(AUTH_TOKEN, null)).appendQueryParameter(
                        DISTANCE, pref.getString(DISTANCE, String.valueOf(1)));
        String urlString = builder.build().toString();
        Log.v(TAG, "Get Request URI: " + urlString);
        // fetching data from server
        GetRequestHandler requestHandler = new GetRequestHandler(this) {
            @Override
            protected void onPostExecute(String response) {
                try {
                    whatToDo(response);
                } catch (JSONException exception) {
                    Log.e(TAG, exception.getLocalizedMessage().toString());
                }
            }
        };
        requestHandler.execute(urlString);
    }

    public void whatToDo(String response) throws JSONException {
        Log.v("Size", "" + sosMessages.size());
        Log.v("ref", sosMessages + "");
        sosMessages.clear();
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        //string to json and parse
        JSONObject responseObj = new JSONObject(response);
        JSONArray openRequests = responseObj.getJSONArray(OPEN_SOS_REQUEST);
        Log.v(TAG, "Json array of open sos requests " + openRequests.toString());
        Log.v(TAG, openRequests.toString());
        for (int i = 0; i < openRequests.length(); i++) {
            JSONObject object = openRequests.getJSONObject(i);
            String username = object.getString("username");
            String message = object.getString("message");
            String lat = object.getString("latitude");
            String lon = object.getString("longitude");

            sosMessages.add(new SOSMessage(username,message,lat,lon));
        }
        Log.v("Dilraj", sosMessages.toString());
        mAdapter.addItem(sosMessages);
    }

    public void postRequestActivity(View view) {
        Intent intent = new Intent(this, PostSOSRequest.class);
        startActivity(intent);
    }


    private int eventCount =0;
    private long firstEventTime =0, eventDiff = 1000;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ) {
            if (eventCount == 1 && (event.getEventTime()-firstEventTime) < eventDiff)  {

                Toast.makeText(this, "Help Me!!!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, PostSOSRequest.class);
                //sent using gesture
                intent.putExtra("Gesture",true);
                startActivity(intent);
                Log.d("key down","key down");
                eventCount = 0;
                return true;
            }
            else{
                eventCount =1;
                firstEventTime=event.getEventTime();
            }
        }
        return false;
    }


}
