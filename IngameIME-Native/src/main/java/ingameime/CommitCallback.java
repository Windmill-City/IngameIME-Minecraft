/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package ingameime;

public class CommitCallback {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected CommitCallback(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CommitCallback obj) {
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
        IngameIMEJNI.delete_CommitCallback(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public CommitCallback(CommitCallback arg0) {
    this(IngameIMEJNI.new_CommitCallback__SWIG_0(CommitCallback.getCPtr(arg0), arg0), true);
  }

  public void call(String arg0) {
    IngameIMEJNI.CommitCallback_call(swigCPtr, this, arg0);
  }

  public CommitCallback(SWIGTYPE_p_f_q_const__std__string__void arg0) {
    this(IngameIMEJNI.new_CommitCallback__SWIG_1(SWIGTYPE_p_f_q_const__std__string__void.getCPtr(arg0)), true);
  }

  public CommitCallback(CommitCallbackImpl in) {
    this(IngameIMEJNI.new_CommitCallback__SWIG_2(CommitCallbackImpl.getCPtr(in), in), true);
  }

}