package com.tzn.common.enums;

/** 情侣绑定邀请状态。 */
public enum BindingStatus {
  /** 等待对方处理。 */
  PENDING,
  /** 已接受并完成绑定。 */
  ACCEPTED,
  /** 已拒绝。 */
  REJECTED;

  public String getCode() {
    return name();
  }
}
