package com.tzn.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单列表查询请求。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderQueryRequest {
  /** 订单状态，空或 0 表示全部。 */
  private Integer status;

  /** 页码。 */
  private Integer pageNum;

  /** 每页数量。 */
  private Integer pageSize;

  public Integer status() {
    return status;
  }

  public int safePageNum() {
    return pageNum == null ? 1 : pageNum;
  }

  public int safePageSize() {
    return pageSize == null ? 10 : pageSize;
  }
}
