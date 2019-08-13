package tp.xmaihh.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.android.librecord.wav.wav;
import com.android.librecord.wav.AudioTrack;
import com.android.librecord.mp3.mp3;

import org.fmod.core.FmodUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity {
    ExecutorService pool = Executors.newSingleThreadExecutor();
    boolean isPlaying = false;
    private wav mediaRecordUtil;
    private AudioTrack audioTrackUtil;
    private mp3 mp3RecordUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FmodUtils.getInstance(this);

    }

    public void playSound(View view) {
//        FmodUtils.stopSound();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                FmodUtils.playSound("file:///android_asset/singing.wav", 2);
            }
        });

//        MediaPlayer player;
//        try {
//            AssetManager assetManager = this.getAssets();
//            AssetFileDescriptor afd = assetManager.openFd("singing.wav");
//            player = new MediaPlayer();
//            player.setDataSource(afd.getFileDescriptor(),
//                    afd.getStartOffset(), afd.getLength());
//            player.setLooping(false);
//            player.prepare();
//            player.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        FmodUtils.getInstance(this).close();
        if (null != audioTrackUtil) {
            audioTrackUtil.stop();
            audioTrackUtil = null;
        }
        if (null != mp3RecordUtil) {
            mp3RecordUtil.stop();
            mp3RecordUtil = null;
        }
        isPlaying = false;
        super.onDestroy();
    }


    public void startRecording(View view) {
        if (isPlaying) {
            audioTrackUtil.stop();
            audioTrackUtil = null;
//            mediaRecordUtil.stopRecording();
//            mediaRecordUtil = null;
            isPlaying = false;
        } else {
            audioTrackUtil = new AudioTrack();
            audioTrackUtil.start();
//            mediaRecordUtil = new wav();
//            mediaRecordUtil.startRecording(this);
            isPlaying = true;
        }
    }


    public void mp3Recording(View view) {
        if (isPlaying) {
            mp3RecordUtil.stop();
            mp3RecordUtil = null;
            isPlaying = false;
        } else {
            mp3RecordUtil = new mp3();
            mp3RecordUtil.record(this);
            isPlaying = true;
        }
    }
}