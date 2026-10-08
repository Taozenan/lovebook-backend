package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("shop_order_track")
public class ShopOrderTrack {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("order_id")
    private Long orderId;
    private Integer status;
    private String title, description;
    @TableField("operate_user_id")
    private Long operateUserId;
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long v) {
        orderId = v;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer v) {
        status = v;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String v) {
        title = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        description = v;
    }

    public Long getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Long v) {
        operateUserId = v;
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