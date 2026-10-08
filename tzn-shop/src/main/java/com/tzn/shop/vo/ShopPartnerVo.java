package com.tzn.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopPartnerVo {
  /** 是否已绑定。 */
  private boolean bound;

  /** 用户昵称。 */
  private String nickname;

  /** 手机号。 */
  private String phone;

  public boolean bound() {
    return bound;
  }

  public String nickname() {
    return nickname;
  }

  public String phone() {
    return phone;
  }
}
