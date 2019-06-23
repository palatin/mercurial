//
// Created by Игорь on 22.06.2019.
//

#include <jni.h>
#include <android/bitmap.h>
#include <string.h>
static inline void recyclePreviousBitmap(JNIEnv *env, jobject *bitmap) {
    jclass bitmapCls = env->GetObjectClass(*bitmap);
    jmethodID recycle = env->GetMethodID(bitmapCls, "recycle", "()V");
    env->CallVoidMethod(*bitmap, recycle);
}


extern "C" JNIEXPORT jobject Java_com_palatin_mercurial_util_Util_rotateImageBy90(JNIEnv *env, jclass cl, jobject bitmap, jint width, jint height) {
    void *pixels = 0;
    AndroidBitmap_lockPixels(env, bitmap, &pixels);
    int pixelsCount = width * height;
    uint32_t* originData = static_cast<uint32_t *>(pixels);
    uint32_t* newData = new uint32_t[pixelsCount];
    int c = 0;
    for (int i = height - 1; i >= 0; --i) {
        for (int j = 0; j < width; ++j) {
            uint32_t temp = originData[c++];
            newData[height * j + i] = temp;
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
    recyclePreviousBitmap(env, &bitmap);
    jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapFunction = env->GetStaticMethodID(bitmapCls,
                                                            "createBitmap",
                                                            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jstring configName = env->NewStringUTF("ARGB_8888");
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfBitmapConfigFunction = env->GetStaticMethodID(
            bitmapConfigClass, "valueOf",
            "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass,
                                                       valueOfBitmapConfigFunction, configName);
    jobject newBitmap = env->CallStaticObjectMethod(bitmapCls,
                                                    createBitmapFunction, height,
                                                    width, bitmapConfig);
    void *newImagePixels = 0;
    AndroidBitmap_lockPixels(env, newBitmap, &newImagePixels);
    memcpy(newImagePixels, newData,
           sizeof(uint32_t) * pixelsCount);
    AndroidBitmap_unlockPixels(env, newBitmap);
    delete[] newData;
    return newBitmap;
}

