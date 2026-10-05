package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_user_role")
public class SysUserRole {
    @TableField("user_id") private Long userId;
    @TableField("role_id") private Long roleId;
    public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;}
    public Long getRoleId(){return roleId;} public void setRoleId(Long v){roleId=v;}
}