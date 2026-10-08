package com.tzn.shop.controller;

import com.tzn.common.api.ApiResponse;
import com.tzn.shop.dto.ShopItemRecommendationRequest;
import com.tzn.shop.dto.ShopItemRequest;
import com.tzn.shop.dto.ShopItemStatusRequest;
import com.tzn.shop.service.ShopManagementService;
import com.tzn.shop.vo.ShopIdVo;
import com.tzn.shop.vo.ShopManagementOverviewVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/manage")
public class ShopManagementController {
  private final ShopManagementService service;

  public ShopManagementController(ShopManagementService service) {
    this.service = service;
  }

  @GetMapping("/overview")
  public ApiResponse<ShopManagementOverviewVo> overview(
      @RequestHeader("Authorization") String authorization,
      @RequestParam(name = "type", required = false) Integer type,
      @RequestParam(name = "status", required = false) Integer status) {
    return ApiResponse.success(service.overview(authorization, type, status));
  }

  @PostMapping("/items")
  public ApiResponse<ShopIdVo> create(
      @RequestHeader("Authorization") String authorization,
      @Valid @RequestBody ShopItemRequest request) {
    return ApiResponse.success(service.create(authorization, request));
  }

  @PutMapping("/items/{id}")
  public ApiResponse<ShopIdVo> update(
      @RequestHeader("Authorization") String authorization,
      @PathVariable("id") long id,
      @Valid @RequestBody ShopItemRequest request) {
    return ApiResponse.success(service.update(authorization, id, request));
  }

  @PutMapping("/items/{id}/status")
  public ApiResponse<Void> status(
      @RequestHeader("Authorization") String authorization,
      @PathVariable("id") long id,
      @Valid @RequestBody ShopItemStatusRequest request) {
    service.changeStatus(authorization, id, request.status());
    return ApiResponse.success(null);
  }

  @PutMapping("/items/{id}/recommend")
  public ApiResponse<Void> recommend(
      @RequestHeader("Authorization") String authorization,
      @PathVariable("id") long id,
      @Valid @RequestBody ShopItemRecommendationRequest request) {
    service.changeRecommendation(authorization, id, request.recommended());
    return ApiResponse.success(null);
  }

  @DeleteMapping("/items/{id}")
  public ApiResponse<Void> delete(
      @RequestHeader("Authorization") String authorization, @PathVariable("id") long id) {
    service.delete(authorization, id);
    return ApiResponse.success(null);
  }
}
