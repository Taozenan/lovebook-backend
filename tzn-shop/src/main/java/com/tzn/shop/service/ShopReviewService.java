package com.tzn.shop.service;

import com.tzn.shop.dto.ShopReviewCreateRequest;
import com.tzn.shop.vo.ShopReviewCreatedVo;

public interface ShopReviewService {
    ShopReviewCreatedVo create(String authorization, long orderId, ShopReviewCreateRequest request);
}
