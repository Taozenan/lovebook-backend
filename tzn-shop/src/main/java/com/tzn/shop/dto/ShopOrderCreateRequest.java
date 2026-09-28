package com.tzn.shop.dto;

public record ShopOrderCreateRequest(Long itemId, Integer quantity, String appointmentTime, String remark) {
}