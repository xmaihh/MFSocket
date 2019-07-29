#include "org_fmod_core_FmodUtils.h"
#include "inc/fmod.h"
#include "inc/fmod.hpp"
#include <unistd.h>

using namespace FMOD;

System *system;
Sound *sound;
Channel *channel = 0;
DSP *dsp;

void stopSound();
/*
 * Class:     org_fmod_core_FmodUtils
 * Method:    playSound
 * Signature: (Ljava/lang/String;)V
 */
extern "C"
JNIEXPORT void JNICALL Java_org_fmod_core_FmodUtils_playSound
        (JNIEnv *env, jclass type, jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);
    //创建对象
    System_Create(&system);

    //初始化
    void *extradriverdata;
    //手机录音一般是16位  采样位数:一个采样样本用多少位二进制数编码
    system->init(32, FMOD_INIT_NORMAL, extradriverdata);

    //創建一個聲音
    system->createSound(path, FMOD_DEFAULT, 0, &sound);

    //播放声音
    system->playSound(sound, 0, false, &channel);
    system->update();
    //是否播放
    bool isplaying = true;
    //阻塞线程
    //进程休眠 单位微秒 us
    //每秒钟判断是否在播放
    while (isplaying) {
        channel->isPlaying(&isplaying);
        usleep(1000 * 1000);
    }
    //释放资源
    sound->release();
    system->close();
    system->release();
    env->ReleaseStringUTFChars(path_, path);
}

/*
 * Class:     org_fmod_core_FmodUtils
 * Method:    stopSound
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_fmod_core_FmodUtils_stopSound
        (JNIEnv *env, jclass jclas) {
    stopSound();
}

void stopSound() {
    channel->stop();
}