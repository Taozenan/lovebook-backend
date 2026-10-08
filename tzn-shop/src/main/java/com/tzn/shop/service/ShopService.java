package com.tzn.shop.service;

import com.tzn.shop.vo.ShopHomeVo;
import com.tzn.shop.vo.ShopItemDetailVo;
import com.tzn.shop.vo.ShopItemListVo;

public interface ShopService {

  ShopHomeVo home(String authorization);

  ShopItemListVo list(
      String authorization, Integer itemType, String keyword, Long categoryId, Boolean recommended);

  ShopItemDetailVo detail(long itemId);
}
