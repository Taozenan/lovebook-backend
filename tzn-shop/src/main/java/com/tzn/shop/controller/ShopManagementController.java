package com.tzn.shop.controller;

import com.tzn.common.api.ApiResponse;
import com.tzn.shop.dto.ShopItemRequest;
import com.tzn.shop.service.ShopManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shop/manage")
public class ShopManagementController {
    private final ShopManagementService service;

    public ShopManagementController(ShopManagementService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "status", required = false) Integer status) {
        return ApiResponse.success(service.overview(authorization, type, status));
    }

    @PostMapping("/items")
    public ApiResponse<Map<String, Object>> create(
            @RequestHeader("Authorization") String authorization, @RequestBody ShopItemRequest request) {
        return ApiResponse.success(service.create(authorization, request));
    }

    @PutMapping("/items/{id}")
    public ApiResponse<Map<String, Object>> update(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("id") long id,
            @RequestBody ShopItemRequest request) {
        return ApiResponse.success(service.update(authorization, id, request));
    }

    @PutMapping("/items/{id}/status")
    public ApiResponse<Void> status(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("id") long id,
            @RequestBody Map<String, Integer> body) {
        service.changeStatus(authorization, id, body.get("status"));
        return ApiResponse.success(null);
    }

    @PutMapping("/items/{id}/recommend")
    public ApiResponse<Void> recommend(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("id") long id,
            @RequestBody Map<String, Integer> body) {
        service.changeRecommendation(authorization, id, body.get("recommended"));
        return ApiResponse.success(null);
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> delete(
            @RequestHeader("Authorization") String authorization, @PathVariable("id") long id) {
        service.delete(authorization, id);
        return ApiResponse.success(null);
    }
}
