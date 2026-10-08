package com.tzn.shop.controller;

import com.tzn.common.api.ApiResponse;
import com.tzn.shop.dto.ShopOrderCreateRequest;
import com.tzn.shop.dto.ShopOrderQueryRequest;
import com.tzn.shop.service.ShopOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shop/orders")
public class ShopOrderController {
  private final ShopOrderService shopOrderService;

  public ShopOrderController(ShopOrderService shopOrderService) {
    this.shopOrderService = shopOrderService;
  }

  @GetMapping("/account")
  public ApiResponse<Map<String, Object>> account(
          @RequestHeader("Authorization") String authorization) {
    return ApiResponse.success(shopOrderService.account(authorization));
  }

  @PostMapping
  public ApiResponse<Map<String, Object>> create(
          @RequestHeader("Authorization") String authorization,
          @RequestBody ShopOrderCreateRequest request) {
    return ApiResponse.success(shopOrderService.create(authorization, request));
  }

  @PostMapping("/query")
  public ApiResponse<Map<String, Object>> query(
          @RequestHeader("Authorization") String authorization,
          @RequestBody(required = false) ShopOrderQueryRequest request) {
    ShopOrderQueryRequest condition =
            request == null ? new ShopOrderQueryRequest(null, 1, 10) : request;
    return ApiResponse.success(
            shopOrderService.buyerList(
                    authorization, condition.status(), condition.safePageNum(), condition.safePageSize()));
  }

  @GetMapping("/get/{id}")
  public ApiResponse<Map<String, Object>> get(
          @RequestHeader("Authorization") String authorization, @PathVariable("id") long id) {
    return ApiResponse.success(shopOrderService.detail(authorization, id));
  }

  @PutMapping("/complete/{id}")
  public ApiResponse<Void> complete(
          @RequestHeader("Authorization") String authorization, @PathVariable("id") long id) {
    shopOrderService.complete(authorization, id);
    return ApiResponse.success(null);
  }
}
