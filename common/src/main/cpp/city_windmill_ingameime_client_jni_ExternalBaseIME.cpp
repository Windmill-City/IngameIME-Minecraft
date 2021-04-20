#include "IngameIME-JNI/IngameIME/IngameIME/BaseIME.h"
#include "IngameIME-JNI/IngameIME/IngameIME/IMM.cpp"
#include "city_windmill_ingameime_client_jni_ExternalBaseIME.h"

#define GLOBAL(x) env->NewGlobalRef(x)
#define FIELD(clazz, fieldName, type) env->GetObjectField(clazz, env->GetFieldID(clazz, fieldName, type))
#define STATICFIELD(clazz, fieldName, type) env->GetStaticObjectField(clazz, env->GetStaticFieldID(clazz, fieldName, type))
#define FREEGLOBAL(x) if(x){ env->DeleteGlobalRef(x); x = NULL; }

IngameIME::BaseIME* api = new IngameIME::IMM();

JavaVM* g_vm;
jobject go_ExternalBaseIME = NULL;
//CallbackMethods
jmethodID gmtd_onCandidateList = NULL;
jmethodID gmtd_onComposition = NULL;
jmethodID gmtd_onGetCompExt = NULL;
jmethodID gmtd_onAlphaMode = NULL;
//CompositionState
jobject go_CompositionState_START = NULL;
jobject go_CompositionState_UPDATE = NULL;
jobject go_CompositionState_END = NULL;
jobject go_CompositionState_COMMIT = NULL;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* jvm, void* reserved) {
	jint result = -1;
	JNIEnv* env;
	if (jvm->GetEnv((void**)&env, JNI_VERSION_1_8) != JNI_OK) {
		return -1;
	}

	g_vm = jvm;

	result = JNI_VERSION_1_8;
	return result;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
{
	Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nUninitialize(NULL, NULL);
}

template<typename F>
HRESULT call_with_env(F const& pf) {
	if (g_vm == NULL) return E_FAIL;

	JNIEnv* env;
	jint status = g_vm->GetEnv((void**)&env, JNI_VERSION_1_8);

	if (status == JNI_EDETACHED || env == NULL) {
		status = g_vm->AttachCurrentThread((void**)&env, NULL);
		if (status == JNI_OK) {
			pf(env);
			g_vm->DetachCurrentThread();
			return S_OK;
		}
		return status;
	}
	pf(env);
	return S_OK;
}

void CALLBACK onCandidateList(libtf::CandidateList* list) {
	call_with_env([list](JNIEnv* env){
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
		env->CallVoidMethod(go_ExternalBaseIME, gmtd_onCandidateList, cand);
		});
}

void CALLBACK onGetTextExt(PRECT prect) {
	call_with_env([prect](JNIEnv* env) {
		jintArray jrect = (jintArray)env->CallObjectMethod(go_ExternalBaseIME, gmtd_onGetCompExt);
		env->GetIntArrayRegion(jrect, 0, 4, (jint*)prect);
	});
}

void CALLBACK onAlphaMode(BOOL isAlphaMode) {
	call_with_env([isAlphaMode](JNIEnv* env) {
		env->CallVoidMethod(go_ExternalBaseIME, gmtd_onAlphaMode, isAlphaMode);
	});
}

void CALLBACK onComposition(libtf::CompositionEventArgs* args) {
	call_with_env([args](JNIEnv* env) {
		jobject jstate = NULL;
		jstring str = NULL;
		switch (args->m_state)
		{
		case libtf::CompositionState::StartComposition:
			jstate = go_CompositionState_START;
			break;
		case libtf::CompositionState::EndComposition:
			jstate = go_CompositionState_END;
			break;
		case libtf::CompositionState::Commit:
			str = env->NewString((jchar*)args->m_strCommit.c_str(), args->m_strCommit.size());
			jstate = go_CompositionState_COMMIT;
			break;
		case libtf::CompositionState::Composing:
			str = env->NewString((jchar*)args->m_strComposition.c_str(), args->m_strComposition.size());
			jstate = go_CompositionState_UPDATE;
			break;
		default:
			break;
		}
		env->CallVoidMethod(go_ExternalBaseIME, gmtd_onComposition, str, args->m_lCaretPos, jstate);
	});
}

JNIEXPORT void JNICALL Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nInitialize(JNIEnv* env, jobject obj, jlong hwnd)
{
	if (api->m_initialized) return;

	go_ExternalBaseIME = GLOBAL(obj);
	//CompositionState
	auto clStateName = "city/windmill/ingameime/client/jni/ExternalBaseIME$CompositionState";
	auto clStateType = "Lcity/windmill/ingameime/client/jni/ExternalBaseIME$CompositionState;";
	jclass clState = env->FindClass(clStateName);
	go_CompositionState_START = GLOBAL(STATICFIELD(clState, "Start", clStateType));
	go_CompositionState_UPDATE = GLOBAL(STATICFIELD(clState, "Update", clStateType));
	go_CompositionState_END = GLOBAL(STATICFIELD(clState, "End", clStateType));
	go_CompositionState_COMMIT = GLOBAL(STATICFIELD(clState, "Commit", clStateType));
	//Callbacks
	jclass clBaseIME = env->GetObjectClass(obj);
	gmtd_onCandidateList = env->GetMethodID(clBaseIME, "onCandidateList", "([Ljava/lang/String;)V");
	gmtd_onComposition = env->GetMethodID(clBaseIME, "onComposition", "(Ljava/lang/String;ILcity/windmill/ingameime/client/jni/ExternalBaseIME$CompositionState;)V");
	gmtd_onGetCompExt = env->GetMethodID(clBaseIME, "onGetCompExt", "()[I");
	gmtd_onAlphaMode = env->GetMethodID(clBaseIME, "onAlphaMode", "(Z)V");
	//RegCallback
	api->m_sigAlphaMode = onAlphaMode;
	api->m_sigComposition = onComposition;
	api->m_sigCandidateList = onCandidateList;
	api->m_sigGetTextExt = onGetTextExt;
	api->Initialize((HWND)hwnd);
}

JNIEXPORT void JNICALL Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nUninitialize(JNIEnv* env, jobject)
{
	api->Uninitialize();
	FREEGLOBAL(go_ExternalBaseIME);
	FREEGLOBAL(go_CompositionState_START);
	FREEGLOBAL(go_CompositionState_UPDATE);
	FREEGLOBAL(go_CompositionState_END);
	FREEGLOBAL(go_CompositionState_COMMIT);
}

JNIEXPORT void JNICALL Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nSetState(JNIEnv*, jobject, jboolean state)
{
	api->setState(state);
}

JNIEXPORT void JNICALL Java_city_windmill_ingameime_client_jni_ExternalBaseIME_nSetFullScreen(JNIEnv*, jobject, jboolean fullscreen)
{
	api->setFullScreen(fullscreen);
}
