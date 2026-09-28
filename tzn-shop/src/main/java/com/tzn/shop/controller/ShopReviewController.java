package com.tzn.shop.controller;

import com.tzn.common.api.ApiResponse;
import com.tzn.shop.dto.ShopReviewCreateRequest;
import com.tzn.shop.service.ShopReviewService;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop/orders")
public class ShopReviewController {
    private final ShopReviewService shopReviewService;
    public ShopReviewController(ShopReviewService shopReviewService) { this.shopReviewService = shopReviewService; }

    @PostMapping("/{id}/review")
    public ApiResponse<Map<String, Object>> create(@RequestHeader("Authorization") String authorization, @PathVariable("id") long id, @RequestBody ShopReviewCreateRequest request) { return ApiResponse.success(shopReviewService.create(authorization, id, request)); }
}