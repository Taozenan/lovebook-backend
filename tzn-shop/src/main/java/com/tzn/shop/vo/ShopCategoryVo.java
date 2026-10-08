package com.tzn.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopCategoryVo {
  /** 标识。 */
  private Long id;

  /** 分类类型。 */
  private Integer categoryType;

  /** 名称。 */
  private String name;

  /** 图标地址。 */
  private String iconUrl;

  public Long id() {
    return id;
  }

  public Integer categoryType() {
    return categoryType;
  }

  public String name() {
    return name;
  }

  public String iconUrl() {
    return iconUrl;
  }
}
