package com.android.librecord;

import android.media.AudioFormat;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioRecord {
    private String TAG = "521";
    // 录音状态
    private volatile static AudioRecordState mState = AudioRecordState.RELEASE;
    private ExecutorService mExecutor;
    private FileOutputStream mFileOutputStream;
    private RandomAccessFile mRandomAccessFile;
    private AACEncode mAacEncode;
    private MP3Encode mMp3Encode;
    private WAVEncode mWavEncode;
    private AMREncode mAmrEncode;
    // 边录边播 开关
    private boolean isPlaying = false;

    public AudioRecord(AudioRecordConfig recordConfig, String filePath, String fileName) {
        mExecutor = Executors.newCachedThreadPool();
        this.mRecordConfig = recordConfig;
        this.strFilePath = filePath;
        this.strFileName = fileName;
    }

    private AudioRecordConfig mRecordConfig;
    private String strFilePath;
    private String strFileName;
    // 录音缓存区大小
    private int bufferSizeInBytes;
    private android.media.AudioRecord mAudioRecord;

    public void prepare() {
        if (mState != AudioRecordState.RELEASE) {
            throw new IllegalStateException("AudioRecord is not yet initialized.");
        } else {
            Log.d("521", "prepare: ");
            // 录音最小缓存大小
            bufferSizeInBytes = android.media.AudioRecord.getMinBufferSize(
                    mRecordConfig.sampleRate, mRecordConfig.channelConfig,
                    mRecordConfig.audioFormat);
            mAudioRecord = new android.media.AudioRecord(
                    mRecordConfig.audioSource, mRecordConfig.sampleRate,
                    AudioFormat.CHANNEL_IN_STEREO,
                    mRecordConfig.audioFormat, bufferSizeInBytes);
            if (isPlaying()) {
                AudioTrack.getInstance().prepare(mRecordConfig);
            }
            updateState(AudioRecordState.PREPARE);
        }
    }

    // 开始录音
    public void start() {
        if (mState == AudioRecordState.RECORDING) {
            throw new IllegalStateException("AudioRecord is Recording.");
        }
        if (mState == AudioRecordState.RELEASE) {
            throw new IllegalStateException("AudioRecord is not yet initialized.");
        }
        Log.d("521", "start: ");
        updateState(AudioRecordState.RECORDING);
        mAudioRecord.startRecording();
        if (isPlaying()) {
            AudioTrack.getInstance().start();
        }
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    initEncode();
                    FSDataOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 暂停
    public void pause() {
        if (mState == AudioRecordState.RECORDING) {
            Log.d("521", "pause: ");
            mAudioRecord.stop();
            updateState(AudioRecordState.PAUSE);
        }
    }

    // 从暂停处恢复
    public void resume() {
        Log.d("521", "resume: ");
        if (mState != AudioRecordState.PAUSE) {
            throw new IllegalStateException("AudioRecord not in Status:pause. Cannot resume");
        } else {
            mAudioRecord.startRecording();
            updateState(AudioRecordState.RECORDING);
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        FSDataOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // 停止录音
    public void stop() {
        Log.d("521", "stop: ");
        if (mState == AudioRecordState.RECORDING || mState == AudioRecordState.PAUSE) {
            updateState(AudioRecordState.STOP);
            mAudioRecord.stop();
            if (isPlaying()) {
                AudioTrack.getInstance().stop();
            }
            release();
        }
    }

    // 释放资源
    public void release() {
        Log.d("521", "release: ");
        try {
            mAudioRecord.release();
            updateState(AudioRecordState.RELEASE);
            FSDataOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updateState(AudioRecordState recordState) {
        if (mState == recordState) {
            return;
        } else {
            mState = recordState;
        }
    }

    void initEncode() throws IOException {
        switch (mRecordConfig.outputFormat) {
            case AAC:
                mFileOutputStream = new FileOutputStream(strFilePath + strFileName + mRecordConfig.outputFormat.getName());
                mAacEncode = new AACEncode();
                mAacEncode.prepare();
                break;
            case MP3:
                mFileOutputStream = new FileOutputStream(strFilePath + strFileName + mRecordConfig.outputFormat.getName());
                int channel = mRecordConfig.channelConfig;
                if (mRecordConfig.channelConfig == AudioFormat.CHANNEL_IN_STEREO) {
                    channel = 2;
                } else if (mRecordConfig.channelConfig == AudioFormat.CHANNEL_IN_MONO) {
                    channel = 1;
                }
                int kbps = 128;
                mMp3Encode = new MP3Encode(
                        mRecordConfig.sampleRate,
                        channel,
                        mRecordConfig.sampleRate,
                        kbps, 5);
                mMp3Encode.prepare();
                break;
            case WAV:
                mWavEncode = new WAVEncode();
                mRandomAccessFile = new RandomAccessFile(strFilePath + strFileName + mRecordConfig.outputFormat.getName(), "rw");
                // 留出文件头的位置
                mRandomAccessFile.seek(44);
                break;
            case PCM:
                mFileOutputStream = new FileOutputStream(strFilePath + strFileName + mRecordConfig.outputFormat.getName());
                break;
            case AMR:
                mAmrEncode = new AMREncode();
                mFileOutputStream = new FileOutputStream(strFilePath + strFileName + mRecordConfig.outputFormat.getName());
                byte[] header = new byte[]{'#', '!', 'A', 'M', 'R', '\n'};  //写入AMR-NB文件头
                mFileOutputStream.write(header);
                mAmrEncode.prepare();
                break;
        }
    }

    void FSDataOutputStream() throws IOException {
        // 文件输出流
        byte[] readBuffer = new byte[bufferSizeInBytes];
        short[] readMP3Buffer = new short[bufferSizeInBytes];
        int readSize = 0;
        while (mState == AudioRecordState.RECORDING && mAudioRecord.getRecordingState() == android.media.AudioRecord.RECORDSTATE_RECORDING) {
            if (mRecordConfig.outputFormat == AudioRecordConfig.OutputFormat.MP3) {
                readSize = mAudioRecord.read(readMP3Buffer, 0, bufferSizeInBytes);
//                Encode(readSize, shortToBytes(readMP3Buffer));
                EncodeMp3(readSize, readMP3Buffer);
                if (isPlaying()) {
                    AudioTrack.getInstance().play(shortToBytes(readMP3Buffer));
                }
            } else {
                readSize = mAudioRecord.read(readBuffer, 0, bufferSizeInBytes);
                Encode(readSize, readBuffer);
                if (isPlaying()) {
                    AudioTrack.getInstance().play(readBuffer);
                }
            }
        }

        if (mState == AudioRecordState.RELEASE) {
            switch (mRecordConfig.outputFormat) {
                case MP3:
                    mMp3Encode.close(mFileOutputStream);
                    break;
                case WAV:
                    int sampleRate = mRecordConfig.sampleRate;
                    int channel = mRecordConfig.channelConfig;
                    if (mRecordConfig.channelConfig == AudioFormat.CHANNEL_IN_STEREO) {
                        channel = 2;
                    } else if (mRecordConfig.channelConfig == AudioFormat.CHANNEL_IN_MONO) {
                        channel = 1;
                    }
                    int bitRate = mRecordConfig.audioFormat;
                    if (mRecordConfig.audioFormat == AudioFormat.ENCODING_PCM_8BIT) {
                        bitRate = 8;
                    } else if (mRecordConfig.audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
                        bitRate = 16;
                    }

                    long byteRate = sampleRate * bitRate * channel / 8;
                    mWavEncode.WriteWaveFileHeader(mRandomAccessFile,
                            mRandomAccessFile.length(),
                            mRecordConfig.sampleRate,
                            channel,
                            byteRate);
                    break;
            }

            if (mFileOutputStream != null) {
                mFileOutputStream.close();
            }
            if (mRandomAccessFile != null) {
                mRandomAccessFile.close();
            }
        }
    }

    void Encode(int readSize, byte[] readBuffer) throws IOException {
        if (readSize != android.media.AudioRecord.ERROR_INVALID_OPERATION) {
            switch (mRecordConfig.outputFormat) {
                case PCM:
                    if (mFileOutputStream != null) {
                        mFileOutputStream.write(readBuffer, 0, readBuffer.length);
                    }
                    break;
                case AMR:
                    if (mAmrEncode != null && mFileOutputStream != null) {
                        mAmrEncode.encode(readSize, readBuffer, mFileOutputStream);
                    }
                    break;
                case AAC:
                    if (mAacEncode != null && mFileOutputStream != null) {
                        mAacEncode.encode(readSize, readBuffer, mFileOutputStream);
                    }
                    break;
//                case MP3:
//                    if (mMp3Encode != null && mFileOutputStream != null) {
//
//                        Log.d(TAG, "Encode: " + readSize);
//                        mMp3Encode.encode(readSize, bytesToShort(readBuffer), mFileOutputStream);
//                    }
//                    break;
                case WAV:
                    if (mRandomAccessFile != null) {
                        mRandomAccessFile.write(readBuffer, 0, bufferSizeInBytes);
                    }
                    break;
            }
        }
    }

    void EncodeMp3(int readSize, short[] readBuffer) throws IOException {
        if (mMp3Encode != null && mFileOutputStream != null) {
            mMp3Encode.encode(readSize, readBuffer, mFileOutputStream);
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public static short[] bytesToShort(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        short[] shorts = new short[bytes.length / 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts;
    }

    public static byte[] shortToBytes(short[] shorts) {
        if (shorts == null) {
            return null;
        }
        byte[] bytes = new byte[shorts.length * 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts);

        return bytes;
    }
}
