package com.tzn.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 绑定邀请请求。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindingInviteRequest {
  /** 目标用户手机号。 */
  private String targetPhone;

  public String targetPhone() {
    return targetPhone;
  }
}
