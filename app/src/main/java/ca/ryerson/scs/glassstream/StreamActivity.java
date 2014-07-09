package ca.ryerson.scs.glassstream;

import com.google.android.glass.timeline.LiveCard;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;



/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */
public class StreamActivity extends Activity {

    private static final String TAG = "StreamActivity";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
