package com.android.liblame;

public class MP3Recorder {
    static {
        System.loadLibrary("mp3lame");
    }

    /**
     * 初始化 lame编码器
     *
     * @param inSampleRate  输入采样率
     * @param outChannel    声道数
     * @param outSampleRate 输出采样率
     * @param outBitrate    比特率(kbps)
     * @param quality       0~9，0最好
     */
    public static native void init(int inSampleRate, int outChannel, int outSampleRate, int outBitrate, int quality);

    /**
     * 编码，把 AudioRecord 录制的 PCM 数据转换成 mp3 格式
     *
     * @param buffer_l 左声道输入数据
     * @param buffer_r 右声道输入数据
     * @param samples  输入数据的size
     * @param mp3buf   输出数据
     * @return 输出到mp3buf的byte数量  计算公式:(7200 + buffer.length * 1.25)
     * See {@link cpp/lame.h#lame_encode_buffer}
     */
    public static native int encode(short[] buffer_l, short[] buffer_r, int samples, byte[] mp3buf);

    /**
     * 刷写
     *
     * @param mp3buf mp3数据缓存区
     * @return 返回刷写的数量
     */
    public static native int flush(byte[] mp3buf);

    /**
     * 关闭 lame 编码器，释放资源
     */
    public static native void close();
}
