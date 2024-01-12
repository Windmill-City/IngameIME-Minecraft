/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package ingameime;

public class CandidateListCallbackImpl {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected CandidateListCallbackImpl(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CandidateListCallbackImpl obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        IngameIMEJNI.delete_CandidateListCallbackImpl(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  protected void swigDirectorDisconnect() {
    swigCMemOwn = false;
    delete();
  }

  public void swigReleaseOwnership() {
    swigCMemOwn = false;
    IngameIMEJNI.CandidateListCallbackImpl_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    IngameIMEJNI.CandidateListCallbackImpl_change_ownership(this, swigCPtr, true);
  }

  protected void call(CandidateListState arg0, CandidateListContext arg1) {
    IngameIMEJNI.CandidateListCallbackImpl_call(swigCPtr, this, arg0.swigValue(), CandidateListContext.getCPtr(arg1), arg1);
  }

  public CandidateListCallbackImpl() {
    this(IngameIMEJNI.new_CandidateListCallbackImpl(), true);
    IngameIMEJNI.CandidateListCallbackImpl_director_connect(this, swigCPtr, true, true);
  }

}