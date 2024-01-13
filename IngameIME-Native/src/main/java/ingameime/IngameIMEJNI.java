/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package ingameime;

public class IngameIMEJNI {
  public final static native void delete_PreEditCallbackImpl(long jarg1);
  public final static native void PreEditCallbackImpl_call(long jarg1, PreEditCallbackImpl jarg1_, int jarg2, long jarg3, PreEditContext jarg3_);
  public final static native long new_PreEditCallbackImpl();
  public final static native void PreEditCallbackImpl_director_connect(PreEditCallbackImpl obj, long cptr, boolean mem_own, boolean weak_global);
  public final static native void PreEditCallbackImpl_change_ownership(PreEditCallbackImpl obj, long cptr, boolean take_or_release);
  public final static native void PreEditCallback_call(long jarg1, PreEditCallback jarg1_, int jarg2, long jarg3, PreEditContext jarg3_);
  public final static native long new_PreEditCallback(long jarg1, PreEditCallbackImpl jarg1_);
  public final static native void delete_PreEditCallback(long jarg1);
  public final static native void delete_CommitCallbackImpl(long jarg1);
  public final static native void CommitCallbackImpl_call(long jarg1, CommitCallbackImpl jarg1_, String jarg2);
  public final static native long new_CommitCallbackImpl();
  public final static native void CommitCallbackImpl_director_connect(CommitCallbackImpl obj, long cptr, boolean mem_own, boolean weak_global);
  public final static native void CommitCallbackImpl_change_ownership(CommitCallbackImpl obj, long cptr, boolean take_or_release);
  public final static native void CommitCallback_call(long jarg1, CommitCallback jarg1_, String jarg2);
  public final static native long new_CommitCallback(long jarg1, CommitCallbackImpl jarg1_);
  public final static native void delete_CommitCallback(long jarg1);
  public final static native void delete_CandidateListCallbackImpl(long jarg1);
  public final static native void CandidateListCallbackImpl_call(long jarg1, CandidateListCallbackImpl jarg1_, int jarg2, long jarg3, CandidateListContext jarg3_);
  public final static native long new_CandidateListCallbackImpl();
  public final static native void CandidateListCallbackImpl_director_connect(CandidateListCallbackImpl obj, long cptr, boolean mem_own, boolean weak_global);
  public final static native void CandidateListCallbackImpl_change_ownership(CandidateListCallbackImpl obj, long cptr, boolean take_or_release);
  public final static native void CandidateListCallback_call(long jarg1, CandidateListCallback jarg1_, int jarg2, long jarg3, CandidateListContext jarg3_);
  public final static native long new_CandidateListCallback(long jarg1, CandidateListCallbackImpl jarg1_);
  public final static native void delete_CandidateListCallback(long jarg1);
  public final static native void delete_InputModeCallbackImpl(long jarg1);
  public final static native void InputModeCallbackImpl_call(long jarg1, InputModeCallbackImpl jarg1_, int jarg2);
  public final static native long new_InputModeCallbackImpl();
  public final static native void InputModeCallbackImpl_director_connect(InputModeCallbackImpl obj, long cptr, boolean mem_own, boolean weak_global);
  public final static native void InputModeCallbackImpl_change_ownership(InputModeCallbackImpl obj, long cptr, boolean take_or_release);
  public final static native void InputModeCallback_call(long jarg1, InputModeCallback jarg1_, int jarg2);
  public final static native long new_InputModeCallback(long jarg1, InputModeCallbackImpl jarg1_);
  public final static native void delete_InputModeCallback(long jarg1);
  public final static native void string_list_Iterator_set_unchecked(long jarg1, string_list.Iterator jarg1_, String jarg2);
  public final static native long string_list_Iterator_next_unchecked(long jarg1, string_list.Iterator jarg1_);
  public final static native long string_list_Iterator_previous_unchecked(long jarg1, string_list.Iterator jarg1_);
  public final static native String string_list_Iterator_deref_unchecked(long jarg1, string_list.Iterator jarg1_);
  public final static native long string_list_Iterator_advance_unchecked(long jarg1, string_list.Iterator jarg1_, long jarg2);
  public final static native void delete_string_list_Iterator(long jarg1);
  public final static native long new_string_list__SWIG_0();
  public final static native long new_string_list__SWIG_1(long jarg1, string_list jarg1_);
  public final static native boolean string_list_isEmpty(long jarg1, string_list jarg1_);
  public final static native void string_list_clear(long jarg1, string_list jarg1_);
  public final static native long string_list_remove(long jarg1, string_list jarg1_, long jarg2, string_list.Iterator jarg2_);
  public final static native void string_list_removeLast(long jarg1, string_list jarg1_);
  public final static native void string_list_removeFirst(long jarg1, string_list jarg1_);
  public final static native void string_list_addLast(long jarg1, string_list jarg1_, String jarg2);
  public final static native void string_list_addFirst(long jarg1, string_list jarg1_, String jarg2);
  public final static native long string_list_begin(long jarg1, string_list jarg1_);
  public final static native long string_list_end(long jarg1, string_list jarg1_);
  public final static native long string_list_insert(long jarg1, string_list jarg1_, long jarg2, string_list.Iterator jarg2_, String jarg3);
  public final static native long new_string_list__SWIG_2(int jarg1, String jarg2);
  public final static native int string_list_doSize(long jarg1, string_list jarg1_);
  public final static native int string_list_doPreviousIndex(long jarg1, string_list jarg1_, long jarg2, string_list.Iterator jarg2_);
  public final static native int string_list_doNextIndex(long jarg1, string_list jarg1_, long jarg2, string_list.Iterator jarg2_);
  public final static native boolean string_list_doHasNext(long jarg1, string_list jarg1_, long jarg2, string_list.Iterator jarg2_);
  public final static native void delete_string_list(long jarg1);
  public final static native int CandidateListContext_selection_get(long jarg1, CandidateListContext jarg1_);
  public final static native long CandidateListContext_candidates_get(long jarg1, CandidateListContext jarg1_);
  public final static native void delete_CandidateListContext(long jarg1);
  public final static native void PreEditRect_x_set(long jarg1, PreEditRect jarg1_, int jarg2);
  public final static native int PreEditRect_x_get(long jarg1, PreEditRect jarg1_);
  public final static native void PreEditRect_y_set(long jarg1, PreEditRect jarg1_, int jarg2);
  public final static native int PreEditRect_y_get(long jarg1, PreEditRect jarg1_);
  public final static native void PreEditRect_width_set(long jarg1, PreEditRect jarg1_, int jarg2);
  public final static native int PreEditRect_width_get(long jarg1, PreEditRect jarg1_);
  public final static native void PreEditRect_height_set(long jarg1, PreEditRect jarg1_, int jarg2);
  public final static native int PreEditRect_height_get(long jarg1, PreEditRect jarg1_);
  public final static native long new_PreEditRect();
  public final static native void delete_PreEditRect(long jarg1);
  public final static native int PreEditContext_selStart_get(long jarg1, PreEditContext jarg1_);
  public final static native int PreEditContext_selEnd_get(long jarg1, PreEditContext jarg1_);
  public final static native String PreEditContext_content_get(long jarg1, PreEditContext jarg1_);
  public final static native void delete_PreEditContext(long jarg1);
  public final static native String InputContext_Version_get();
  public final static native void delete_InputContext(long jarg1);
  public final static native int InputContext_getInputMode(long jarg1, InputContext jarg1_);
  public final static native void InputContext_setPreEditRect(long jarg1, InputContext jarg1_, long jarg2, PreEditRect jarg2_);
  public final static native long InputContext_getPreEditRect(long jarg1, InputContext jarg1_);
  public final static native void InputContext_setActivated(long jarg1, InputContext jarg1_, boolean jarg2);
  public final static native boolean InputContext_getActivated(long jarg1, InputContext jarg1_);
  public final static native long InputContext_setCallback__SWIG_0(long jarg1, InputContext jarg1_, long jarg2, PreEditCallback jarg2_);
  public final static native long InputContext_setCallback__SWIG_1(long jarg1, InputContext jarg1_, long jarg2, CommitCallback jarg2_);
  public final static native long InputContext_setCallback__SWIG_2(long jarg1, InputContext jarg1_, long jarg2, CandidateListCallback jarg2_);
  public final static native long InputContext_setCallback__SWIG_3(long jarg1, InputContext jarg1_, long jarg2, InputModeCallback jarg2_);
  public final static native long CreateInputContextWin32__SWIG_0(long jarg1, int jarg2, boolean jarg3);
  public final static native long CreateInputContextWin32__SWIG_1(long jarg1, int jarg2);

  public static void SwigDirector_PreEditCallbackImpl_call(PreEditCallbackImpl jself, int arg0, long arg1) {
    jself.call(CompositionState.swigToEnum(arg0), (arg1 == 0) ? null : new PreEditContext(arg1, false));
  }
  public static void SwigDirector_CommitCallbackImpl_call(CommitCallbackImpl jself, String arg0) {
    jself.call(arg0);
  }
  public static void SwigDirector_CandidateListCallbackImpl_call(CandidateListCallbackImpl jself, int arg0, long arg1) {
    jself.call(CandidateListState.swigToEnum(arg0), (arg1 == 0) ? null : new CandidateListContext(arg1, false));
  }
  public static void SwigDirector_InputModeCallbackImpl_call(InputModeCallbackImpl jself, int arg0) {
    jself.call(InputMode.swigToEnum(arg0));
  }

  private final static native void swig_module_init();
  static {
    swig_module_init();
  }
}
