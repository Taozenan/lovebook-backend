package com.tzn.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 商品上下架状态更新请求。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemStatusRequest {
  /** 状态。 */
  private Integer status;

  public Integer status() {
    return status;
  }
}
