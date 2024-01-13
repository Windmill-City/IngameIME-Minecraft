/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package ingameime;

public class PreEditRect {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected PreEditRect(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(PreEditRect obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(PreEditRect obj) {
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
        IngameIMEJNI.delete_PreEditRect(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setX(int value) {
    IngameIMEJNI.PreEditRect_x_set(swigCPtr, this, value);
  }

  public int getX() {
    return IngameIMEJNI.PreEditRect_x_get(swigCPtr, this);
  }

  public void setY(int value) {
    IngameIMEJNI.PreEditRect_y_set(swigCPtr, this, value);
  }

  public int getY() {
    return IngameIMEJNI.PreEditRect_y_get(swigCPtr, this);
  }

  public void setWidth(int value) {
    IngameIMEJNI.PreEditRect_width_set(swigCPtr, this, value);
  }

  public int getWidth() {
    return IngameIMEJNI.PreEditRect_width_get(swigCPtr, this);
  }

  public void setHeight(int value) {
    IngameIMEJNI.PreEditRect_height_set(swigCPtr, this, value);
  }

  public int getHeight() {
    return IngameIMEJNI.PreEditRect_height_get(swigCPtr, this);
  }

  public PreEditRect() {
    this(IngameIMEJNI.new_PreEditRect(), true);
  }

}
