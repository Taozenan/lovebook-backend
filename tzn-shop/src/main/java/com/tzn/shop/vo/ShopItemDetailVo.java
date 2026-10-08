package com.tzn.shop.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemDetailVo {
  /** 商品信息。 */
  private ShopItemVo item;

  /** 商品图片列表。 */
  private List<ShopItemImageVo> images;

  /** 评价列表。 */
  private List<ShopItemReviewVo> reviews;

  /** 评价汇总。 */
  private ShopReviewSummaryVo reviewSummary;

  public ShopItemVo item() {
    return item;
  }

  public List<ShopItemImageVo> images() {
    return images;
  }

  public List<ShopItemReviewVo> reviews() {
    return reviews;
  }

  public ShopReviewSummaryVo reviewSummary() {
    return reviewSummary;
  }
}
