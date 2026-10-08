package com.tzn.shop.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopManagementOverviewVo {
  /** 列表数据。 */
  private List<ShopManagementItemVo> items;

  /** 已发布商品数量。 */
  private int publishedCount;

  /** 待处理订单数量。 */
  private long pendingOrderCount;

  /** 今日收入。 */
  private BigDecimal todayIncome;

  public List<ShopManagementItemVo> items() {
    return items;
  }

  public int publishedCount() {
    return publishedCount;
  }

  public long pendingOrderCount() {
    return pendingOrderCount;
  }

  public BigDecimal todayIncome() {
    return todayIncome;
  }
}
