package com.tzn.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopBannerVo {
  /** 标识。 */
  private Long id;

  /** 标题。 */
  private String title;

  /** 图片地址。 */
  private String imageUrl;

  /** 关联商品标识。 */
  private Long linkItemId;

  public Long id() {
    return id;
  }

  public String title() {
    return title;
  }

  public String imageUrl() {
    return imageUrl;
  }

  public Long linkItemId() {
    return linkItemId;
  }
}
