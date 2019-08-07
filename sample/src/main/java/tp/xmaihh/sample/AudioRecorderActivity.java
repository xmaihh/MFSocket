package tp.xmaihh.sample;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.librecord.AudioRecord;
import com.android.librecord.AudioRecordConfig;

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

        mAudioRecord = new AudioRecord(
                new AudioRecordConfig(
                        MediaRecorder.AudioSource.MIC,
                        AudioRecordConfig.SampleRate.MID_QUALITY,
                        AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        AudioRecordConfig.OutputFormat.MP3),
                this.getExternalCacheDir() + "/",
                "demo");
        mAudioRecord.prepare();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mr_btnStart) {
            mAudioRecord.start();
        }
    }


    @Override
    protected void onDestroy() {
        mAudioRecord.release();
        super.onDestroy();
    }
}