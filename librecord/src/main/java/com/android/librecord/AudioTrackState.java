package com.android.librecord;

public enum AudioTrackState {
    /**
     * 准备状态
     */
    PREPARE,
    /**
     * 播放
     */
    PLAYING,
    /**
     * 停止
     */
    STOP,
    /**
     * 结束,释放资源
     */
    RELEASE
}
