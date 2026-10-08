package com.tzn.shop.enums;

/** 订单评价状态。 */
public enum ShopReviewStatus {
  /** 未评价。 */
  NOT_REVIEWED(0),
  /** 已评价。 */
  REVIEWED(1);

  private final int code;

  ShopReviewStatus(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
