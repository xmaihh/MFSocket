package com.android.librecord.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.IOException;
import java.io.RandomAccessFile;

/***
 * AudioRecord录制wav格式音频
 */

public class AudioRecordUtil {
    // 录音状态
    private boolean isRecording = true;

    public void startRecording(final Context c) {
        // 耗时操作要开线程
        new Thread() {
            @Override
            public void run() {
                // 音源
                int audioSource = MediaRecorder.AudioSource.MIC;
                // 采样率
                int sampleRate = 44100;
                // 声道数
                int channelConfig = AudioFormat.CHANNEL_IN_STEREO;//双声道
                // 采样位数
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                // 获取最小缓存区大小
                int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                // 创建录音对象
                AudioRecord audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, minBufferSize);
                try {
                    // 创建随机读写流
                    RandomAccessFile raf = new RandomAccessFile(c.getExternalCacheDir() + "/demo.wav", "rw");
                    // 留出文件头的位置
                    raf.seek(44);
                    byte[] buffer = new byte[minBufferSize];

                    // 录音中
                    audioRecord.startRecording();
                    isRecording = true;
                    while (isRecording) {
                        int readSize = audioRecord.read(buffer, 0, minBufferSize);
                        raf.write(buffer, 0, readSize);
                    }

                    // 录音停止
                    audioRecord.stop();
                    audioRecord.release();

                    // 写文件头
                    WriteWaveFileHeader(raf, raf.length(), sampleRate, 2, sampleRate * 16 * 2 / 8);
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 为 wav 文件添加文件头，前提是在头部预留了 44字节空间
     *
     * @param raf        随机读写流
     * @param fileLength 文件总长
     * @param sampleRate 采样率
     * @param channels   声道数量
     * @param byteRate   码率 = 采样率 * 采样位数 * 声道数 / 8
     * @throws IOException
     */
    private void WriteWaveFileHeader(RandomAccessFile raf, long fileLength, long sampleRate, int channels, long byteRate) throws IOException {
        long totalDataLen = fileLength + 36;
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (fileLength & 0xff);
        header[41] = (byte) ((fileLength >> 8) & 0xff);
        header[42] = (byte) ((fileLength >> 16) & 0xff);
        header[43] = (byte) ((fileLength >> 24) & 0xff);
        raf.seek(0);
        raf.write(header, 0, 44);
    }


    public void stopRecording() {
        // 停止录音
        isRecording = false;
    }
}
