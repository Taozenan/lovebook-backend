package com.tzn.shop.enums;

public enum ShopOrderStatus {
    WAIT_ACCEPT(1, "待接单"), ACCEPTED(2, "已接待"), COMPLETED(3, "已完成");
    private final int code;
    private final String description;

    ShopOrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ShopOrderStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ShopOrderStatus value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的订单状态：" + code);
    }
}