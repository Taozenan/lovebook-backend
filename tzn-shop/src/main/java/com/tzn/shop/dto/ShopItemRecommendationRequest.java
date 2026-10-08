package com.tzn.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品推荐状态更新请求。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemRecommendationRequest {
    /**
     * 是否推荐。
     */
    private Integer recommended;

    public Integer recommended() {
        return recommended;
    }
}
