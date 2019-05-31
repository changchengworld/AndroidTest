//
// Created by silvercc on 18/2/1.
//
#include <jni.h>
#include <stdio.h>

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     wenba_com_androidtest_jni_JniUtils
 * Method:    set
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_wenba_com_androidtest_jni_JniUtils_set
        (JNIEnv *env, jobject thiz, jstring string) {
    printf("invoke set from c++/n");
    char* str =(char*)env->GetStringChars(string, NULL);
    printf("%s/n", str);
    env->ReleaseStringUTFChars(string, str);
}

/*
 * Class:     wenba_com_androidtest_jni_JniUtils
 * Method:    get
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_wenba_com_androidtest_jni_JniUtils_get
        (JNIEnv *env, jobject thiz) {
    printf("invoke get from c++/n");
    return env->NewStringUTF("Hello world !!!! haha");
}

#ifdef __cplusplus
}
#endif