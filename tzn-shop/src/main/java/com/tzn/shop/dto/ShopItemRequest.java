package com.tzn.shop.dto;

import java.math.BigDecimal;
import java.util.List;

public record ShopItemRequest(Long categoryId, Integer itemType, String title, String subtitle,
                              String description, BigDecimal price, Integer stock, String coverUrl,
                              Integer serviceDurationMin, String serviceRule, List<String> imageUrls,
                              Integer status) {
}
