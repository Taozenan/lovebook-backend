package com.tzn.common.enums;

/** 逻辑删除状态。 */
public enum DeletedStatus {
  /** 正常。 */
  NOT_DELETED(0),
  /** 已删除。 */
  DELETED(1);

  private final int code;

  DeletedStatus(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
