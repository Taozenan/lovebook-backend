package com.tzn.shop.service;

import com.tzn.shop.vo.ShopHomeVo;

public interface ShopService {

    ShopHomeVo home(String authorization);

    ShopItemVoList list(
            String authorization, Integer itemType, String keyword, Long categoryId, Boolean recommended);

    ShopItemVoDetail detail(long itemId);
}
