package com.tzn.shop.dto;

public record ShopOrderQueryRequest(Integer status, Integer pageNum, Integer pageSize) {
    public int safePageNum() {
        return pageNum == null ? 1 : pageNum;
    }

    public int safePageSize() {
        return pageSize == null ? 10 : pageSize;
    }
}
