package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("shop_category")
public class ShopCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("category_type")
    private Integer categoryType;
    private String name;
    @TableField("icon_url")
    private String iconUrl;
    @TableField("sort_no")
    private Integer sortNo;
    private Integer status;
    @TableField("del_flag")
    private Integer delFlag;
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

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer v) {
        categoryType = v;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        name = v;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String v) {
        iconUrl = v;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer v) {
        sortNo = v;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer v) {
        status = v;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer v) {
        delFlag = v;
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