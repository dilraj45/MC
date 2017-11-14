package aroundme.mcproject.com.safetyapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Locale;

public class AcceptedSOSRequest extends AppCompatActivity {

    public String latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_sosrequests);
        Intent intent = getIntent();
        this.latitude = intent.getStringExtra("latitude");
        this.longitude = intent.getStringExtra("longitude");
    }

    public void goToLocation(View view) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }
}
