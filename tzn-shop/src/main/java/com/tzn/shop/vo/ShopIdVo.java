package com.tzn.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopIdVo {
  /** 标识。 */
  private Long id;

  public Long id() {
    return id;
  }
}
