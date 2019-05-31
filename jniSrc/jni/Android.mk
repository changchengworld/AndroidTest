LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := jni-utils
LOCAL_SRC_FILES := JniUtils.cpp

include $(BUILD_SHARED_LIBRARY)
