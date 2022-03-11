#include <jni.h>
#include "art.h"

extern "C"
JNIEXPORT jint JNICALL
Java_me_weishu_reflection_Reflection_unsealNative(JNIEnv *env, jclass type, jint targetSdkVersion) {
    return unseal(env, targetSdkVersion);
}
