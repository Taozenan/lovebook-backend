package com.tzn.shop.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 订单详情。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderVo {
  /** 标识。 */
  private Long id;

  /** 订单号。 */
  private String orderNo;

  /** 买家用户标识。 */
  private Long buyerUserId;

  /** 卖家用户标识。 */
  private Long sellerUserId;

  /** 订单总金额。 */
  private BigDecimal totalAmount;

  /** 状态。 */
  private Integer status;

  /** 评价标记。 */
  private Integer isReviewed;

  /** 支付状态。 */
  private Integer paymentStatus;

  /** 支付方式。 */
  private Integer paymentMethod;

  /** 已支付金额。 */
  private BigDecimal paidAmount;

  /** 支付时间。 */
  private LocalDateTime paidTime;

  /** 预约时间。 */
  private LocalDateTime appointmentTime;

  /** 备注。 */
  private String remark;

  /** 创建时间。 */
  private LocalDateTime createTime;

  /** 买家昵称。 */
  private String buyerName;

  /** 卖家昵称。 */
  private String sellerName;

  public Long id() {
    return id;
  }

  public String orderNo() {
    return orderNo;
  }

  public Long buyerUserId() {
    return buyerUserId;
  }

  public Long sellerUserId() {
    return sellerUserId;
  }

  public BigDecimal totalAmount() {
    return totalAmount;
  }

  public Integer status() {
    return status;
  }

  public Integer isReviewed() {
    return isReviewed;
  }

  public Integer paymentStatus() {
    return paymentStatus;
  }

  public Integer paymentMethod() {
    return paymentMethod;
  }

  public BigDecimal paidAmount() {
    return paidAmount;
  }

  public LocalDateTime paidTime() {
    return paidTime;
  }

  public LocalDateTime appointmentTime() {
    return appointmentTime;
  }

  public String remark() {
    return remark;
  }

  public LocalDateTime createTime() {
    return createTime;
  }

  public String buyerName() {
    return buyerName;
  }

  public String sellerName() {
    return sellerName;
  }
}
