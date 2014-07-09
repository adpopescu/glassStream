package ca.ryerson.scs.glassstream;

import com.google.android.glass.timeline.LiveCard;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.video.VideoQuality;


/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */
public class StreamActivity extends Activity implements Session.Callback,SurfaceHolder.Callback {

    private static final String TAG = "StreamActivity";
    private Session mSession;
    private SurfaceView mSurfaceView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream);

        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);

        mSession = SessionBuilder.getInstance()
                .setCallback(this)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(90)
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                .setAudioQuality(new AudioQuality(16000, 32000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setVideoQuality(new VideoQuality(320,240,20,500000))
                .build();

        mSurfaceView.getHolder().addCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onPreviewStarted() {
        Log.d(TAG, "Preview started.");
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
    }

    @Override
    public void onSessionStopped() {
        Log.d(TAG,"Streaming session stopped.");
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
}
