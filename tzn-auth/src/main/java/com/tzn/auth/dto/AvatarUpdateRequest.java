package com.tzn.auth.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 头像更新请求。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarUpdateRequest {
  /** 头像图片地址。 */
  @Size(max = 500, message = "头像地址不能超过500个字符")
  /** 头像图片地址。 */
  private String avatarUrl;

  public String avatarUrl() {
    return avatarUrl;
  }
}
