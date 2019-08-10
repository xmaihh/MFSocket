package org.fmod.core;

import android.content.Context;

import org.fmod.FMOD;

public class FmodUtils {
    static {
        /*
         * To simplify our examples we try to load all possible FMOD
         * libraries, the Android.mk will copy in the correct ones
         * for each example. For real products you would just load
         * 'fmod' and if you use the FMOD Studio tool you would also
         * load 'fmodstudio'.
         */

//    	// Try debug libraries...
//    	try { System.loadLibrary("fmodD");
//    		  System.loadLibrary("fmodstudioD"); }
//    	catch (UnsatisfiedLinkError e) { }
//    	// Try logging libraries...
//    	try { System.loadLibrary("fmodL");
//    		  System.loadLibrary("fmodstudioL"); }
//    	catch (UnsatisfiedLinkError e) { }
        // Try release libraries...
        try {
            System.loadLibrary("fmod");
            System.loadLibrary("fmodstudio");
        } catch (UnsatisfiedLinkError e) {
        }

        System.loadLibrary("playsound");
    }

    public enum Effect {
        ORIGINAL(0),  //原声
        LOLITA(1), //萝莉
        UNCLE(2),  //大叔
        THRILLER(3), //惊悚
        FUNNY(4),  //搞怪
        ETHEREAL(5), //空灵
        DRAWL(6), //慢吞吞
        CHORUS(7);//合唱
        int mode;

        Effect(int i) {
            mode = i;
        }

        public int getMode() {
            return mode;
        }
    }

    public static native void playSound(String path, int effect_mode);

    public static native void pauseSound(boolean paused);

    public static native void stopSound();


    private volatile static FmodUtils instance;

    private FmodUtils(Context mContext) {
        if (!FMOD.checkInit()) {
            FMOD.init(mContext);
        }
    }

    public static FmodUtils getInstance(Context mContext) {
        if (instance == null) {
            synchronized (FmodUtils.class) {
                if (instance == null) {
                    instance = new FmodUtils(mContext);
                }
            }
        }
        return instance;
    }

    public void close() {
        FMOD.close();
    }
}
