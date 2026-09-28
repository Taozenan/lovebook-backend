package com.tzn.shop.enums;

public enum ShopEnabledStatus {
    DISABLED(0), ENABLED(1);
    private final int code;
    ShopEnabledStatus(int code) { this.code = code; }
    public int getCode() { return code; }
}
