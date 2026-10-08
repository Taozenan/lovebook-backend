package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("shop_account_flow")
public class ShopAccountFlow {
  @TableId(type = IdType.AUTO)
  private Long id;

  @TableField("user_id")
  private Long userId;

  @TableField("order_id")
  private Long orderId;

  @TableField("order_no")
  private String orderNo;

  @TableField("flow_type")
  private Integer flowType;

  private BigDecimal amount;

  @TableField("balance_before")
  private BigDecimal balanceBefore;

  @TableField("balance_after")
  private BigDecimal balanceAfter;

  private String remark;

  @TableField("create_user")
  private Long createUser;

  @TableField("update_user")
  private Long updateUser;

  @TableField("create_time")
  private LocalDateTime createTime;

  public Long getId() {
    return id;
  }

  public void setId(Long v) {
    id = v;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long v) {
    userId = v;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long v) {
    orderId = v;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String v) {
    orderNo = v;
  }

  public Integer getFlowType() {
    return flowType;
  }

  public void setFlowType(Integer v) {
    flowType = v;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal v) {
    amount = v;
  }

  public BigDecimal getBalanceBefore() {
    return balanceBefore;
  }

  public void setBalanceBefore(BigDecimal v) {
    balanceBefore = v;
  }

  public BigDecimal getBalanceAfter() {
    return balanceAfter;
  }

  public void setBalanceAfter(BigDecimal v) {
    balanceAfter = v;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String v) {
    remark = v;
  }

  public Long getCreateUser() {
    return createUser;
  }

  public void setCreateUser(Long v) {
    createUser = v;
  }

  public Long getUpdateUser() {
    return updateUser;
  }

  public void setUpdateUser(Long v) {
    updateUser = v;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime v) {
    createTime = v;
  }
}
