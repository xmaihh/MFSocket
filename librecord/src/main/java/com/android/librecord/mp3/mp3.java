package com.android.librecord.mp3;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.android.liblame.MP3Recorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 录制 mp3 格式音频（编译lame库）
 */

public class mp3 {
    // 录音状态
    private boolean isRecording;

    //开始录音
    public void record(final Context c) {
        new Thread() {
            @Override
            public void run() {
                // 音源
                int audioSource = MediaRecorder.AudioSource.MIC;
                // 采样率
                int sampleRate = 44100;
                // 声道
                int channelConfig = AudioFormat.CHANNEL_IN_MONO;//单声道
                // 采样位数
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                // 录音缓存区大小
                int bufferSizeInBytes;
                // 文件输出流
                FileOutputStream fos;
                // 录音最小缓存大小
                bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSizeInBytes);
                try {
                    fos = new FileOutputStream(c.getExternalCacheDir() + "/demo.mp3");
                    MP3Recorder.init(sampleRate, 2, sampleRate, 128, 5);
                    short[] buffer = new short[bufferSizeInBytes];
                    byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 1.25)];
                    audioRecord.startRecording();
                    isRecording = true;
                    while (isRecording && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                        int readSize = audioRecord.read(buffer, 0, bufferSizeInBytes);
                        if (readSize > 0) {
                            int encodeSize = MP3Recorder.encode(buffer, buffer, readSize, mp3buffer);
                            if (encodeSize > 0) {
                                try {
                                    fos.write(mp3buffer, 0, encodeSize);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    int flushSize = MP3Recorder.flush(mp3buffer);
                    if (flushSize > 0) {
                        try {
                            fos.write(mp3buffer, 0, flushSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    audioRecord.stop();
                    audioRecord.release();
                    MP3Recorder.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 停止录音
    public void stop() {
        isRecording = false;
    }
}
