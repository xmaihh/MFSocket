:notes:

<div align=center><img src="https://github.com/xmaihh/MFSocket/raw/master/art/liblame.png" height="200"/></div>

 Android 中没有提供录制 mp3 的 API，需要使用开源库 lame，lame 是专门用于编码 mp3 的轻量高效的 c 代码库。
 
 liblame工程是在AndroidStudio下使用Cmake把lame编译成so文件
 
 Android通过Jni调用so文件的c代码,可将录制pcm转换成mp3格式数据。
 
 ## native方法
 
 [MP3Recorder.java](https://github.com/xmaihh/MFSocket/blob/4730c4bbacfb3bd9c25d9cf32929e67304a639d2/liblame/src/main/java/com/android/liblame/MP3Recorder.java)
 
 ```java
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
     * @return 输出到mp3buf的byte数量
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
```

## 使用依赖

- Gradle引用

```
 implementation 'tp.xmaihh:libmp3lame:1.0'
```

- Maven引用

```
<dependency>
  <groupId>tp.xmaihh</groupId>
  <artifactId>libmp3lame</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```

Android调用录制MP3的例子

[MP3RecordUtil.java](https://github.com/xmaihh/MFSocket/blob/4730c4bbacfb3bd9c25d9cf32929e67304a639d2/librecord/src/main/java/com/android/librecord/mp3/MP3RecordUtil.java)

在 AndroidManifest 配置文件中添加录音权限：

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
```

 Android 6.0 以上自行实现动态获取权限。
 
 
<div align=center><img src="https://github.com/xmaihh/MFSocket/raw/master/art/sample.png" width="480" height="854"/></div>