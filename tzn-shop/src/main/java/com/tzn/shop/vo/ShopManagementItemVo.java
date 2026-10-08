package com.tzn.shop.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopManagementItemVo {
  /** 标识。 */
  private Long id;

  /** 商品分类标识。 */
  private Long categoryId;

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

  /** 服务规则。 */
  private String serviceRule;

  /** 是否推荐。 */
  private Integer isRecommended;

  /** 状态。 */
  private Integer status;

  /** 发布时间。 */
  private LocalDateTime publishTime;

  /** 下架时间。 */
  private LocalDateTime offShelfTime;

  /** 下架原因。 */
  private String offShelfReason;

  public Long id() {
    return id;
  }

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

  public Integer isRecommended() {
    return isRecommended;
  }

  public Integer status() {
    return status;
  }

  public LocalDateTime publishTime() {
    return publishTime;
  }

  public LocalDateTime offShelfTime() {
    return offShelfTime;
  }

  public String offShelfReason() {
    return offShelfReason;
  }
}
