package com.tzn.shop.service;

import com.tzn.shop.dto.ShopOrderCreateRequest;
import com.tzn.shop.vo.ShopAccountVo;
import com.tzn.shop.vo.ShopOrderCreatedVo;
import com.tzn.shop.vo.ShopOrderDetailVo;
import com.tzn.shop.vo.ShopOrderPageVo;

public interface ShopOrderService {
  ShopAccountVo account(String authorization);

  ShopOrderCreatedVo create(String authorization, ShopOrderCreateRequest request);

  ShopOrderPageVo buyerList(String authorization, Integer status, int pageNum, int pageSize);

  ShopOrderPageVo sellerList(String authorization, Integer status, int pageNum, int pageSize);

  ShopOrderDetailVo detail(String authorization, long orderId);

  void accept(String authorization, long orderId);

  void complete(String authorization, long orderId);
}
