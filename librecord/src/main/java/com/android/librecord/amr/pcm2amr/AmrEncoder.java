package com.android.librecord.amr.pcm2amr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
public class AmrEncoder {
    public static void pcm2Amr(String pcmPath, String amrPath) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(pcmPath);
            pcm2Amr(fis, amrPath);
            fis.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pcm2Amr(InputStream pcmStream, String amrPath) {
        try {
            AmrInputStream ais = new AmrInputStream(pcmStream);
            OutputStream out = new FileOutputStream(amrPath);
            byte[] buf = new byte[4096];
            int len = -1;
            /*
             * 下面的AMR的文件头
             */
            out.write(0x23);
            out.write(0x21);
            out.write(0x41);
            out.write(0x4D);
            out.write(0x52);
            out.write(0x0A);
            while ((len = ais.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            ais.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
