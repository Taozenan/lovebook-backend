package com.tzn.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 登录请求。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
  /** 登录账号。 */
  @NotBlank private String username;

  /** 登录密码。 */
  @NotBlank private String password;

  public String username() {
    return username;
  }

  public String password() {
    return password;
  }
}
