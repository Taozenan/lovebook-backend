package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("shop_item_image")
public class ShopItemImage {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("item_id")
    private Long itemId;

    @TableField("image_url")
    private String imageUrl;

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

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long v) {
        itemId = v;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String v) {
        imageUrl = v;
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
}
