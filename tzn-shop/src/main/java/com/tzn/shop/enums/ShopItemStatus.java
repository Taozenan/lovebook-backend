package com.tzn.shop.enums;

public enum ShopItemStatus {
    DRAFT(0), ON_SHELF(1), OFF_SHELF(2);
    private final int code;
    ShopItemStatus(int code) { this.code = code; }
    public int getCode() { return code; }
    public static ShopItemStatus fromCode(Integer code) {
        if (code == null) { return null; }
        for (ShopItemStatus value : values()) { if (value.code == code) { return value; } }
        throw new IllegalArgumentException("不支持的商品状态：" + code);
    }
}
