package com.tzn.shop.enums;

public enum ShopCategoryType {
    FOOD(1, "点餐"), SERVICE(2, "专属服务"), PRODUCT(3, "商品");
    private final int code;
    private final String description;
    ShopCategoryType(int code, String description) { this.code = code; this.description = description; }
    public int getCode() { return code; }
    public String getDescription() { return description; }
}
