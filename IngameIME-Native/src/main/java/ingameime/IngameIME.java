/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package ingameime;

public class IngameIME {
  public static InputContext CreateInputContextWin32(long hWnd, API api, boolean uiLess) {
    long cPtr = IngameIMEJNI.CreateInputContextWin32__SWIG_0(hWnd, api.swigValue(), uiLess);
    return (cPtr == 0) ? null : new InputContext(cPtr, true);
  }

  public static InputContext CreateInputContextWin32(long hWnd, API api) {
    long cPtr = IngameIMEJNI.CreateInputContextWin32__SWIG_1(hWnd, api.swigValue());
    return (cPtr == 0) ? null : new InputContext(cPtr, true);
  }

}
