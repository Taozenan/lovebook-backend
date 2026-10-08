package com.tzn.shop.enums;

/** 商城账户流水类型。 */
public enum ShopAccountFlowType {
  /** 购买商品扣款。 */
  PURCHASE(1);

  private final int code;

  ShopAccountFlowType(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
