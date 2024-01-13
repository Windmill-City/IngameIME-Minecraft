/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package ingameime;

public class InputModeCallbackImpl {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected InputModeCallbackImpl(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(InputModeCallbackImpl obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(InputModeCallbackImpl obj) {
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
        IngameIMEJNI.delete_InputModeCallbackImpl(swigCPtr);
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
    IngameIMEJNI.InputModeCallbackImpl_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    IngameIMEJNI.InputModeCallbackImpl_change_ownership(this, swigCPtr, true);
  }

  protected void call(InputMode arg0) {
    IngameIMEJNI.InputModeCallbackImpl_call(swigCPtr, this, arg0.swigValue());
  }

  public InputModeCallbackImpl() {
    this(IngameIMEJNI.new_InputModeCallbackImpl(), true);
    IngameIMEJNI.InputModeCallbackImpl_director_connect(this, swigCPtr, true, true);
  }

}
