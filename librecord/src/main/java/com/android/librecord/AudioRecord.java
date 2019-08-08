package com.android.librecord;

import com.android.liblame.MP3Recorder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioRecord {
    // 录音状态
    private volatile static RecordState mState = RecordState.RELEASE;
    private ExecutorService mExecutor;
    private FileOutputStream mFileOutputStream;
    private RandomAccessFile mRandomAccessFile;
    private AACEncode mAacEncode;
    private MP3Encode mMp3Encode;
    private WAVEncode mWavEncode;
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
        if (mState != RecordState.RELEASE) {
            throw new IllegalStateException("AudioRecord is not yet initialized.");
        } else {
            // 录音最小缓存大小
            bufferSizeInBytes = android.media.AudioRecord.getMinBufferSize(
                    mRecordConfig.sampleRate, mRecordConfig.channelConfig,
                    mRecordConfig.bitRate);
            mAudioRecord = new android.media.AudioRecord(
                    mRecordConfig.audioSource, mRecordConfig.sampleRate,
                    mRecordConfig.channelConfig,
                    mRecordConfig.bitRate, bufferSizeInBytes);
            updateState(RecordState.PREPARE);
        }
    }

    // 开始录音
    public void start() {
        if (mState == RecordState.RECORDING) {
            throw new IllegalStateException("Recording ... ");
        }
        if (mState == RecordState.RELEASE) {
            throw new IllegalStateException("AudioRecord is not yet initialized.");
        }
        updateState(RecordState.RECORDING);
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
        if (mState == RecordState.RECORDING) {
            mAudioRecord.stop();
            updateState(RecordState.PAUSE);
        }
    }

    // 从暂停处恢复
    public void resume() {
        if (mState != RecordState.PAUSE) {
            throw new IllegalStateException("AudioRecord not in Status:pause. Cannot resume");
        } else {
            mAudioRecord.startRecording();
            updateState(RecordState.RECORDING);
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    // 停止录音
    public void stop() {
        if (mState == RecordState.RECORDING || mState == RecordState.PAUSE) {
            updateState(RecordState.RELEASE);
            mAudioRecord.stop();
        }
    }

    // 释放资源
    public void release() {
        mAudioRecord.release();
        MP3Recorder.close();
        updateState(RecordState.RELEASE);
    }

    void updateState(RecordState recordState) {
        if (mState == recordState) {
            return;
        } else {
            mState = recordState;
        }
    }

    void initEncode() throws IOException {
        mFileOutputStream = new FileOutputStream(strFilePath + strFileName + mRecordConfig.outputFormat.getName());
        switch (mRecordConfig.outputFormat) {
            case AAC:
                mAacEncode = new AACEncode();
                mAacEncode.prepare();
                break;
            case MP3:
                mMp3Encode = new MP3Encode(
                        mRecordConfig.sampleRate,
                        mRecordConfig.channelConfig,
                        mRecordConfig.channelConfig,
                        mRecordConfig.bitRate, 5);
                mMp3Encode.prepare();
                break;
            case WAV:
                mWavEncode = new WAVEncode();
                mRandomAccessFile = new RandomAccessFile(strFilePath + strFileName + mRecordConfig.outputFormat.getName(), "rw");
                // 留出文件头的位置
                mRandomAccessFile.seek(44);
                break;
        }
    }

    void FSDataOutputStream() throws IOException {
        // 文件输出流
        short[] readBuffer = new short[bufferSizeInBytes];
        mAudioRecord.startRecording();
        while (mState == RecordState.RECORDING && mAudioRecord.getRecordingState() == android.media.AudioRecord.RECORDSTATE_RECORDING) {
            int readSize = mAudioRecord.read(readBuffer, 0, bufferSizeInBytes);

            // 编码
            Encode(readSize, readBuffer);
        }

        if (mState == RecordState.RELEASE) {
            switch (mRecordConfig.outputFormat) {
                case MP3:
                    mMp3Encode.close(mFileOutputStream);
                    break;
                case WAV:
                    mWavEncode.WriteWaveFileHeader(mRandomAccessFile,
                            mRandomAccessFile.length(),
                            mRecordConfig.sampleRate,
                            mRecordConfig.channelConfig,
                            mRecordConfig.sampleRate * mRecordConfig.bitRate * mRecordConfig.channelConfig / 8);
                    break;
            }
            mFileOutputStream.close();
            mRandomAccessFile.close();
            mAudioRecord.stop();
            mAudioRecord.release();
        }
    }

    void Encode(int readSize, short[] readBuffer) throws IOException {
        if (readSize > 0) {
            switch (mRecordConfig.outputFormat) {
                case PCM:
                    mFileOutputStream.write(shortToBytes(readBuffer), 0, readBuffer.length);
                    break;
                case AMR:
                    break;
                case AAC:
                    mAacEncode.encode(readSize, shortToBytes(readBuffer), mFileOutputStream);
                    break;
                case MP3:
                    mMp3Encode.encode(readSize, readBuffer, mFileOutputStream);
                    break;
                case WAV:
                    mRandomAccessFile.write(shortToBytes(readBuffer), 0, readSize);
                    break;
            }
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
