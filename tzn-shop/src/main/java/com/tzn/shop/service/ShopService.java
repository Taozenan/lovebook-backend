package com.tzn.shop.service;

import java.util.Map;

public interface ShopService {

  Map<String, Object> home(String authorization);

  Map<String, Object> list(
          String authorization, Integer itemType, String keyword, Long categoryId, Boolean recommended);

  Map<String, Object> detail(long itemId);
}
