package com.tzn.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品评价创建请求。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopReviewCreateRequest {
  /** 评分。 */
  private Integer rating;

  /** 内容。 */
  private String content;

  public Integer rating() {
    return rating;
  }

  public String content() {
    return content;
  }
}
