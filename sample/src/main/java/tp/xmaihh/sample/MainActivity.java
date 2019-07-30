package tp.xmaihh.sample;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import org.fmod.core.FmodUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    ExecutorService pool = Executors.newSingleThreadExecutor();

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
        super.onDestroy();
    }
}