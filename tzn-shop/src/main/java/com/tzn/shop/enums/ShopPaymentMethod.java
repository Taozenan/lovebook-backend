package com.tzn.shop.enums;

/** 商城支付方式。 */
public enum ShopPaymentMethod {
  /** 余额支付。 */
  BALANCE(1);

  private final int code;

  ShopPaymentMethod(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
