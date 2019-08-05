package tp.xmaihh.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MediaRecorderActivity extends Activity {
    private RadioGroup mRGOutputFormat;
    private RadioGroup mRGSamplingRate;
    private RadioGroup mRGBitRate;
    private TextView mTvFilePath;
    private TextView mTvRecordState;
    private Button mBtnRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);

        //*
        mRGOutputFormat = findViewById(R.id.mr_rgOutputFormat);
        mRGSamplingRate = findViewById(R.id.mr_rgSamplingRate);
        mRGBitRate = findViewById(R.id.mr_rgBitrate);
        mTvFilePath = findViewById(R.id.mr_tvFilePath);
        mTvRecordState = findViewById(R.id.mr_tvTimeState);
        mBtnRecord = findViewById(R.id.mr_btnRecorder);
    }
}