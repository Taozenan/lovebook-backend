package com.tzn.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemImageVo {
  /** 标识。 */
  private Long id;

  /** 图片地址。 */
  private String imageUrl;

  /** 排序号。 */
  private Integer sortNo;

  public Long id() {
    return id;
  }

  public String imageUrl() {
    return imageUrl;
  }

  public Integer sortNo() {
    return sortNo;
  }
}
