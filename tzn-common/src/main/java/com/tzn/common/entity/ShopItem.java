package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("shop_item")
public class ShopItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("category_id")
    private Long categoryId;

    @TableField("item_type")
    private Integer itemType;

    private String title, subtitle, description;
    private BigDecimal price;
    private Integer stock;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("seller_user_id")
    private Long sellerUserId;

    @TableField("service_duration_min")
    private Integer serviceDurationMin;

    @TableField("service_rule")
    private String serviceRule;

    @TableField("is_recommended")
    private Integer isRecommended;

    private Integer status;

    @TableField("sort_no")
    private Integer sortNo;

    @TableField("del_flag")
    private Integer delFlag;

    @TableField("create_user")
    private Long createUser;

    @TableField("update_user")
    private Long updateUser;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("publish_time")
    private LocalDateTime publishTime;

    @TableField("off_shelf_time")
    private LocalDateTime offShelfTime;

    @TableField("off_shelf_reason")
    private String offShelfReason;

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long v) {
        categoryId = v;
    }

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer v) {
        itemType = v;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String v) {
        title = v;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String v) {
        subtitle = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        description = v;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal v) {
        price = v;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer v) {
        stock = v;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String v) {
        coverUrl = v;
    }

    public Long getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(Long v) {
        sellerUserId = v;
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

    public Integer getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(Integer v) {
        isRecommended = v;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer v) {
        status = v;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer v) {
        sortNo = v;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer v) {
        delFlag = v;
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime v) {
        updateTime = v;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime v) {
        publishTime = v;
    }

    public LocalDateTime getOffShelfTime() {
        return offShelfTime;
    }

    public void setOffShelfTime(LocalDateTime v) {
        offShelfTime = v;
    }

    public String getOffShelfReason() {
        return offShelfReason;
    }

    public void setOffShelfReason(String v) {
        offShelfReason = v;
    }
}
