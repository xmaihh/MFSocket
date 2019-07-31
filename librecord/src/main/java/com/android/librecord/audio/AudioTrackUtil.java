package com.android.librecord.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

/**
 * 实现边录边播（AudioRecord + AudioTrack）
 */

public class AudioTrackUtil {
    // 录音状态
    private boolean isRecording = true;

    public void start() {
        // 耗时操作要开线程
        new Thread() {
            @Override
            public void run() {

                // 音源
                int audioSource = MediaRecorder.AudioSource.MIC;
                // 采样率
                int sampleRate = 8000;
                // 声道数
                int channelConfig = AudioFormat.CHANNEL_IN_STEREO;//双声道
                // 采样位数
                int audioFormat = AudioFormat.ENCODING_PCM_8BIT;
                // 获取录音最小缓存区大小
                int recorderBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                // 创建录音对象
                AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, recorderBufferSize);

                // 音频类型
                int streamType = AudioManager.STREAM_MUSIC;
                // 静态音频还是音频流
                int mode = AudioTrack.MODE_STREAM;
                //  获取播放最小缓存区大小
                int playerBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                // 创建播放对象
                AudioTrack audioTrack = new AudioTrack(streamType, sampleRate, channelConfig, audioFormat, playerBufferSize, mode);

                // 缓存区
                byte[] buffer = new byte[recorderBufferSize];

                // 录音中
                audioTrack.play();
                audioRecord.startRecording();
                isRecording = true;
                while (isRecording) {
                    audioRecord.read(buffer, 0, recorderBufferSize);
                    audioTrack.write(buffer, 0, buffer.length);
                }

                // 录音停止
                audioRecord.stop();
                audioTrack.stop();
                audioRecord.release();
                audioTrack.release();
            }
        }.start();
    }

    public void stop() {
        // 停止录音
        isRecording = false;
    }
}
