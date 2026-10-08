package com.tzn.common.enums;

/** 系统启用状态。 */
public enum EnabledStatus {
  /** 已禁用。 */
  DISABLED(0),
  /** 已启用。 */
  ENABLED(1);

  private final int code;

  EnabledStatus(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
