package com.tzn.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopReviewCreatedVo {
  /** 标识。 */
  private Long id;

  /** 订单标识。 */
  private long orderId;

  /** 评分。 */
  private Integer rating;

  /** 内容。 */
  private String content;

  public Long id() {
    return id;
  }

  public long orderId() {
    return orderId;
  }

  public Integer rating() {
    return rating;
  }

  public String content() {
    return content;
  }
}
