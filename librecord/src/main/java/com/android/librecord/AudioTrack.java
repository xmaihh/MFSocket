package com.android.librecord;

import android.media.AudioManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class AudioTrack {

    private AudioRecordConfig cf;
    private ExecutorService mExecutor;
    private android.media.AudioTrack mAudioTrack;
    private int mPlayerBufferSize;
    private volatile static AudioTrackState mState = AudioTrackState.RELEASE;

    public void prepare(AudioRecordConfig mRecordConfig) {
        if (mState != AudioTrackState.RELEASE) {
            throw new IllegalStateException("AudioTrack is not yet initialized.");
        } else {
            cf = mRecordConfig;
            //  获取播放最小缓存区大小
            mPlayerBufferSize = android.media.AudioTrack.getMinBufferSize(
                    cf.sampleRate,
                    cf.channelConfig,
                    cf.audioFormat);
            mAudioTrack = new android.media.AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    cf.sampleRate,
                    cf.channelConfig,
                    cf.audioFormat,
                    mPlayerBufferSize,
                    android.media.AudioTrack.MODE_STREAM);
            updateState(AudioTrackState.PREPARE);
        }
    }

    public void start() {
        if (mState == AudioTrackState.RELEASE) {
            throw new IllegalStateException("AudioTrack is not yet initialized.");
        }
        if (mState == AudioTrackState.PLAYING) {
            throw new IllegalStateException("AudioTrack is playing.");
        }
        mAudioTrack.play();
        updateState(AudioTrackState.PLAYING);
    }

    void play(byte[] buffer) {
        if (mState == AudioTrackState.PLAYING) {
            mAudioTrack.write(buffer, 0, buffer.length);
        }
    }

    public void playPCMFile(String filePath) {

        if (mState == AudioTrackState.PLAYING) {
            Log.d("521", "playPCMFile: ");
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(filePath);
                byte[] buffer = new byte[mPlayerBufferSize];
                int readSize = fis.read(buffer);
                while (readSize > 0) {
                    mAudioTrack.write(buffer, 0, readSize);
                    readSize = fis.read(buffer);
                }
                stop();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void stop() {
        mAudioTrack.stop();
        updateState(AudioTrackState.STOP);
        release();
    }

    void release() {
        mAudioTrack.release();
        updateState(AudioTrackState.RELEASE);
    }


    void updateState(AudioTrackState trackState) {
        if (mState == trackState) {
            return;
        } else {
            mState = trackState;
        }
    }

    private volatile static AudioTrack instance;

    public static AudioTrack getInstance() {
        if (instance == null) {
            synchronized (AudioTrack.class) {
                if (instance == null) {
                    instance = new AudioTrack();
                }
            }
        }
        return instance;
    }
}
