package org.fmod.core;

public class FmodUtils {
    public volatile static FmodUtils instance;

    private FmodUtils() {
    }

    public static FmodUtils getInstance() {
        if (instance == null) {
            synchronized (FmodUtils.class) {
                if (instance == null) {
                    instance = new FmodUtils();
                }
            }
        }
        return instance;
    }

    static {
        /*
         * To simplify our examples we try to load all possible FMOD
         * libraries, the Android.mk will copy in the correct ones
         * for each example. For real products you would just load
         * 'fmod' and if you use the FMOD Studio tool you would also
         * load 'fmodstudio'.
         */

        // Try debug libraries...
        try {
            System.loadLibrary("fmodD");
            System.loadLibrary("fmodstudioD");
        } catch (UnsatisfiedLinkError e) {
        }
        // Try logging libraries...
        try {
            System.loadLibrary("fmodL");
            System.loadLibrary("fmodstudioL");
        } catch (UnsatisfiedLinkError e) {
        }
        // Try release libraries...
        try {
            System.loadLibrary("fmod");
            System.loadLibrary("fmodstudio");
        } catch (UnsatisfiedLinkError e) {
        }

        System.loadLibrary("stlport_shared");
        System.loadLibrary("example");
    }


    private native String getButtonLabel(int index);
    private native void buttonDown(int index);
    private native void buttonUp(int index);
    private native void setStateCreate();
    private native void setStateStart();
    private native void setStateStop();
    private native void setStateDestroy();
    private native void main();
}