package com.android.librecord;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * # 录音配置
 * The AudioRecordConfig class collects the information describing an audio recording
 */
public class AudioRecordConfig {


    /**
     * @param audioSource   音源 {@link MediaRecorder.AudioSource}
     *                      MIC : See {@link MediaRecorder.AudioSource#MIC}
     * @param sampleRate    采样率 {@link AudioRecordConfig.SampleRate}
     * @param channelConfig 声道  {@linkplain AudioFormat}
     *                      单声道：See {@link AudioFormat#CHANNEL_IN_MONO}
     *                      双声道：See {@link AudioFormat#CHANNEL_IN_STEREO}
     * @param audioFormat   采样位数 {@linkplain AudioFormat}
     *                      8Bit： See {@link AudioFormat#ENCODING_PCM_8BIT}
     *                      16Bit: See {@link AudioFormat#ENCODING_PCM_16BIT}
     * @param outputFormat  输出格式 {@link AudioRecordConfig.OutputFormat}
     * @see <a href="http://en.wikipedia.org/wiki/Sampling_rate">Sampling_rate</a>
     */
    public AudioRecordConfig(int audioSource, int sampleRate, int channelConfig, int audioFormat, OutputFormat outputFormat) {
        this.audioSource = audioSource;
        this.sampleRate = sampleRate;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
        this.outputFormat = outputFormat;
    }

    // 音源
    int audioSource = MediaRecorder.AudioSource.MIC;
    // 采样率
    int sampleRate = SampleRate.MID_QUALITY;
    // 声道
    int channelConfig = AudioFormat.CHANNEL_IN_MONO;//单声道
    // 采样位数
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 输出格式
    OutputFormat outputFormat = OutputFormat.MP3;


    enum OutputFormat {
        AAC(0, ".aac"),
        MP3(1, ".mp3"),
        WAV(2, ".wav"),
        PCM(3, ".pcm"),
        AMR(4, ".amr");

        public static String getName(int index) {
            for (OutputFormat format : OutputFormat.values()) {
                if (format.getIndex() == index) {
                    return format.name;
                }
            }
            return null;
        }

        public int getIndex() {
            return index;
        }


        public String getName() {
            return name;
        }

        private int index;
        private String name;

        OutputFormat(int index, String name) {
            this.index = index;
            this.name = name;
        }
    }

    public final class SampleRate {
        public static final int HIGHT_QUALITY = 48000;
        public static final int MID_QUALITY = 44100;
        public static final int LOW_QUALITY = 1600;
        public static final int PHONE_QUALITY = 800;

        SampleRate() {
            throw new RuntimeException("Stub!");
        }
    }


}
