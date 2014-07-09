package ca.ryerson.scs.glassstream;

import com.google.android.glass.timeline.DirectRenderingCallback;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.service.textservice.SpellCheckerService;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.RemoteViews;
import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.gl.SurfaceView;


/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */
public class StreamService extends Service implements Session.Callback{

    private static final String LIVE_CARD_TAG = "StreamService";

    private LiveCard mLiveCard;

    private Session mSession;

    private SurfaceView mSurfaceView;

    public void onPreviewStarted() {
        Log.d(TAG,"Preview started.");
    }

    @Override
    public void onSessionConfigured() {
        Log.d(TAG,"Preview configured.");
        // Once the stream is configured, you can get a SDP formated session description
        // that you can send to the receiver of the stream.
        // For example, to receive the stream in VLC, store the session description in a .sdp file
        // and open it with VLC while streming.
        Log.d(TAG, mSession.getSessionDescription());
        mSession.start();
    }

    @Override
    public void onSessionStarted() {
        Log.d(TAG,"Streaming session started.");
        ...
    }

    @Override
    public void onSessionStopped() {
        Log.d(TAG,"Streaming session stopped.");
        ...
    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        // Informs you of the bandwidth consumption of the streams
        Log.d(TAG,"Bitrate: "+bitrate);
    }

    @Override
    public void onSessionError(int message, int streamType, Exception e) {
        // Might happen if the streaming at the requested resolution is not supported
        // or if the preview surface is not ready...
        // Check the Session class for a list of the possible errors.
        Log.e(TAG, "An error occured", e);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Starts the preview of the Camera
        mSession.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Stops the streaming session
        mSession.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.stream);
            mLiveCard.setViews(remoteViews);

            mSurfaceView = (SurfaceView) findViewById(R.id.surface);

            mSession = SessionBuilder.getInstance().setCallback(this)
                    .setSurfaceView(mSurfaceView)
                    .setPreviewOrientation(90)
                    .setContext(getApplicationContext())
                    .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                    .setAudioQuality(new AudioQuality(16000, 32000))
                    .setVideoEncoder(SessionBuilder.VIDEO_H264)
                    .setVideoQuality(new VideoQuality(320,240,20,500000))
                    .build();

            mSurfaceView.getHolder().addCallback(this);


            // Display the options menu when the live card is tapped.
            Intent menuIntent = new Intent(this, LiveCardMenuActivity.class);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
            mLiveCard.publish(PublishMode.REVEAL);
        } else {
            mLiveCard.navigate();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }
}
