package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("shop_item_review")
public class ShopItemReview {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("item_id")
    private Long itemId;

    @TableField("reviewer_user_id")
    private Long reviewerUserId;

    @TableField("seller_user_id")
    private Long sellerUserId;

    private Integer rating;
    private String content;

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

    public Long getReviewerUserId() {
        return reviewerUserId;
    }

    public void setReviewerUserId(Long v) {
        reviewerUserId = v;
    }

    public Long getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(Long v) {
        sellerUserId = v;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer v) {
        rating = v;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String v) {
        content = v;
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
}
