/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package ingameime;

public class CandidateListCallback {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected CandidateListCallback(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CandidateListCallback obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(CandidateListCallback obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        IngameIMEJNI.delete_CandidateListCallback(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void call(CandidateListState arg0, CandidateListContext arg1) {
    IngameIMEJNI.CandidateListCallback_call(swigCPtr, this, arg0.swigValue(), CandidateListContext.getCPtr(arg1), arg1);
  }

  public CandidateListCallback(CandidateListCallbackImpl in) {
    this(IngameIMEJNI.new_CandidateListCallback(CandidateListCallbackImpl.getCPtr(in), in), true);
  }

}
