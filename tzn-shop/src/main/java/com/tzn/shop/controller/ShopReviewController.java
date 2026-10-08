package com.tzn.shop.controller;

import com.tzn.common.api.ApiResponse;
import com.tzn.shop.dto.ShopReviewCreateRequest;
import com.tzn.shop.service.ShopReviewService;
import com.tzn.shop.vo.ShopReviewCreatedVo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/orders")
public class ShopReviewController {
  private final ShopReviewService shopReviewService;

  public ShopReviewController(ShopReviewService shopReviewService) {
    this.shopReviewService = shopReviewService;
  }

  @PostMapping("/{id}/review")
  public ApiResponse<ShopReviewCreatedVo> create(
          @RequestHeader("Authorization") String authorization,
          @PathVariable("id") long id,
          @RequestBody ShopReviewCreateRequest request) {
    return ApiResponse.success(shopReviewService.create(authorization, id, request));
  }
}
