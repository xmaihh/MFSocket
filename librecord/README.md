:rooster:

# AudioTrack

AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode)

- streamType：指定流的类型 

    - STREAM_ALARM：警告声 
    - STREAM_MUSCI：音乐声 
    - STREAM_RING：铃声 
    - STREAM_SYSTEM：系统声音 
    - STREAM_VOCIE_CALL：电话声音 

- sampleRateInHz ： 采样率

- channelConfig : 声道

- audioFormat ： 采样精度

- bufferSizeInBytes ：缓冲区大小，AudioTrack.getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat)

- mode ： MODE_STATIC和MODE_STREAM： 

    - MODE_STATIC : 直接把所有的数据加载到缓存区
    - MODE_STREAM ：需要多次write，一般用于从网络获取数据或者实时解码
   
# 待办事项
   
- [x] 边录边播
   
- [x] 录制pcm
- [x] pcm播放

- [x] 录制aac
- [x] aac播放

- [x] 录制mp3
- [x] mp3播放

- [x] 录制wav
- [x] wav播放

 ~~录制amr~~
 
- [ ] 录制amr