package aroundme.mcproject.com.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Locale;

import static aroundme.mcproject.com.safetyapp.Constants.AUTH_TOKEN;
import static aroundme.mcproject.com.safetyapp.Constants.CLOSE_SOS_REQUEST_URI;
import static aroundme.mcproject.com.safetyapp.Constants.DISTANCE;
import static aroundme.mcproject.com.safetyapp.Constants.HOST;
import static aroundme.mcproject.com.safetyapp.Constants.OPEN_SOS_REQUEST;
import static aroundme.mcproject.com.safetyapp.Constants.OPEN_SOS_REQUEST_URI;
import static aroundme.mcproject.com.safetyapp.Constants.PREF_CONSTANT;
import static aroundme.mcproject.com.safetyapp.Constants.URI_BUILD_SCHEME;

public class DeleteRequest extends AppCompatActivity {

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private final String TAG = "DeleteSOS";
    private static int count = 0;
    private ArrayList<SOSMessage> sosMessages;
    private MyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_request);



        mainListView = (ListView) findViewById( R.id.mainListView2 );
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
                printToast();

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

    private void printToast() {
        Toast.makeText(this, "Points Rewarded",Toast.LENGTH_SHORT).show();
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
        builder.appendEncodedPath(CLOSE_SOS_REQUEST_URI).appendQueryParameter(
                AUTH_TOKEN, pref.getString(AUTH_TOKEN, null));
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


            sosMessages.add(new SOSMessage(username,"Coming","lat","lon"));
        }
        Log.v("Dilraj", sosMessages.toString());
        mAdapter.addItem(sosMessages);
    }

    public void postRequestActivity(View view) {
        Intent intent = new Intent(this, PostSOSRequest.class);
        startActivity(intent);
    }

}
