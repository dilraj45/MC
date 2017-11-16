package aroundme.mcproject.com.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author dilraj
 */
public class PostSOSRequest extends AppCompatActivity implements Constants {

    private final String TAG = "PostSOSRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_sosrequest);
        Intent i = getIntent();
        //sent using gesture
        if(i.getBooleanExtra("Gesture",false)){
            postSOSRequest("Help Me !!!");
        }

        // todo: move this to landing activity after login
        Log.v(TAG, "Triggering background service");
        Intent serviceIntent = new Intent(this, LocationUpdateService.class);
        this.startService(serviceIntent);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ourmenu, menu);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
        int x = pref.getInt("Reputation",0);
        pref.edit().putInt("Reputation",x);
        MenuItem item = menu.findItem(R.id.action_repo);
        item.setTitle("Reputation: " +String.valueOf(x));

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_repo)
        {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
            int x = pref.getInt("Reputation",0)+1;
            pref.edit().putInt("Reputation",x);
            //String x = (String) item.getTitle();
            //item.setTitle("Reputation: " + String.valueOf(Integer.valueOf(x.charAt(x.length()-1))-47));
            item.setTitle("Reputation: " +String.valueOf(x));
            return true;
        }
        else if(item.getItemId()==R.id.action_settings)
        {
            Intent i = new Intent(this,Setting.class);
            startActivity(i);
        }
        return false;
    }

    public void postSOSRequest(View view) {
        String message = ((EditText) findViewById(R.id.message)).getText().toString();
        postSOSRequest(message);

    }

    public void postSOSRequest(String message) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URI_BUILD_SCHEME).encodedAuthority(HOST);
        builder.appendPath(POST_SOS_REQUEST_URI);
        String urlString = builder.build().toString();

        String token = getAuthenticationToken();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put(AUTH_TOKEN, token);
            requestBody.put(MESSAGE, message);
        } catch (JSONException exception) {
            exception.printStackTrace();
            return;
        }

        PostRequestHandler handler = new PostRequestHandler(getApplicationContext()) {
        @Override
        protected void onPostExecute(Void aVoid) {
            Intent i = new Intent(this.appContext, DeleteRequest.class);
            startActivity(i);
        }};
        Log.v(TAG, String.format("URL: %s", urlString));
        Log.v(TAG, String.format("Request Body: %s", requestBody.toString()));
        handler.execute(urlString, requestBody.toString());
    }

    private String getAuthenticationToken() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_CONSTANT, Context.MODE_PRIVATE);
        return pref.getString(AUTH_TOKEN, null);
    }
}
