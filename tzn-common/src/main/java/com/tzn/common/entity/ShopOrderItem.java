package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("shop_order_item")
public class ShopOrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("order_id")
    private Long orderId;
    @TableField("item_id")
    private Long itemId;
    @TableField("item_title")
    private String itemTitle;
    @TableField("item_type")
    private Integer itemType;
    @TableField("unit_price")
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    @TableField("cover_url")
    private String coverUrl;
    @TableField("service_duration_min")
    private Integer serviceDurationMin;
    @TableField("service_rule")
    private String serviceRule;
    @TableField("create_time")
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long v) {
        orderId = v;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long v) {
        itemId = v;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String v) {
        itemTitle = v;
    }

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer v) {
        itemType = v;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal v) {
        unitPrice = v;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer v) {
        quantity = v;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal v) {
        subtotal = v;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String v) {
        coverUrl = v;
    }

    public Integer getServiceDurationMin() {
        return serviceDurationMin;
    }

    public void setServiceDurationMin(Integer v) {
        serviceDurationMin = v;
    }

    public String getServiceRule() {
        return serviceRule;
    }

    public void setServiceRule(String v) {
        serviceRule = v;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime v) {
        createTime = v;
    }
}