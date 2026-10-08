package com.tzn.shop.controller;

import com.tzn.common.api.ApiResponse;
import com.tzn.shop.dto.ShopOrderQueryRequest;
import com.tzn.shop.service.ShopOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shop/manage/orders")
public class ShopOrderManagementController {
    private final ShopOrderService shopOrderService;

    public ShopOrderManagementController(ShopOrderService shopOrderService) {
        this.shopOrderService = shopOrderService;
    }

    @PostMapping("/query")
    public ApiResponse<Map<String, Object>> query(
            @RequestHeader("Authorization") String authorization,
            @RequestBody(required = false) ShopOrderQueryRequest request) {
        ShopOrderQueryRequest condition =
                request == null ? new ShopOrderQueryRequest(null, 1, 10) : request;
        return ApiResponse.success(
                shopOrderService.sellerList(
                        authorization, condition.status(), condition.safePageNum(), condition.safePageSize()));
    }

    @GetMapping("/get/{id}")
    public ApiResponse<Map<String, Object>> get(
            @RequestHeader("Authorization") String authorization, @PathVariable("id") long id) {
        return ApiResponse.success(shopOrderService.detail(authorization, id));
    }

    @PutMapping("/accept/{id}")
    public ApiResponse<Void> accept(
            @RequestHeader("Authorization") String authorization, @PathVariable("id") long id) {
        shopOrderService.accept(authorization, id);
        return ApiResponse.success(null);
    }
}
