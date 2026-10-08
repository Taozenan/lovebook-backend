package com.tzn.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 绑定邀请创建结果。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindingInvitationVo {
  /** 状态。 */
  private String status;

  /** 目标用户手机号。 */
  private String targetPhone;

  public String status() {
    return status;
  }

  public String targetPhone() {
    return targetPhone;
  }
}
