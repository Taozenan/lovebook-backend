package com.tzn.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 订单创建请求。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderCreateRequest {
  /** 商品标识。 */
  private Long itemId;

  /** 购买数量。 */
  private Integer quantity;

  /** 预约时间。 */
  private String appointmentTime;

  /** 订单备注。 */
  private String remark;

  public Long itemId() {
    return itemId;
  }

  public Integer quantity() {
    return quantity;
  }

  public String appointmentTime() {
    return appointmentTime;
  }

  public String remark() {
    return remark;
  }
}
