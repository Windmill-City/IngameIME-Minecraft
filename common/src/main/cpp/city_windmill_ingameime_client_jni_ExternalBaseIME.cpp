#include "IngameIME-JNI/IngameIME/IngameIME/BaseIME.h"
#include "IngameIME-JNI/IngameIME/IngameIME/IMM.cpp"
#include "city_windmill_ingameime_client_jni_ExternalBaseIME.h"

#define GLOBAL(x) env->NewGlobalRef(x)
#define FIELD(clazz, fieldName, type) env->GetObjectField(clazz, env->GetFieldID(clazz, fieldName, type))
#define STATICFIELD(clazz, fieldName, type) env->GetStaticObjectField(clazz, env->GetStaticFieldID(clazz, fieldName, type))
#define FREEGLOBAL(x) if(x){ env->DeleteGlobalRef(x); x = NULL; }

IngameIME::BaseIME* api = IngameIME::IMM::getInstance();
JNIEnv* env = NULL;
jobject O_BaseIME = NULL;
//CallbackMethods
jmethodID M_onCandidateList = NULL;
jmethodID M_onComposition = NULL;
jmethodID M_onGetCompExt = NULL;
jmethodID M_onAlphaMode = NULL;
//CompositionState
jobject STATE_START = NULL;
jobject STATE_UPDATE = NULL;
jobject STATE_END = NULL;
jobject STATE_COMMIT = NULL;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* jvm, void* reserved) {
	jint result = -1;

	if (jvm->GetEnv((void**)&env, JNI_VERSION_1_8) != JNI_OK) {
		return -1;
	}

	result = JNI_VERSION_1_8;
	return result;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
{
	Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nUninitialize(NULL, NULL);
}

void CALLBACK onCandidateList(std::wstring* candStr, size_t size) {
	jobjectArray cand = NULL;
	if (candStr) {
		jclass clString = env->FindClass("java/lang/String");
		cand = env->NewObjectArray(size, clString, NULL);
		for (size_t i = 0; i < size; i++)
		{
			auto str = env->NewString((jchar*)candStr[i].c_str(), candStr[i].size());
			env->SetObjectArrayElement(cand, i, str);
		}
	}
	env->CallVoidMethod(O_BaseIME, M_onCandidateList, cand);
}

void CALLBACK onComposition(PWCHAR pstr, BOOL state, INT caret) {
	jobject jstate = NULL;
	jstring str = NULL;
	if (pstr) {//Update/Commit
		str = env->NewString((jchar*)pstr, wcslen(pstr));
		jstate = state ? STATE_UPDATE : STATE_COMMIT;
	}
	else {//Start/End
		jstate = state ? STATE_START : STATE_END;
	}
	env->CallVoidMethod(O_BaseIME, M_onComposition, str, caret, jstate);
}

void CALLBACK onGetCompExt(PRECT prect) {
	jintArray jrect = (jintArray)env->CallObjectMethod(O_BaseIME, M_onGetCompExt);
	env->GetIntArrayRegion(jrect, 0, 4, (jint*)prect);
}

void CALLBACK onAlphaMode(BOOL isAlphaMode) {
	env->CallVoidMethod(O_BaseIME, M_onAlphaMode, isAlphaMode);
}

JNIEXPORT void JNICALL Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nInitialize(JNIEnv*, jobject obj, jlong hwnd)
{
	if (api->m_initialized) return;

	O_BaseIME = GLOBAL(obj);
	//CompositionState
	auto clStateName = "city/windmill/ingameime/client/jni/ExternalBaseIME$CompositionState";
	auto clStateType = "Lcity/windmill/ingameime/client/jni/ExternalBaseIME$CompositionState;";
	jclass clState = env->FindClass(clStateName);
	STATE_START = GLOBAL(STATICFIELD(clState, "Start", clStateType));
	STATE_UPDATE = GLOBAL(STATICFIELD(clState, "Update", clStateType));
	STATE_END = GLOBAL(STATICFIELD(clState, "End", clStateType));
	STATE_COMMIT = GLOBAL(STATICFIELD(clState, "Commit", clStateType));
	//Callbacks
	jclass clBaseIME = env->GetObjectClass(obj);
	M_onCandidateList = env->GetMethodID(clBaseIME, "onCandidateList", "([Ljava/lang/String;)V");
	M_onComposition = env->GetMethodID(clBaseIME, "onComposition", "(Ljava/lang/String;ILcity/windmill/ingameime/client/jni/ExternalBaseIME$CompositionState;)V");
	M_onGetCompExt = env->GetMethodID(clBaseIME, "onGetCompExt", "()[I");
	M_onAlphaMode = env->GetMethodID(clBaseIME, "onAlphaMode", "(Z)V");
	//RegCallback
	api->onCandidateList = onCandidateList;
	api->onComposition = onComposition;
	api->onGetCompExt = onGetCompExt;
	api->onAlphaMode = onAlphaMode;
	api->Initialize((HWND)hwnd);
}

JNIEXPORT void JNICALL Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nUninitialize(JNIEnv*, jobject)
{
	api->Uninitialize();
	FREEGLOBAL(O_BaseIME);
	FREEGLOBAL(STATE_START);
	FREEGLOBAL(STATE_UPDATE);
	FREEGLOBAL(STATE_END);
	FREEGLOBAL(STATE_COMMIT);
}

JNIEXPORT void JNICALL Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nSetState(JNIEnv*, jobject, jboolean state)
{
	api->setState(state);
}

JNIEXPORT void JNICALL Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nSetFullScreen(JNIEnv*, jobject, jboolean fullscreen)
{
	api->m_fullscreen = fullscreen;
}

JNIEXPORT void JNICALL Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nSetHandleComposition(JNIEnv*, jobject, jboolean handlecomp)
{
	api->m_handleCompStr = handlecomp;
}
