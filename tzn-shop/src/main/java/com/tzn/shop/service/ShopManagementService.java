package com.tzn.shop.service;

import com.tzn.shop.dto.ShopItemRequest;
import java.util.Map;

public interface ShopManagementService {
    Map<String, Object> overview(String authorization, Integer itemType, Integer status);
    Map<String, Object> create(String authorization, ShopItemRequest request);
    Map<String, Object> update(String authorization, long itemId, ShopItemRequest request);
    void changeStatus(String authorization, long itemId, Integer status);
    void changeRecommendation(String authorization, long itemId, Integer recommended);
    void delete(String authorization, long itemId);
}
