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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.librecord.AudioRecord;
import com.android.librecord.AudioRecordConfig;
import com.android.librecord.mp3.mp3;
import com.android.librecord.wav.wav;

import org.fmod.core.FmodUtils;

import java.io.File;

import tp.xmaihh.sample.utils.Uri2path;

public class AudioRecorderActivity extends Activity implements View.OnClickListener {
    private RadioGroup mRGOutputFormat;
    private RadioGroup mRGSamplingRate;
    private RadioGroup mRGBitRate;
    private TextView mTvFilePath;
    private TextView mTvRecordState;
    private TextView mTvChooseFile;
    private Button mBtnStart;
    private Button mBtnPause;
    private Button mBtnStop;
    private Button mBtnPlaySound;

    private AudioRecord mAudioRecord;
    private AudioRecordConfig mConfig;
    private String path;

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
        mBtnPlaySound = findViewById(R.id.mr_btnPlaySound);
        mTvChooseFile = findViewById(R.id.mr_tvChooseFile);

        mBtnStart.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mTvChooseFile.setOnClickListener(this);
        mBtnPlaySound.setOnClickListener(this);

        mConfig = new AudioRecordConfig(
                MediaRecorder.AudioSource.MIC,
                AudioRecordConfig.SampleRate.MID_QUALITY,
                AudioFormat.CHANNEL_IN_MONO,
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


    wav wav1 = new wav();
    com.android.librecord.mp3.mp3 mp31 = new mp3();

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.mr_btnStart:
                mAudioRecord.start();
//                wav1.startRecording(this);
//                mp31.record(this);
                break;
            case R.id.mr_btnPause:
                mAudioRecord.pause();
                break;
            case R.id.mr_btnStop:
                mAudioRecord.stop();
//                wav1.stopRecording();
//                mp31.stop();
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
//                AudioTrack.getInstance().prepare(mConfig);
//                AudioTrack.getInstance().start();
//                AudioTrack.getInstance().playPCMFile(this.getExternalCacheDir() + "/demo.pcm");
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

    @Override
    protected void onDestroy() {
        FmodUtils.getInstance(this).close();
        super.onDestroy();
    }

    public static boolean checkExist(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }
}