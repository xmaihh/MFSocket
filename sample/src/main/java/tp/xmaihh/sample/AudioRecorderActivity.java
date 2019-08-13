package tp.xmaihh.sample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.librecord.AudioRecord;
import com.android.librecord.AudioRecordConfig;

import org.fmod.core.FmodUtils;

import java.io.File;

import tp.xmaihh.sample.utils.Uri2path;

public class AudioRecorderActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private RadioGroup mRGOutputFormat;
    private RadioGroup mRGSamplingRate;
    private RadioGroup mRGBitRate;
    private RadioGroup mRGChannel;
    private TextView mTvFilePath;
    private TextView mTvRecordState;
    private TextView mTvChooseFile;
    private Button mBtnStart;
    private Button mBtnPause;
    private Button mBtnStop;
    private Button mBtnPlaySound;
    private Switch mSWplaying;

    private AudioRecord mAudioRecord;
    private AudioRecordConfig mConfig;
    private String path;
    private boolean isPaused = false;  //是否暂停
    private AudioRecorderActivityPState mState = AudioRecorderActivityPState.RELEASE;
    private AudioRecordConfig.OutputFormat outputFormat = AudioRecordConfig.OutputFormat.MP3;
    private int sampleRate = AudioRecordConfig.SampleRate.SAMPPLERATE_44100;
    private int bitRate = AudioFormat.ENCODING_PCM_16BIT;
    private int channels = AudioFormat.CHANNEL_IN_STEREO;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        //*
        mRGOutputFormat = findViewById(R.id.mr_rgOutputFormat);
        mRGSamplingRate = findViewById(R.id.mr_rgSamplingRate);
        mRGBitRate = findViewById(R.id.mr_rgBitrate);
        mRGChannel = findViewById(R.id.mr_rgChannel);
        mTvFilePath = findViewById(R.id.mr_tvFilePath);
        mTvRecordState = findViewById(R.id.mr_tvTimeState);
        mBtnStart = findViewById(R.id.mr_btnStart);
        mBtnPause = findViewById(R.id.mr_btnPause);
        mBtnStop = findViewById(R.id.mr_btnStop);
        mBtnPlaySound = findViewById(R.id.mr_btnPlaySound);
        mTvChooseFile = findViewById(R.id.mr_tvChooseFile);
        mSWplaying = findViewById(R.id.mr_switch);

        mBtnStart.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mTvChooseFile.setOnClickListener(this);
        mBtnPlaySound.setOnClickListener(this);

        mRGOutputFormat.setOnCheckedChangeListener(this);
        mRGBitRate.setOnCheckedChangeListener(this);
        mRGSamplingRate.setOnCheckedChangeListener(this);
        mRGChannel.setOnCheckedChangeListener(this);
        mSWplaying.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isPlaying = mSWplaying.isChecked();
                Log.d(TAG, "onCheckedChanged: RealTime " + isPlaying);
            }
        });


        // Request necessary permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(perms[1]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }

        prepare();
    }

    void prepare() {
        if (mState == AudioRecorderActivityPState.RELEASE) {
            mConfig = new AudioRecordConfig(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channels,
                    bitRate,
                    outputFormat);
            mAudioRecord = new AudioRecord(mConfig, this.getExternalCacheDir() + "/",
                    "demo");
            mAudioRecord.prepare();

            updateState(AudioRecorderActivityPState.PREPARE);
        } else {
            Toast.makeText(this, "Please click the stop button.", Toast.LENGTH_LONG).show();
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
                if (isPaused) {
                    isPaused = false;
                    mAudioRecord.resume();
                    mBtnPause.setText(R.string.mr_pause);
                } else {
                    isPaused = true;
                    mAudioRecord.pause();
                    mBtnPause.setText(R.string.mr_resume);
                }
                break;
            case R.id.mr_btnStop:
                mAudioRecord.stop();
                break;
            case R.id.mr_btnPlaySound:
                if (path != null && checkExist(path)) {
                    FmodUtils.getInstance(this).playSound(path, FmodUtils.Effect.ORIGINAL.getMode());
                } else {
                    Toast.makeText(this, "No file is selected.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.mr_tvChooseFile:
                chooseFile();
                break;
            default:
                break;
        }
    }


    private static final String TAG1 = "FileChoose";

    // 调用系统文件管理器
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*").addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Choose File"), CHOOSE_FILE_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int CHOOSE_FILE_CODE = 0;

    @Override
// 文件选择完之后，自动调用此函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_FILE_CODE) {
                Uri uri = data.getData();
                if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                    path = uri.getPath();
                    mTvChooseFile.setText(path);
                    Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//Android4.4以上
                    path = Uri2path.getPath(this, uri);
                    mTvChooseFile.setText(path);
                    Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                } else {//4.4以下下系统调用方法
                    path = Uri2path.getRealPathFromURI(this, uri);
                    mTvChooseFile.setText(path);
                    Toast.makeText(this, path + "222222", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Log.e(TAG1, "onActivityResult() error, resultCode: " + resultCode);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void updateState(AudioRecorderActivityPState pState) {
        if (mState == pState) {
            return;
        } else {
            mState = pState;
        }
    }

    @Override
    protected void onDestroy() {
        FmodUtils.getInstance(this).close();
        super.onDestroy();
    }

    public static boolean checkExist(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }

    String TAG = "521";

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        if (mState == AudioRecorderActivityPState.RELEASE) {
            switch (id) {
                case R.id.mr_rbOutputFormat_mp3:
                    outputFormat = AudioRecordConfig.OutputFormat.MP3;
                    Log.d(TAG, "onCheckedChanged: mp3");
                    break;
                case R.id.mr_rbOutputFormat_aac:
                    outputFormat = AudioRecordConfig.OutputFormat.AAC;
                    Log.d(TAG, "onCheckedChanged: aac");
                    break;
                case R.id.mr_rbOutputFormat_wav:
                    outputFormat = AudioRecordConfig.OutputFormat.WAV;
                    Log.d(TAG, "onCheckedChanged: wav");
                    break;
                case R.id.mr_rbOutputFormat_pcm:
                    outputFormat = AudioRecordConfig.OutputFormat.PCM;
                    Log.d(TAG, "onCheckedChanged: pcm");
                    break;
                case R.id.mr_rbSamplingRate_800:
                    sampleRate = AudioRecordConfig.SampleRate.SAMPPLERATE_800;
                    Log.d(TAG, "onCheckedChanged: sampleRate 800");
                    break;
                case R.id.mr_rbSamplingRate_1600:
                    sampleRate = AudioRecordConfig.SampleRate.SAMPPLERATE_1600;
                    Log.d(TAG, "onCheckedChanged: sampleRate 1600");
                    break;
                case R.id.mr_rbSamplingRate_44100:
                    sampleRate = AudioRecordConfig.SampleRate.SAMPPLERATE_44100;
                    Log.d(TAG, "onCheckedChanged: sampleRate 44100");
                    break;
                case R.id.mr_rbSamplingRate_48000:
                    sampleRate = AudioRecordConfig.SampleRate.SAMPPLERATE_48000;
                    Log.d(TAG, "onCheckedChanged: sampleRate 48000");
                    break;
                case R.id.mr_rbBitrate_8:
                    bitRate = AudioFormat.ENCODING_PCM_8BIT;
                    Log.d(TAG, "onCheckedChanged: bitRate 8");
                    break;
                case R.id.mr_rbBitrate_16:
                    bitRate = AudioFormat.ENCODING_PCM_16BIT;
                    Log.d(TAG, "onCheckedChanged: bitRate 16");
                    break;
                case R.id.mr_rbChannel_1:
                    channels = AudioFormat.CHANNEL_IN_MONO;
                    Log.d(TAG, "onCheckedChanged: channel MONO");
                    break;
                case R.id.mr_rbChannel_2:
                    channels = AudioFormat.CHANNEL_IN_STEREO;
                    Log.d(TAG, "onCheckedChanged: channel STEREO");
                    break;
            }

        } else {
            Toast.makeText(this, "Please click the stop button.", Toast.LENGTH_LONG).show();
        }
    }
}