package tp.xmaihh.sample;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.librecord.AudioRecord;
import com.android.librecord.AudioRecordConfig;
import com.android.librecord.AudioTrack;

public class AudioRecorderActivity extends Activity implements View.OnClickListener {
    private RadioGroup mRGOutputFormat;
    private RadioGroup mRGSamplingRate;
    private RadioGroup mRGBitRate;
    private TextView mTvFilePath;
    private TextView mTvRecordState;
    private Button mBtnStart;
    private Button mBtnPause;
    private Button mBtnStop;

    private AudioRecord mAudioRecord;
    private AudioRecordConfig mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        //*
        mRGOutputFormat = findViewById(R.id.mr_rgOutputFormat);
        mRGSamplingRate = findViewById(R.id.mr_rgSamplingRate);
        mRGBitRate = findViewById(R.id.mr_rgBitrate);
        mTvFilePath = findViewById(R.id.mr_tvFilePath);
        mTvRecordState = findViewById(R.id.mr_tvTimeState);
        mBtnStart = findViewById(R.id.mr_btnStart);
        mBtnPause = findViewById(R.id.mr_btnPause);
        mBtnStop = findViewById(R.id.mr_btnStop);

        mBtnStart.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mConfig = new AudioRecordConfig(
                MediaRecorder.AudioSource.MIC,
                AudioRecordConfig.SampleRate.MID_QUALITY,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioRecordConfig.OutputFormat.PCM);

        mAudioRecord = new AudioRecord(mConfig, this.getExternalCacheDir() + "/",
                "demo");
        mAudioRecord.prepare();

        // Request necessary permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(perms[1]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.mr_btnStart:
                mAudioRecord.start();
                break;
            case R.id.mr_btnPause:
                mAudioRecord.pause();
                break;
            case R.id.mr_btnStop:
                mAudioRecord.stop();
                break;
            case R.id.mr_btnPlayPCM:
                AudioTrack.getInstance().prepare(mConfig);
                AudioTrack.getInstance().start();
                AudioTrack.getInstance().playPCMFile(this.getExternalCacheDir() + "/demo.pcm");
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}