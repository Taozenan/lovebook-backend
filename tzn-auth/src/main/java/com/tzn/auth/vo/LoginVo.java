package com.tzn.auth.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 登录成功后的身份与令牌信息。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVo {
  /** 访问令牌。 */
  private String token;

  /** 登录账号。 */
  private String username;

  /** 用户昵称。 */
  private String nickname;

  /** 头像图片地址。 */
  private String avatarUrl;

  /** 角色编码列表。 */
  private List<String> roles;

  /** 令牌有效期，单位为秒。 */
  private long expiresIn;

  public String token() {
    return token;
  }

  public String username() {
    return username;
  }

  public String nickname() {
    return nickname;
  }

  public String avatarUrl() {
    return avatarUrl;
  }

  public List<String> roles() {
    return roles;
  }

  public long expiresIn() {
    return expiresIn;
  }
}
