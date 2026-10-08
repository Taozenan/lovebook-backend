package com.tzn.shop.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderCreatedVo {
  /** 标识。 */
  private Long id;

  /** 订单号。 */
  private String orderNo;

  /** 状态。 */
  private int status;

  /** 订单总金额。 */
  private BigDecimal totalAmount;

  /** 剩余余额。 */
  private BigDecimal remainingBalance;

  public Long id() {
    return id;
  }

  public String orderNo() {
    return orderNo;
  }

  public int status() {
    return status;
  }

  public BigDecimal totalAmount() {
    return totalAmount;
  }

  public BigDecimal remainingBalance() {
    return remainingBalance;
  }
}
