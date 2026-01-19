#include <jni.h>
#include <string>

#define R2_ACCOUNT_ID   "c784b8e571db8c9b498b351a77ba63b4"
#define R2_ACCESS_KEY   "0f68c743da42a2841213ee8dde89f715"
#define R2_SECRET_KEY   "665647c8f26cc669aac4b23e1d22a8483b2559b33f6d6b0ac15dc8d7bbeaa45e"
#define R2_BUCKET_NAME  "mehendidesign"


extern "C" JNIEXPORT jstring JNICALL
Java_com_softdesk_mehendidesign_utils_R2DataManager_getAccountId(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF(R2_ACCOUNT_ID);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_softdesk_mehendidesign_utils_R2DataManager_getAccessKey(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF(R2_ACCESS_KEY);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_softdesk_mehendidesign_utils_R2DataManager_getSecretKey(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF(R2_SECRET_KEY);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_softdesk_mehendidesign_utils_R2DataManager_getBucketName(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF(R2_BUCKET_NAME);
}