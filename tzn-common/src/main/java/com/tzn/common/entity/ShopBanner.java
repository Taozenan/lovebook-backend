package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("shop_banner")
public class ShopBanner {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    @TableField("image_url")
    private String imageUrl;
    @TableField("link_item_id")
    private Long linkItemId;
    @TableField("sort_no")
    private Integer sortNo;
    private Integer status;
    @TableField("start_time")
    private LocalDateTime startTime;
    @TableField("end_time")
    private LocalDateTime endTime;
    @TableField("del_flag")
    private Integer delFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String v) {
        title = v;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String v) {
        imageUrl = v;
    }

    public Long getLinkItemId() {
        return linkItemId;
    }

    public void setLinkItemId(Long v) {
        linkItemId = v;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime v) {
        startTime = v;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime v) {
        endTime = v;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer v) {
        delFlag = v;
    }
}