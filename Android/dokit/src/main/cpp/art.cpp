//
// Created by weishu on 2018/6/7.
//
#include "art.h"
#include <android/log.h>
#include <vector>
#include <string>
#include <stdlib.h>
#include <sys/system_properties.h>

#define LOGV(...)  ((void)__android_log_print(ANDROID_LOG_INFO, "FreeReflect", __VA_ARGS__))

template<typename T>
int findOffset(void *start, int regionStart, int regionEnd, T value) {

    if (NULL == start || regionEnd <= 0 || regionStart < 0) {
        return -1;
    }
    char *c_start = (char *) start;

    for (int i = regionStart; i < regionEnd; i += 4) {
        T *current_value = (T *) (c_start + i);
        if (value == *current_value) {
            LOGV("found offset: %d", i);
            return i;
        }
    }
    return -2;
}

template<typename Runtime>
int unseal0(Runtime *partialRuntime) {
    bool is_java_debuggable = partialRuntime->is_java_debuggable_;
    bool is_native_debuggable = partialRuntime->is_native_debuggable_;
    bool safe_mode = partialRuntime->safe_mode_;

    // TODO validate

    LOGV("is_java_debuggable: %d, is_native_debuggable: %d, safe_mode: %d", is_java_debuggable,
         is_native_debuggable, safe_mode);
    LOGV("hidden api policy before : %d", partialRuntime->hidden_api_policy_);
    LOGV("fingerprint: %s", partialRuntime->fingerprint_.c_str());

    partialRuntime->hidden_api_policy_ = EnforcementPolicy::kNoChecks;
    LOGV("hidden api policy after: %d", partialRuntime->hidden_api_policy_);
    return 0;
}

int unseal(JNIEnv *env, jint targetSdkVersion) {

    char api_level_str[5];
    char preview_api_str[5];
    __system_property_get("ro.build.version.sdk", api_level_str);
    __system_property_get("ro.build.version.preview_sdk", preview_api_str);

    int api_level = atoi(api_level_str);
    bool is_preview = atoi(preview_api_str) > 0;

    bool isAndroidR = api_level >= 30 || (api_level == 29 && is_preview);

    JavaVM *javaVM;
    env->GetJavaVM(&javaVM);

    JavaVMExt *javaVMExt = (JavaVMExt *) javaVM;
    void *runtime = javaVMExt->runtime;

    LOGV("runtime ptr: %p, vmExtPtr: %p", runtime, javaVMExt);

    const int MAX = 2000;
    int offsetOfVmExt = findOffset(runtime, 0, MAX, (size_t) javaVMExt);
    LOGV("offsetOfVmExt: %d", offsetOfVmExt);

    if (offsetOfVmExt < 0) {
        return -1;
    }

    int startOffset = offsetOfVmExt;
    if (isAndroidR) {
        startOffset += 200;
    }

    int targetSdkVersionOffset = findOffset(runtime, startOffset, MAX, targetSdkVersion);
    LOGV("target: %d", targetSdkVersionOffset);

    if (targetSdkVersionOffset < 0) {
        return -2;
    }

    /*
    const int retry = 3;
    bool targetSdkVersionCorrect = false;
    int vector_string_size = sizeof(std::vector<std::string>);
    LOGV("vector string size: %d", vector_string_size);

    for (int i = 0; i < retry; ++i) {
        int cpu_abilist_offset = targetSdkVersionOffset - vector_string_size;
        void *cpu_abilist_address = ((char *) runtime + cpu_abilist_offset);

        auto cpu_abi_list_ = reinterpret_cast<std::vector<std::string>*>(cpu_abilist_address);

        bool correct = false;
        for (auto itr = cpu_abi_list_->begin(); itr != cpu_abi_list_->end(); itr++) {
            std::string value = *itr;
            LOGV("value : %s", value.c_str());
            if (strcmp(value.c_str(), "x86") == 0) {
                correct = true;
                break;
            }
        }
        if (correct) {
            targetSdkVersionCorrect = true;
            break;
        }
    }

    if (!targetSdkVersionCorrect) {
        return -3;
    }
    */

    if (isAndroidR) {
        auto *partialRuntime = reinterpret_cast<PartialRuntimeR *>((char *) runtime +
                                                                              targetSdkVersionOffset);
        unseal0<PartialRuntimeR>(partialRuntime);
    } else {
        auto *partialRuntime = (PartialRuntime *) ((char *) runtime +
                                                             targetSdkVersionOffset);
        unseal0<PartialRuntime>(partialRuntime);
    }

    return 0;
}

