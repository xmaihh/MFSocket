package com.android.librecord;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * AMR-NB文件头： "#!AMR\n" (or 0x2321414d520a in hexadecimal)(引号内的部分)
 * 语音带宽范围：300－3400Hz
 * <p>
 * 8KHz抽样
 * <p>
 * AMR-WB 文件头："#!AMR-WB\n" (or 0x2321414d522d57420a in hexadecimal).（引号内）
 * 语音带宽范围： 50－7000Hz
 * <p>
 * 16KHz抽样
 */


@Deprecated()
public class AMREncode {
    String MIME_TYPE = MediaFormat.MIMETYPE_AUDIO_AMR_NB;
    int CHANNEL_COUNT = 2;
    int SAMPLE_RATE = 44100;
    int BIT_RATE = 64000;

    MediaCodec mEncoder;
    ByteBuffer[] mInputBuffers;
    ByteBuffer[] mOutputBuffers;
    MediaCodec.BufferInfo mBufferInfo;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void prepare() throws IOException {
        mBufferInfo = new MediaCodec.BufferInfo();
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        MediaFormat mediaFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, CHANNEL_COUNT);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        mediaFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AMR_NB);
        mEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mEncoder.start();

        mInputBuffers = mEncoder.getInputBuffers();
        mOutputBuffers = mEncoder.getOutputBuffers();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void encode(int readSize, byte[] readBuffer, FileOutputStream fileOutputStream) throws IOException {
        boolean hasMoreData = true;
        double presentationTimeUs = 0;
        int totalBytesRead = 0;
        do {
            int inputBufIndex = 0;
            while (inputBufIndex != -1 && hasMoreData) {
                inputBufIndex = mEncoder.dequeueInputBuffer(0);

                if (inputBufIndex >= 0) {
                    ByteBuffer inputBuffer = mInputBuffers[inputBufIndex];
                    inputBuffer.clear();

                    if (readSize == -1) { // -1 implies EOS
                        hasMoreData = false;
                        mEncoder.queueInputBuffer(inputBufIndex, 0, 0, (long) presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        totalBytesRead += readSize;
                        inputBuffer.put(readBuffer, 0, readSize);
                        mEncoder.queueInputBuffer(inputBufIndex, 0, readSize, (long) presentationTimeUs, 0);
                        presentationTimeUs = 1000000l * (totalBytesRead / 2) / SAMPLE_RATE;
                    }
                }
            }
            // Drain audio
            int outputBufIndex = 0;
            while (outputBufIndex != MediaCodec.INFO_TRY_AGAIN_LATER) {
                outputBufIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
                if (outputBufIndex >= 0) {
                    ByteBuffer encodedData = mOutputBuffers[outputBufIndex];
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
                    byte[] outData = new byte[mBufferInfo.size];
                    encodedData.get(outData, 0, mBufferInfo.size);
                    fileOutputStream.write(outData, 0, mBufferInfo.size);
                    mEncoder.releaseOutputBuffer(outputBufIndex, false);
                }
            }
        } while (mBufferInfo.flags != MediaCodec.BUFFER_FLAG_END_OF_STREAM);

        fileOutputStream.flush();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void getOutputBufferAndWrite(FileOutputStream fileOutputStream) throws IOException {
        for (int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0L);
             outputBufferIndex >= 0;
             outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0L)) {
            int outBitsSize = mBufferInfo.size;
            int outPacketSize = outBitsSize + 6;
            ByteBuffer outputBuffer = mOutputBuffers[outputBufferIndex];
            outputBuffer.position(mBufferInfo.offset);
            outputBuffer.limit(mBufferInfo.size + outBitsSize);
            byte[] outData = new byte[outPacketSize];
            addAMRNBtoPacket(outData, outPacketSize);
            // 给adts头字段空出前6个字节
            outputBuffer.get(outData, 6, outBitsSize);
            outputBuffer.position(mBufferInfo.offset);
            fileOutputStream.write(outData);
            mEncoder.releaseOutputBuffer(outputBufferIndex, false);
        }
    }


    /**
     * 给编码出的amr裸流添加AMR-NB头字段
     *
     * @param packet    空出前6个字节
     * @param packetLen 23 21 41 4d 52 0a
     */
    private void addAMRNBtoPacket(byte[] packet, int packetLen) {
//        packet = new byte[]{'#', '!', 'A', 'M', 'R', '\n'};
//        0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A
        packet[0] = (byte) 0x23;
        packet[1] = (byte) 0x21;
        packet[2] = (byte) 0x41;
        packet[3] = (byte) 0x4d;
        packet[4] = (byte) 0x52;
        packet[5] = (byte) 0x0a;
    }

    public static void main(String args[]) {
        byte[] a = new byte[]{'#', '!', 'A', 'M', 'R', '\n'};

        System.out.println("长度: " + bytesToHexFun1(a));
    }

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 方法二：
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexFun1(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for (byte b : bytes) { // 使用除与取余进行转换
            if (b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }
}
