package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("couple_binding")
public class CoupleBinding {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("requester_user_id")
    private Long requesterUserId;

    @TableField("target_user_id")
    private Long targetUserId;

    private String status;

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

    public Long getRequesterUserId() {
        return requesterUserId;
    }

    public void setRequesterUserId(Long v) {
        requesterUserId = v;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long v) {
        targetUserId = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        status = v;
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
