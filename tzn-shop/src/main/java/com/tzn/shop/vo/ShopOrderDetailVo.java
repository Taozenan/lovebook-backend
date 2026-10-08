package com.tzn.shop.vo;

import com.tzn.common.entity.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 订单详情响应。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderDetailVo {
  /** 订单信息。 */
  private ShopOrderVo order;

  /** 是否可以接单。 */
  private boolean canAccept;

  /** 是否可以确认完成。 */
  private boolean canComplete;

  /** 是否已评价。 */
  private boolean reviewed;

  /** 是否可以评价。 */
  private boolean canReview;

  /** 列表数据。 */
  private List<ShopOrderItem> items;

  /** 评价信息。 */
  private List<ShopItemReview> review;

  /** 订单轨迹列表。 */
  private List<ShopOrderTrack> tracks;

  public ShopOrderVo order() {
    return order;
  }

  public boolean canAccept() {
    return canAccept;
  }

  public boolean canComplete() {
    return canComplete;
  }

  public boolean reviewed() {
    return reviewed;
  }

  public boolean canReview() {
    return canReview;
  }

  public List<ShopOrderItem> items() {
    return items;
  }

  public List<ShopItemReview> review() {
    return review;
  }

  public List<ShopOrderTrack> tracks() {
    return tracks;
  }
}
