package aroundme.mcproject.com.safetyapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by papa on 14-11-2017.
 */

public class ActivityRecognizedService extends IntentService {

    private String activityRecognised = "Activity Recognised";

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        builder.setContentText( "You are currently in a Vehicle." );
                        builder.setSmallIcon( R.drawable.ic_safeapp);
                        builder.setContentTitle( activityRecognised  );
                        NotificationManagerCompat.from(this).notify(0, builder.build());
                    }

                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        builder.setContentText( "You are currently on Bicycle." );
                        builder.setSmallIcon( R.drawable.ic_safeapp);
                        builder.setContentTitle( activityRecognised);
                        NotificationManagerCompat.from(this).notify(0, builder.build());
                    }

                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        builder.setContentText( "You are currently on Foot." );
                        builder.setSmallIcon( R.drawable.ic_safeapp);
                        builder.setContentTitle(activityRecognised  );
                        NotificationManagerCompat.from(this).notify(0, builder.build());
                    }

                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        builder.setContentText( "You are currently Running." );
                        builder.setSmallIcon(R.drawable.ic_safeapp);
                        builder.setContentTitle( activityRecognised);
                        NotificationManagerCompat.from(this).notify(0, builder.build());
                    }

                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        builder.setContentText( "You are currently Still." );
                        builder.setSmallIcon( R.drawable.ic_safeapp);
                        builder.setContentTitle( activityRecognised );
                        NotificationManagerCompat.from(this).notify(0, builder.build());
                    }

                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );

                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        builder.setContentText( "You are currently Walking." );
                        builder.setSmallIcon( R.drawable.ic_safeapp);
                        builder.setContentTitle( getString( R.string.app_name ) );
                        NotificationManagerCompat.from(this).notify(0, builder.build());
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );

                    break;
                }
            }
        }
    }
}