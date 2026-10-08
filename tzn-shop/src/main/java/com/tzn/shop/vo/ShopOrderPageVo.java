package com.tzn.shop.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderPageVo {
  /** 列表数据。 */
  private List<ShopOrderSummaryVo> items;

  /** 总数量。 */
  private long total;

  /** 页码。 */
  private int pageNum;

  /** 每页数量。 */
  private int pageSize;

  public List<ShopOrderSummaryVo> items() {
    return items;
  }

  public long total() {
    return total;
  }

  public int pageNum() {
    return pageNum;
  }

  public int pageSize() {
    return pageSize;
  }
}
