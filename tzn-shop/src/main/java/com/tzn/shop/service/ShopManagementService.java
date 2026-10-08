package com.tzn.shop.service;

import com.tzn.shop.dto.ShopItemRequest;
import com.tzn.shop.vo.ShopIdVo;
import com.tzn.shop.vo.ShopManagementOverviewVo;

public interface ShopManagementService {
  ShopManagementOverviewVo overview(String authorization, Integer itemType, Integer status);

  ShopIdVo create(String authorization, ShopItemRequest request);

  ShopIdVo update(String authorization, long itemId, ShopItemRequest request);

  void changeStatus(String authorization, long itemId, Integer status);

  void changeRecommendation(String authorization, long itemId, Integer recommended);

  void delete(String authorization, long itemId);
}
