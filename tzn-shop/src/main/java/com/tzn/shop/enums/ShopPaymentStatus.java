package com.tzn.shop.enums;

/** 订单支付状态。 */
public enum ShopPaymentStatus {
  /** 未支付。 */
  UNPAID(0),
  /** 已支付。 */
  PAID(1);

  private final int code;

  ShopPaymentStatus(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
