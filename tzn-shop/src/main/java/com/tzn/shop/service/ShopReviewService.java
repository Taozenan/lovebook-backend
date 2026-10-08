package com.tzn.shop.service;

import com.tzn.shop.dto.ShopReviewCreateRequest;

import java.util.Map;

public interface ShopReviewService {
    Map<String, Object> create(String authorization, long orderId, ShopReviewCreateRequest request);
}