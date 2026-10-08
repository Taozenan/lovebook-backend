package com.tzn.shop.service;

import com.tzn.shop.dto.ShopOrderCreateRequest;
import com.tzn.shop.vo.ShopAccountVo;
import com.tzn.shop.vo.ShopOrderCreatedVo;
import com.tzn.shop.vo.ShopOrderDetailVo;

public interface ShopOrderService {
    ShopAccountVo account(String authorization);

    ShopOrderCreatedVo create(String authorization, ShopOrderCreateRequest request);

    ShopOrderVoPage buyerList(String authorization, Integer status, int pageNum, int pageSize);

    ShopOrderVoPage sellerList(String authorization, Integer status, int pageNum, int pageSize);

    ShopOrderDetailVo detail(String authorization, long orderId);

  void accept(String authorization, long orderId);

  void complete(String authorization, long orderId);
}
