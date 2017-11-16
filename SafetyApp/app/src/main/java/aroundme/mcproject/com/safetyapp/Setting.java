package aroundme.mcproject.com.safetyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public void saveValue(View view) {
        String message = ((EditText) findViewById(R.id.txt3)).getText().toString();
        ((TextView)findViewById(R.id.textView)).setText("Range: "+message+"km");
    }
}
