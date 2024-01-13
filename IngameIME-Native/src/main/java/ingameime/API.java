/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package ingameime;

public final class API {
  public final static API TextServiceFramework = new API("TextServiceFramework");
  public final static API Imm32 = new API("Imm32");

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static API swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + API.class + " with value " + swigValue);
  }

  private API(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private API(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private API(String swigName, API swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static API[] swigValues = { TextServiceFramework, Imm32 };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

