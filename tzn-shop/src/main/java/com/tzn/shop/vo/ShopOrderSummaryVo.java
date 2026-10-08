package com.tzn.shop.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderSummaryVo {
  /** 标识。 */
  private Long id;

  /** 订单号。 */
  private String orderNo;

  /** 订单总金额。 */
  private BigDecimal totalAmount;

  /** 状态。 */
  private Integer status;

  /** 评价标记。 */
  private Integer isReviewed;

  /** 是否已评价。 */
  private Integer reviewed;

  /** 是否可以评价。 */
  private boolean canReview;

  /** 创建时间。 */
  private LocalDateTime createTime;

  /** 商品标题。 */
  private String itemTitle;

  /** 商品类型。 */
  private Integer itemType;

  /** 购买数量。 */
  private Integer quantity;

  /** 封面图片地址。 */
  private String coverUrl;

  /** 买家昵称。 */
  private String buyerName;

  /** 买家头像地址。 */
  private String buyerAvatarUrl;

  public Long id() {
    return id;
  }

  public String orderNo() {
    return orderNo;
  }

  public BigDecimal totalAmount() {
    return totalAmount;
  }

  public Integer status() {
    return status;
  }

  public Integer isReviewed() {
    return isReviewed;
  }

  public Integer reviewed() {
    return reviewed;
  }

  public boolean canReview() {
    return canReview;
  }

  public LocalDateTime createTime() {
    return createTime;
  }

  public String itemTitle() {
    return itemTitle;
  }

  public Integer itemType() {
    return itemType;
  }

  public Integer quantity() {
    return quantity;
  }

  public String coverUrl() {
    return coverUrl;
  }

  public String buyerName() {
    return buyerName;
  }

  public String buyerAvatarUrl() {
    return buyerAvatarUrl;
  }
}
