package com.tzn.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册请求。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
  /** 手机号。 */
  @NotBlank
  @Pattern(regexp = "^1\\d{10}$")
  /** 手机号。 */
  private String phone;

  /**
   * 用户昵称。
   */
  @NotBlank
  @Size(max = 32)
  /** 用户昵称。 */
  private String nickname;

  /**
   * 登录密码。
   */
  @NotBlank
  @Size(min = 6, max = 64)
  /** 登录密码。 */
  private String password;

  public String phone() {
    return phone;
  }

  public String nickname() {
    return nickname;
  }

  public String password() {
    return password;
  }
}
