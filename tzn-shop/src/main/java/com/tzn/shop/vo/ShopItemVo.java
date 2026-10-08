package com.tzn.shop.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemVo {
  /** 标识。 */
  private Long id;

  /** 商品类型。 */
  private Integer itemType;

  /** 标题。 */
  private String title;

  /** 副标题。 */
  private String subtitle;

  /** 描述。 */
  private String description;

  /** 商品单价。 */
  private BigDecimal price;

  /** 商品库存。 */
  private Integer stock;

  /** 封面图片地址。 */
  private String coverUrl;

  /** 服务时长，单位为分钟。 */
  private Integer serviceDurationMin;

  /** 卖家用户标识。 */
  private Long sellerId;

  /** 卖家手机号。 */
  private String sellerPhone;

  /** 卖家昵称。 */
  private String sellerName;

  public Long id() {
    return id;
  }

  public Integer itemType() {
    return itemType;
  }

  public String title() {
    return title;
  }

  public String subtitle() {
    return subtitle;
  }

  public String description() {
    return description;
  }

  public BigDecimal price() {
    return price;
  }

  public Integer stock() {
    return stock;
  }

  public String coverUrl() {
    return coverUrl;
  }

  public Integer serviceDurationMin() {
    return serviceDurationMin;
  }

  public Long sellerId() {
    return sellerId;
  }

  public String sellerPhone() {
    return sellerPhone;
  }

  public String sellerName() {
    return sellerName;
  }
}
