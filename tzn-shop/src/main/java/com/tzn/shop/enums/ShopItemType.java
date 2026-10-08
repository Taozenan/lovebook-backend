package com.tzn.shop.enums;

public enum ShopItemType {
  FOOD(1, "餐品"),
  SERVICE(2, "服务"),
  PRODUCT(3, "商品");
  private final int code;
  private final String description;

  ShopItemType(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public int getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public static ShopItemType fromCode(Integer code) {
    if (code == null) {
      return null;
    }
    for (ShopItemType value : values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("不支持的商品类型：" + code);
  }
}
