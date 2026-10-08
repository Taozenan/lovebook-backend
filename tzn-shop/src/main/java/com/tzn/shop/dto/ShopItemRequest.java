package com.tzn.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品新增或更新请求。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemRequest {
  /** 商品分类标识。 */
  private Long categoryId;

  /** 商品类型。 */
  private Integer itemType;

  /** 商品标题。 */
  private String title;

  /** 商品副标题。 */
  private String subtitle;

  /** 商品描述。 */
  private String description;

  /** 商品单价。 */
  private BigDecimal price;

  /** 商品库存。 */
  private Integer stock;

  /** 封面图片地址。 */
  private String coverUrl;

  /** 服务时长，单位为分钟。 */
  private Integer serviceDurationMin;

  /** 服务规则。 */
  private String serviceRule;

  /**
   * 商品图片地址列表。
   */
  private List<String> imageUrls;

  /**
   * 商品状态。
   */
  private Integer status;

  public Long categoryId() {
    return categoryId;
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

  public String serviceRule() {
    return serviceRule;
  }

  public List<String> imageUrls() {
    return imageUrls;
  }

  public Integer status() {
    return status;
  }
}
