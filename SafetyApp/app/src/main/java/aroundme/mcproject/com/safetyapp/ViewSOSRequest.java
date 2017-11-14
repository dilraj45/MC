package aroundme.mcproject.com.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static aroundme.mcproject.com.safetyapp.Constants.AUTH_TOKEN;
import static aroundme.mcproject.com.safetyapp.Constants.DISTANCE;
import static aroundme.mcproject.com.safetyapp.Constants.DISTANCE_VALUE;
import static aroundme.mcproject.com.safetyapp.Constants.HOST;
import static aroundme.mcproject.com.safetyapp.Constants.LATITUDE;
import static aroundme.mcproject.com.safetyapp.Constants.LONGITUDE;
import static aroundme.mcproject.com.safetyapp.Constants.PREF_CONSTANT;
import static aroundme.mcproject.com.safetyapp.Constants.UPDATE_LOCATION_URI;
import static aroundme.mcproject.com.safetyapp.Constants.URI_BUILD_SCHEME;

public class ViewSOSRequest extends AppCompatActivity {

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private final String TAG = "ViewSOS";

    private ArrayList<SOSMessage> sosMessages;
    private MyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sosrequest);

        pollToServer();

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );

//        this.mAdapter = new MyListAdapter(this, sosMessages);
  //      mainListView.setAdapter(mAdapter);


        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                //Log.i("m", "-"+pos);

                Intent myIntent = new Intent(ViewSOSRequest.this, AcceptedSOSRequest.class);
                myIntent.putExtra("latitude", sosMessages.get(pos).latitude);
                myIntent.putExtra("longitude", sosMessages.get(pos).longitude);
                startActivity(myIntent);
            }
        });
    }

    private void pollToServer() {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_BUILD_SCHEME).encodedAuthority(HOST);
        builder.appendPath(UPDATE_LOCATION_URI);
        String urlString = builder.build().toString();

        JSONObject requestBody = new JSONObject();
        while (true) {
            try {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
                requestBody.put(AUTH_TOKEN, pref.getString(AUTH_TOKEN, null));
                requestBody.put(DISTANCE, DISTANCE_VALUE);
            } catch (JSONException exception) {
                Log.v(TAG, exception.getStackTrace().toString());
                exception.printStackTrace();
            }

            Log.v(TAG, "URL: " + urlString);
            Log.v(TAG, "Request body: " + requestBody.toString());
            PostRequestHandler handler = new PostRequestHandler(getApplicationContext()) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (this.response == null || this.response.isEmpty()) {
                        Toast.makeText(this.appContext, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(appContext, this.response, Toast.LENGTH_SHORT).show();
                    try {
                        whatToDo(this.response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };
            handler.execute(urlString, requestBody.toString());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Log.v(TAG, e.getStackTrace().toString());
            }
        }
    }

    private void whatToDo(String response) throws JSONException {
        mAdapter.clear();
        sosMessages.clear();

        //string to json and parse
        JSONArray array = new JSONArray(response);

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            String username = object.getString("username");
            String message = object.getString("status");
            String lat = object.getString("lat");
            String lon = object.getString("lon");

            sosMessages.add(new SOSMessage(username,message,lat,lon));
        }
        mAdapter.addAll(sosMessages);
    }

    public void postRequestActivity(View view) {
        Intent intent = new Intent(this, PostSOSRequest.class);
        startActivity(intent);
    }
}
