package com.android.librecord.aac;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaRecorder;

import java.io.IOException;

/***
 * MediaRecorder 录制aac音频
 */

public class aac {
    private MediaRecorder mediaRecorder;

    public void startRecording(Context c) {

        // 采样率
        int sampleRate = 44100;
        // 声道
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;//单声道
        // 采样位数
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        // 创建录音对象
        mediaRecorder = new MediaRecorder();
        // 设置声音来源 MIC 即手机麦克风
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置音频采样率
        mediaRecorder.setAudioSamplingRate(sampleRate);
        // 设置音频声道
        mediaRecorder.setAudioChannels(channelConfig);
        // 设置音频采样位数
        mediaRecorder.setAudioEncodingBitRate(audioFormat);
        // 设置音频格式 aac
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        // 设置录音文件
        mediaRecorder.setOutputFile(c.getExternalCacheDir() + "/demo.aac");
        // 设置编码器
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            // 准备录音
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 开始录音
        mediaRecorder.start();
    }

    public void stopRecording() {
        // 停止录音
        mediaRecorder.stop();
        // 释放资源
        mediaRecorder.release();
        // 引用置空
        mediaRecorder = null;
    }

}
