//
// Created by silvercc on 18/2/1.
//

#include "wenba_com_androidtest_jni_JniTest.h"
#include <stdio.h>

JNIEXPORT void JNICALL Java_wenba_com_androidtest_jni_JniTest_set
        (JNIEnv *env, jobject thiz, jstring string) {
    printf("invoke set from c++/n");
    char *str = (char *)env->GetStringUTFChars(string, NULL);
    printf("%s/n", str);
    env->ReleaseStringUTFChars(string, str);
}

JNIEXPORT jstring JNICALL Java_wenba_com_androidtest_jni_JniTest_get
        (JNIEnv *env, jobject thiz) {
    printf("invoke get from c++/n");
    return env->NewStringUTF("hello world!!!");
}