package com.tzn.shop.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopReviewSummaryVo {
  /** 平均评分。 */
  private BigDecimal averageRating;

  /** 评价数量。 */
  private int reviewCount;

  public BigDecimal averageRating() {
    return averageRating;
  }

  public int reviewCount() {
    return reviewCount;
  }
}
