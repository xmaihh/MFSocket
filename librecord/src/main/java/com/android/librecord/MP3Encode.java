package com.android.librecord;

import com.android.liblame.MP3Recorder;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * MP3编码器
 * See {@link MP3Recorder}
 */
public class MP3Encode {
    private int mSampleRate;
    private int mOutChannel;
    private int mOutSampleRate;
    private int mOutBitrate;
    private int mQuality;
    private byte[] mp3buffer;

    /**
     * @param mSampleRate    输入采样率
     * @param mOutChannel    声道数
     * @param mOutSampleRate 输出采样率
     * @param mOutBitrate    比特率(kbps)
     * @param mQuality       0~9，0最好
     */
    public MP3Encode(int mSampleRate, int mOutChannel, int mOutSampleRate, int mOutBitrate, int mQuality) {
        this.mSampleRate = mSampleRate;
        this.mOutChannel = mOutChannel;
        this.mOutSampleRate = mOutSampleRate;
        this.mOutBitrate = mOutBitrate;
        this.mQuality = mQuality;
    }

    void prepare() {
        MP3Recorder.init(mSampleRate, mOutChannel, mOutSampleRate, mOutBitrate, mQuality);
    }

    void encode(int readSize, short[] readBuffer, FileOutputStream fos) throws IOException {
        if (readSize > 0) {
            mp3buffer = new byte[(int) (7200 + readBuffer.length * 1.25)];
            int encodeSize = MP3Recorder.encode(readBuffer, readBuffer, readSize, mp3buffer);
            if (encodeSize > 0) {
                fos.write(mp3buffer, 0, encodeSize);
            }
        }
    }

    void close(FileOutputStream fos) throws IOException {
        int flushSize = MP3Recorder.flush(mp3buffer);
        if (flushSize > 0) {
            fos.write(mp3buffer, 0, flushSize);
        }
        MP3Recorder.close();
    }
}
