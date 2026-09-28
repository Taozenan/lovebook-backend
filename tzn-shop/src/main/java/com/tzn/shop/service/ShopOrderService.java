package com.tzn.shop.service;

import com.tzn.shop.dto.ShopOrderCreateRequest;
import java.util.Map;

public interface ShopOrderService {
    Map<String, Object> account(String authorization);
    Map<String, Object> create(String authorization, ShopOrderCreateRequest request);
    Map<String, Object> buyerList(String authorization, Integer status, int pageNum, int pageSize);
    Map<String, Object> sellerList(String authorization, Integer status, int pageNum, int pageSize);
    Map<String, Object> detail(String authorization, long orderId);
    void accept(String authorization, long orderId);
    void complete(String authorization, long orderId);
}