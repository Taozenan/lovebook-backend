package com.tzn.shop.controller;

import com.tzn.common.api.ApiResponse;
import com.tzn.shop.service.ShopService;
import com.tzn.shop.vo.ShopHomeVo;
import com.tzn.shop.vo.ShopItemDetailVo;
import com.tzn.shop.vo.ShopItemListVo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

  private final ShopService shopService;

  public ShopController(ShopService shopService) {
    this.shopService = shopService;
  }

  @GetMapping("/home")
  public ApiResponse<ShopHomeVo> home(@RequestHeader("Authorization") String authorization) {
    return ApiResponse.success(shopService.home(authorization));
  }

  @GetMapping("/items")
  public ApiResponse<ShopItemListVo> list(
      @RequestHeader("Authorization") String authorization,
      @RequestParam(name = "type", required = false) Integer type,
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "categoryId", required = false) Long categoryId,
      @RequestParam(name = "recommended", required = false) Boolean recommended) {
    return ApiResponse.success(
        shopService.list(authorization, type, keyword, categoryId, recommended));
  }

  @GetMapping("/items/{id}")
  public ApiResponse<ShopItemDetailVo> detail(@PathVariable("id") long id) {
    return ApiResponse.success(shopService.detail(id));
  }
}
