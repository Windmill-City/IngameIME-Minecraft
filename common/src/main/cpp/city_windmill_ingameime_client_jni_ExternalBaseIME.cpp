#include "IngameIME-JNI/IngameIME/IngameIME/BaseIME.h"
#include "IngameIME-JNI/IngameIME/IngameIME/IMM.cpp"
#include "city_windmill_ingameime_client_jni_ExternalBaseIME.h"

#define GLOBAL(x) env->NewGlobalRef(x)
#define FIELD(clazz, fieldName, type) env->GetObjectField(clazz, env->GetFieldID(clazz, fieldName, type))
#define STATICFIELD(clazz, fieldName, type) env->GetStaticObjectField(clazz, env->GetStaticFieldID(clazz, fieldName, type))
#define FREEGLOBAL(x) if(x){ env->DeleteGlobalRef(x); x = NULL; }

IngameIME::BaseIME* api = new IngameIME::IMM();
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

void CALLBACK onCandidateList(libtf::CandidateList* list) {
	jobjectArray cand = NULL;
	if (list->m_lPageSize > 0) {
		jclass clString = env->FindClass("java/lang/String");
		cand = env->NewObjectArray(list->m_lPageSize, clString, NULL);
		for (size_t i = 0; i < list->m_lPageSize; i++)
		{
			auto str = env->NewString((jchar*)list->m_pCandidates[i].c_str(), list->m_pCandidates[i].size());
			env->SetObjectArrayElement(cand, i, str);
		}
	}
	env->CallVoidMethod(O_BaseIME, M_onCandidateList, cand);
}

void CALLBACK onGetTextExt(PRECT prect) {
	jintArray jrect = (jintArray)env->CallObjectMethod(O_BaseIME, M_onGetCompExt);
	env->GetIntArrayRegion(jrect, 0, 4, (jint*)prect);
}

void CALLBACK onAlphaMode(BOOL isAlphaMode) {
	env->CallVoidMethod(O_BaseIME, M_onAlphaMode, isAlphaMode);
}

void CALLBACK onComposition(libtf::CompositionEventArgs* args) {
	jobject jstate = NULL;
	jstring str = NULL;
	switch (args->m_state)
	{
	case libtf::CompositionState::StartComposition:
		jstate = STATE_START;
		break;
	case libtf::CompositionState::EndComposition:
		jstate = STATE_END;
		break;
	case libtf::CompositionState::Commit:
		str = env->NewString((jchar*)args->m_strCommit.c_str(), args->m_strCommit.size());
		jstate = STATE_COMMIT;
		break;
	case libtf::CompositionState::Composing:
		str = env->NewString((jchar*)args->m_strComposition.c_str(), args->m_strComposition.size());
		jstate = STATE_UPDATE;
		break;
	default:
		break;
	}
	env->CallVoidMethod(O_BaseIME, M_onComposition, str, args->m_lCaretPos, jstate);
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
	api->m_sigAlphaMode = onAlphaMode;
	api->m_sigComposition = onComposition;
	api->m_sigCandidateList = onCandidateList;
	api->m_sigGetTextExt = onGetTextExt;
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
	api->setFullScreen(fullscreen);
}
