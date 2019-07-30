#include "org_fmod_core_FmodUtils.h"
#include "org_fmod_core_FmodUtils_Effect.h"
#include "inc/fmod.h"
#include "inc/fmod.hpp"
//#include <stdlib.h>
#include <unistd.h>
#include <android/log.h>

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"libfmod",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"libfmod",FORMAT,##__VA_ARGS__);

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
        (JNIEnv *env, jclass type, jstring path_, jint effect_mode) {
    const char *path = env->GetStringUTFChars(path_, 0);
    //是否播放
    bool isplaying = true;
    //创建对象
    System_Create(&system);

    //初始化
    void *extradriverdata;
    //采样位数:一个采样样本用多少位二进制数编码
    //设置系统对象最大声轨为32
    system->init(32, FMOD_INIT_NORMAL, extradriverdata);

    //創建一個聲音
    system->createSound(path, FMOD_DEFAULT, 0, &sound);

    //播放声音,指定的是channel音轨0
    system->playSound(sound, 0, false, &channel);

    try {
        //播放过程中处理不同的音效
        switch (effect_mode) {
            case MODE_ORIGINAL:
                //原声
                LOGI("s%", "原声播放");
                break;
            case MODE_LOLITA:
                //萝莉
                //DSP digital signal process
                //dsp -> 音效::改变声音的两个参数：响度（振幅） 声调（频率）
                //女声为高声，将声音提高 8 个音调
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 2.0);
                channel->addDSP(0, dsp);
                LOGI("s%", "萝莉");
                break;
            case MODE_UNCLE:
                //大叔
                //男声为低声，将声音音效降低降低音调到 0.8
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 0.8);
                channel->addDSP(0, dsp);
                LOGI("s%", "大叔");
                break;
            case MODE_THRILLER:
                //惊悚
                //声音抖动，设置颤音效果（Tremolo）
                system->createDSPByType(FMOD_DSP_TYPE_TREMOLO, &dsp);
                dsp->setParameterFloat(FMOD_DSP_TREMOLO_SKEW, 0.5);
                dsp->setParameterFloat(FMOD_DSP_TREMOLO_FREQUENCY, 20);
                channel->addDSP(0, dsp);
                LOGI("s%", "惊悚");
                break;
            case MODE_FUNNY:
                //搞怪
                //提高说话速度
                //加快声音的播放速度
                float frequency;
                channel->getFrequency(&frequency);
                channel->setFrequency(frequency * 2);
                LOGI("s%", "搞怪");
                break;
            case MODE_ETHEREAL:
                //空灵
                //添加回声
                system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp);
                //声音延迟
                dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY, 300);
                //回声次数
                dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 3);
                channel->addDSP(0, dsp);
                LOGI("s%", "空灵");
                break;
            case MODE_DRAWL:
                //慢吞吞
                //放慢说话速度
                float frequency_m;
                channel->getFrequency(&frequency_m);
                channel->setFrequency(frequency_m * 0.8);
                LOGI("s%", "慢吞吞");
                break;
            case MODE_CHORUS:
                //合唱
                system->createDSPByType(FMOD_DSP_TYPE_CHORUS, &dsp);
                //混音
                dsp->setParameterFloat(FMOD_DSP_CHORUS_MIX, 50);
                //速率
                dsp->setParameterFloat(FMOD_DSP_CHORUS_RATE, 1.1);
                channel->addDSP(0, dsp);
                LOGI("s%", "合唱");
                break;
            case 8:
                //山谷
                //設置回声（Echo）
                system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp);
                dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY, 500);
                dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 22);
                dsp->setParameterFloat(FMOD_DSP_ECHO_WETLEVEL, -15);
                channel->addDSP(0, dsp);
                LOGI("s%", "山谷");
                break;
            case 9:
                //禮堂
                //設置混響FMOD_PRESET_AUDITORIUM { 4300, 20, 30, 5000, 59, 100, 100, 250, 0, 5850, 64, -11.7f }
                system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 4300);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_EARLYDELAY, 20);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_LATEDELAY, 30);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_HFREFERENCE, 5000);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_HFDECAYRATIO, 59);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 100);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DENSITY, 100);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_LOWSHELFFREQUENCY, 250);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_LOWSHELFGAIN, 0);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_HIGHCUT, 5850);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_EARLYLATEMIX, 64);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, -11.7f);
                channel->addDSP(0, dsp);
                LOGI("s%", "礼堂");
                break;
            case 10:
                //教室
                //設置混響{ 400, 2, 3, 5000, 83, 100, 100, 250, 0, 6050, 88, -9.4f }
                system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 400);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_EARLYDELAY, 2);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_LATEDELAY, 3);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_HFREFERENCE, 5000);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_HFDECAYRATIO, 83);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 100);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DENSITY, 100);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_LOWSHELFFREQUENCY, 250);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_LOWSHELFGAIN, 0);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_HIGHCUT, 6050);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_EARLYLATEMIX, 88);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, -9.4f);
                channel->addDSP(0, dsp);
                LOGI("s%", "教室");
                break;
            case 11:
                //音樂廳
                //設置混響FMOD_PRESET_CONCERTHALL { 3900, 20, 29, 5000, 70, 100, 100, 250, 0, 5650, 80, -9.8f }
                system->createDSPByType(FMOD_DSP_TYPE_SFXREVERB, &dsp);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DECAYTIME, 3900);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_EARLYDELAY, 20);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_LATEDELAY, 29);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_HFREFERENCE, 5000);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_HFDECAYRATIO, 70);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DIFFUSION, 100);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_DENSITY, 100);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_LOWSHELFFREQUENCY, 250);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_LOWSHELFGAIN, 0);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_HIGHCUT, 5650);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_EARLYLATEMIX, 80);
                dsp->setParameterFloat(FMOD_DSP_SFXREVERB_WETLEVEL, -9.8f);
                channel->addDSP(0, dsp);
                LOGI("s%", "音乐厅");
                break;
            case 12:
                //機器人
                //設置鋸齒（Flange）
                system->createDSPByType(FMOD_DSP_TYPE_FLANGE, &dsp);
                dsp->setParameterFloat(FMOD_DSP_FLANGE_RATE, 0.2f);
                dsp->setParameterFloat(FMOD_DSP_FLANGE_DEPTH, 40);
                channel->addDSP(0, dsp);
                LOGI("s%", "机器人");
                break;
            case 13:
                //小黃人
                //提高 8 个音调，加快语速 120%
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 2.0);
                channel->addDSP(0, dsp);
                float frequency_minions;
                channel->getFrequency(&frequency_minions);
                channel->setFrequency(frequency_minions * 1.2);
                LOGI("s%", "小黄人");
                break;
            case 14:
                //明亮
                //调整 EQ，将 500-2000Hz 的 Q 值调高
                system->createDSPByType(FMOD_DSP_TYPE_THREE_EQ, &dsp);
                dsp->setParameterFloat(FMOD_DSP_THREE_EQ_HIGHGAIN, 10);
                channel->addDSP(0, dsp);
                LOGI("s%", "明亮");
                break;
            default:
                break;
        }
    } catch (...) {
        LOGE("s%", "effect error !!!")
        goto END;
    }

    //update的时候才会播放
    system->update();

    //阻塞线程
    //进程休眠 单位微秒 us
    //每秒钟判断是否在播放
    while (isplaying) {
        channel->isPlaying(&isplaying);
        usleep(1000 * 1000);
    }
    goto END;

    //释放资源
    END:
    env->ReleaseStringUTFChars(path_, path);
    sound->release();
    system->close();
    system->release();

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