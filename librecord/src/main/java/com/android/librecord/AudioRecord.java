package com.android.librecord;

import android.renderscript.ScriptIntrinsic;

import com.android.liblame.MP3Recorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioRecord {
    // 录音状态
    private volatile static RecordState mState = RecordState.RELEASE;
    ExecutorService mExecutor;

    public AudioRecord(AudioRecordConfig recordConfig, String filePath, String fileName) {
        mExecutor = Executors.newCachedThreadPool();
        this.mRecordConfig = recordConfig;
        this.strFilePath = filePath;
        this.strFileName = fileName;
    }

    AudioRecordConfig mRecordConfig;
    String strFilePath;
    String strFileName;
    // 录音缓存区大小
    int bufferSizeInBytes;
    android.media.AudioRecord mAudioRecord;

    public void prepare() {
        if (mState != RecordState.RELEASE) {
            throw new IllegalStateException("AudioRecord is not yet initialized.");
        } else {
            // 录音最小缓存大小
            bufferSizeInBytes = android.media.AudioRecord.getMinBufferSize(
                    mRecordConfig.sampleRate, mRecordConfig.channelConfig,
                    mRecordConfig.audioFormat);
            mAudioRecord = new android.media.AudioRecord(
                    mRecordConfig.audioSource, mRecordConfig.sampleRate,
                    mRecordConfig.channelConfig,
                    mRecordConfig.audioFormat, bufferSizeInBytes);

            updateState(RecordState.PREPARE);
        }
    }

    //开始录音
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
                // 文件输出流
                FileOutputStream fos;
                short[] readBuffer = new short[bufferSizeInBytes];

                try {
                    fos = new FileOutputStream(strFilePath + strFileName + mRecordConfig.outputFormat.getName());

                    MP3Recorder.init(mRecordConfig.sampleRate, 2, mRecordConfig.sampleRate, 128, 5);
                    byte[] mp3Buffer = new byte[(int) (7200 + readBuffer.length * 1.25)];
                    mAudioRecord.startRecording();
                    while (mState == RecordState.RECORDING && mAudioRecord.getRecordingState() == android.media.AudioRecord.RECORDSTATE_RECORDING) {
                        int readSize = mAudioRecord.read(readBuffer, 0, bufferSizeInBytes);


                        if (readSize > 0) {
                            int encodeSize = MP3Recorder.encode(readBuffer, readBuffer, readSize, mp3Buffer);
                            if (encodeSize > 0) {
                                try {
                                    fos.write(mp3Buffer, 0, encodeSize);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    if (mState == RecordState.RELEASE) {
                        int flushSize = MP3Recorder.flush(mp3Buffer);

                        if (flushSize > 0) {
                            try {
                                fos.write(mp3Buffer, 0, flushSize);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mAudioRecord.stop();
                        mAudioRecord.release();
                        MP3Recorder.close();
                    }
                } catch (FileNotFoundException e) {
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

    public void resume() {
        if (mState != RecordState.PAUSE) {
            throw new IllegalStateException("AudioRecord not in Status:pause. Cannot resume");
        } else {
            mAudioRecord.startRecording();

            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // 文件输出流
                    FileOutputStream fos;
                    short[] readBuffer = new short[bufferSizeInBytes];

                    try {
                        fos = new FileOutputStream(strFilePath + strFileName + mRecordConfig.outputFormat.getName());

                        MP3Recorder.init(mRecordConfig.sampleRate, 2, mRecordConfig.sampleRate, 128, 5);
                        byte[] mp3Buffer = new byte[(int) (7200 + readBuffer.length * 1.25)];
                        mAudioRecord.startRecording();
                        while (mState == RecordState.RECORDING && mAudioRecord.getRecordingState() == android.media.AudioRecord.RECORDSTATE_RECORDING) {
                            int readSize = mAudioRecord.read(readBuffer, 0, bufferSizeInBytes);


                            if (readSize > 0) {
                                int encodeSize = MP3Recorder.encode(readBuffer, readBuffer, readSize, mp3Buffer);
                                if (encodeSize > 0) {
                                    try {
                                        fos.write(mp3Buffer, 0, encodeSize);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        if (mState == RecordState.RELEASE) {
                            int flushSize = MP3Recorder.flush(mp3Buffer);

                            if (flushSize > 0) {
                                try {
                                    fos.write(mp3Buffer, 0, flushSize);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }


                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    // 停止录音
    public void stop() {
        if (mState == RecordState.RECORDING || mState == RecordState.PAUSE) {
            updateState(RecordState.RELEASE);
            mAudioRecord.stop();
            mAudioRecord.release();
            MP3Recorder.close();
        }
    }

    void updateState(RecordState recordState) {
        if (mState == recordState) {
            return;
        } else {
            mState = recordState;
        }
    }
}
